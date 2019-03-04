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

package com.liferay.headless.foundation.internal.resource.v1_0;

import com.liferay.headless.foundation.dto.v1_0.Segment;
import com.liferay.headless.foundation.resource.v1_0.SegmentResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.segments.context.Context;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryService;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/segment.properties",
	scope = ServiceScope.PROTOTYPE, service = SegmentResource.class
)
public class SegmentResourceImpl extends BaseSegmentResourceImpl {

	@Override
	public Page<Segment> getUserSegmentsPage(Long userId, Pagination pagination)
		throws Exception {

		User user = _userService.getUserById(userId);

		MultivaluedMap<String, String> requestHeaders =
			_httpHeaders.getRequestHeaders();

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			user.getModelClassName(), user.getPrimaryKey(),
			_create(requestHeaders));

		List<SegmentsEntry> segmentsEntries = new ArrayList<>(
			segmentsEntryIds.length);

		for (long segmentEntryId : segmentsEntryIds) {
			SegmentsEntry segmentsEntry =
				_segmentsEntryService.getSegmentsEntry(segmentEntryId);

			segmentsEntries.add(segmentsEntry);
		}

		return Page.of(
			transform(segmentsEntries, this::_toSegment), pagination,
			segmentsEntries.size());
	}

	private Context _create(MultivaluedMap<String, String> requestHeaders) {
		Context context = new Context();

		Set<Map.Entry<String, List<String>>> requestHeadersEntrySet =
			requestHeaders.entrySet();

		for (Map.Entry<String, List<String>> header : requestHeadersEntrySet) {
			List<String> values = header.getValue();

			String contextValue = values.get(0);

			String key = StringUtil.toLowerCase(header.getKey());

			if (key.startsWith("x-")) {
				String contextKey = key.replace("x-", "");

				String contextKeyCamelCase = CamelCaseUtil.toCamelCase(
					contextKey);

				context.put(contextKeyCamelCase, contextValue);
			}
			else if (key.equals("accept-language")) {
				String value = contextValue.replace("-", "_");

				context.put(Context.LANGUAGE_ID, value);
			}
			else if (key.equals("host")) {
				context.put(Context.URL, contextValue);
			}
			else if (key.equals("user-agent")) {
				context.put(Context.USER_AGENT, contextValue);
			}
			else {
				context.put(key, contextValue);
			}
		}

		context.put(Context.LOCAL_DATE, LocalDate.from(ZonedDateTime.now()));

		return context;
	}

	private Segment _toSegment(SegmentsEntry segmentsEntry) {
		return new Segment() {
			{
				active = segmentsEntry.isActive();
				criteria = segmentsEntry.getCriteria();
				dateCreated = segmentsEntry.getCreateDate();
				dateModified = segmentsEntry.getModifiedDate();
				id = segmentsEntry.getSegmentsEntryId();
				name = segmentsEntry.getName(
					segmentsEntry.getDefaultLanguageId());
				source = segmentsEntry.getSource();
			}
		};
	}

	@javax.ws.rs.core.Context
	private HttpHeaders _httpHeaders;

	@Reference
	private SegmentsEntryProvider _segmentsEntryProvider;

	@Reference
	private SegmentsEntryService _segmentsEntryService;

	@Reference
	private UserService _userService;

}