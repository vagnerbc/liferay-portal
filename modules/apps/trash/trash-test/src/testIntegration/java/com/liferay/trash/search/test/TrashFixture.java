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

package com.liferay.trash.search.test;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLTrashLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestDataConstants;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luan Maoski
 */
public class TrashFixture {

	public TrashFixture() {
	}

	public FileEntry createFileEntry() {
		try {
			User user = UserTestUtil.getAdminUser(_group.getCompanyId());

			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId());

			FileEntry fileEntry = DLAppLocalServiceUtil.addFileEntry(
				user.getUserId(), _group.getGroupId(),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomString(), ContentTypes.TEXT_PLAIN,
				TestDataConstants.TEST_BYTE_ARRAY, serviceContext);

			DLTrashLocalServiceUtil.moveFileEntryToTrash(
				user.getUserId(), fileEntry.getRepositoryId(),
				fileEntry.getFileEntryId());

			fileEntry = DLAppLocalServiceUtil.getFileEntry(
				fileEntry.getFileEntryId());

			TrashEntry trashEntry = TrashEntryLocalServiceUtil.getEntry(
				DLFileEntry.class.getName(), fileEntry.getFileEntryId());

			_trashEntries.add(trashEntry);

			return fileEntry;
		}
		catch (PortalException pe) {
			pe.printStackTrace();
		}

		return null;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	public List<TrashEntry> getTrashEntries() {
		return _trashEntries;
	}

	public void setGroup(Group group) {
		_group = group;
	}

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private Group _group;
	private final List<TrashEntry> _trashEntries = new ArrayList<>();

}