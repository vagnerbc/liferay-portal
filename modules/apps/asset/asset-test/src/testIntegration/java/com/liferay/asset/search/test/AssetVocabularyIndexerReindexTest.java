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

package com.liferay.asset.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
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
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class AssetVocabularyIndexerReindexTest {

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
		setUpAssetVocabularyIndexerFixture();
		setUpAssetVocabularyFixture();
	}

	@Test
	public void testReindexing() throws Exception {
		Locale locale = LocaleUtil.US;

		AssetVocabulary assetVocabulary =
			assetVocabularyFixture.createAssetVocabulary();

		String searchTerm = assetVocabulary.getName();

		assetVocabularyFixture.updateDisplaySettings(locale);

		assetVocabularyIndexerFixture.searchOnlyOne(searchTerm);

		Document document = assetVocabularyIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		assetVocabularyIndexerFixture.deleteDocument(document);

		assetVocabularyIndexerFixture.searchNoOne(searchTerm, locale);

		assetVocabularyIndexerFixture.reindex(assetVocabulary.getCompanyId());

		assetVocabularyIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpAssetVocabularyFixture() throws Exception {
		assetVocabularyFixture = new AssetVocabularyFixture();

		assetVocabularyFixture.setUp();

		assetVocabularyFixture.setGroup(group);

		_assetVocabularies = assetVocabularyFixture.getAssetVocabularies();
	}

	protected void setUpAssetVocabularyIndexerFixture() {
		assetVocabularyIndexerFixture = new IndexerFixture<>(
			AssetVocabulary.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected AssetVocabularyFixture assetVocabularyFixture;
	protected IndexerFixture assetVocabularyIndexerFixture;
	protected Group group;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularies;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}