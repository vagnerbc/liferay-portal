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

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalFolderIndexerIndexedFieldsTest
	extends BaseJournalFolderIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@Test
	public void testIndexedFields() throws Exception {
		JournalFolder journalFolder =
			journalFolderFixture.createJournalFolder();

		String searchTerm = journalFolder.getName();

		Document document = journalFolderIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(journalFolder);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	private Map<String, String> _expectedFieldValues(
			JournalFolder journalFolder)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.TITLE, journalFolder.getName());

		map.put(
			Field.TITLE + "_sortable",
			StringUtil.toLowerCase(journalFolder.getName()));

		map.put(Field.DESCRIPTION, journalFolder.getDescription());

		map.put(Field.COMPANY_ID, String.valueOf(journalFolder.getCompanyId()));

		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(journalFolder.getPrimaryKey()));

		map.put(Field.ENTRY_CLASS_NAME, JournalFolder.class.getName());

		map.put(
			Field.FOLDER_ID, String.valueOf(journalFolder.getParentFolderId()));

		map.put(Field.GROUP_ID, String.valueOf(journalFolder.getGroupId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(journalFolder.getGroupId()));

		map.put(Field.STAGING_GROUP, "false");

		map.put(Field.STATUS, String.valueOf(journalFolder.getStatus()));

		map.put(Field.USER_ID, String.valueOf(journalFolder.getUserId()));

		map.put(
			Field.USER_NAME,
			StringUtil.toLowerCase(journalFolder.getUserName()));

		map.put("visible", "true");

		_populateTreePath(journalFolder, map);

		_populateLocalizedTitles(journalFolder, map);

		indexedFieldsFixture.populatePriority("0.0", map);

		indexedFieldsFixture.populateUID(
			JournalFolder.class.getName(), journalFolder.getPrimaryKey(), map);

		_populateDates(journalFolder, map);

		_populateRoles(journalFolder, map);

		return map;
	}

	private void _populateDates(
		JournalFolder journalFolder, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, journalFolder.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, journalFolder.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, journalFolder.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		JournalFolder journalFolder, Map<String, String> map) {

		String title = StringUtil.lowerCase(journalFolder.getName());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private void _populateRoles(
			JournalFolder journalFolder, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			journalFolder.getCompanyId(), JournalFolder.class.getName(),
			journalFolder.getPrimaryKey(), journalFolder.getGroupId(), null,
			map);
	}

	private void _populateTreePath(
		JournalFolder journalFolder, Map<String, String> map) {

		String treePath = null;

		String[] treePaths = StringUtil.split(
			journalFolder.getTreePath(), CharPool.SLASH);

		if (treePaths != null) {
			if (treePaths.length == 1) {
				treePath = treePaths[0];
			}
			else {
				treePath = String.valueOf(Arrays.asList(treePaths));
			}
		}

		map.put(Field.TREE_PATH, treePath);
	}

}