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
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

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
 * @author Luan Maoski
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class WikiPageMultiLanguageSearchTest {

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

		setUpWikiFixture();

		setUpWikiPageIndexerFixture();

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseTitle() throws Exception {
		_testLocaleKeywords(LocaleUtil.CHINA, _TITLE, "你好");
	}

	@Test
	public void testEnglishTitle() throws Exception {
		_testLocaleKeywords(
			LocaleUtil.US, _TITLE,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
	}

	@Test
	public void testJapaneseTitle() throws Exception {
		_testLocaleKeywords(LocaleUtil.JAPAN, _TITLE, "東京");
	}

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		Document document = wikiPageIndexerFixture.searchOnlyOne(
			_user.getUserId(), searchTerm, locale);

		FieldValuesAssert.assertFieldValues(map, prefix, document, searchTerm);
	}

	protected void setTestLocale(Locale locale) throws Exception {
		wikiPageFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
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
		wikiPageFixture = new WikiFixture(_group, _user);

		_wikiNodes = wikiPageFixture.getWikiNodes();
		_wikiPages = wikiPageFixture.getWikiPages();
	}

	protected void setUpWikiPageIndexerFixture() {
		wikiPageIndexerFixture = new IndexerFixture<>(WikiPage.class);
	}

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;
	protected WikiFixture wikiPageFixture;
	protected IndexerFixture<WikiPage> wikiPageIndexerFixture;

	private Map<String, String> _getResultMap(String field, String keywords) {
		return new HashMap<String, String>() {
			{
				put(field, keywords);
				put(Field.getSortableFieldName(field), keywords);

				for (Locale locale :
						LanguageUtil.getAvailableLocales(_group.getGroupId())) {

					String languageId = LocaleUtil.toLanguageId(locale);

					put(
						LocalizationUtil.getLocalizedName(field, languageId),
						keywords);
				}
			}
		};
	}

	private void _testLocaleKeywords(
			Locale locale, String field, String keywords)
		throws Exception {

		setTestLocale(locale);

		wikiPageFixture.createWikiPage(keywords, RandomTestUtil.randomString());

		assertFieldValues(
			field, locale, _getResultMap(field, keywords), keywords);
	}

	private static final String _TITLE = "title";

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

	@DeleteAfterTestRun
	private List<WikiPage> _wikiPages;

}