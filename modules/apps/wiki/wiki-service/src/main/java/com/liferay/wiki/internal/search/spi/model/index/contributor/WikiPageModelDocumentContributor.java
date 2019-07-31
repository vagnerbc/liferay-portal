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

package com.liferay.wiki.internal.search.spi.model.index.contributor;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.WikiFormatException;
import com.liferay.wiki.model.WikiPage;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiPage",
	service = ModelDocumentContributor.class
)
public class WikiPageModelDocumentContributor
	implements ModelDocumentContributor<WikiPage> {

	@Override
	public void contribute(Document document, WikiPage wikiPage) {
		String content = null;

		try {
			content = HtmlUtil.extractText(
				_wikiEngineRenderer.convert(wikiPage, null, null, null));

			document.addText(Field.CONTENT, content);
		}
		catch (WikiFormatException wfe) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get wiki engine for " + wikiPage.getFormat());
			}
		}
		catch (PageContentException pce) {
			if (_log.isDebugEnabled()) {
				_log.debug(pce, pce);
			}
		}

		document.addKeyword(Field.NODE_ID, wikiPage.getNodeId());

		String title = wikiPage.getTitle();

		if (wikiPage.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);

		for (Locale locale :
				LanguageUtil.getAvailableLocales(wikiPage.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			document.addText(
				LocalizationUtil.getLocalizedName(Field.CONTENT, languageId),
				content);
			document.addText(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				title);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageModelDocumentContributor.class);

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

}