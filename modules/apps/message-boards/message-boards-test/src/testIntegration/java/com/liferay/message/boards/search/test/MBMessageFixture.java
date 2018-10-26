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

package com.liferay.message.boards.search.test;

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.message.boards.service.MBMessageLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luan Maoski
 */
public class MBMessageFixture {

	public MBMessageFixture(Group group, User user) {
		_group = group;
		_user = user;
	}

	public MBMessage createMBMessage() throws Exception {
		MBCategory mbCategory = MBCategoryLocalServiceUtil.addCategory(
			getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), getServiceContext());

		_mbCategories.add(mbCategory);

		MBMessage mbMessage = MBMessageLocalServiceUtil.addMessage(
			getUserId(), RandomTestUtil.randomString(),
			TestPropsValues.getGroupId(), mbCategory.getCategoryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			getServiceContext());

		_mbMessages.add(mbMessage);

		_mbThreads.add(mbMessage.getThread());

		return mbMessage;
	}

	public List<MBCategory> getMbCategories() {
		return _mbCategories;
	}

	public List<MBMessage> getMbMessages() {
		return _mbMessages;
	}

	public List<MBThread> getMbThreads() {
		return _mbThreads;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	protected long getUserId() {
		return _user.getUserId();
	}

	private final Group _group;
	private final List<MBCategory> _mbCategories = new ArrayList<>();
	private final List<MBMessage> _mbMessages = new ArrayList<>();
	private final List<MBThread> _mbThreads = new ArrayList<>();
	private final User _user;

}