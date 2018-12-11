package com.liferay.journal.search.test;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePluginPackageIndexerTestCase {

	public void setUp() throws Exception {
		pluginPackageFixture = createPluginPackageFixture();

		pluginPackageFixture.setUp();

		setGroup(pluginPackageFixture.addGroup());

		pluginPackageIndexerFixture = createPluginPackageIndexerFixture();

		indexedFieldsFixture = createIndexedFieldsFixture();
	}

	protected IndexedFieldsFixture createIndexedFieldsFixture() {
		return new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected PluginPackageFixture createPluginPackageFixture() {
		return new PluginPackageFixture(_groups, _pluginPackages);
	}

	protected PluginPackageIndexerFixture createPluginPackageIndexerFixture() {
		Indexer<PluginPackage> indexer = indexerRegistry.getIndexer(
			PluginPackage.class);

		return new PluginPackageIndexerFixture(indexer);
	}

	protected void setGroup(Group group) {
		pluginPackageFixture.setGroup(group);
	}

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	protected PluginPackageFixture pluginPackageFixture;
	protected PluginPackageIndexerFixture pluginPackageIndexerFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<PluginPackage> _pluginPackages = new ArrayList<>(1);
}
