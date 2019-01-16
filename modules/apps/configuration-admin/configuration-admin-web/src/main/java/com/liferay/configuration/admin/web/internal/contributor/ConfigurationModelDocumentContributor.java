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

package com.liferay.configuration.admin.web.internal.contributor;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.search.FieldNames;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProvider;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.configuration.admin.web.internal.model.ConfigurationModel",
	service = ModelDocumentContributor.class
)
public class ConfigurationModelDocumentContributor
	implements ModelDocumentContributor<ConfigurationModel> {

	@Override
	public void contribute(
		Document document, ConfigurationModel configurationModel) {

		document.addUID(
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
			configurationModel.getID());
		document.addKeyword(
			FieldNames.CONFIGURATION_MODEL_FACTORY_PID,
			configurationModel.getFactoryPid());
		document.addKeyword(
			FieldNames.CONFIGURATION_MODEL_ID, configurationModel.getID());
		document.addKeyword(Field.COMPANY_ID, CompanyConstants.SYSTEM);

		document.addKeyword(Field.ENTRY_CLASS_NAME, getClassName());

		AttributeDefinition[] requiredAttributeDefinitions =
			configurationModel.getAttributeDefinitions(
				ObjectClassDefinition.ALL);

		List<String> attributeNames = new ArrayList<>(
			requiredAttributeDefinitions.length);

		List<String> attributeDescriptions = new ArrayList<>(
			requiredAttributeDefinitions.length);

		for (AttributeDefinition attributeDefinition :
				requiredAttributeDefinitions) {

			attributeNames.add(attributeDefinition.getName());
			attributeDescriptions.add(attributeDefinition.getDescription());
		}

		document.addKeyword(
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_NAME,
			attributeNames.toArray(new String[attributeNames.size()]));
		document.addText(
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_DESCRIPTION,
			attributeDescriptions.toArray(
				new String[attributeDescriptions.size()]));

		ResourceBundleLoader resourceBundleLoader =
			_resourceBundleLoaderProvider.getResourceBundleLoader(
				configurationModel.getBundleSymbolicName());

		List<TranslationHelper> translationHelpers = new ArrayList<>(3);

		ConfigurationCategory configurationCategory =
			_configurationEntryRetriever.getConfigurationCategory(
				configurationModel.getCategory());

		if (configurationCategory != null) {
			translationHelpers.add(
				new TranslationHelper(
					"category." + configurationModel.getCategory(),
					FieldNames.CONFIGURATION_CATEGORY));
		}

		translationHelpers.add(
			new TranslationHelper(
				configurationModel.getDescription(), Field.DESCRIPTION));
		translationHelpers.add(
			new TranslationHelper(configurationModel.getName(), Field.TITLE));

		_addLocalizedText(document, resourceBundleLoader, translationHelpers);
	}

	public String getClassName() {
		return ConfigurationModel.class.getName();
	}

	private void _addLocalizedText(
		Document document, ResourceBundleLoader resourceBundleLoader,
		List<TranslationHelper> translationHelpers) {

		ResourceBundle defaultResourceBundle =
			resourceBundleLoader.loadResourceBundle(LocaleUtil.getDefault());

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(locale);

			for (TranslationHelper translationHelper : translationHelpers) {
				if (resourceBundle != null) {
					translationHelper.accept(resourceBundle, locale);
				}
				else if (defaultResourceBundle != null) {
					translationHelper.accept(defaultResourceBundle, locale);
				}
			}
		}

		for (TranslationHelper translationHelper : translationHelpers) {
			document.addLocalizedText(
				translationHelper._name, translationHelper._values);
		}
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference
	private ResourceBundleLoaderProvider _resourceBundleLoaderProvider;

	private static class TranslationHelper {

		public void accept(ResourceBundle resourceBundle, Locale locale) {
			String value = ResourceBundleUtil.getString(resourceBundle, _key);

			if (Validator.isNotNull(value)) {
				_values.put(locale, value);
			}
		}

		private TranslationHelper(String key, String name) {
			_key = GetterUtil.getString(key);
			_name = name;
		}

		private final String _key;
		private final String _name;
		private final Map<Locale, String> _values = new HashMap<>();

	}

}