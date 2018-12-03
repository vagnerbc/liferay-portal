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

package com.liferay.journal.search.test;

import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vagner B.C
 */
public abstract class BaseJournalFolderIndexerTestCase {

	public void setUp() throws Exception {
		journalFolderFixture = createJournalFolderFixture();

		journalFolderFixture.setUp();

		setGroup(journalFolderFixture.addGroup());

		journalFolderIndexerFixture = createJournalFolderIndexerFixture();

		indexedFieldsFixture = createIndexedFieldsFixture();
	}

	protected IndexedFieldsFixture createIndexedFieldsFixture() {
		return new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected JournalFolderFixture createJournalFolderFixture() {
		return new JournalFolderFixture(_groups, _journalFolders);
	}

	protected JournalFolderIndexerFixture createJournalFolderIndexerFixture() {
		Indexer<JournalFolder> indexer = indexerRegistry.getIndexer(
			JournalFolder.class);

		return new JournalFolderIndexerFixture(indexer);
	}

	protected void setGroup(Group group) {
		journalFolderFixture.setGroup(group);
	}

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	protected JournalFolderFixture journalFolderFixture;
	protected JournalFolderIndexerFixture journalFolderIndexerFixture;

	@Inject
	protected JournalFolderLocalService journalFolderLocalService;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<JournalFolder> _journalFolders = new ArrayList<>(1);

}