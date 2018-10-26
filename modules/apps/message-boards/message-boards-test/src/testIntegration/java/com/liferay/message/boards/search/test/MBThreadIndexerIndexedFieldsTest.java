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

package com.liferay.message.boards.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class MBThreadIndexerIndexedFieldsTest {

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
		setUpMBThreadIndexerFixture();
		setUpMBThreadFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		Locale locale = Locale.US;

		MBMessage mbMessage = mbThreadFixture.createMBMessage();

		MBThread mbThread = mbMessage.getThread();

		String searchTerm = mbThread.getUserName();

		Document document = mbThreadIndexerFixture.searchOnlyOne(
			user.getUserId(), searchTerm, locale);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(
			mbThread, mbMessage);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpMBThreadFixture() {
		mbThreadFixture = new MBMessageFixture(group, user);

		_mbThreads = mbThreadFixture.getMbThreads();

		_mbCategories = mbThreadFixture.getMbCategories();

		_mbMessages = mbThreadFixture.getMbMessages();
	}

	protected void setUpMBThreadIndexerFixture() {
		mbThreadIndexerFixture = new IndexerFixture<>(MBThread.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);
	}

	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;
	protected MBMessageFixture mbThreadFixture;
	protected IndexerFixture mbThreadIndexerFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected User user;
	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			MBThread mbThread, MBMessage mbMessage)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.ENTRY_CLASS_PK, String.valueOf(mbThread.getThreadId()));
		map.put(Field.ENTRY_CLASS_NAME, MBThread.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(mbThread.getCompanyId()));

		map.put(Field.GROUP_ID, String.valueOf(mbThread.getGroupId()));

		map.put(Field.SCOPE_GROUP_ID, String.valueOf(mbThread.getGroupId()));

		map.put(Field.STAGING_GROUP, String.valueOf(group.isStagingGroup()));

		map.put(Field.STATUS, String.valueOf(mbThread.getStatus()));

		map.put(Field.USER_ID, String.valueOf(mbThread.getUserId()));

		for (int i = 0; i < mbThread.getParticipantUserIds().length; i++) {
			map.put(
				"participantUserIds",
				String.valueOf(mbThread.getParticipantUserIds()[i]));
		}

		map.put(
			"lastPostDate",
			String.valueOf(mbThread.getLastPostDate().getTime()));

		map.put("discussion", "false");

		map.put(Field.USER_NAME, StringUtil.lowerCase(mbThread.getUserName()));

		indexedFieldsFixture.populateUID(
			MBThread.class.getName(), mbThread.getThreadId(), map);

		_populateDates(mbThread, mbMessage, map);
		_populateRoles(mbThread, map);

		return map;
	}

	private void _populateDates(
		MBThread mbThread, MBMessage mbMessage, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, mbMessage.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, mbThread.getCreateDate(), map);
	}

	private void _populateRoles(MBThread mbThread, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			mbThread.getCompanyId(), MBThread.class.getName(),
			mbThread.getThreadId(), mbThread.getGroupId(), null, map);
	}

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<MBCategory> _mbCategories;

	@DeleteAfterTestRun
	private List<MBMessage> _mbMessages;

	@DeleteAfterTestRun
	private List<MBThread> _mbThreads;

	@DeleteAfterTestRun
	private List<User> _users;

}