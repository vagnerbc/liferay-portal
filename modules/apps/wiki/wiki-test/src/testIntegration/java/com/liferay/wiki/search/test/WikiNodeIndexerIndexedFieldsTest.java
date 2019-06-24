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

package com.liferay.wiki.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.TrashHelper;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;

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
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class WikiNodeIndexerIndexedFieldsTest {

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
		setUpIndexedFieldsFixture();
		setUpWikiNodeIndexerFixture();
		setUpWikiFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		String searchTerm = wikiNode.getDescription();

		Document document = wikiNodeIndexerFixture.searchOnlyOne(searchTerm);

		Map<String, String> expected = _expectedFieldValues(wikiNode);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	@Test
	public void testWikiNodeMovedToTrash() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		String searchTerm = wikiNode.getDescription();

		WikiNodeLocalServiceUtil.moveNodeToTrash(
			wikiNodeFixture.getUserId(), wikiNode.getNodeId());

		wikiNodeIndexerFixture.searchNoOne(searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		_users = userSearchFixture.getUsers();
	}

	protected void setUpWikiFixture() {
		wikiNodeFixture = new WikiFixture(_group, _user);

		_wikiNodes = wikiNodeFixture.getWikiNodes();
	}

	protected void setUpWikiNodeIndexerFixture() {
		wikiNodeIndexerFixture = new IndexerFixture<>(WikiNode.class);
	}

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;
	protected WikiFixture wikiNodeFixture;
	protected IndexerFixture<WikiNode> wikiNodeIndexerFixture;

	private Map<String, String> _expectedFieldValues(WikiNode wikiNode)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(wikiNode.getCompanyId()));
		map.put(Field.DESCRIPTION, wikiNode.getDescription());
		map.put(Field.ENTRY_CLASS_NAME, WikiNode.class.getName());
		map.put(Field.ENTRY_CLASS_PK, String.valueOf(wikiNode.getNodeId()));
		map.put(Field.GROUP_ID, String.valueOf(wikiNode.getGroupId()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(wikiNode.getGroupId()));
		map.put(Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup()));
		map.put(Field.STATUS, String.valueOf(wikiNode.getStatus()));
		map.put(Field.TITLE, wikiNode.getName());
		map.put(Field.USER_ID, String.valueOf(wikiNode.getUserId()));
		map.put(Field.USER_NAME, StringUtil.lowerCase(wikiNode.getUserName()));
		map.put("title_sortable", StringUtil.lowerCase(wikiNode.getName()));

		indexedFieldsFixture.populateUID(
			WikiNode.class.getName(), wikiNode.getNodeId(), map);

		_populateDates(wikiNode, map);
		_populateLocalizedValues(wikiNode, map);
		_populateRoles(wikiNode, map);

		return map;
	}

	private void _populateDates(WikiNode wikiNode, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, wikiNode.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, wikiNode.getModifiedDate(), map);
	}

	private void _populateLocalizedValues(
		WikiNode wikiNode, Map<String, String> map) {

		for (Locale locale :
				LanguageUtil.getAvailableLocales(wikiNode.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			map.put(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				wikiNode.getDescription());

			String title = wikiNode.getName();

			if (wikiNode.isInTrash()) {
				title = _trashHelper.getOriginalTitle(title);
			}

			map.put(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				title);
		}
	}

	private void _populateRoles(WikiNode wikiNode, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			wikiNode.getCompanyId(), WikiNode.class.getName(),
			wikiNode.getNodeId(), wikiNode.getGroupId(), null, map);
	}

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private TrashHelper _trashHelper;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

}