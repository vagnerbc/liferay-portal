/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.journal.internal.search.spi.model.index.contributor;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.Fields;
import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.dynamic.data.mapping.util.FieldsToDDMFormValuesConverter;
import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.util.JournalConverter;
import com.liferay.journal.util.impl.JournalUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelDocumentContributor.class
)
public class JournalArticleModelDocumentContributor
	implements ModelDocumentContributor<JournalArticle> {

	@Override
	public void contribute(Document document, JournalArticle journalArticle) {
		long classPK = journalArticle.getId();

		if (!isIndexAllArticleVersions()) {
			classPK = journalArticle.getResourcePrimKey();
		}

		document.addUID(_CLASS_NAME, classPK);

		String articleDefaultLanguageId = LocalizationUtil.getDefaultLanguageId(
			journalArticle.getDocument());

		String[] languageIds = LocalizationUtil.getAvailableLanguageIds(
			journalArticle.getDocument());

		for (String languageId : languageIds) {
			try {
				String content = extractDDMContent(journalArticle, languageId);

				String description = journalArticle.getDescription(languageId);

				String title = journalArticle.getTitle(languageId);

				if (languageId.equals(articleDefaultLanguageId)) {
					document.addText(Field.CONTENT, content);
					document.addText(Field.DESCRIPTION, description);
					document.addText("defaultLanguageId", languageId);
				}

				document.addText(
					LocalizationUtil.getLocalizedName(
						Field.CONTENT, languageId),
					content);
				document.addText(
					LocalizationUtil.getLocalizedName(
						Field.DESCRIPTION, languageId),
					description);
				document.addText(
					LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
					title);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to index journal article " +
							journalArticle.getArticleId(),
						e);
				}
			}
		}

		document.addKeyword(Field.FOLDER_ID, journalArticle.getFolderId());

		String articleId = journalArticle.getArticleId();

		if (journalArticle.isInTrash()) {
			articleId = _trashHelper.getOriginalTitle(articleId);
		}

		document.addKeywordSortable(Field.ARTICLE_ID, articleId);

		document.addDate(Field.DISPLAY_DATE, journalArticle.getDisplayDate());
		document.addKeyword(Field.LAYOUT_UUID, journalArticle.getLayoutUuid());
		document.addKeyword(
			Field.TREE_PATH,
			StringUtil.split(journalArticle.getTreePath(), CharPool.SLASH));
		document.addKeyword(Field.VERSION, journalArticle.getVersion());

		document.addKeyword(
			"ddmStructureKey", journalArticle.getDDMStructureKey());
		document.addKeyword(
			"ddmTemplateKey", journalArticle.getDDMTemplateKey());
		document.addKeyword("head", JournalUtil.isHead(journalArticle));

		boolean headListable = JournalUtil.isHeadListable(journalArticle);

		document.addKeyword("headListable", headListable);

		boolean latestArticle = JournalUtil.isLatestArticle(journalArticle);

		document.addKeyword("latest", latestArticle);

		// Scheduled listable articles should be visible in asset browser

		if (journalArticle.isScheduled() && headListable) {
			boolean visible = GetterUtil.getBoolean(document.get("visible"));

			if (!visible) {
				document.addKeyword("visible", true);
			}
		}

		try {
			addDDMStructureAttributes(document, journalArticle);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to index journal article " + articleId, e);
			}
		}
	}

	protected void addDDMStructureAttributes(
			Document document, JournalArticle article)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_portal.getSiteGroupId(article.getGroupId()),
			_portal.getClassNameId(JournalArticle.class),
			article.getDDMStructureKey(), true);

		if (ddmStructure == null) {
			return;
		}

		document.addKeyword(Field.CLASS_TYPE_ID, ddmStructure.getStructureId());

		DDMFormValues ddmFormValues = null;

		try {
			Fields fields = _journalConverter.getDDMFields(
				ddmStructure, article.getDocument());

			ddmFormValues = _fieldsToDDMFormValuesConverter.convert(
				ddmStructure, fields);
		}
		catch (Exception e) {
			return;
		}

		if (ddmFormValues != null) {
			_ddmIndexer.addAttributes(document, ddmStructure, ddmFormValues);
		}
	}

	protected String extractDDMContent(
			JournalArticle article, String languageId)
		throws Exception {

		DDMStructure ddmStructure = _ddmStructureLocalService.fetchStructure(
			_portal.getSiteGroupId(article.getGroupId()),
			_portal.getClassNameId(JournalArticle.class),
			article.getDDMStructureKey(), true);

		if (ddmStructure == null) {
			return StringPool.BLANK;
		}

		DDMFormValues ddmFormValues = null;

		try {
			Fields fields = _journalConverter.getDDMFields(
				ddmStructure, article.getDocument());

			ddmFormValues = _fieldsToDDMFormValuesConverter.convert(
				ddmStructure, fields);
		}
		catch (Exception e) {
			return StringPool.BLANK;
		}

		if (ddmFormValues == null) {
			return StringPool.BLANK;
		}

		return _ddmIndexer.extractIndexableAttributes(
			ddmStructure, ddmFormValues, LocaleUtil.fromLanguageId(languageId));
	}

	protected boolean isIndexAllArticleVersions() {
		JournalServiceConfiguration journalServiceConfiguration = null;

		try {
			journalServiceConfiguration =
				_configurationProvider.getCompanyConfiguration(
					JournalServiceConfiguration.class,
					CompanyThreadLocal.getCompanyId());

			return journalServiceConfiguration.indexAllArticleVersionsEnabled();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		return false;
	}

	@Reference(unbind = "-")
	protected void setConfigurationProvider(
		ConfigurationProvider configurationProvider) {

		_configurationProvider = configurationProvider;
	}

	@Reference(unbind = "-")
	protected void setDDMIndexer(DDMIndexer ddmIndexer) {
		_ddmIndexer = ddmIndexer;
	}

	@Reference(unbind = "-")
	protected void setDDMStructureLocalService(
		DDMStructureLocalService ddmStructureLocalService) {

		_ddmStructureLocalService = ddmStructureLocalService;
	}

	@Reference(unbind = "-")
	protected void setFieldsToDDMFormValuesConverter(
		FieldsToDDMFormValuesConverter fieldsToDDMFormValuesConverter) {

		_fieldsToDDMFormValuesConverter = fieldsToDDMFormValuesConverter;
	}

	@Reference(unbind = "-")
	protected void setJournalConverter(JournalConverter journalConverter) {
		_journalConverter = journalConverter;
	}

	private static final String _CLASS_NAME = JournalArticle.class.getName();

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelDocumentContributor.class);

	private ConfigurationProvider _configurationProvider;
	private DDMIndexer _ddmIndexer;
	private DDMStructureLocalService _ddmStructureLocalService;
	private FieldsToDDMFormValuesConverter _fieldsToDDMFormValuesConverter;
	private JournalConverter _journalConverter;

	@Reference
	private Portal _portal;

	@Reference
	private TrashHelper _trashHelper;

}