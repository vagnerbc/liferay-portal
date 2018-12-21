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
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexerRegistry;
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
import java.util.Map;

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
public class AssetCategoryIndexerIndexedFieldsTest {

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
		setUpAssetCategoryIndexerFixture();
		setUpAssetCategoryFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		AssetCategory assetCategory =
			assetCategoryFixture.createAssetCategory();

		String searchTerm = assetCategory.getName();

		Document document = assetCategoryIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(assetCategory);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpAssetCategoryFixture() throws Exception {
		assetCategoryFixture = new AssetCategoryFixture();

		assetCategoryFixture.setUp();

		assetCategoryFixture.setGroup(group);

		_assetCategories = assetCategoryFixture.getAssetCategories();
	}

	protected void setUpAssetCategoryIndexerFixture() {
		assetCategoryIndexerFixture = new IndexerFixture<>(AssetCategory.class);
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

	protected AssetCategoryFixture assetCategoryFixture;
	protected IndexerFixture assetCategoryIndexerFixture;
	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			AssetCategory assetCategory)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(assetCategory.getCategoryId()));

		map.put(Field.ENTRY_CLASS_NAME, AssetCategory.class.getName());
		map.put(Field.COMPANY_ID, String.valueOf(assetCategory.getCompanyId()));

		map.put(Field.GROUP_ID, String.valueOf(assetCategory.getGroupId()));

		map.put(
			Field.ASSET_CATEGORY_ID,
			String.valueOf(assetCategory.getCategoryId()));

		map.put(
			"leftCategoryId",
			String.valueOf(assetCategory.getLeftCategoryId()));

		map.put(
			Field.ASSET_VOCABULARY_ID,
			String.valueOf(assetCategory.getVocabularyId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(assetCategory.getGroupId()));

		map.put(
			Field.STAGING_GROUP,
			String.valueOf(assetCategoryFixture.getGroup().isStagingGroup()));

		map.put(
			"parentCategoryId",
			String.valueOf(assetCategory.getParentCategoryId()));

		map.put(Field.TITLE, assetCategory.getName());

		map.put(
			Field.ASSET_CATEGORY_TITLE,
			StringUtil.lowerCase(assetCategory.getName()));

		map.put(
			Field.ASSET_CATEGORY_TITLE + "_en_US",
			StringUtil.lowerCase(assetCategory.getName()));

		map.put("title_en_US", assetCategory.getName());

		map.put(
			"title_sortable", StringUtil.lowerCase(assetCategory.getName()));

		map.put(Field.NAME, assetCategory.getName());

		map.put("name_sortable", StringUtil.lowerCase(assetCategory.getName()));

		map.put(Field.USER_ID, String.valueOf(assetCategory.getUserId()));

		map.put(
			Field.USER_NAME, StringUtil.lowerCase(assetCategory.getUserName()));

		indexedFieldsFixture.populateUID(
			AssetCategory.class.getName(), assetCategory.getCategoryId(), map);

		_populateDates(assetCategory, map);
		_populateRoles(assetCategory, map);

		return map;
	}

	private void _populateDates(
		AssetCategory assetCategory, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, assetCategory.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, assetCategory.getCreateDate(), map);
	}

	private void _populateRoles(
			AssetCategory assetCategory, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			assetCategory.getCompanyId(), AssetCategory.class.getName(),
			assetCategory.getCategoryId(), assetCategory.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategories;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}