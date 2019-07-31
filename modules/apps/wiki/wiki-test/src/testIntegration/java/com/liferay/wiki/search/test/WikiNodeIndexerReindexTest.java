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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;

import java.util.List;

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
public class WikiNodeIndexerReindexTest {

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
		setUpWikiNodeIndexerFixture();
		setUpWikiNodeFixture();
	}

	@Test
	public void testReindex() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		String searchTerm = wikiNode.getDescription();

		Document document = wikiNodeIndexerFixture.searchOnlyOne(searchTerm);

		wikiNodeIndexerFixture.deleteDocument(document);

		wikiNodeIndexerFixture.searchNoOne(searchTerm);

		wikiNodeIndexerFixture.reindex(wikiNode.getCompanyId());

		wikiNodeIndexerFixture.searchOnlyOne(searchTerm);
	}

	@Test
	public void testWikiNodeMovedToTrashReindex() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		WikiNodeLocalServiceUtil.moveNodeToTrash(
			wikiNodeFixture.getUserId(), wikiNode.getNodeId());

		String searchTerm = wikiNode.getDescription();

		wikiNodeIndexerFixture.searchNoOne(searchTerm);

		wikiNodeIndexerFixture.reindex(wikiNode.getCompanyId());

		wikiNodeIndexerFixture.searchNoOne(searchTerm);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		_users = userSearchFixture.getUsers();
	}

	protected void setUpWikiNodeFixture() {
		wikiNodeFixture = new WikiFixture(_group, _user);

		_wikiNodes = wikiNodeFixture.getWikiNodes();
	}

	protected void setUpWikiNodeIndexerFixture() {
		wikiNodeIndexerFixture = new IndexerFixture<>(WikiNode.class);
	}

	protected UserSearchFixture userSearchFixture;
	protected WikiFixture wikiNodeFixture;
	protected IndexerFixture<WikiNode> wikiNodeIndexerFixture;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

}