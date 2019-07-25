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

package com.liferay.user.groups.admin.internal.search.spi.model.index.contributor;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.UserGroup",
	service = ModelDocumentContributor.class
)
public class UserGroupModelDocumentContributor
	implements ModelDocumentContributor<UserGroup> {

	@Override
	public void contribute(Document document, UserGroup userGroup) {
		document.addKeyword(Field.COMPANY_ID, userGroup.getCompanyId());
		document.addText(Field.DESCRIPTION, userGroup.getDescription());
		document.addText(Field.NAME, userGroup.getName());
		document.addKeyword(Field.USER_GROUP_ID, userGroup.getUserGroupId());

		_addLocalizedFields(document, userGroup);
	}

	private void _addLocalizedFields(Document document, UserGroup userGroup) {
		try {
			for (Locale locale :
					LanguageUtil.getAvailableLocales(userGroup.getGroupId())) {

				String languageId = LocaleUtil.toLanguageId(locale);

				document.addText(
					LocalizationUtil.getLocalizedName(
						Field.DESCRIPTION, languageId),
					userGroup.getDescription());
			}
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserGroupModelDocumentContributor.class);

}