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

package com.liferay.wiki.internal.search.spi.model.index.contributor;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiPage",
	service = ModelIndexerWriterContributor.class
)
public class WikiPageModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<WikiPage> {

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setAddCriteriaMethod(
			dynamicQuery -> {
				Property headProperty = PropertyFactoryUtil.forName("head");

				dynamicQuery.add(headProperty.eq(true));
			});

		batchIndexingActionable.setPerformActionMethod(
			(WikiPage wikiPage) -> {
				if (!wikiPage.isHead() ||
					(!wikiPage.isApproved() && !wikiPage.isInTrash())) {

					return;
				}

				if (Validator.isNotNull(wikiPage.getRedirectTitle())) {
					return;
				}

				batchIndexingActionable.addDocuments(
					modelIndexerWriterDocumentHelper.getDocument(wikiPage));
			});
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_wikiPageLocalService.getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(WikiPage wikiPage) {
		return wikiPage.getCompanyId();
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(WikiPage wikiPage) {
		if (!wikiPage.isHead() ||
			(!wikiPage.isApproved() && !wikiPage.isInTrash())) {

			return IndexerWriterMode.SKIP;
		}

		if (Validator.isNotNull(wikiPage.getRedirectTitle())) {
			return IndexerWriterMode.SKIP;
		}

		return IndexerWriterMode.UPDATE;
	}

	@Override
	public void modelIndexed(WikiPage wikiPage) {
		Indexer<DLFileEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			DLFileEntry.class);

		try {
			for (FileEntry attachmentsFileEntry :
					wikiPage.getAttachmentsFileEntries()) {

				indexer.reindex((DLFileEntry)attachmentsFileEntry.getModel());
			}
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@Reference
	private DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

	@Reference
	private WikiPageLocalService _wikiPageLocalService;

}