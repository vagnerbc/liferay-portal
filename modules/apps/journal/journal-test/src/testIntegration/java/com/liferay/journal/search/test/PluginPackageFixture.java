package com.liferay.journal.search.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.plugin.License;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.plugin.Version;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.plugin.PluginPackageImpl;
import com.liferay.portal.plugin.PluginPackageUtil;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class PluginPackageFixture {

	public PluginPackageFixture(
		List<Group> groups, List<PluginPackage> pluginPackages) {

		_groups = groups;
		_pluginPackages = pluginPackages;
	}

	protected PluginPackage addPluginPackage()
		throws PortalException {

		String context = RandomTestUtil.randomString();

		String moduleId = StringBundler.concat(
			context, StringPool.SLASH, context,
			StringPool.SLASH, Version.UNKNOWN, StringPool.SLASH, "war");

		PluginPackage pluginPackage = new PluginPackageImpl(moduleId);

		pluginPackage.setName(context);
		pluginPackage.setContext(context);

		pluginPackage.setModifiedDate(new Date());
		pluginPackage.setAuthor(RandomTestUtil.randomString());

		List<String> types = new ArrayList<>();
		types.add(RandomTestUtil.randomString());
		pluginPackage.setTypes(types);

		List<License> licenses = new ArrayList<>();
		License license = new License();
		license.setName(RandomTestUtil.randomString());
		licenses.add(license);
		pluginPackage.setLicenses(licenses);


		List<String> liferayVersions = new ArrayList<>();
		liferayVersions.add(RandomTestUtil.randomString());
		pluginPackage.setLiferayVersions(liferayVersions);

		List<String> tags = new ArrayList<>();
		tags.add(RandomTestUtil.randomString());
		pluginPackage.setTags(tags);

		pluginPackage.setShortDescription(RandomTestUtil.randomString());
		pluginPackage.setLongDescription(RandomTestUtil.randomString());
		pluginPackage.setChangeLog(RandomTestUtil.randomString());
		pluginPackage.setPageURL(RandomTestUtil.randomString());
		pluginPackage.setDownloadURL(RandomTestUtil.randomString());

		PluginPackageUtil.registerInstalledPluginPackage(pluginPackage);

		return pluginPackage;
	}

	public Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	public Group getGroup() {
		return _group;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	public void setGroup(Group group) {
		_group = group;
	}

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private Group _group;
	private final List<Group> _groups;
	private final List<PluginPackage> _pluginPackages;
}
