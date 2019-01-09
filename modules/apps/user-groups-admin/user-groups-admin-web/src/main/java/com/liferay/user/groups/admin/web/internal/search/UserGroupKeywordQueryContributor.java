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

package com.liferay.user.groups.admin.web.internal.search;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ExpandoQueryContributor;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.io.Serializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Hugo Huijser
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.UserGroup",
	service = KeywordQueryContributor.class
)
public class UserGroupKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		try {
			addSearchTerm(booleanQuery, searchContext, "description", false);
			addSearchTerm(booleanQuery, searchContext, "name", false);

			LinkedHashMap<String, Object> params =
				(LinkedHashMap<String, Object>)
					searchContext.getAttribute("params");

			if (params != null) {
				String expandoAttributes = (String)params.get(
					"expandoAttributes");

				if (Validator.isNotNull(expandoAttributes)) {
					addSearchExpando(
						booleanQuery, searchContext, expandoAttributes);
				}
			}
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected Map<String, Query> addSearchExpando(
		BooleanQuery searchQuery, SearchContext searchContext,
		String keywords) {

		_expandoQueryContributor.contribute(
			keywords, searchQuery, new String[] {UserGroup.class.getName()},
			searchContext);

		return new HashMap<>();
	}

	protected Query addSearchTerm(
			BooleanQuery searchQuery, SearchContext searchContext, String field,
			boolean like)
		throws Exception {

		if (Validator.isNull(field)) {
			return null;
		}

		String value = null;

		Serializable serializable = searchContext.getAttribute(field);

		if (serializable != null) {
			Class<?> clazz = serializable.getClass();

			if (clazz.isArray()) {
				value = StringUtil.merge((Object[])serializable);
			}
			else {
				value = GetterUtil.getString(serializable);
			}
		}
		else {
			value = GetterUtil.getString(serializable);
		}

		if (Validator.isNotNull(value) &&
			(searchContext.getFacet(field) != null)) {

			return null;
		}

		if (Validator.isNull(value)) {
			value = searchContext.getKeywords();
		}

		if (Validator.isNull(value)) {
			return null;
		}

		Query query = null;

		if (searchContext.isAndSearch()) {
			query = searchQuery.addRequiredTerm(field, value, like);
		}
		else {
			query = searchQuery.addTerm(field, value, like);
		}

		return query;
	}

	@Reference
	protected QueryHelper queryHelper;

	@Reference
	private ExpandoQueryContributor _expandoQueryContributor;

}