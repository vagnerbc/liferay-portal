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

package com.liferay.configuration.admin.web.internal.search;

import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.Field;

import org.osgi.service.component.annotations.Component;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "model.class.name=com.liferay.configuration.admin.web.internal.model.ConfigurationModel",
	service = BaseSearcher.class
)
public class ConfigurationModelSearcher extends BaseSearcher {

	public static final String CLASS_NAME = ConfigurationModel.class.getName();

	public ConfigurationModelSearcher() {
		setCommitImmediately(false);
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.DESCRIPTION, Field.ENTRY_CLASS_NAME,
			Field.TITLE, Field.UID,
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_DESCRIPTION,
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_NAME,
			FieldNames.CONFIGURATION_MODEL_FACTORY_PID,
			FieldNames.CONFIGURATION_MODEL_ID);
		setFilterSearch(false);
		setPermissionAware(false);
		setSelectAllLocales(false);
		setStagingAware(false);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

}