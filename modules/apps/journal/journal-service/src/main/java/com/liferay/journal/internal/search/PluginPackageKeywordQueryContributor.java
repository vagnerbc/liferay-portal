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

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.plugin.PluginPackageImpl;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import org.osgi.service.component.annotations.Component;

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
	service = KeywordQueryContributor.class
)
public class PluginPackageKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();
		
		BooleanFilter booleanFilter = booleanQuery.getPreBooleanFilter();

		if (booleanFilter == null) {
			booleanFilter = new BooleanFilter();
		}

		String type = (String)searchContext.getAttribute("type");

		if (Validator.isNotNull(type)) {
			booleanFilter.addRequiredTerm("type", type);
		}

		String tag = (String)searchContext.getAttribute("tag");

		if (Validator.isNotNull(tag)) {
			booleanFilter.addRequiredTerm("tag", tag);
		}

		String repositoryURL = (String)searchContext.getAttribute(
			"repositoryURL");

		if (Validator.isNotNull(repositoryURL)) {
			booleanFilter.addRequiredTerm("repositoryURL", repositoryURL);
		}

		String license = (String)searchContext.getAttribute("license");

		if (Validator.isNotNull(license)) {
			booleanFilter.addRequiredTerm("license", license);
		}

		String status = (String)searchContext.getAttribute(Field.STATUS);

		if (Validator.isNull(status) || status.equals("all")) {
			return;
		}

		if (status.equals(
			PluginPackageImpl.
				STATUS_NOT_INSTALLED_OR_OLDER_VERSION_INSTALLED)) {

			BooleanFilter statusBooleanFilter = new BooleanFilter();

			statusBooleanFilter.addTerm(
				Field.STATUS, PluginPackageImpl.STATUS_NOT_INSTALLED);
			statusBooleanFilter.addTerm(
				Field.STATUS, PluginPackageImpl.STATUS_OLDER_VERSION_INSTALLED);

			booleanFilter.add(statusBooleanFilter, BooleanClauseOccur.MUST);
		}
		else {
			booleanFilter.addRequiredTerm(Field.STATUS, status);
		}

		if (booleanFilter.hasClauses()) {
			booleanQuery.setPreBooleanFilter(booleanFilter);
		}
	}

}
