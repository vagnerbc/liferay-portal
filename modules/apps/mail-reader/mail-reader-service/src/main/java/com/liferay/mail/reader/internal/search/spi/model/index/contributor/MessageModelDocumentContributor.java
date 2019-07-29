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

package com.liferay.mail.reader.internal.search.spi.model.index.contributor;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.expando.kernel.util.ExpandoBridgeIndexerUtil;
import com.liferay.mail.reader.model.Message;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Scott Lee
 * @author Peter Fellwock
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.mail.reader.model.Message",
	service = ModelDocumentContributor.class
)
public class MessageModelDocumentContributor
	implements ModelDocumentContributor<Message> {

	@Override
	public void contribute(Document document, Message message) {
		ExpandoBridge expandoBridge = message.getExpandoBridge();

		document.addText(
			Field.CONTENT, HtmlUtil.extractText(message.getBody()));
		document.addKeyword(Field.FOLDER_ID, message.getFolderId());
		document.addText(Field.TITLE, message.getSubject());

		document.addKeyword("accountId", message.getAccountId());
		document.addKeyword("remoteMessageId", message.getRemoteMessageId());

		ExpandoBridgeIndexerUtil.addAttributes(document, expandoBridge);

		_addLocalizedFields(document, message);
	}

	private void _addLocalizedFields(Document document, Message message) {
		try {
			for (Locale locale :
					LanguageUtil.getAvailableLocales(message.getGroupId())) {

				String languageId = LocaleUtil.toLanguageId(locale);

				document.addText(
					LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
					message.getSubject());

				document.addText(
					LocalizationUtil.getLocalizedName(
						Field.CONTENT, languageId),
					HtmlUtil.extractText(message.getBody()));
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MessageModelDocumentContributor.class);

}