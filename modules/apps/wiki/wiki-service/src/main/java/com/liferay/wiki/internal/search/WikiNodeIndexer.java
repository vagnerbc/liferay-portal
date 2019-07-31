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

package com.liferay.wiki.internal.search;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * @author Eudaldo Alonso
 * @deprecated As of Judson (7.1.x)
 */
@Deprecated
public class WikiNodeIndexer extends BaseIndexer<WikiNode> {

	public static final String CLASS_NAME = WikiNode.class.getName();

	public WikiNodeIndexer() {
		setDefaultSelectedFieldNames(
			Field.COMPANY_ID, Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
			Field.UID);
		setFilterSearch(false);
		setPermissionAware(false);
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return _wikiNodeModelResourcePermission.contains(
			permissionChecker, _wikiNodeLocalService.getNode(entryClassPK),
			ActionKeys.VIEW);
	}

	@Override
	protected void doDelete(WikiNode wikiNode) throws Exception {
		deleteDocument(wikiNode.getCompanyId(), wikiNode.getNodeId());
	}

	@Override
	protected Document doGetDocument(WikiNode wikiNode) throws Exception {
		Document document = getBaseModelDocument(CLASS_NAME, wikiNode);

		document.addText(Field.DESCRIPTION, wikiNode.getDescription());

		String title = wikiNode.getName();

		if (wikiNode.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);

		return document;
	}

	@Override
	protected Summary doGetSummary(
			Document document, Locale locale, String snippet,
			PortletRequest portletRequest, PortletResponse portletResponse)
		throws Exception {

		return null;
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		doReindex(_wikiNodeLocalService.getNode(classPK));
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexEntries(companyId);
	}

	@Override
	protected void doReindex(WikiNode wikiNode) throws Exception {
		Document document = getDocument(wikiNode);

		if (wikiNode.isInTrash()) {
			_indexWriterHelper.deleteDocument(
				getSearchEngineId(), wikiNode.getCompanyId(),
				document.get(Field.UID), isCommitImmediately());

			return;
		}

		_indexWriterHelper.updateDocument(
			getSearchEngineId(), wikiNode.getCompanyId(), document,
			isCommitImmediately());
	}

	protected void reindexEntries(long companyId) throws PortalException {
		final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			_wikiNodeLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					property.eq(WorkflowConstants.STATUS_APPROVED));
			});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(WikiNode node) -> {
				try {
					indexableActionableDynamicQuery.addDocuments(
						getDocument(node));
				}
				catch (PortalException pe) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to index wiki node " + node.getNodeId(),
							pe);
					}
				}
			});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	protected void setWikiNodeLocalService(
		WikiNodeLocalService wikiNodeLocalService) {

		_wikiNodeLocalService = wikiNodeLocalService;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiNodeIndexer.class);

	private IndexWriterHelper _indexWriterHelper;
	private TrashHelper _trashHelper;
	private WikiNodeLocalService _wikiNodeLocalService;
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

}