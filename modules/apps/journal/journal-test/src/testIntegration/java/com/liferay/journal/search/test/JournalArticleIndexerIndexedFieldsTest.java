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

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalArticleIndexerIndexedFieldsTest
	extends BaseJournalArticleIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_content = RandomTestUtil.randomString(30);
		_title = RandomTestUtil.randomString(30);
	}

	@Test
	public void testIndexedFields() throws Exception {
		JournalArticle journalArticle = addArticle(_title, _content);

		String searchTerm = journalArticle.getTitle();

		Document document = journalArticleIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(journalArticle);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	private Map<String, String> _expectedFieldValues(
			JournalArticle journalArticle)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ARTICLE_ID,
			StringUtil.toLowerCase(journalArticle.getArticleId()));
		map.put(
			Field.CLASS_NAME_ID,
			String.valueOf(journalArticle.getClassNameId()));
		map.put(Field.CLASS_PK, String.valueOf(journalArticle.getClassPK()));
		map.put(
			Field.CLASS_TYPE_ID,
			String.valueOf(journalArticle.getDDMStructure().getStructureId()));
		map.put(
			Field.COMPANY_ID, String.valueOf(journalArticle.getCompanyId()));
		map.put(Field.ENTRY_CLASS_NAME, JournalArticle.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(journalArticle.getResourcePrimKey()));
		map.put(Field.FOLDER_ID, String.valueOf(journalArticle.getFolderId()));
		map.put(Field.GROUP_ID, String.valueOf(journalArticle.getGroupId()));
		map.put(
			Field.LAYOUT_UUID,
			StringUtil.toLowerCase(journalArticle.getLayoutUuid()));
		map.put(
			Field.ROOT_ENTRY_CLASS_PK,
			String.valueOf(journalArticle.getResourcePrimKey()));
		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(journalArticle.getGroupId()));
		map.put(Field.STAGING_GROUP, "false");
		map.put(Field.STATUS, String.valueOf(journalArticle.getStatus()));
		map.put(Field.USER_ID, String.valueOf(journalArticle.getUserId()));
		map.put(
			Field.USER_NAME,
			StringUtil.toLowerCase(journalArticle.getUserName()));
		map.put(Field.VERSION, String.valueOf(journalArticle.getVersion()));

		map.put(
			"articleId_String_sortable",
			StringUtil.toLowerCase(journalArticle.getArticleId()));
		map.put("ddmStructureKey", journalArticle.getDDMStructureKey());
		map.put("ddmTemplateKey", journalArticle.getDDMTemplateKey());
		map.put("head", "true");
		map.put("headListable", "true");
		map.put("latest", "true");
		map.put("visible", "true");

		indexedFieldsFixture.populatePriority("0.0", map);

		indexedFieldsFixture.populateUID(
			JournalArticle.class.getName(), journalArticle.getPrimaryKey(),
			map);

		_populateContentDescriptionTitleAvaliableLanguages(journalArticle, map);

		_populateDates(journalArticle, map);

		_populateLocalizedTitles(journalArticle, map);

		_populateRoles(journalArticle, map);

		_populateTreePath(journalArticle, map);

		return map;
	}

	private void _populateContentDescriptionTitleAvaliableLanguages(
		JournalArticle journalArticle, Map<String, String> map) {

		String articleDefaultLanguageId = LocalizationUtil.getDefaultLanguageId(
			journalArticle.getDocument());

		String[] languageIds = LocalizationUtil.getAvailableLanguageIds(
			journalArticle.getDocument());

		for (String languageId : languageIds) {
			String content = _content;

			String title = journalArticle.getTitle(languageId);

			if (languageId.equals(articleDefaultLanguageId)) {
				map.put(Field.CONTENT, content);
				map.put(Field.DEFAULT_LANGUAGE_ID, languageId);
			}

			map.put(
				LocalizationUtil.getLocalizedName(Field.CONTENT, languageId),
				content);

			map.put(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				title);

			String ddmKey =
				"ddm__text__" +
					journalArticle.getDDMStructure().getStructureId() +
						"__content_" + languageId;

			map.put(ddmKey, content);

			map.put(
				ddmKey.concat("_String_sortable"),
				StringUtil.toLowerCase(content));
		}
	}

	private void _populateDates(
		JournalArticle journalArticle, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, journalArticle.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.DISPLAY_DATE, journalArticle.getDisplayDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, journalArticle.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, journalArticle.getDisplayDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		JournalArticle journalArticle, Map<String, String> map) {

		String title = StringUtil.lowerCase(journalArticle.getTitle());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private void _populateRoles(
			JournalArticle journalArticle, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			journalArticle.getCompanyId(), JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey(), journalArticle.getGroupId(),
			null, map);
	}

	private void _populateTreePath(
		JournalArticle journalArticle, Map<String, String> map) {

		String treePath = null;

		String[] treePaths = StringUtil.split(
			journalArticle.getTreePath(), CharPool.SLASH);

		if (treePaths != null) {
			if (treePaths.length == 1) {
				treePath = treePaths[0];
			}
			else {
				treePath = String.valueOf(Arrays.asList(treePaths));
			}
		}

		map.put(Field.TREE_PATH, treePath);
	}

	private String _content;
	private String _title;

}