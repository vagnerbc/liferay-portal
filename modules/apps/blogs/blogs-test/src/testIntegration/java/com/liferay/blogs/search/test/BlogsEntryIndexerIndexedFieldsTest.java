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

package com.liferay.blogs.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
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
public class BlogsEntryIndexerIndexedFieldsTest {

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
		setUpBlogsEntryIndexerFixture();
		setUpBlogsEntryFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		BlogsEntry blogsEntry = blogsEntryFixture.createBlogsEntry();

		String searchTerm = blogsEntry.getDescription();

		Document document = blogsEntryIndexerFixture.searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(blogsEntry);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpBlogsEntryFixture() throws Exception {
		blogsEntryFixture = new BlogsEntryFixture();

		blogsEntryFixture.setUp();

		blogsEntryFixture.setGroup(group);

		_blogsEntries = blogsEntryFixture.getBlogsEntries();
	}

	protected void setUpBlogsEntryIndexerFixture() {
		blogsEntryIndexerFixture = new IndexerFixture<>(BlogsEntry.class);
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

	protected BlogsEntryFixture blogsEntryFixture;
	protected IndexerFixture blogsEntryIndexerFixture;
	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(BlogsEntry blogsEntry)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.ENTRY_CLASS_PK, String.valueOf(blogsEntry.getEntryId()));
		map.put(Field.ENTRY_CLASS_NAME, BlogsEntry.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(blogsEntry.getCompanyId()));

		map.put(Field.GROUP_ID, String.valueOf(blogsEntry.getGroupId()));

		map.put(Field.SCOPE_GROUP_ID, String.valueOf(blogsEntry.getGroupId()));

		map.put(
			Field.STAGING_GROUP,
			String.valueOf(blogsEntryFixture.getGroup().isStagingGroup()));

		map.put(Field.CONTENT, blogsEntry.getContent());

		map.put(Field.TITLE, blogsEntry.getTitle());

		map.put("localized_title", StringUtil.lowerCase(blogsEntry.getTitle()));

		map.put(
			Field.TITLE + "_sortable",
			StringUtil.lowerCase(blogsEntry.getTitle()));

		map.put(Field.SUBTITLE, blogsEntry.getSubtitle());

		map.put(Field.DESCRIPTION, blogsEntry.getDescription());

		map.put(Field.USER_ID, String.valueOf(blogsEntry.getUserId()));

		map.put(Field.STATUS, String.valueOf(blogsEntry.getStatus()));

		map.put("visible", "true");

		map.put(
			Field.USER_NAME, StringUtil.lowerCase(blogsEntry.getUserName()));

		indexedFieldsFixture.populateUID(
			BlogsEntry.class.getName(), blogsEntry.getEntryId(), map);

		indexedFieldsFixture.populatePriority("0.0", map);

		_populateDates(blogsEntry, map);
		_populateRoles(blogsEntry, map);
		_populateTitle(blogsEntry, map);

		return map;
	}

	private void _populateDates(
		BlogsEntry blogsEntry, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, blogsEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, blogsEntry.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.DISPLAY_DATE, blogsEntry.getDisplayDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, blogsEntry.getDisplayDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(BlogsEntry blogsEntry, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			blogsEntry.getCompanyId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), blogsEntry.getGroupId(), null, map);
	}

	private void _populateTitle(
		BlogsEntry blogsEntry, Map<String, String> map) {

		for (Locale locale :
				LanguageUtil.getAvailableLocales(blogsEntry.getGroupId())) {

			String title = StringUtil.lowerCase(blogsEntry.getTitle());

			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}