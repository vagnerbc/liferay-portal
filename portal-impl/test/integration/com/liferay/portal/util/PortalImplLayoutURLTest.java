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

package com.liferay.portal.util;

import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.test.LayoutTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Vilmos Papp
 * @author Akos Thurzo
 */
public class PortalImplLayoutURLTest extends BasePortalImplURLTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testFromControlPanel() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		themeDisplay = initThemeDisplay(
			company, group, controlPanelLayout, VIRTUAL_HOSTNAME);

		String controlPanelFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		Assert.assertEquals(
			virtualHostnameFriendlyURL, controlPanelFriendlyURL);
	}

	@Test
	public void testNotPreserveParameters() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		themeDisplay.setDoAsUserId("impersonated");

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, false);

		Assert.assertEquals(
			StringPool.BLANK,
			HttpUtil.getParameter(virtualHostnameFriendlyURL, "doAsUserId"));
	}

	@Test
	public void testNotPreserveParametersForLayoutTypeURL() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		themeDisplay.setDoAsUserId("impersonated");

		Layout layout = LayoutTestUtil.addLayout(group);

		layout.setType(LayoutConstants.TYPE_URL);

		LayoutLocalServiceUtil.updateLayout(layout);

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			layout, themeDisplay, true);

		if (Validator.isNotNull(
				layout.getTypeSettingsProperty(
					LayoutTypePortletConstants.URL)) &&
			!virtualHostnameFriendlyURL.startsWith(StringPool.SLASH) &&
			!virtualHostnameFriendlyURL.startsWith(
				PortalUtil.getPortalURL(layout, themeDisplay))) {

			Assert.assertEquals(
				StringPool.BLANK,
				HttpUtil.getParameter(
					virtualHostnameFriendlyURL, "doAsUserId"));
		}

		virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			layout, themeDisplay, false);

		Assert.assertEquals(
			StringPool.BLANK,
			HttpUtil.getParameter(virtualHostnameFriendlyURL, "doAsUserId"));
	}

	@Test
	public void testPreserveParameters() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		themeDisplay.setDoAsUserId("impersonated");

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		if (virtualHostnameFriendlyURL.startsWith(StringPool.SLASH) ||
			virtualHostnameFriendlyURL.startsWith(
				PortalUtil.getPortalURL(themeDisplay))) {

			Assert.assertEquals(
				"impersonated",
				HttpUtil.getParameter(
					virtualHostnameFriendlyURL, "doAsUserId"));
		}
	}

	@Test
	public void testUsingLocalhost() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		themeDisplay.setServerName(LOCALHOST);

		String localhostFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		Assert.assertEquals(localhostFriendlyURL, virtualHostnameFriendlyURL);
	}

	@Test
	public void testUsingLocalhostFromControlPanel() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, controlPanelLayout, VIRTUAL_HOSTNAME);

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		themeDisplay.setServerName(LOCALHOST);

		String localhostFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		Assert.assertEquals(localhostFriendlyURL, virtualHostnameFriendlyURL);
	}

	@Test
	public void testUsingLocalhostFromControlPanelOnly() throws Exception {
		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME);

		String virtualHostnameFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		themeDisplay = initThemeDisplay(
			company, group, controlPanelLayout, VIRTUAL_HOSTNAME, LOCALHOST);

		String controlPanelFriendlyURL = PortalUtil.getLayoutURL(
			publicLayout, themeDisplay, true);

		Assert.assertEquals(
			virtualHostnameFriendlyURL, controlPanelFriendlyURL);
	}

	@Test
	public void testUsingPublicLayoutSetWithVirtualHostToControlPanel()
		throws Exception {

		// Set virtual hostname for public layout Set

		LayoutSet publicLayoutSet = publicLayout.getLayoutSet();

		VirtualHostLocalServiceUtil.updateVirtualHost(
			company.getCompanyId(), publicLayoutSet.getLayoutSetId(),
			_PUBLIC_LAYOUT_SET_VIRTUAL_HOSTNAME);

		// Test generated layout URL for Control Panel navigating from the
		// public layout

		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME,
			_PUBLIC_LAYOUT_SET_VIRTUAL_HOSTNAME);

		Group controlPanelGroup = controlPanelLayout.getGroup();

		String expectedControlPanelFriendlyURL =
			PortalUtil.getPortalURL(themeDisplay) +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING +
					controlPanelGroup.getFriendlyURL() +
						controlPanelLayout.getFriendlyURL();

		String controlPanelFriendlyURL = PortalUtil.getLayoutURL(
			controlPanelLayout, themeDisplay, true);

		Assert.assertEquals(
			expectedControlPanelFriendlyURL, controlPanelFriendlyURL);
	}

	@Test
	public void testUsingPublicLayoutSetWithVirtualHostToScopedControlPanel()
		throws Exception {

		// Set virtual hostname for public layout set

		LayoutSet publicLayoutSet = publicLayout.getLayoutSet();

		VirtualHostLocalServiceUtil.updateVirtualHost(
			company.getCompanyId(), publicLayoutSet.getLayoutSetId(),
			_PUBLIC_LAYOUT_SET_VIRTUAL_HOSTNAME);

		// Create group for public layout (will be used as scope group)

		Group scopeGroup = GroupTestUtil.addGroup(
			TestPropsValues.getUserId(), publicLayout);

		// Test generated layout URL for scoped Control Panel navigating from
		// the public layout

		Layout scopedControlPanelLayout = new VirtualLayout(
			controlPanelLayout, scopeGroup);

		ThemeDisplay themeDisplay = initThemeDisplay(
			company, group, publicLayout, VIRTUAL_HOSTNAME,
			_PUBLIC_LAYOUT_SET_VIRTUAL_HOSTNAME);

		themeDisplay.setScopeGroupId(scopedControlPanelLayout.getGroupId());

		String expectedScopedControlPanelFriendlyURL =
			PortalUtil.getPortalURL(themeDisplay) +
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING +
					scopeGroup.getFriendlyURL() +
						scopedControlPanelLayout.getFriendlyURL();

		String scopedControlPanelFriendlyURL = PortalUtil.getLayoutURL(
			scopedControlPanelLayout, themeDisplay, true);

		Assert.assertEquals(
			expectedScopedControlPanelFriendlyURL,
			scopedControlPanelFriendlyURL);

		GroupTestUtil.deleteGroup(scopeGroup);
	}

	private static final String _PUBLIC_LAYOUT_SET_VIRTUAL_HOSTNAME =
		"test-public-layout.com";

}