package com.liferay.journal.search.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.test.util.HitsAssert;

import java.util.Locale;
import java.util.Objects;

public class PluginPackageIndexerFixture {

	public PluginPackageIndexerFixture(Indexer<PluginPackage> indexer) {
		_indexer = indexer;
	}

	public void deleteDocument(Document document)
		throws PortalException, SearchException {

		IndexWriterHelperUtil.deleteDocument(
			_indexer.getSearchEngineId(), CompanyConstants.SYSTEM,
			document.getUID(), true);
	}

	public SearchContext getSearchContext(String keywords, Locale locale)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(CompanyConstants.SYSTEM);
		searchContext.setKeywords(keywords);
		searchContext.setLocale(Objects.requireNonNull(locale));

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	public void searchNoOne(String keywords, Locale locale) throws Exception {
		HitsAssert.assertNoHits(search(getSearchContext(keywords, locale)));
	}

	public Document searchOnlyOne(String keywords) throws Exception {
		return searchOnlyOne(keywords, Locale.US);
	}

	public Document searchOnlyOne(String keywords, Locale locale)
		throws Exception {

		SearchContext searchContext = getSearchContext(keywords, locale);

		Hits search = search(searchContext);

		return HitsAssert.assertOnlyOne(search);
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	protected void reindex(long companyId) throws Exception {
		String[] ids = {String.valueOf(companyId)};

		_indexer.reindex(ids);
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		return _indexer.search(searchContext);
	}

	private final Indexer<PluginPackage> _indexer;
}
