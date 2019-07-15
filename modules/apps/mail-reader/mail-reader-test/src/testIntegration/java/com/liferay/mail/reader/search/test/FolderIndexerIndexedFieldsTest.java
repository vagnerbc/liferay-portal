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

package com.liferay.mail.reader.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.mail.reader.model.Account;
import com.liferay.mail.reader.model.Folder;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class FolderIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();

		setUpMailReaderFixture();

		setUpFolderIndexerFixture();

		setUpIndexedFieldsFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		Folder folder = mailReaderFixture.createFolder();

		String searchTerm = folder.getDisplayName();

		Document document = folderIndexerFixture.searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(folder);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpFolderIndexerFixture() {
		folderIndexerFixture = new IndexerFixture<>(Folder.class);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpMailReaderFixture() {
		mailReaderFixture = new MailReaderIndexerFixture(_group, _user);

		_accounts = mailReaderFixture.getAccounts();
		_folders = mailReaderFixture.getFolders();
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		_users = userSearchFixture.getUsers();
	}

	protected IndexerFixture<Folder> folderIndexerFixture;
	protected IndexedFieldsFixture indexedFieldsFixture;
	protected MailReaderIndexerFixture mailReaderFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(Folder folder) {
		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(folder.getCompanyId()));

		map.put(Field.ENTRY_CLASS_NAME, Folder.class.getName());

		map.put(Field.ENTRY_CLASS_PK, String.valueOf(folder.getFolderId()));

		map.put(Field.NAME, folder.getDisplayName());

		map.put(
			Field.getSortableFieldName(Field.NAME), folder.getDisplayName());

		map.put(Field.USER_ID, String.valueOf(folder.getUserId()));

		map.put(Field.USER_NAME, StringUtil.lowerCase(folder.getUserName()));

		map.put("accountId", String.valueOf(folder.getAccountId()));

		map.put("folderId", String.valueOf(folder.getFolderId()));

		indexedFieldsFixture.populateUID(
			Folder.class.getName(), folder.getFolderId(), map);

		_populateDates(folder, map);

		return map;
	}

	private void _populateDates(Folder folder, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, folder.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, folder.getModifiedDate(), map);
	}

	@DeleteAfterTestRun
	private List<Account> _accounts;

	@DeleteAfterTestRun
	private List<Folder> _folders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}