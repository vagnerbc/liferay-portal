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
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
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
 */
@RunWith(Arquillian.class)
public class WikiNodeIndexerReindexTest {

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
		setUpWikiNodeIndexerFixture();
		setUpWikiNodeFixture();
	}

	@Test
	public void testReindexing() throws Exception {
		WikiNode wikiNode = wikiNodeFixture.createWikiNode();

		wikiNode = WikiNodeLocalServiceUtil.moveNodeToTrash(
			TestPropsValues.getUserId(), wikiNode.getNodeId());

		String searchTerm = wikiNode.getDescription();

		Document document = wikiNodeIndexerFixture.searchOnlyOne(searchTerm);

		wikiNodeIndexerFixture.deleteDocument(document);

		wikiNodeIndexerFixture.searchNoOne(searchTerm);

		wikiNodeIndexerFixture.reindex(wikiNode.getCompanyId());

		wikiNodeIndexerFixture.searchOnlyOne(searchTerm);
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
	protected UserSearchFixture userSearchFixture;
	protected WikiNodeFixture wikiNodeFixture;
	protected IndexerFixture<WikiNode> wikiNodeIndexerFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

}