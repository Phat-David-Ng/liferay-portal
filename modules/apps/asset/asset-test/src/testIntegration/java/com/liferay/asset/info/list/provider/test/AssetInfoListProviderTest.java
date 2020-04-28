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

package com.liferay.asset.info.list.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.list.provider.DefaultInfoListProviderContext;
import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.list.provider.InfoListProviderTracker;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Accessor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.kernel.service.RatingsEntryLocalService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pavel Savinov
 */
@RunWith(Arquillian.class)
public class AssetInfoListProviderTest {

	public static final Accessor<AssetEntry, Long> CLASS_PK_ACCESSOR =
		new Accessor<AssetEntry, Long>() {

			@Override
			public Long get(AssetEntry assetEntry) {
				return assetEntry.getClassPK();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<AssetEntry> getTypeClass() {
				return AssetEntry.class;
			}

		};

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_infoListProviderContext = new DefaultInfoListProviderContext(
			_group, TestPropsValues.getUser());
	}

	@Test
	public void testHighestRatedAssetsInfoListProvider() throws Exception {
		InfoListProvider<AssetEntry> infoListProvider =
			_infoListProviderTracker.getInfoListProvider(
				_HIGHEST_RATES_ASSETS_INFO_LIST_PROVIDER_KEY);

		JournalArticle article1 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle article2 = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), JournalArticle.class.getName(),
			article2.getResourcePrimKey(), 1, serviceContext);

		List<AssetEntry> assetEntries = infoListProvider.getInfoList(
			_infoListProviderContext);

		int assetEntriesCount = infoListProvider.getInfoListCount(
			_infoListProviderContext);

		Assert.assertEquals(2, assetEntriesCount);

		Assert.assertEquals(
			Long.valueOf(article2.getResourcePrimKey()),
			CLASS_PK_ACCESSOR.get(assetEntries.get(0)));

		Assert.assertEquals(
			Long.valueOf(article1.getResourcePrimKey()),
			CLASS_PK_ACCESSOR.get(assetEntries.get(1)));

		_ratingsEntryLocalService.deleteEntry(
			TestPropsValues.getUserId(), JournalArticle.class.getName(),
			article2.getResourcePrimKey());

		_ratingsEntryLocalService.updateEntry(
			TestPropsValues.getUserId(), JournalArticle.class.getName(),
			article1.getResourcePrimKey(), 1, serviceContext);

		assetEntries = infoListProvider.getInfoList(_infoListProviderContext);

		Assert.assertEquals(
			Long.valueOf(article1.getResourcePrimKey()),
			CLASS_PK_ACCESSOR.get(assetEntries.get(0)));

		Assert.assertEquals(
			Long.valueOf(article2.getResourcePrimKey()),
			CLASS_PK_ACCESSOR.get(assetEntries.get(1)));
	}

	private static final String _HIGHEST_RATES_ASSETS_INFO_LIST_PROVIDER_KEY =
		"com.liferay.asset.internal.info.list.provider." +
			"HighestRatedAssetsInfoListProvider";

	@DeleteAfterTestRun
	private Group _group;

	private InfoListProviderContext _infoListProviderContext;

	@Inject
	private InfoListProviderTracker _infoListProviderTracker;

	@Inject
	private RatingsEntryLocalService _ratingsEntryLocalService;

}