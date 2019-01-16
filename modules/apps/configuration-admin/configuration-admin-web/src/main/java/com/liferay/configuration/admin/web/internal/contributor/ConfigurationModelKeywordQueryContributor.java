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

import com.liferay.configuration.admin.web.internal.search.FieldNames;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.configuration.admin.web.internal.model.ConfigurationModel",
	service = KeywordQueryContributor.class
)
public class ConfigurationModelKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, FieldNames.CONFIGURATION_CATEGORY,
			false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.DESCRIPTION, false);
		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.TITLE, false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext,
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_DESCRIPTION, false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext,
			FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_NAME, false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext,
			FieldNames.CONFIGURATION_MODEL_FACTORY_PID, false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, FieldNames.CONFIGURATION_MODEL_ID,
			false);

		try {
			booleanQuery.add(booleanQuery, BooleanClauseOccur.MUST);
		}
		catch (ParseException pe) {
			throw new SystemException(pe);
		}
	}

	@Reference
	protected QueryHelper queryHelper;

}