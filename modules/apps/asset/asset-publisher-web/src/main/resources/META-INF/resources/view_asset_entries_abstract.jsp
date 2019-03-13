<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
AssetEntryResult assetEntryResult = (AssetEntryResult)request.getAttribute("view.jsp-assetEntryResult");

for (AssetEntry assetEntry : assetEntryResult.getAssetEntries()) {
	AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassNameId(assetEntry.getClassNameId());

	if (assetRendererFactory == null) {
		continue;
	}

	AssetRenderer<?> assetRenderer = null;

	try {
		assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());
	}
	catch (Exception e) {
		if (_log.isWarnEnabled()) {
			_log.warn(e, e);
		}
	}

	if ((assetRenderer == null) || !assetRenderer.isDisplayable()) {
		continue;
	}

	request.setAttribute("view.jsp-assetEntry", assetEntry);
	request.setAttribute("view.jsp-assetRenderer", assetRenderer);

	try {
		String title = assetRenderer.getTitle(locale);

		String viewURL = assetPublisherHelper.getAssetViewURL(liferayPortletRequest, liferayPortletResponse, assetRenderer, assetEntry, assetPublisherDisplayContext.isAssetLinkBehaviorViewInPortlet());
%>

		<div class="asset-abstract <%= assetPublisherWebUtil.isDefaultAssetPublisher(layout, portletDisplay.getId(), assetPublisherDisplayContext.getPortletResource()) ? "default-asset-publisher" : StringPool.BLANK %>">
			<span class="asset-anchor lfr-asset-anchor" id="<%= assetEntry.getEntryId() %>"></span>

			<div class="autofit-row autofit-row-center mb-3">
				<div class="autofit-col">
					<h4 class="asset-title component-title">
						<a class="h2" href="<%= viewURL %>">
							<%= HtmlUtil.escape(title) %>
						</a>
					</h4>
				</div>

				<div class="autofit-col autofit-col-end inline-item-after">
					<liferay-util:include page="/asset_actions.jsp" servletContext="<%= application %>" />
				</div>
			</div>

			<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() || (assetPublisherDisplayContext.isShowCreateDate() && (assetEntry.getCreateDate() != null)) || (assetPublisherDisplayContext.isShowPublishDate() && (assetEntry.getPublishDate() != null)) || (assetPublisherDisplayContext.isShowExpirationDate() && (assetEntry.getExpirationDate() != null)) || (assetPublisherDisplayContext.isShowModifiedDate() && (assetEntry.getModifiedDate() != null)) || assetPublisherDisplayContext.isShowViewCount() %>">

				<%
				User assetRendererUser = UserLocalServiceUtil.getUser(assetRenderer.getUserId());
				%>

				<div class="autofit-row mb-3 metadata-author">
					<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() %>">
						<div class="asset-avatar autofit-col inline-item-before mr-3 pt-1">
							<liferay-ui:user-portrait
								cssClass="sticker-lg"
								user="<%= assetRendererUser %>"
							/>
						</div>
					</c:if>

					<div class="autofit-col autofit-col-expand">
						<div class="autofit-row">
							<div class="autofit-col autofit-col-expand">
								<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() %>">
									<div class="text-truncate-inline">
										<span class="text-truncate user-info"><strong><%= HtmlUtil.escape(assetRendererUser.getFullName()) %></strong></span>
									</div>
								</c:if>

								<%
								StringBundler sb = new StringBundler(13);

								if (assetPublisherDisplayContext.isShowCreateDate() && (assetEntry.getCreateDate() != null)) {
									sb.append(LanguageUtil.get(request, "created"));
									sb.append(StringPool.SPACE);
									sb.append(dateFormatDate.format(assetEntry.getCreateDate()));
									sb.append(" - ");
								}

								if (assetPublisherDisplayContext.isShowPublishDate() && (assetEntry.getPublishDate() != null)) {
									sb.append(LanguageUtil.get(request, "published"));
									sb.append(StringPool.SPACE);
									sb.append(dateFormatDate.format(assetEntry.getPublishDate()));
									sb.append(" - ");
								}

								if (assetPublisherDisplayContext.isShowExpirationDate() && (assetEntry.getExpirationDate() != null)) {
									sb.append(LanguageUtil.get(request, "expired"));
									sb.append(StringPool.SPACE);
									sb.append(dateFormatDate.format(assetEntry.getExpirationDate()));
									sb.append(" - ");
								}

								if (assetPublisherDisplayContext.isShowModifiedDate() && (assetEntry.getModifiedDate() != null)) {
									Date modifiedDate = assetEntry.getModifiedDate();

									String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true);

									sb.append(LanguageUtil.format(request, "modified-x-ago", modifiedDateDescription));
								}
								else if (sb.index() > 1) {
									sb.setIndex(sb.index() - 1);
								}
								%>

								<div class="asset-user-info text-secondary">
									<span class="date-info"><%= sb.toString() %></span>
								</div>

								<c:if test="<%= assetPublisherDisplayContext.isShowViewCount() %>">
									<div class="asset-view-count-info text-secondary">
										<span class="view-count-info"><%= assetEntry.getViewCount() %> <liferay-ui:message key='<%= (assetEntry.getViewCount() == 1) ? "view" : "views" %>' /></span>
									</div>
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</c:if>

			<div class="asset-content mb-3">
				<liferay-asset:asset-display
					abstractLength="<%= assetPublisherDisplayContext.getAbstractLength() %>"
					assetEntry="<%= assetEntry %>"
					assetRenderer="<%= assetRenderer %>"
					assetRendererFactory="<%= assetRendererFactory %>"
					template="<%= AssetRenderer.TEMPLATE_ABSTRACT %>"
					viewURL="<%= viewURL %>"
				/>
			</div>

			<c:if test="<%= assetPublisherDisplayContext.isShowCategories() %>">
				<div class="asset-categories mb-3">
					<liferay-asset:asset-categories-summary
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						displayStyle="simple-category"
						portletURL="<%= renderResponse.createRenderURL() %>"
					/>
				</div>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isShowTags() %>">
				<div class="asset-tags mb-3">
					<liferay-asset:asset-tags-summary
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						portletURL="<%= renderResponse.createRenderURL() %>"
					/>
				</div>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isShowPriority() %>">
				<div class="asset-priority mb-3 text-secondary">
					<liferay-ui:message key="priority" />: <%= assetEntry.getPriority() %>
				</div>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isEnableRelatedAssets() %>">

				<%
				PortletURL assetLingsURL = renderResponse.createRenderURL();

				assetLingsURL.setParameter("mvcPath", "/view_content.jsp");
				%>

				<liferay-asset:asset-links
					assetEntryId="<%= assetEntry.getEntryId() %>"
					portletURL="<%= assetLingsURL %>"
					viewInContext="<%= assetPublisherDisplayContext.isAssetLinkBehaviorViewInPortlet() %>"
				/>
			</c:if>

			<c:if test="<%= (assetPublisherDisplayContext.isEnableRatings() && assetRenderer.isRatable()) || assetPublisherDisplayContext.isEnableFlags() || assetPublisherDisplayContext.isEnablePrint() || (assetPublisherDisplayContext.isShowAvailableLocales() && assetRenderer.isLocalizable()) || (assetPublisherDisplayContext.isEnableConversions() && assetRenderer.isConvertible()) %>">
				<div class="separator"><!-- --></div>

				<div class="asset-details autofit-row autofit-row-center">
					<c:if test="<%= assetPublisherDisplayContext.isEnableRatings() && assetRenderer.isRatable() %>">
						<div class="asset-ratings autofit-col mr-3">
							<liferay-ui:ratings
								className="<%= assetEntry.getClassName() %>"
								classPK="<%= assetEntry.getClassPK() %>"
							/>
						</div>
					</c:if>

					<c:if test="<%= assetPublisherDisplayContext.isEnableFlags() %>">
						<div class="asset-flag autofit-col mr-3">

							<%
							TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(assetRenderer.getClassName());

							boolean inTrash = trashHandler.isInTrash(assetEntry.getClassPK());
							%>

							<liferay-flags:flags
								className="<%= assetEntry.getClassName() %>"
								classPK="<%= assetEntry.getClassPK() %>"
								contentTitle="<%= title %>"
								enabled="<%= !inTrash %>"
								label="<%= false %>"
								message='<%= inTrash ? "flags-are-disabled-because-this-entry-is-in-the-recycle-bin" : null %>'
								reportedUserId="<%= assetRenderer.getUserId() %>"
							/>
						</div>
					</c:if>

					<c:if test="<%= assetPublisherDisplayContext.isEnablePrint() %>">
						<div class="autofit-col component-subtitle mr-3 print-action">

							<%
							PortletURL printAssetURL = renderResponse.createRenderURL();

							printAssetURL.setParameter("mvcPath", "/view_content.jsp");
							printAssetURL.setParameter("assetEntryId", String.valueOf(assetEntry.getEntryId()));
							printAssetURL.setParameter("viewMode", Constants.PRINT);
							printAssetURL.setParameter("type", assetRendererFactory.getType());
							printAssetURL.setParameter("languageId", LanguageUtil.getLanguageId(request));

							String urlTitle = assetRenderer.getUrlTitle(locale);

							if (Validator.isNotNull(urlTitle)) {
								if (assetRenderer.getGroupId() != scopeGroupId) {
									printAssetURL.setParameter("groupId", String.valueOf(assetRenderer.getGroupId()));
								}

								printAssetURL.setParameter("urlTitle", urlTitle);
							}

							printAssetURL.setWindowState(LiferayWindowState.POP_UP);

							String id = assetEntry.getEntryId() + StringUtil.randomId();
							%>

							<liferay-ui:icon
								icon="print"
								markupView="lexicon"
								message='<%= LanguageUtil.format(request, "print-x-x", new Object[] {"hide-accessible", HtmlUtil.escape(title)}, false) %>'
								url='<%= "javascript:" + renderResponse.getNamespace() + "printPage_" + id + "();" %>'
							/>

							<aui:script>
								function <portlet:namespace />printPage_<%= id %>() {
									window.open('<%= printAssetURL %>', '', 'directories=0,height=480,left=80,location=1,menubar=1,resizable=1,scrollbars=yes,status=0,toolbar=0,top=180,width=640');
								}
							</aui:script>
						</div>
					</c:if>

					<c:if test="<%= assetPublisherDisplayContext.isShowAvailableLocales() && assetRenderer.isLocalizable() %>">

						<%
						String[] availableLanguageIds = assetRenderer.getAvailableLanguageIds();
						%>

						<c:if test="<%= availableLanguageIds.length > 1 %>">
							<div class="autofit-col locale-actions mr-3">
								<liferay-ui:language
									formAction="<%= currentURL %>"
									languageId="<%= LanguageUtil.getLanguageId(request) %>"
									languageIds="<%= availableLanguageIds %>"
								/>
							</div>
						</c:if>
					</c:if>

					<c:if test="<%= assetPublisherDisplayContext.isEnableConversions() && assetRenderer.isConvertible() %>">

						<%
						PortletURL exportAssetURL = assetRenderer.getURLExport(liferayPortletRequest, liferayPortletResponse);

						exportAssetURL.setParameter("plid", String.valueOf(themeDisplay.getPlid()));
						exportAssetURL.setParameter("portletResource", portletDisplay.getId());
						exportAssetURL.setWindowState(LiferayWindowState.EXCLUSIVE);

						for (String extension : assetPublisherDisplayContext.getExtensions(assetRenderer)) {
							Map<String, Object> data = new HashMap<>();

							exportAssetURL.setParameter("targetExtension", extension);

							data.put("resource-href", exportAssetURL.toString());
						%>

							<div class="autofit-col component-subtitle export-action mr-3">
								<aui:a data="<%= data %>" href="<%= exportAssetURL.toString() %>" label='<%= LanguageUtil.format(request, "x-convert-x-to-x", new Object[] {"hide-accessible", title, StringUtil.toUpperCase(HtmlUtil.escape(extension))}, false) %>' />
							</div>

						<%
						}
						%>

					</c:if>
				</div>
			</c:if>

			<c:if test="<%= Validator.isNotNull(assetPublisherDisplayContext.getSocialBookmarksTypes()) %>">
				<div class="separator"><!-- --></div>

				<%
				PortletURL viewFullContentURL = renderResponse.createRenderURL();

				viewFullContentURL.setParameter("mvcPath", "/view_content.jsp");
				viewFullContentURL.setParameter("type", assetRendererFactory.getType());

				if (Validator.isNotNull(assetRenderer.getUrlTitle())) {
					if (assetRenderer.getGroupId() != scopeGroupId) {
						viewFullContentURL.setParameter("groupId", String.valueOf(assetRenderer.getGroupId()));
					}

					viewFullContentURL.setParameter("urlTitle", assetRenderer.getUrlTitle());
				}
				%>

				<liferay-social-bookmarks:bookmarks
					className="<%= assetEntry.getClassName() %>"
					classPK="<%= assetEntry.getClassPK() %>"
					displayStyle="<%= assetPublisherDisplayContext.getSocialBookmarksDisplayStyle() %>"
					target="_blank"
					title="<%= title %>"
					types="<%= assetPublisherDisplayContext.getSocialBookmarksTypes() %>"
					urlImpl="<%= viewFullContentURL %>"
				/>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isEnableComments() && assetRenderer.isCommentable() %>">
				<div class="col-md-12">
					<liferay-comment:discussion
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						formName='<%= "fm" + assetEntry.getClassPK() %>'
						ratingsEnabled="<%= assetPublisherDisplayContext.isEnableCommentRatings() %>"
						redirect="<%= currentURL %>"
						userId="<%= assetRenderer.getUserId() %>"
					/>
				</div>
			</c:if>
		</div>

		<div class="separator"><!-- --></div>

<%
	}
	catch (Exception e) {
		_log.error(e.getMessage());
	}
}
%>

<%!
private static Log _log = LogFactoryUtil.getLog("com_liferay_asset_publisher_web.view_asset_entries_abstract_jsp");
%>