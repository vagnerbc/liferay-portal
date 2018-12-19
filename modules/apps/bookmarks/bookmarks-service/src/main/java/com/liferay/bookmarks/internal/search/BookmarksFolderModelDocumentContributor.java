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
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Bruno Farache
 * @author Luan Maoski
 * @author Lucas Marques
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.bookmarks.model.BookmarksFolder",
	service = ModelDocumentContributor.class
)
public class BookmarksFolderModelDocumentContributor
	implements ModelDocumentContributor<BookmarksFolder> {

	@Override
	public void contribute(Document document, BookmarksFolder bookmarksFolder) {
		document.addText(Field.DESCRIPTION, bookmarksFolder.getDescription());
		document.addKeyword(
			Field.FOLDER_ID, bookmarksFolder.getParentFolderId());
		document.addText(Field.TITLE, bookmarksFolder.getName());
		document.addKeyword(
			Field.TREE_PATH,
			StringUtil.split(bookmarksFolder.getTreePath(), CharPool.SLASH));
	}

}