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

package com.liferay.polls.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.polls.model.PollsQuestion;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class PollsQuestionIndexerReindexTest {

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
		setUpPollsQuestionIndexerFixture();
		setUpPollsQuestionFixture();
	}

	@Test
	public void testReindexingFolders() throws Exception {
		Locale locale = LocaleUtil.US;

		PollsQuestion pollsQuestion =
			pollsQuestionFixture.createPollsQuestion();

		String searchTerm = pollsQuestion.getTitle(locale);

		pollsQuestionFixture.updateDisplaySettings(locale);

		pollsQuestionIndexerFixture.searchOnlyOne(searchTerm);

		Document document = pollsQuestionIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		pollsQuestionIndexerFixture.deleteDocument(document);

		pollsQuestionIndexerFixture.searchNoOne(searchTerm, locale);

		pollsQuestionIndexerFixture.reindex(pollsQuestion.getCompanyId());

		pollsQuestionIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpPollsQuestionFixture() {
		pollsQuestionFixture = new PollsQuestionFixture(group);

		_pollsQuestions = pollsQuestionFixture.getPollsQuestions();
	}

	protected void setUpPollsQuestionIndexerFixture() {
		pollsQuestionIndexerFixture = new IndexerFixture<>(PollsQuestion.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected Group group;
	protected PollsQuestionFixture pollsQuestionFixture;
	protected IndexerFixture<PollsQuestion> pollsQuestionIndexerFixture;
	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<PollsQuestion> _pollsQuestions;

	@DeleteAfterTestRun
	private List<User> _users;

}