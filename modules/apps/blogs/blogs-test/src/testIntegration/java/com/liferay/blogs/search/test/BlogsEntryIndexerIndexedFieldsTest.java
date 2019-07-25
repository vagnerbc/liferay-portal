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
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
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
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_setUpUserSearchFixture();

		_setUpBlogsEntryFixture();

		_setUpBlogsEntryIndexerFixture();

		_setUpIndexedFieldsFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		String searchTerm = "新規";

		String title = "新規作成";

		blogsEntryFixture.updateDisplaySettings(locale);

		BlogsEntry blogsEntry = blogsEntryFixture.createBlogsEntry(title);

		Document document = blogsEntryIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		indexedFieldsFixture.postProcessDocument(document);

		FieldValuesAssert.assertFieldValues(
			_expectedFieldValues(blogsEntry), document, searchTerm);
	}

	protected BlogsEntryFixture blogsEntryFixture;
	protected IndexerFixture<BlogsEntry> blogsEntryIndexerFixture;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(BlogsEntry blogsEntry)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(blogsEntry.getCompanyId()));
		map.put(Field.ENTRY_CLASS_PK, String.valueOf(blogsEntry.getEntryId()));
		map.put(Field.ENTRY_CLASS_NAME, BlogsEntry.class.getName());
		map.put(Field.CONTENT, blogsEntry.getContent());
		map.put(Field.DESCRIPTION, blogsEntry.getDescription());
		map.put(Field.GROUP_ID, String.valueOf(blogsEntry.getGroupId()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(blogsEntry.getGroupId()));
		map.put(Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup()));
		map.put(Field.STATUS, String.valueOf(blogsEntry.getStatus()));
		map.put(Field.SUBTITLE, blogsEntry.getSubtitle());
		map.put(Field.TITLE, blogsEntry.getTitle());
		map.put(Field.USER_ID, String.valueOf(blogsEntry.getUserId()));
		map.put(
			Field.USER_NAME, StringUtil.lowerCase(blogsEntry.getUserName()));
		map.put("visible", "true");

		indexedFieldsFixture.populateUID(
			BlogsEntry.class.getName(), blogsEntry.getEntryId(), map);
		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateViewCount(
			BlogsEntry.class, blogsEntry.getEntryId(), map);

		_populateDates(blogsEntry, map);
		_populateLocalizedValues(blogsEntry, map);
		_populateRoles(blogsEntry, map);

		return map;
	}

	private void _populateDates(
		BlogsEntry blogsEntry, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, blogsEntry.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.DISPLAY_DATE, blogsEntry.getDisplayDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, blogsEntry.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, blogsEntry.getDisplayDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedValues(
		BlogsEntry blogsEntry, Map<String, String> map) {

		String title = StringUtil.lowerCase(blogsEntry.getTitle());

		map.put(_LOCALIZED + Field.TITLE, title);
		map.put(Field.getSortableFieldName(Field.TITLE), title);

		for (Locale locale :
				LanguageUtil.getAvailableLocales(blogsEntry.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			title = StringUtil.lowerCase(blogsEntry.getTitle());

			String localizedTitle = LocalizationUtil.getLocalizedName(
				Field.TITLE, languageId);

			map.put(localizedTitle, title);

			map.put(_LOCALIZED + localizedTitle, title);

			map.put(
				Field.getSortableFieldName(_LOCALIZED + localizedTitle), title);

			map.put(
				LocalizationUtil.getLocalizedName(Field.CONTENT, languageId),
				HtmlUtil.extractText(blogsEntry.getContent()));

			map.put(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				blogsEntry.getDescription());
		}
	}

	private void _populateRoles(BlogsEntry blogsEntry, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			blogsEntry.getCompanyId(), BlogsEntry.class.getName(),
			blogsEntry.getEntryId(), blogsEntry.getGroupId(), null, map);
	}

	private void _setUpBlogsEntryFixture() {
		blogsEntryFixture = new BlogsEntryFixture(_group);

		_blogsEntries = blogsEntryFixture.getBlogsEntries();
	}

	private void _setUpBlogsEntryIndexerFixture() {
		blogsEntryIndexerFixture = new IndexerFixture<>(BlogsEntry.class);
	}

	private void _setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	private void _setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();
	}

	private static final String _LOCALIZED = "localized_";

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}