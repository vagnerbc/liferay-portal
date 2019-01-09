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

package com.liferay.user.groups.admin.web.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
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
public class UserGroupIndexerIndexedFieldsTest {

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
		setUpUserGroupIndexerFixture();
		setUpUserGroupFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		UserGroup userGroup = userGroupFixture.createUserGroup();

		String searchTerm = userGroup.getName();

		Document document = userGroupIndexerFixture.searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(userGroup);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpUserGroupFixture() {
		userGroupFixture = new UserGroupFixture(group);

		_userGroups = userGroupFixture.getUserGroups();
	}

	protected void setUpUserGroupIndexerFixture() {
		userGroupIndexerFixture = new IndexerFixture<>(UserGroup.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserGroupFixture userGroupFixture;
	protected IndexerFixture<UserGroup> userGroupIndexerFixture;
	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(UserGroup userGroup)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ENTRY_CLASS_PK, String.valueOf(userGroup.getUserGroupId()));
		map.put(Field.ENTRY_CLASS_NAME, UserGroup.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(userGroup.getCompanyId()));

		map.put(
			Field.USER_GROUP_ID, String.valueOf(userGroup.getUserGroupId()));

		map.put(Field.NAME, userGroup.getName());

		map.put("name_sortable", StringUtil.lowerCase(userGroup.getName()));

		map.put(Field.DESCRIPTION, userGroup.getDescription());

		map.put(Field.USER_ID, String.valueOf(userGroup.getUserId()));

		map.put(Field.USER_NAME, StringUtil.lowerCase(userGroup.getUserName()));

		indexedFieldsFixture.populateUID(
			UserGroup.class.getName(), userGroup.getUserGroupId(), map);

		_populateDates(userGroup, map);

		return map;
	}

	private void _populateDates(UserGroup userGroup, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, userGroup.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, userGroup.getCreateDate(), map);
	}

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<UserGroup> _userGroups;

	@DeleteAfterTestRun
	private List<User> _users;

}