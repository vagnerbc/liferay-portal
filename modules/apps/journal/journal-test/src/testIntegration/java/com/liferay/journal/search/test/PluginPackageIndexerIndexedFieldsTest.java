package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.plugin.License;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class PluginPackageIndexerIndexedFieldsTest
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
	public void testIndexedFields() throws Exception {
		PluginPackage pluginPackage = pluginPackageFixture.addPluginPackage();

		String searchTerm = pluginPackage.getName();

		Document document = pluginPackageIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(pluginPackage);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	private Map<String, String> _expectedFieldValues(
		PluginPackage pluginPackage)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.TITLE, pluginPackage.getName());

		map.put(
			Field.TITLE + "_sortable",
			StringUtil.toLowerCase(pluginPackage.getName()));

		map.put(Field.ENTRY_CLASS_NAME, PluginPackage.class.getName());

		map.put(Field.GROUP_ID, String.valueOf(pluginPackage.getGroupId()));

		map.put("artifactId", pluginPackage.getArtifactId());

		map.put("author", pluginPackage.getAuthor());

		map.put("changeLog", pluginPackage.getChangeLog());

		map.put(Field.COMPANY_ID, String.valueOf(CompanyConstants.SYSTEM));

		map.put("installedVersion", "unknown");

		map.put("longDescription", pluginPackage.getLongDescription());

		map.put("moduleId", pluginPackage.getModuleId());

		map.put("osi-approved-license", "false");

		map.put("pageURL", pluginPackage.getPageURL());

		map.put("repositoryURL", pluginPackage.getRepositoryURL());

		map.put("shortDescription", pluginPackage.getShortDescription());

		map.put(Field.STATUS, "newerVersionInstalled");

		map.put(Field.VERSION, pluginPackage.getVersion());

		indexedFieldsFixture.populateUID(PluginPackage.class.getName(), pluginPackage.getModuleId(), map);

		_populateLicenses("license", pluginPackage.getLicenses(), map);

		_populateValues(Field.TYPE, pluginPackage.getTypes(), map);

		_populateValues("tag", pluginPackage.getTags(), map);

		_populateDates(pluginPackage, map);

		_populateContent(pluginPackage, map);

		return map;
	}

	private void _populateDates(
		PluginPackage pluginPackage, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, pluginPackage.getModifiedDate(), map);
	}

	private void _populateContent(PluginPackage pluginPackage, Map<String, String> map) {
		StringBundler sb = new StringBundler(7);

		sb.append(pluginPackage.getAuthor());
		sb.append(StringPool.SPACE);

		String longDescription = HtmlUtil.extractText(
			pluginPackage.getLongDescription());

		sb.append(longDescription);

		sb.append(StringPool.SPACE);
		sb.append(pluginPackage.getName());
		sb.append(StringPool.SPACE);

		String shortDescription = HtmlUtil.extractText(
			pluginPackage.getShortDescription());

		sb.append(shortDescription);

		map.put(Field.CONTENT, sb.toString());
	}

	private void _populateValues(
		String field, List<String> values, Map<String, String> map) {

		if (values.size() == 1) {
			map.put(field, values.get(0));
		}
		else if (values.size() > 1) {
			map.put(field, values.toString());
		}
	}

	private void _populateLicenses(
		String field, List<License> values, Map<String, String> map) {

		List<String> licenses = new ArrayList<>();

		Stream<License> stream = values.stream();

		stream.forEach(license -> {
			licenses.add(license.getName());
		});

		_populateValues(field, licenses, map);
	}

}
