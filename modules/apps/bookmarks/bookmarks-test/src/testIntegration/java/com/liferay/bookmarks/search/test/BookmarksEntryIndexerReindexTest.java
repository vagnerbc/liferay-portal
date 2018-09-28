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

package com.liferay.bookmarks.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.bookmarks.model.BookmarksEntry;
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.bookmarks.model.BookmarksFolderConstants;
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
import com.liferay.bookmarks.util.test.BookmarksTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class BookmarksEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();
		setUpBookmarksEntryIndexerFixture();
		setUpBookmarksFolderIndexerFixture();
		setUpBookmarksEntryFixture();
		setUpFolder();
	}

	@Test
	public void testReindexing() throws Exception {
		Locale locale = LocaleUtil.US;

		String name = "test";
		String url = "https://www.liferay.com";
		String description = "test";

		String searchTerm = url;

		bookmarksEntryFixture.updateDisplaySettings(locale);

		BookmarksEntry bookmarksEntry =
			bookmarksEntryFixture.createBookmarksEntry(name, url, description);

		bookmarksEntryIndexerFixture.searchOnlyOne(searchTerm);

		Document document = bookmarksEntryIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		bookmarksEntryIndexerFixture.deleteDocument(document);

		bookmarksEntryIndexerFixture.searchNoOne(searchTerm, locale);

		bookmarksEntryIndexerFixture.reindex(bookmarksEntry.getCompanyId());

		bookmarksEntryIndexerFixture.searchOnlyOne(searchTerm);
	}

	@Test
	public void testReindexingFolders() throws Exception {
		Locale locale = LocaleUtil.US;

		String searchTerm = TestPropsValues.getUser().getFullName();

		bookmarksEntryFixture.updateDisplaySettings(locale);

		bookmarksFolderIndexerFixture.searchOnlyOne(searchTerm);

		Document document = bookmarksFolderIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		bookmarksFolderIndexerFixture.deleteDocument(document);

		bookmarksFolderIndexerFixture.searchNoOne(searchTerm, locale);

		bookmarksFolderIndexerFixture.reindex(bookmarksFolder.getCompanyId());

		bookmarksFolderIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpBookmarksEntryFixture() throws Exception {
		bookmarksEntryFixture = new BookmarksEntryFixture(
			bookmarksEntryLocalService);

		bookmarksEntryFixture.setUp();

		bookmarksEntryFixture.setGroup(group);

		_bookmarksEntries = bookmarksEntryFixture.getBookmarksEntries();
	}

	protected void setUpBookmarksEntryIndexerFixture() {
		bookmarksEntryIndexerFixture = new IndexerFixture<>(
			BookmarksEntry.class);
	}

	protected void setUpBookmarksFolderIndexerFixture() {
		bookmarksFolderIndexerFixture = new IndexerFixture<>(
			BookmarksFolder.class);
	}

	protected void setUpFolder() throws Exception {
		bookmarksFolder = BookmarksTestUtil.addFolder(
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(),
			bookmarksEntryFixture.getServiceContext());
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected BookmarksEntryFixture bookmarksEntryFixture;
	protected IndexerFixture bookmarksEntryIndexerFixture;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

	protected BookmarksFolder bookmarksFolder;
	protected IndexerFixture bookmarksFolderIndexerFixture;
	protected Group group;
	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}