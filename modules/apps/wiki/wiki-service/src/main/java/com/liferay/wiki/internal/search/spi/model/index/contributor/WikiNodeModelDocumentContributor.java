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
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.model.WikiNode;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiNode",
	service = ModelDocumentContributor.class
)
public class WikiNodeModelDocumentContributor
	implements ModelDocumentContributor<WikiNode> {

	@Override
	public void contribute(Document document, WikiNode wikiNode) {
		document.addText(Field.DESCRIPTION, wikiNode.getDescription());

		document.addText(Field.TITLE, getTitle(wikiNode));

		populateLocalizedFields(document, wikiNode);
	}

	protected String getTitle(WikiNode wikiNode) {
		String title = wikiNode.getName();

		if (wikiNode.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		return title;
	}

	protected void populateLocalizedFields(
		Document document, WikiNode wikiNode) {

		for (Locale locale :
				LanguageUtil.getAvailableLocales(wikiNode.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			document.addText(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				wikiNode.getDescription());

			document.addText(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				getTitle(wikiNode));
		}
	}

	@Reference
	private TrashHelper _trashHelper;

}