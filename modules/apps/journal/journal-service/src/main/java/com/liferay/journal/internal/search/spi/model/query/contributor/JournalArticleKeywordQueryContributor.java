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

package com.liferay.journal.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.localization.SearchLocalizationHelper;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = KeywordQueryContributor.class
)
public class JournalArticleKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		SearchContext searchContext =
			keywordQueryContributorHelper.getSearchContext();

		try {
			queryHelper.addSearchTerm(
				booleanQuery, searchContext, Field.ARTICLE_ID, false);
			queryHelper.addSearchTerm(
				booleanQuery, searchContext, Field.CLASS_PK, false);
			addSearchLocalizedTerm(
				booleanQuery, searchContext, Field.CONTENT, false);
			addSearchLocalizedTerm(
				booleanQuery, searchContext, Field.DESCRIPTION, false);
			queryHelper.addSearchTerm(
				booleanQuery, searchContext, Field.ENTRY_CLASS_PK, false);
			addSearchLocalizedTerm(
				booleanQuery, searchContext, Field.TITLE, false);
			queryHelper.addSearchTerm(
				booleanQuery, searchContext, Field.USER_NAME, false);

			QueryConfig queryConfig = searchContext.getQueryConfig();

			String[] localizedFieldNames =
				_searchLocalizationHelper.getLocalizedFieldNames(
					new String[] {
						Field.CONTENT, Field.DESCRIPTION, Field.TITLE
					},
					searchContext);

			queryConfig.addHighlightFieldNames(localizedFieldNames);
		}
		catch (Exception ex) {
			throw new SystemException(
				"Unable to execute JournalArticleKeywordQueryContributor", ex);
		}
	}

	protected Map<String, Query> addLocalizedFields(
			BooleanQuery searchQuery, String field, String value, boolean like,
			SearchContext searchContext)
		throws ParseException {

		String[] localizedFieldNames =
			_searchLocalizationHelper.getLocalizedFieldNames(
				new String[] {field}, searchContext);

		Map<String, Query> queries = new HashMap<>();

		for (String localizedFieldName : localizedFieldNames) {
			Query query = searchQuery.addTerm(localizedFieldName, value, like);

			queries.put(field, query);
		}

		return queries;
	}

	protected void addLocalizedQuery(
			BooleanQuery searchQuery, BooleanQuery localizedQuery,
			SearchContext searchContext)
		throws ParseException {

		BooleanClauseOccur booleanClauseOccur = BooleanClauseOccur.SHOULD;

		if (searchContext.isAndSearch()) {
			booleanClauseOccur = BooleanClauseOccur.MUST;
		}

		searchQuery.add(localizedQuery, booleanClauseOccur);
	}

	protected Map<String, Query> addSearchLocalizedTerm(
			BooleanQuery searchQuery, SearchContext searchContext, String field,
			boolean like)
		throws Exception {

		if (Validator.isBlank(field)) {
			return Collections.emptyMap();
		}

		String value = GetterUtil.getString(searchContext.getAttribute(field));

		if (Validator.isBlank(value)) {
			value = searchContext.getKeywords();
		}

		if (Validator.isBlank(value)) {
			return Collections.emptyMap();
		}

		Map<String, Query> queries = null;

		if (Validator.isBlank(searchContext.getKeywords())) {
			BooleanQuery localizedQuery = new BooleanQueryImpl();

			queries = addLocalizedFields(
				localizedQuery, field, value, like, searchContext);

			addLocalizedQuery(searchQuery, localizedQuery, searchContext);
		}
		else {
			queries = addLocalizedFields(
				searchQuery, field, value, like, searchContext);
		}

		return queries;
	}

	@Reference
	protected QueryHelper queryHelper;

	@Reference
	private SearchLocalizationHelper _searchLocalizationHelper;

}