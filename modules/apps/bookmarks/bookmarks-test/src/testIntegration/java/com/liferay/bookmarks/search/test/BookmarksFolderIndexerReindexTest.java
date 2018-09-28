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
import com.liferay.bookmarks.model.BookmarksFolder;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.IndexerFixture;
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
 * @author Luca Marques
 */
@RunWith(Arquillian.class)
public class BookmarksFolderIndexerReindexTest {

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
		setUpBookmarksFolderIndexerFixture();
		setUpBookmarksFolderFixture();
	}

	@Test
	public void testReindexingFolders() throws Exception {
		Locale locale = LocaleUtil.US;

		BookmarksFolder bookmarksFolder =
			bookmarksFolderFixture.createBookmarksFolder();

		String searchTerm = bookmarksFolder.getName();

		bookmarksFolderFixture.updateDisplaySettings(locale);

		bookmarksFolderIndexerFixture.searchOnlyOne(searchTerm);

		Document document = bookmarksFolderIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		bookmarksFolderIndexerFixture.deleteDocument(document);

		bookmarksFolderIndexerFixture.searchNoOne(searchTerm, locale);

		bookmarksFolderIndexerFixture.reindex(bookmarksFolder.getCompanyId());

		bookmarksFolderIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpBookmarksFolderFixture() throws Exception {
		bookmarksFolderFixture = new BookmarksFolderFixture();

		bookmarksFolderFixture.setUp();

		bookmarksFolderFixture.setGroup(group);

		_bookmarksFolders = bookmarksFolderFixture.getBookmarksFolders();
	}

	protected void setUpBookmarksFolderIndexerFixture() {
		bookmarksFolderIndexerFixture = new IndexerFixture<>(
			BookmarksFolder.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected BookmarksFolderFixture bookmarksFolderFixture;
	protected IndexerFixture bookmarksFolderIndexerFixture;
	protected Group group;
	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}