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
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.Locale;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalFolderIndexerReindexTest
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
	public void testReindexingFolders() throws Exception {
		Locale locale = LocaleUtil.US;

		JournalFolder journalFolder =
			journalFolderFixture.createJournalFolder();

		String searchTerm = journalFolder.getName();

		journalFolderFixture.updateDisplaySettings(locale);

		journalFolderIndexerFixture.searchOnlyOne(searchTerm);

		Document document = journalFolderIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		journalFolderIndexerFixture.deleteDocument(document);

		journalFolderIndexerFixture.searchNoOne(searchTerm, locale);

		journalFolderIndexerFixture.reindex(journalFolder.getCompanyId());

		journalFolderIndexerFixture.searchOnlyOne(searchTerm);
	}

}