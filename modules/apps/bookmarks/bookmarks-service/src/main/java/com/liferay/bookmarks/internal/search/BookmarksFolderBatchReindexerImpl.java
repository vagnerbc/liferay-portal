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

import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Luan Maoski
 * @author Lucas Marques
 * @author Raymond Augé
 */
@Component(immediate = true, service = BookmarksFolderBatchReindexer.class)
public class BookmarksFolderBatchReindexerImpl
	implements BookmarksFolderBatchReindexer {

	@Override
	public void reindex(long folderId, long companyId) {
		BatchIndexingActionable batchIndexingActionable =
			indexerWriter.getBatchIndexingActionable();

		batchIndexingActionable.setAddCriteriaMethod(
			dynamicQuery -> {
				Property folderIdPropery = PropertyFactoryUtil.forName(
					"folderId");

				dynamicQuery.add(folderIdPropery.eq(folderId));
			});

		batchIndexingActionable.setCompanyId(companyId);

		batchIndexingActionable.setPerformActionMethod(
			(BookmarksFolder bookmarksFolder) -> {
				Document document = indexerDocumentBuilder.getDocument(
					bookmarksFolder);

				batchIndexingActionable.addDocuments(document);
			});

		batchIndexingActionable.performActions();
	}

	@Reference(
		target = "(indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	protected IndexerDocumentBuilder indexerDocumentBuilder;

	@Reference(
		target = "(indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder)"
	)
	protected IndexerWriter<BookmarksFolder> indexerWriter;

}