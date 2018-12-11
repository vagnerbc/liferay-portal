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
package com.liferay.journal.internal.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.plugin.License;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.plugin.ModuleId;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import org.osgi.service.component.annotations.Component;

import java.util.List;

/**
 * @author Jorge Ferrer
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.plugin.PluginPackage",
	service = ModelDocumentContributor.class
)
public class PluginPackageModelDocumentContributor
	implements ModelDocumentContributor<PluginPackage> {

	public static final String CLASS_NAME = PluginPackage.class.getName();

	@Override
	public void contribute(
		Document document, PluginPackage pluginPackage) {

		document.addUID(CLASS_NAME, pluginPackage.getModuleId());

		document.addKeyword(Field.COMPANY_ID, CompanyConstants.SYSTEM);

		StringBundler sb = new StringBundler(7);

		sb.append(pluginPackage.getAuthor());
		sb.append(StringPool.SPACE);

		String longDescription = HtmlUtil.extractText(
			pluginPackage.getLongDescription());

		sb.append(longDescription);

		sb.append(StringPool.SPACE);
		sb.append(pluginPackage.getName());
		sb.append(StringPool.SPACE);

		String shortDescription = HtmlUtil.extractText(
			pluginPackage.getShortDescription());

		sb.append(shortDescription);

		document.addText(Field.CONTENT, sb.toString());

		document.addKeyword(
			Field.ENTRY_CLASS_NAME, PluginPackage.class.getName());

		ModuleId moduleIdObj = ModuleId.getInstance(
			pluginPackage.getModuleId());

		document.addKeyword(Field.GROUP_ID, moduleIdObj.getGroupId());

		document.addDate(Field.MODIFIED_DATE, pluginPackage.getModifiedDate());

		String[] statusAndInstalledVersion =
			PluginPackageUtil.getStatusAndInstalledVersion(pluginPackage);

		document.addKeyword(Field.STATUS, statusAndInstalledVersion[0]);

		document.addText(Field.TITLE, pluginPackage.getName());

		document.addKeyword("artifactId", moduleIdObj.getArtifactId());
		document.addText("author", pluginPackage.getAuthor());
		document.addText("changeLog", pluginPackage.getChangeLog());
		document.addKeyword("installedVersion", statusAndInstalledVersion[1]);

		List<License> licenses = pluginPackage.getLicenses();

		document.addKeyword(
			"license", ListUtil.toArray(licenses, License.NAME_ACCESSOR));

		document.addText("longDescription", longDescription);
		document.addKeyword("moduleId", pluginPackage.getModuleId());

		boolean osiLicense = false;

		for (License license : licenses) {
			if (license.isOsiApproved()) {
				osiLicense = true;

				break;
			}
		}

		document.addKeyword("osi-approved-license", osiLicense);
		document.addText("pageURL", pluginPackage.getPageURL());
		document.addKeyword("repositoryURL", pluginPackage.getRepositoryURL());
		document.addText("shortDescription", shortDescription);

		List<String> tags = pluginPackage.getTags();

		document.addKeyword("tag", tags.toArray(new String[tags.size()]));

		List<String> types = pluginPackage.getTypes();

		document.addKeyword("type", types.toArray(new String[types.size()]));

		document.addKeyword("version", pluginPackage.getVersion());
	}
}
