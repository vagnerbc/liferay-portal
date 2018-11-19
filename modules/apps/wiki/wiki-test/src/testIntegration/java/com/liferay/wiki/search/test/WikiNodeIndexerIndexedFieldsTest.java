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
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;

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
public class WikiNodeIndexerIndexedFieldsTest {

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
		setUpWikiNodeIndexerFixture();
		setUpWikiNodeFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		String wikiNodeName = wikiNode.getName();

		wikiNode = WikiNodeLocalServiceUtil.moveNodeToTrash(
			TestPropsValues.getUserId(), wikiNode.getNodeId());

		String searchTerm = wikiNode.getDescription();

		Document document = wikiNodeIndexerFixture.searchOnlyOne(searchTerm);

		String removedDateSort = document.get("removedDate_sortable");

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(
			wikiNode, wikiNodeName, removedDateSort);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
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

	protected void setUpWikiNodeFixture() {
		wikiNodeFixture = new WikiNodeFixture(group);

		_wikiNodes = wikiNodeFixture.getWikiNodes();
	}

	protected void setUpWikiNodeIndexerFixture() {
		wikiNodeIndexerFixture = new IndexerFixture<>(WikiNode.class);
	}

	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;
	protected WikiNodeFixture wikiNodeFixture;
	protected IndexerFixture<WikiNode> wikiNodeIndexerFixture;

	private Map<String, String> _expectedFieldValues(
			WikiNode wikiNode, String wikiNodeName, String removedDateSort)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.ENTRY_CLASS_PK, String.valueOf(wikiNode.getNodeId()));

		map.put(Field.ENTRY_CLASS_NAME, WikiNode.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(wikiNode.getCompanyId()));

		map.put(Field.GROUP_ID, String.valueOf(wikiNode.getGroupId()));

		map.put(Field.SCOPE_GROUP_ID, String.valueOf(wikiNode.getGroupId()));

		map.put(Field.STAGING_GROUP, String.valueOf(group.isStagingGroup()));

		map.put(Field.DESCRIPTION, wikiNode.getDescription());

		map.put(Field.TITLE, wikiNodeName);

		map.put(Field.TITLE + "_sortable", StringUtil.lowerCase(wikiNodeName));

		map.put(Field.TYPE, "wiki_node");

		map.put(Field.STATUS, String.valueOf(wikiNode.getStatus()));

		map.put(Field.USER_ID, String.valueOf(wikiNode.getUserId()));

		map.put(Field.USER_NAME, StringUtil.lowerCase(wikiNode.getUserName()));

		map.put(
			Field.REMOVED_BY_USER_NAME,
			StringUtil.lowerCase(wikiNode.getUserName()));

		indexedFieldsFixture.populateUID(
			WikiNode.class.getName(), wikiNode.getNodeId(), map);

		_populateDates(wikiNode, map);
		_populateRoles(wikiNode, map);

		map.put(Field.REMOVED_DATE + "_sortable", removedDateSort);

		return map;
	}

	private void _populateDates(WikiNode wikiNode, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, wikiNode.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, wikiNode.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.REMOVED_DATE, wikiNode.getModifiedDate(), map);
	}

	private void _populateRoles(WikiNode wikiNode, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			wikiNode.getCompanyId(), WikiNode.class.getName(),
			wikiNode.getNodeId(), wikiNode.getGroupId(), null, map);
	}

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

}