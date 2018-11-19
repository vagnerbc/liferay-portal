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

package com.liferay.wiki.search.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luan Maoski
 */
public class WikiNodeFixture {

	public WikiNodeFixture(Group group)
	{

		_group = group;
	}

	public WikiNode createWikiNode() throws Exception {
		try {
			WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), getServiceContext());

			_wikiNodes.add(wikiNode);

			return wikiNode;
		}
		catch (PortalException e)
		{
			throw new RuntimeException(e);
		}
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	public List<WikiNode> getWikiNodes() {
		return _wikiNodes;
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private final Group _group;
	private final List<WikiNode> _wikiNodes = new ArrayList<>();

}