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
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class BookmarksFolderIndexerIndexedFieldsTest {

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
		setUpIndexedFieldsFixture();
		setUpBookmarksFolderIndexerFixture();
		setUpBookmarksFolderFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		BookmarksFolder bookmarksFolder =
			bookmarksFolderFixture.createBookmarksFolder();

		String searchTerm = bookmarksFolder.getName();

		Document document = bookmarksFolderIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(bookmarksFolder);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
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

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
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
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			BookmarksFolder bookmarksFolder)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(bookmarksFolder.getFolderId()));
		map.put(Field.ENTRY_CLASS_NAME, BookmarksFolder.class.getName());
		map.put(
			Field.COMPANY_ID, String.valueOf(bookmarksFolder.getCompanyId()));

		map.put(
			Field.FOLDER_ID,
			String.valueOf(bookmarksFolder.getParentFolderId()));

		map.put(Field.GROUP_ID, String.valueOf(bookmarksFolder.getGroupId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksFolder.getGroupId()));

		map.put(
			Field.STAGING_GROUP,
			String.valueOf(bookmarksFolderFixture.getGroup().isStagingGroup()));

		map.put(Field.STATUS, String.valueOf(bookmarksFolder.getStatus()));

		map.put(Field.TITLE, bookmarksFolder.getName());

		map.put(
			"title_sortable", StringUtil.lowerCase(bookmarksFolder.getName()));

		map.put(
			Field.TREE_PATH,
			String.valueOf(
				Arrays.asList(
					StringUtil.split(
						bookmarksFolder.getTreePath(), CharPool.SLASH))));

		map.put(Field.DESCRIPTION, bookmarksFolder.getDescription());

		map.put(Field.USER_ID, String.valueOf(bookmarksFolder.getUserId()));

		map.put(
			Field.USER_NAME,
			StringUtil.lowerCase(bookmarksFolder.getUserName()));

		map.put("visible", "true");

		indexedFieldsFixture.populatePriority("0.0", map);

		indexedFieldsFixture.populateUID(
			BookmarksFolder.class.getName(), bookmarksFolder.getFolderId(),
			map);

		_populateDates(bookmarksFolder, map);
		_populateLocalizedTitles(bookmarksFolder, map);
		_populateRoles(bookmarksFolder, map);

		return map;
	}

	private void _populateDates(
		BookmarksFolder bookmarksFolder, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksFolder.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksFolder.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksFolder.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		BookmarksFolder bookmarksFolder, Map<String, String> map) {

		String title = StringUtil.lowerCase(bookmarksFolder.getName());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private void _populateRoles(
			BookmarksFolder bookmarksFolder, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			bookmarksFolder.getCompanyId(), BookmarksFolder.class.getName(),
			bookmarksFolder.getFolderId(), bookmarksFolder.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<BookmarksFolder> _bookmarksFolders;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}