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

package com.liferay.document.library.internal.search;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchRegistrarHelper;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;
import com.liferay.portal.search.spi.model.result.contributor.ModelVisibilityContributor;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = {})
public class DLFileEntrySearchRegistrar {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = modelSearchRegistrarHelper.register(
			DLFileEntry.class, bundleContext,
			modelSearchDefinition -> {
				modelSearchDefinition.setDefaultSelectedFieldNames(
					Field.ASSET_TAG_NAMES, Field.COMPANY_ID, Field.CONTENT,
					Field.ENTRY_CLASS_NAME, Field.ENTRY_CLASS_PK,
					Field.GROUP_ID, Field.MODIFIED_DATE, Field.SCOPE_GROUP_ID,
					Field.TITLE, Field.UID);
				modelSearchDefinition.setModelIndexWriteContributor(
					modelIndexWriterContributor);
				modelSearchDefinition.setModelDocumentContributors(
					modelDocumentContributors);
				modelSearchDefinition.setModelVisibilityContributor(
					modelVisibilityContributor);
				modelSearchDefinition.setModelSummaryContributor(
					modelSummaryContributor);
			});
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	protected void addModelDocumentContributor(
		ModelDocumentContributor modelDocumentContributor) {

		modelDocumentContributors.add(modelDocumentContributor);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	protected void removeModelDocumentContributor(
		ModelDocumentContributor modelDocumentContributor) {

		modelDocumentContributors.remove(modelDocumentContributor);
	}

	protected List<ModelDocumentContributor> modelDocumentContributors =
		new ArrayList<>();

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	protected ModelIndexerWriterContributor<DLFileEntry>
		modelIndexWriterContributor;

	@Reference
	protected ModelSearchRegistrarHelper modelSearchRegistrarHelper;

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	protected ModelSummaryContributor modelSummaryContributor;

	@Reference(
		target = "(indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry)"
	)
	protected ModelVisibilityContributor modelVisibilityContributor;

	private ServiceRegistration<?> _serviceRegistration;

}