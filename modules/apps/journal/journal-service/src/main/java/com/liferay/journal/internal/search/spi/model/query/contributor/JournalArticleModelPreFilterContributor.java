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

import com.liferay.dynamic.data.mapping.util.DDMIndexer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.search.filter.QueryFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.io.Serializable;

import java.text.Format;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Hugo Huijser
 * @author Tibor Lipusz
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelPreFilterContributor.class
)
public class JournalArticleModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter contextBooleanFilter,
		ModelSearchSettings modelSearchSettings, SearchContext searchContext) {

		Long classNameId = (Long)searchContext.getAttribute(
			Field.CLASS_NAME_ID);

		if ((classNameId != null) && (classNameId != 0)) {
			contextBooleanFilter.addRequiredTerm(
				Field.CLASS_NAME_ID, classNameId.toString());
		}

		try {
			addStatus(contextBooleanFilter, searchContext);

			addSearchClassTypeIds(contextBooleanFilter, searchContext);

			String ddmStructureFieldName = (String)searchContext.getAttribute(
				"ddmStructureFieldName");
			Serializable ddmStructureFieldValue = searchContext.getAttribute(
				"ddmStructureFieldValue");

			if (Validator.isNotNull(ddmStructureFieldName) &&
				Validator.isNotNull(ddmStructureFieldValue)) {

				QueryFilter queryFilter =
					_ddmIndexer.createFieldValueQueryFilter(
						ddmStructureFieldName, ddmStructureFieldValue,
						searchContext.getLocale());

				contextBooleanFilter.add(queryFilter, BooleanClauseOccur.MUST);
			}
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to execute " +
						"JournalArticleModelPreFilterContributor");
			}
		}

		String ddmStructureKey = (String)searchContext.getAttribute(
			"ddmStructureKey");

		if (Validator.isNotNull(ddmStructureKey)) {
			contextBooleanFilter.addRequiredTerm(
				"ddmStructureKey", ddmStructureKey);
		}

		String ddmTemplateKey = (String)searchContext.getAttribute(
			"ddmTemplateKey");

		if (Validator.isNotNull(ddmTemplateKey)) {
			contextBooleanFilter.addRequiredTerm(
				"ddmTemplateKey", ddmTemplateKey);
		}

		boolean head = GetterUtil.getBoolean(
			searchContext.getAttribute("head"), Boolean.TRUE);
		boolean latest = GetterUtil.getBoolean(
			searchContext.getAttribute("latest"));
		boolean relatedClassName = GetterUtil.getBoolean(
			searchContext.getAttribute("relatedClassName"));
		boolean showNonindexable = GetterUtil.getBoolean(
			searchContext.getAttribute("showNonindexable"));

		if (latest && !relatedClassName && !showNonindexable) {
			contextBooleanFilter.addRequiredTerm("latest", Boolean.TRUE);
		}
		else if (head && !relatedClassName && !showNonindexable) {
			contextBooleanFilter.addRequiredTerm("head", Boolean.TRUE);
		}

		if (latest && !relatedClassName && showNonindexable) {
			contextBooleanFilter.addRequiredTerm("latest", Boolean.TRUE);
		}
		else if (!relatedClassName && showNonindexable) {
			contextBooleanFilter.addRequiredTerm("headListable", Boolean.TRUE);
		}

		boolean filterExpired = GetterUtil.getBoolean(
			searchContext.getAttribute("filterExpired"));

		if (!filterExpired) {
			return;
		}

		DateRangeFilterBuilder dateRangeFilterBuilder =
			_filterBuilders.dateRangeFilterBuilder();

		dateRangeFilterBuilder.setFieldName(Field.EXPIRATION_DATE);

		String formatPattern = PropsUtil.get(
			PropsKeys.INDEX_DATE_FORMAT_PATTERN);

		dateRangeFilterBuilder.setFormat(formatPattern);

		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			formatPattern);

		dateRangeFilterBuilder.setFrom(dateFormat.format(new Date()));

		dateRangeFilterBuilder.setIncludeLower(false);
		dateRangeFilterBuilder.setIncludeUpper(false);

		contextBooleanFilter.add(dateRangeFilterBuilder.build());
	}

	protected Filter addSearchClassTypeIds(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		long[] classTypeIds = searchContext.getClassTypeIds();

		if (ArrayUtil.isEmpty(classTypeIds)) {
			return null;
		}

		TermsFilter classTypeIdsTermsFilter = new TermsFilter(
			Field.CLASS_TYPE_ID);

		classTypeIdsTermsFilter.addValues(
			ArrayUtil.toStringArray(classTypeIds));

		return contextBooleanFilter.add(
			classTypeIdsTermsFilter, BooleanClauseOccur.MUST);
	}

	protected void addStatus(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		int[] statuses = GetterUtil.getIntegerValues(
			searchContext.getAttribute(Field.STATUS), null);

		if (ArrayUtil.isEmpty(statuses)) {
			int status = GetterUtil.getInteger(
				searchContext.getAttribute(Field.STATUS),
				WorkflowConstants.STATUS_APPROVED);

			statuses = new int[] {status};
		}

		if (!ArrayUtil.contains(statuses, WorkflowConstants.STATUS_ANY)) {
			TermsFilter statusesTermsFilter = new TermsFilter(Field.STATUS);

			statusesTermsFilter.addValues(ArrayUtil.toStringArray(statuses));

			contextBooleanFilter.add(
				statusesTermsFilter, BooleanClauseOccur.MUST);
		}
		else {
			contextBooleanFilter.addTerm(
				Field.STATUS, String.valueOf(WorkflowConstants.STATUS_IN_TRASH),
				BooleanClauseOccur.MUST_NOT);
		}
	}

	@Reference(unbind = "-")
	protected void setDDMIndexer(DDMIndexer ddmIndexer) {
		_ddmIndexer = ddmIndexer;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelPreFilterContributor.class);

	private DDMIndexer _ddmIndexer;

	@Reference
	private FilterBuilders _filterBuilders;

}