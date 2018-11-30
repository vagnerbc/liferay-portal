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

package com.liferay.journal.internal.search.spi.model.result.contributor;

import com.liferay.journal.model.JournalArticleDisplay;
import com.liferay.journal.util.JournalContent;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletRequestModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Harry Mark
 * @author Bruno Farache
 * @author Raymond Augé
 * @author Hugo Huijser
 * @author Tibor Lipusz
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalArticle",
	service = ModelSummaryContributor.class
)
public class JournalArticleModelSummaryContributor
	implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			document.get("defaultLanguageId"));

		Locale snippetLocale = getSnippetLocale(document, locale);

		String localizedTitleName = Field.getLocalizedName(locale, Field.TITLE);

		if ((snippetLocale == null) &&
			(document.getField(localizedTitleName) == null)) {

			snippetLocale = defaultLocale;
		}
		else {
			snippetLocale = locale;
		}

		String title = document.get(
			snippetLocale, Field.SNIPPET + StringPool.UNDERLINE + Field.TITLE,
			Field.TITLE);

		if (Validator.isNull(title) && !snippetLocale.equals(defaultLocale)) {
			title = document.get(
				defaultLocale,
				Field.SNIPPET + StringPool.UNDERLINE + Field.TITLE,
				Field.TITLE);
		}

		String content = StringPool.BLANK;

		Summary summary = new Summary(snippetLocale, title, content);

		summary.setMaxContentLength(200);

		return summary;
	}

	protected String getDDMContentSummary(
		Document document, Locale snippetLocale, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		String content = StringPool.BLANK;

		if ((portletRequest == null) || (portletResponse == null)) {
			return content;
		}

		try {
			String articleId = document.get(Field.ARTICLE_ID);
			long groupId = GetterUtil.getLong(document.get(Field.GROUP_ID));
			double version = GetterUtil.getDouble(document.get(Field.VERSION));
			PortletRequestModel portletRequestModel = new PortletRequestModel(
				portletRequest, portletResponse);
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			JournalArticleDisplay articleDisplay = _journalContent.getDisplay(
				groupId, articleId, version, null, Constants.VIEW,
				LocaleUtil.toLanguageId(snippetLocale), 1, portletRequestModel,
				themeDisplay);

			String description = document.get(
				snippetLocale,
				Field.SNIPPET + StringPool.UNDERLINE + Field.DESCRIPTION,
				Field.DESCRIPTION);

			if (Validator.isNull(description)) {
				content = HtmlUtil.stripHtml(articleDisplay.getDescription());
			}
			else {
				content = _stripAndHighlight(description);
			}

			content = HtmlUtil.replaceNewLine(content);

			if (Validator.isNull(content)) {
				content = HtmlUtil.extractText(articleDisplay.getContent());
			}

			String snippet = document.get(
				snippetLocale,
				Field.SNIPPET + StringPool.UNDERLINE + Field.CONTENT);

			Set<String> highlights = new HashSet<>();

			HighlightUtil.addSnippet(document, highlights, snippet, "temp");

			content = HighlightUtil.highlight(
				content, ArrayUtil.toStringArray(highlights),
				HighlightUtil.HIGHLIGHT_TAG_OPEN,
				HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return content;
	}

	protected Locale getSnippetLocale(Document document, Locale locale) {
		String prefix = Field.SNIPPET + StringPool.UNDERLINE;

		String localizedAssetCategoryTitlesName =
			prefix +
				Field.getLocalizedName(locale, Field.ASSET_CATEGORY_TITLES);
		String localizedContentName =
			prefix + Field.getLocalizedName(locale, Field.CONTENT);
		String localizedDescriptionName =
			prefix + Field.getLocalizedName(locale, Field.DESCRIPTION);
		String localizedTitleName =
			prefix + Field.getLocalizedName(locale, Field.TITLE);

		if ((document.getField(localizedAssetCategoryTitlesName) != null) ||
			(document.getField(localizedContentName) != null) ||
			(document.getField(localizedDescriptionName) != null) ||
			(document.getField(localizedTitleName) != null)) {

			return locale;
		}

		return null;
	}

	@Reference(unbind = "-")
	protected void setJournalContent(JournalContent journalContent) {
		_journalContent = journalContent;
	}

	private String _stripAndHighlight(String text) {
		text = StringUtil.replace(
			text, _HIGHLIGHT_TAGS, _ESCAPE_SAFE_HIGHLIGHTS);

		text = HtmlUtil.stripHtml(text);

		text = StringUtil.replace(
			text, _ESCAPE_SAFE_HIGHLIGHTS, _HIGHLIGHT_TAGS);

		return text;
	}

	private static final String[] _ESCAPE_SAFE_HIGHLIGHTS = {
		"[@HIGHLIGHT1@]", "[@HIGHLIGHT2@]"
	};

	private static final String[] _HIGHLIGHT_TAGS = {
		HighlightUtil.HIGHLIGHT_TAG_OPEN, HighlightUtil.HIGHLIGHT_TAG_CLOSE
	};

	private static final Log _log = LogFactoryUtil.getLog(
		JournalArticleModelSummaryContributor.class);

	private JournalContent _journalContent;

}