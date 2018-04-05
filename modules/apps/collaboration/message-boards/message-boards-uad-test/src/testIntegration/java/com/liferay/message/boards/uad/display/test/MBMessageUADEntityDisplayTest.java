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

package com.liferay.message.boards.uad.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.uad.constants.MBUADConstants;
import com.liferay.message.boards.uad.test.MBMessageUADEntityTestHelper;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import com.liferay.user.associated.data.aggregator.UADEntityAggregator;
import com.liferay.user.associated.data.display.UADEntityDisplay;
import com.liferay.user.associated.data.test.util.BaseUADEntityDisplayTestCase;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;

import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@RunWith(Arquillian.class)
public class MBMessageUADEntityDisplayTest extends BaseUADEntityDisplayTestCase {
	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule = new LiferayIntegrationTestRule();

	@Override
	protected BaseModel<?> addBaseModel(long userId) throws Exception {
		MBMessage mbMessage = _mbMessageUADEntityTestHelper.addMBMessage(userId);

		_mbMessages.add(mbMessage);

		return mbMessage;
	}

	@Override
	protected String getApplicationName() {
		return MBUADConstants.APPLICATION_NAME;
	}

	@Override
	protected UADEntityAggregator getUADEntityAggregator() {
		return _uadEntityAggregator;
	}

	@Override
	protected UADEntityDisplay getUADEntityDisplay() {
		return _uadEntityDisplay;
	}

	@Override
	protected String getUADEntityTypeDescription() {
		return "A message board message";
	}

	@After
	public void tearDown() throws Exception {
		_mbMessageUADEntityTestHelper.cleanUpDependencies(_mbMessages);
	}

	@DeleteAfterTestRun
	private final List<MBMessage> _mbMessages = new ArrayList<MBMessage>();
	@Inject
	private MBMessageUADEntityTestHelper _mbMessageUADEntityTestHelper;
	@Inject(filter = "model.class.name=" +
	MBUADConstants.CLASS_NAME_MB_MESSAGE)
	private UADEntityAggregator _uadEntityAggregator;
	@Inject(filter = "model.class.name=" +
	MBUADConstants.CLASS_NAME_MB_MESSAGE)
	private UADEntityDisplay _uadEntityDisplay;
}