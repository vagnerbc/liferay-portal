package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class PluginPackageIndexerReindexTest
	extends BasePluginPackageIndexerTestCase {

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
	public void testReindexing() throws Exception {
		Locale locale = LocaleUtil.US;

		PluginPackage pluginPackage = pluginPackageFixture.addPluginPackage();

		String searchTerm = pluginPackage.getName();

		pluginPackageIndexerFixture.searchOnlyOne(searchTerm);

		Document document = pluginPackageIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		pluginPackageIndexerFixture.deleteDocument(document);

		pluginPackageIndexerFixture.searchNoOne(searchTerm, locale);

		pluginPackageIndexerFixture.reindex(CompanyConstants.SYSTEM);

		pluginPackageIndexerFixture.searchOnlyOne(searchTerm);
	}
}
