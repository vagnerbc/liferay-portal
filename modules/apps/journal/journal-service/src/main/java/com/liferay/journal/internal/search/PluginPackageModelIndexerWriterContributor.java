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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelperUtil;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSenderUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;
import org.osgi.service.component.annotations.Component;

import java.util.ArrayList;
import java.util.Collection;
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
	service = ModelIndexerWriterContributor.class
)
public class PluginPackageModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<PluginPackage> {

	public static final String CLASS_NAME = PluginPackage.class.getName();

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		try {
			IndexWriterHelperUtil.deleteEntityDocuments(
				getSearchEngineId(), CompanyConstants.SYSTEM, CLASS_NAME,
				isCommitImmediately());

			Collection<Document> documents = new ArrayList<>();

			List<PluginPackage> pluginPackages =
				PluginPackageUtil.getAllAvailablePluginPackages();

			int total = pluginPackages.size();

			ReindexStatusMessageSenderUtil.sendStatusMessage(
				CLASS_NAME, 0, total);

			for (PluginPackage pluginPackage : pluginPackages) {
				Document document = modelIndexerWriterDocumentHelper.getDocument(pluginPackage);

				documents.add(document);
			}

			IndexWriterHelperUtil.updateDocuments(
				getSearchEngineId(), CompanyConstants.SYSTEM, documents,
				isCommitImmediately());

			ReindexStatusMessageSenderUtil.sendStatusMessage(
				CLASS_NAME, total, total);
		}
		catch (PortalException e) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to execute PluginPackageModelIndexerWriterContributor");
			}
		}
	}

	@Override
	public long getCompanyId(
		PluginPackage pluginPackage) {
		return CompanyConstants.SYSTEM;
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return null;
	}

	public boolean isCommitImmediately() {
		return _commitImmediately;
	}

	public String getSearchEngineId() {
		if (_searchEngineId != null) {
			return _searchEngineId;
		}

		Class<?> clazz = getClass();

		String searchEngineId = GetterUtil.getString(
			PropsUtil.get(
				PropsKeys.INDEX_SEARCH_ENGINE_ID,
				new com.liferay.portal.kernel.configuration.Filter(
					clazz.getName())));

		if (Validator.isNotNull(searchEngineId)) {
			SearchEngine searchEngine = SearchEngineHelperUtil.getSearchEngine(
				searchEngineId);

			if (searchEngine != null) {
				_searchEngineId = searchEngineId;
			}
		}

		if (_searchEngineId == null) {
			_searchEngineId = SearchEngineHelperUtil.getDefaultSearchEngineId();
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Search engine ID for ", clazz.getName(), " is ",
					searchEngineId));
		}

		return _searchEngineId;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PluginPackageModelIndexerWriterContributor.class);

	private boolean _commitImmediately;

	private String _searchEngineId;
}
