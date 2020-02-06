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

import classNames from 'classnames';
import React, {useContext} from 'react';

import {LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS} from '../../config/constants/layoutDataFloatingToolbarButtons';
import {LAYOUT_DATA_ITEM_TYPES} from '../../config/constants/layoutDataItemTypes';
import {ConfigContext} from '../../config/index';
import selectShowLayoutItemTopper from '../../selectors/selectShowLayoutItemTopper';
import {useDispatch, useSelector} from '../../store/index';
import duplicateItem from '../../thunks/duplicateItem';
import {useSelectItem} from '../Controls';
import Topper from '../Topper';
import FloatingToolbar from '../floating-toolbar/FloatingToolbar';
import Container from './Container';

const ContainerWithControls = React.forwardRef(
	({children, item, layoutData}, ref) => {
		const config = useContext(ConfigContext);
		const dispatch = useDispatch();
		const segmentsExperienceId = useSelector(
			state => state.segmentsExperienceId
		);
		const selectItem = useSelectItem();
		const showLayoutItemTopper = useSelector(selectShowLayoutItemTopper);

		const handleButtonClick = id => {
			if (id === LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.duplicateItem.id) {
				dispatch(
					duplicateItem({
						config,
						itemId: item.itemId,
						selectItem,
						store: {segmentsExperienceId}
					})
				);
			}
		};

		const content = (
			<Container
				className={classNames(
					'container-fluid page-editor__container',
					{
						empty: !item.children.length
					}
				)}
				item={item}
				ref={ref}
			>
				<FloatingToolbar
					buttons={[
						LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.duplicateItem,
						LAYOUT_DATA_FLOATING_TOOLBAR_BUTTONS.containerConfiguration
					]}
					item={item}
					itemRef={ref}
					onButtonClick={handleButtonClick}
				/>

				{children}
			</Container>
		);

		return showLayoutItemTopper ? (
			<Topper
				acceptDrop={[
					LAYOUT_DATA_ITEM_TYPES.dropZone,
					LAYOUT_DATA_ITEM_TYPES.container,
					LAYOUT_DATA_ITEM_TYPES.fragment,
					LAYOUT_DATA_ITEM_TYPES.row
				]}
				dropNestedAndSibling
				item={item}
				layoutData={layoutData}
			>
				{() => content}
			</Topper>
		) : (
			content
		);
	}
);

export default ContainerWithControls;
