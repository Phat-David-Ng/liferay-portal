/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.search.experiences.blueprint.parameter;

import com.liferay.petra.string.StringBundler;


import com.liferay.search.experiences.blueprint.parameter.exception.SXPParameterException;

import java.util.Date;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class DateSXPParameter extends BaseSXPParameter {

	public DateSXPParameter(String name, boolean templateVariable, Date value) {
		super(name, templateVariable);

		_value = value;
	}

	@Override
	public boolean accept(EvaluationVisitor evaluationVisitor)
		throws SXPParameterException {

		return evaluationVisitor.visit(this);
	}

	@Override
	public String accept(ToStringVisitor toStringVisitor, Map<String, String> options)
		throws Exception {

		return toStringVisitor.visit(this, options);
	}

	@Override
	public Date getValue() {
		return _value;
	}

	private final Date _value;

}