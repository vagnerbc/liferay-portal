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

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.background.task.DDMStructureBackgroundTask;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.HashMap;
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
@Sync
public class DLFileEntryMetadataDDMStructureMultiLanguageSearchTest
	extends BaseDLIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setGroup(dlFixture.addGroup());
		setIndexerClass(DLFileEntry.class);
		setUser(dlFixture.addUser());

		setUpDLFileEntryIndexerFixture();
		setUpFileEntryMetadataFixture();

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		fileEntryMetadataFixture.tearDown();

		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseContentReindexDLFileEntryMetadataDDMStructure()
		throws Exception {

		_testLocaleContentReindexDLFileEntryMetadataDDMStructure(
			LocaleUtil.CHINA, "locale_zh.txt", "你好");
	}

	@Test
	public void testEnglishContentReindexDLFileEntryMetadataDDMStructure()
		throws Exception {

		_testLocaleContentReindexDLFileEntryMetadataDDMStructure(
			LocaleUtil.US, "locale_en.txt", "LocaleTest");
	}

	@Test
	public void testJapaneseContentReindexDLFileEntryMetadataDDMStructure()
		throws Exception {

		_testLocaleContentReindexDLFileEntryMetadataDDMStructure(
			LocaleUtil.JAPAN, "locale_ja.txt", "新規作成");
	}

	protected Document assertFieldValues(
		String field, Locale locale, String searchTerm) {

		Localization localization = LocalizationUtil.getLocalization();

		String localizedName = localization.getLocalizedName(
			field, LanguageUtil.getLanguageId(locale));

		Map<String, String> map = new HashMap<String, String>() {
			{
				put(localizedName, searchTerm);
			}
		};

		Document document = indexerFixture.searchOnlyOne(searchTerm, locale);

		FieldValuesAssert.assertFieldValues(
			map, localizedName, document, searchTerm);

		return document;
	}

	protected void runBackgroundTaskReindex(DDMStructure structure)
		throws Exception {

		DDMStructureBackgroundTask backgroundTask =
			new DDMStructureBackgroundTask(structure.getStructureId());

		backgroundTaskExecutor.execute(backgroundTask);
	}

	protected void setTestLocale(Locale locale) throws Exception {
		dlFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpDLFileEntryIndexerFixture() {
		indexerFixture = new IndexerFixture<>(DLFileEntry.class);
	}

	protected void setUpFileEntryMetadataFixture() {
		fileEntryMetadataFixture = new DLFileEntryMetadataDDMStructureFixture(
			dlFixture, dlAppLocalService, ddmStructureLocalService,
			dlFileEntryTypeLocalService);
	}

	@Inject(
		filter = "background.task.executor.class.name=com.liferay.dynamic.data.mapping.background.task.DDMStructureIndexerBackgroundTaskExecutor"
	)
	protected BackgroundTaskExecutor backgroundTaskExecutor;

	@Inject
	protected DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected DLFileEntryTypeLocalService dlFileEntryTypeLocalService;

	protected DLFileEntryMetadataDDMStructureFixture fileEntryMetadataFixture;
	protected IndexerFixture<DLFileEntry> indexerFixture;

	private void _testLocaleContentReindexDLFileEntryMetadataDDMStructure(
			Locale locale, String fileName, String searchTerm)
		throws Exception {

		setTestLocale(locale);

		DDMStructure ddmStructure =
			fileEntryMetadataFixture.createStructureWithDLFileEntry(
				fileName, locale);

		Document document = assertFieldValues(_CONTENT, locale, searchTerm);

		indexerFixture.deleteDocument(document);

		runBackgroundTaskReindex(ddmStructure);

		assertFieldValues(_CONTENT, locale, searchTerm);
	}

	private static final String _CONTENT = "content";

	private Locale _defaultLocale;

}