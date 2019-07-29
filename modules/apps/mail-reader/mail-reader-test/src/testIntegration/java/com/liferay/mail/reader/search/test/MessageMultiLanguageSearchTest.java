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
import com.liferay.mail.reader.model.Message;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class MessageMultiLanguageSearchTest {

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

		setUpMessageIndexerFixture();

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseContent() throws Exception {
		_testLocaleField(LocaleUtil.CHINA, _CONTENT, "你好");
	}

	@Test
	public void testEnglishContent() throws Exception {
		String keywords = RandomTestUtil.randomString();

		_testLocaleField(
			LocaleUtil.US, _CONTENT, StringUtil.toLowerCase(keywords));
	}

	@Test
	public void testJapaneseContent() throws Exception {
		_testLocaleField(LocaleUtil.JAPAN, _CONTENT, "東京");
	}

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		Document document = messageIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		FieldValuesAssert.assertFieldValues(map, prefix, document, searchTerm);
	}

	protected void setTestLocale(Locale locale) throws Exception {
		mailReaderFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpMailReaderFixture() {
		mailReaderFixture = new MailReaderIndexerFixture(_group, _user);

		_messages = mailReaderFixture.getMessages();
	}

	protected void setUpMessageIndexerFixture() {
		messageIndexerFixture = new IndexerFixture<>(Message.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		_users = userSearchFixture.getUsers();
	}

	protected MailReaderIndexerFixture mailReaderFixture;
	protected IndexerFixture<Message> messageIndexerFixture;
	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _getResultMap(String field, String keywords) {
		return new HashMap<String, String>() {
			{
				put(field, keywords);

				for (Locale locale :
						LanguageUtil.getAvailableLocales(_group.getGroupId())) {

					String languageId = LocaleUtil.toLanguageId(locale);

					put(
						LocalizationUtil.getLocalizedName(
							Field.CONTENT, languageId),
						keywords);
				}
			}
		};
	}

	private void _testLocaleField(Locale locale, String field, String keywords)
		throws Exception {

		setTestLocale(locale);

		mailReaderFixture.createMessage(keywords, keywords);

		assertFieldValues(
			field, locale, _getResultMap(field, keywords), keywords);
	}

	private static final String _CONTENT = "content";

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<Message> _messages;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}