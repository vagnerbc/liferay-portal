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
import com.liferay.bookmarks.service.BookmarksEntryLocalService;
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
 */
@RunWith(Arquillian.class)
public class BookmarksEntryIndexerIndexedFieldsTest {

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
		setUpBookmarksEntryIndexerFixture();
		setUpBookmarksEntryFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		String name = "test";
		String url = "https://www.liferay.com";
		String description = "test";

		BookmarksEntry bookmarksEntry =
			bookmarksEntryFixture.createBookmarksEntry(name, url, description);

		String searchTerm = "test";

		Document document = bookmarksEntryIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(bookmarksEntry);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
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

	protected BookmarksEntryFixture bookmarksEntryFixture;
	protected IndexerFixture bookmarksEntryIndexerFixture;

	@Inject
	protected BookmarksEntryLocalService bookmarksEntryLocalService;

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
			BookmarksEntry bookmarksEntry)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ENTRY_CLASS_PK, String.valueOf(bookmarksEntry.getEntryId()));
		map.put(Field.ENTRY_CLASS_NAME, BookmarksEntry.class.getName());
		map.put(
			Field.COMPANY_ID, String.valueOf(bookmarksEntry.getCompanyId()));

		map.put("folderId", String.valueOf(bookmarksEntry.getFolderId()));

		map.put(Field.GROUP_ID, String.valueOf(bookmarksEntry.getGroupId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(bookmarksEntry.getGroupId()));

		map.put(
			Field.STAGING_GROUP,
			String.valueOf(bookmarksEntryFixture.getGroup().isStagingGroup()));

		map.put(Field.STATUS, String.valueOf(bookmarksEntry.getStatus()));

		map.put(Field.TITLE, bookmarksEntry.getName());

		map.put("title_sortable", bookmarksEntry.getName());

		map.put(
			Field.TREE_PATH,
			String.valueOf(
				Arrays.asList(
					StringUtil.split(
						bookmarksEntry.getTreePath(), CharPool.SLASH))));

		map.put(Field.URL, bookmarksEntry.getUrl());

		map.put(Field.DESCRIPTION, bookmarksEntry.getDescription());

		map.put(Field.USER_ID, String.valueOf(bookmarksEntry.getUserId()));

		map.put(
			Field.USER_NAME,
			StringUtil.lowerCase(bookmarksEntry.getUserName()));

		map.put("visible", "true");

		indexedFieldsFixture.populatePriority("0.0", map);

		indexedFieldsFixture.populateUID(
			BookmarksEntry.class.getName(), bookmarksEntry.getEntryId(), map);

		_populateDates(bookmarksEntry, map);
		_populateLocalizedTitles(bookmarksEntry, map);
		_populateRoles(bookmarksEntry, map);

		return map;
	}

	private void _populateDates(
		BookmarksEntry bookmarksEntry, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, bookmarksEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, bookmarksEntry.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, bookmarksEntry.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		BookmarksEntry bookmarksEntry, Map<String, String> map) {

		String title = StringUtil.lowerCase(bookmarksEntry.getName());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private void _populateRoles(
			BookmarksEntry bookmarksEntry, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			bookmarksEntry.getCompanyId(), BookmarksEntry.class.getName(),
			bookmarksEntry.getEntryId(), bookmarksEntry.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<BookmarksEntry> _bookmarksEntries;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}