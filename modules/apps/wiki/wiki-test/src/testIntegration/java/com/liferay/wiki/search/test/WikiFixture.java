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

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.util.test.WikiTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Luan Maoski
 * @author Vagner B.C
 */
public class WikiFixture {

	public WikiFixture(Group group, User user) {
		_group = group;
		_user = user;
	}

	public WikiNode createWikiNode() throws Exception {
		return createWikiNode(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	public WikiNode createWikiNode(String name, String description)
		throws Exception {

		WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
			getUserId(), name, description, getServiceContext());

		_wikiNodes.add(wikiNode);

		return wikiNode;
	}

	public WikiPage createWikiPage() throws Exception {
		return createWikiPage(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	public WikiPage createWikiPage(String title, String content)
		throws Exception {

		WikiNode wikiNode = createWikiNode();

		WikiPage wikiPage = WikiTestUtil.addPage(
			getUserId(), wikiNode.getNodeId(), title, content, true,
			getServiceContext());

		_wikiNodes.add(wikiNode);

		_wikiPages.add(wikiPage);

		return wikiPage;
	}

	public List<WikiNode> getWikiNodes() {
		return _wikiNodes;
	}

	public List<WikiPage> getWikiPages() {
		return _wikiPages;
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	protected long getUserId() {
		return _user.getUserId();
	}

	private final Group _group;
	private final User _user;
	private final List<WikiNode> _wikiNodes = new ArrayList<>();
	private final List<WikiPage> _wikiPages = new ArrayList<>();

}