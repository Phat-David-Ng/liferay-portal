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

package com.liferay.portal.search.solr8.internal.logging;

import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.search.generic.TermQueryImpl;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.solr8.internal.SolrIndexSearcher;
import com.liferay.portal.search.solr8.internal.SolrIndexingFixture;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;
import com.liferay.portal.search.test.util.indexing.IndexingFixture;
import com.liferay.portal.search.test.util.logging.ExpectedLogTestRule;

import java.util.logging.Level;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Bryan Engler
 */
public class SolrIndexSearcherLogExceptionsOnlyTest
	extends BaseIndexingTestCase {

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearch() {
		expectedLogTestRule.expectMessage("Cannot parse '+f^eld:text'");

		search(createSearchContext(), getMalformedQuery());
	}

	@Test
	public void testExceptionOnlyLoggedWhenQueryMalformedSearchCount() {
		expectedLogTestRule.expectMessage("Cannot parse '+f^eld:text'");

		searchCount(createSearchContext(), getMalformedQuery());
	}

	@Rule
	public ExpectedLogTestRule expectedLogTestRule = ExpectedLogTestRule.with(
		SolrIndexSearcher.class, Level.WARNING);

	@Override
	protected IndexingFixture createIndexingFixture() throws Exception {
		return new SolrIndexingFixture(
			HashMapBuilder.<String, Object>put(
				"logExceptionsOnly", true
			).build());
	}

	protected Query getMalformedQuery() {
		BooleanQueryImpl booleanQueryImpl = new BooleanQueryImpl();

		booleanQueryImpl.add(
			new TermQueryImpl("f^eld", "text"), BooleanClauseOccur.MUST);

		return booleanQueryImpl;
	}

}