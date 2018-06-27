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

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.Properties;

/**
 * @author Michael Hashimoto
 */
public class JenkinsUtil {

	public static void writeWorkspaceProperties() {
		if (!JenkinsResultsParserUtil.isRunningJenkins()) {
			return;
		}

		File workspacePropertiesFile = new File(
			"workspace.generated.properties");

		if (workspacePropertiesFile.exists()) {
			workspacePropertiesFile.delete();
		}

		Properties workspaceProperties = new Properties();

		Properties buildProperties = new Properties();

		try {
			buildProperties.putAll(
				JenkinsResultsParserUtil.getBuildProperties());
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}

		File baseRepositoryDir = new File(
			buildProperties.getProperty("base.repository.dir"));

		workspaceProperties.putAll(
			_getPluginsWorkspaceProperties(baseRepositoryDir));
		workspaceProperties.putAll(
			_getPortalWorkspaceProperties(baseRepositoryDir));

		File[] repositoryDirs = baseRepositoryDir.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}

			});

		workspaceProperties.putAll(
			_getSubrepositoryWorkspaceProperties(repositoryDirs));
		workspaceProperties.putAll(
			_getSimpleWorkspaceProperties(repositoryDirs));

		JenkinsResultsParserUtil.writePropertiesFile(
			workspacePropertiesFile, workspaceProperties);
	}

	private static Properties _getPluginsWorkspaceProperties(
		File baseRepositoryDir) {

		Properties properties = new Properties();

		for (String pluginsBranchName : _PLUGINS_BRANCH_NAMES) {
			String pluginsRepositoryDir =
				baseRepositoryDir + "/liferay-plugins-" + pluginsBranchName;

			properties.setProperty(
				"repository.dir[liferay-plugins/" + pluginsBranchName + "]",
				pluginsRepositoryDir);
		}

		return properties;
	}

	private static Properties _getPortalWorkspaceProperties(
		File baseRepositoryDir) {

		Properties properties = new Properties();

		for (String portalBranchName : _PORTAL_BRANCH_NAMES) {
			String portalRepositoryDir = baseRepositoryDir + "/liferay-portal";

			if (portalBranchName.equals("master")) {
				properties.setProperty(
					"repository.dir[liferay-portal]", portalRepositoryDir);
			}
			else {
				portalRepositoryDir += "-" + portalBranchName;
			}

			properties.setProperty(
				"repository.dir[liferay-portal/" + portalBranchName + "]",
				portalRepositoryDir);
		}

		return properties;
	}

	private static Properties _getSimpleWorkspaceProperties(
		File[] repositoryDirs) {

		Properties properties = new Properties();

		for (File repositoryDir : repositoryDirs) {
			String repositoryName = repositoryDir.getName();

			if (repositoryName.startsWith("com-liferay") ||
				repositoryName.startsWith("liferay-plugins") ||
				repositoryName.startsWith("liferay-portal")) {

				continue;
			}

			properties.setProperty(
				"repository.dir[" + repositoryName + "/master]",
				repositoryDir.toString());
			properties.setProperty(
				"repository.dir[" + repositoryName + "]",
				repositoryDir.toString());
		}

		return properties;
	}

	private static Properties _getSubrepositoryWorkspaceProperties(
		File[] repositoryDirs) {

		Properties properties = new Properties();

		for (File repositoryDir : repositoryDirs) {
			String repositoryName = repositoryDir.getName();

			if (!repositoryName.startsWith("com-liferay")) {
				continue;
			}

			if (!repositoryName.endsWith("-private")) {
				continue;
			}

			String subrepositoryDirPath = repositoryDir.toString();
			String subrepositoryName = repositoryName.replace("-private", "");

			for (String subrepositoryBranchName : _SUBREPOSITORY_BRANCH_NAMES) {
				properties.setProperty(
					JenkinsResultsParserUtil.combine(
						"repository.dir[", subrepositoryName, "/",
						subrepositoryBranchName, "]"),
					subrepositoryDirPath);
			}

			properties.setProperty(
				JenkinsResultsParserUtil.combine(
					"repository.dir[", subrepositoryName, "]"),
				subrepositoryDirPath);
		}

		return properties;
	}

	private static final String[] _PLUGINS_BRANCH_NAMES =
		{"ee-6.1.x", "ee-6.1.20", "ee-6.2.x", "ee-6.2.10", "7.0.x"};

	private static final String[] _PORTAL_BRANCH_NAMES = {
		"ee-6.1.x", "ee-6.1.30", "ee-6.2.x", "ee-6.2.10", "master",
		"master-private", "7.0.x", "7.0.x-private", "7.1.x", "7.1.x-private"
	};

	private static final String[] _SUBREPOSITORY_BRANCH_NAMES = {
		"master", "master-private", "7.0.x", "7.0.x-private", "7.1.x",
		"7.1.x-private"
	};

}