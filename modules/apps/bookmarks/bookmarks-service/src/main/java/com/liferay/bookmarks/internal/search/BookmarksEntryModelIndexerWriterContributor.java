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

package com.liferay.bookmarks.internal.search;

import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolderConstants;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Luan Maoski
 * @author Lucas Marques
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.bookmarks.model.BookmarksEntry",
	service = ModelIndexerWriterContributor.class
)
public class BookmarksEntryModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<BookmarksEntry> {

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setAddCriteriaMethod(
			dynamicQuery -> {
				long folderId =
					BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID;

				Property folderIdProperty = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdProperty.eq(folderId));

				Property statusProperty = PropertyFactoryUtil.forName("status");

				Integer[] statuses = {
					WorkflowConstants.STATUS_APPROVED,
					WorkflowConstants.STATUS_IN_TRASH
				};

				dynamicQuery.add(statusProperty.in(statuses));
			});

		batchIndexingActionable.setPerformActionMethod(
			(BookmarksEntry bookmarksEntry) -> {
				Document document =
					modelIndexerWriterDocumentHelper.getDocument(
						bookmarksEntry);

				batchIndexingActionable.addDocuments(document);

				bookmarksFolderBatchReindexer.reindex(
					bookmarksEntry.getFolderId(),
					bookmarksEntry.getCompanyId());
			});
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return _dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				_bookmarksEntryLocalService.
					getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(BookmarksEntry bookmarksEntry) {
		return bookmarksEntry.getCompanyId();
	}

	@Override
	public void modelIndexed(BookmarksEntry bookmarksEntry) {
		bookmarksFolderBatchReindexer.reindex(
			bookmarksEntry.getFolderId(), bookmarksEntry.getCompanyId());
	}

	@Reference
	protected BookmarksFolderBatchReindexer bookmarksFolderBatchReindexer;

	@Reference
	private BookmarksEntryLocalService _bookmarksEntryLocalService;

	@Reference
	private DynamicQueryBatchIndexingActionableFactory
		_dynamicQueryBatchIndexingActionableFactory;

}