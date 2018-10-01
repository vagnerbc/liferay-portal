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

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Luan Maoski
 * @author Luca Marques
 */
public class AssetTagFixture {

	public AssetTagFixture() {
	}

	public AssetTag createAssetTag() throws Exception {
		try {
			AssetTag assetTag = AssetTagLocalServiceUtil.addTag(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), getServiceContext());

			_assetTags.add(assetTag);

			return assetTag;
		}
		catch (PortalException e)
		{
			throw new RuntimeException(e);
		}
	}

	public List<AssetTag> getAssetTags() {
		return _assetTags;
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

	private final List<AssetTag> _assetTags = new ArrayList<>();
	private Group _group;

}