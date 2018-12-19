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

package com.liferay.portal.search.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.Locale;

/**
 * @author Adam Brandizzi
 */
public class SearchContextTestUtil {

	public static SearchContext getSearchContext() throws Exception {
		return getSearchContext(TestPropsValues.getGroupId());
	}

	public static SearchContext getSearchContext(long groupId)
		throws Exception {

		return getSearchContext(
			TestPropsValues.getUserId(), new long[] {groupId}, StringPool.BLANK,
			null);
	}

	public static SearchContext getSearchContext(
			long userId, long[] groupIds, String keywords, Locale locale)
		throws PortalException {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setKeywords(keywords);
		searchContext.setGroupIds(groupIds);
		searchContext.setLocale(locale);
		searchContext.setUserId(userId);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	public static SearchContext getSearchContext(
			long userId, String keywords, Locale locale)
		throws PortalException {

		return getSearchContext(userId, null, keywords, locale);
	}

}