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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.polls.model.PollsQuestion;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
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
public class PollsQuestionIndexerIndexedFieldsTest {

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
		setUpPollsQuestionIndexerFixture();
		setUpPollsQuestionFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		PollsQuestion pollsQuestion =
			pollsQuestionFixture.createPollsQuestion();

		String searchTerm = pollsQuestion.getDescription(Locale.US);

		Document document = pollsQuestionIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(pollsQuestion);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected String getDescriptionField(PollsQuestion pollsQuestion) {
		String[] availableLanguageIds = pollsQuestion.getAvailableLanguageIds();

		StringBundler sb = new StringBundler();

		for (String languageId : availableLanguageIds) {
			sb.append(pollsQuestion.getDescription(languageId));
			sb.append(StringPool.SPACE);
		}

		return sb.toString().trim();
	}

	protected String getTitleField(PollsQuestion pollsQuestion) {
		String[] availableLanguageIds = pollsQuestion.getAvailableLanguageIds();

		StringBundler sb = new StringBundler();

		for (String languageId : availableLanguageIds) {
			sb.append(pollsQuestion.getTitle(languageId));
			sb.append(StringPool.SPACE);
		}

		return sb.toString().trim();
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
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
	protected IndexedFieldsFixture indexedFieldsFixture;
	protected PollsQuestionFixture pollsQuestionFixture;
	protected IndexerFixture<PollsQuestion> pollsQuestionIndexerFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			PollsQuestion pollsQuestion)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(pollsQuestion.getQuestionId()));
		map.put(Field.ENTRY_CLASS_NAME, PollsQuestion.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(pollsQuestion.getCompanyId()));

		map.put(Field.GROUP_ID, String.valueOf(pollsQuestion.getGroupId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(pollsQuestion.getGroupId()));

		map.put(Field.STAGING_GROUP, String.valueOf(group.isStagingGroup()));

		map.put(Field.TITLE, getTitleField(pollsQuestion));

		map.put(
			"title_sortable",
			StringUtil.lowerCase(getTitleField(pollsQuestion)));

		map.put(Field.DESCRIPTION, getDescriptionField(pollsQuestion));

		map.put(Field.USER_ID, String.valueOf(pollsQuestion.getUserId()));

		map.put(
			Field.USER_NAME, StringUtil.lowerCase(pollsQuestion.getUserName()));

		indexedFieldsFixture.populateUID(
			PollsQuestion.class.getName(), pollsQuestion.getQuestionId(), map);

		_populateDates(pollsQuestion, map);
		_populateRoles(pollsQuestion, map);

		return map;
	}

	private void _populateDates(
		PollsQuestion pollsQuestion, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, pollsQuestion.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			"createDate_Number", pollsQuestion.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, pollsQuestion.getCreateDate(), map);
		map.remove("createDate_Number");
	}

	private void _populateRoles(
			PollsQuestion pollsQuestion, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			pollsQuestion.getCompanyId(), PollsQuestion.class.getName(),
			pollsQuestion.getQuestionId(), pollsQuestion.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<PollsQuestion> _pollsQuestions;

	@DeleteAfterTestRun
	private List<User> _users;

}