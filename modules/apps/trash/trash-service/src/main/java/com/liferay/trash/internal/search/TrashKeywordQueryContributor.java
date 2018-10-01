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

package com.liferay.trash.internal.search;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.trash.model.TrashEntry",
	service = KeywordQueryContributor.class
)
public class TrashKeywordQueryContributor implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		if (searchContext.getAttributes() == null) {
			return;
		}

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.CONTENT, true);

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.DESCRIPTION, true);

		queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.REMOVED_BY_USER_NAME, true);

		queryHelper.addSearchLocalizedTerm(
			booleanQuery, searchContext, Field.TITLE, true);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.TYPE, false);
		queryHelper.addSearchTerm(
			booleanQuery, searchContext, Field.USER_NAME, true);
	}

	@Reference
	protected QueryHelper queryHelper;

}