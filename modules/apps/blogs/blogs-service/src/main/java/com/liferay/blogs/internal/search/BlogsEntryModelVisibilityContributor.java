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

package com.liferay.blogs.internal.search;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.result.contributor.ModelVisibilityContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.blogs.model.BlogsEntry",
	service = ModelVisibilityContributor.class
)
public class BlogsEntryModelVisibilityContributor
	implements ModelVisibilityContributor {

	@Override
	public boolean isVisible(long classPK, int status) {
		BlogsEntry entry = null;

		try {
			entry = blogsEntryLocalService.getEntry(classPK);

			return isVisible(entry.getStatus(), status);
		}
		catch (PortalException pe) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to check visibility for blogs entry ", pe);
			}
		}

		return false;
	}

	protected boolean isVisible(int entryStatus, int queryStatus) {
		if (((queryStatus != WorkflowConstants.STATUS_ANY) &&
			 (entryStatus == queryStatus)) ||
			(entryStatus != WorkflowConstants.STATUS_IN_TRASH)) {

			return true;
		}

		return false;
	}

	@Reference
	protected BlogsEntryLocalService blogsEntryLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryModelVisibilityContributor.class);

}