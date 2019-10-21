AUI.add(
	'liferay-layout-customization-settings',
	function(A) {
		var BODY = A.getBody();

		var BOUNDING_BOX = 'boundingBox';

		var EVENT_CLICK = 'click';

		var LayoutCustomizationSettings = A.Component.create({
			AUGMENTS: Liferay.PortletBase,

			EXTENDS: A.Base,

			NAME: 'layoutcustomizationsettings',

			prototype: {
				initializer(config) {
					var instance = this;

					instance._controls = instance.byId(
						'layoutCustomizableControls'
					);
					instance._manageCustomization = instance.byId(
						'manageCustomization'
					);

					instance._bindUI();
				},

				destroy(event) {
					var instance = this;

					var columns = A.all('.portlet-column');

					columns.each(function(item, index) {
						var overlayMask = item.getData('customizationControls');

						if (overlayMask) {
							overlayMask.destroy();
						}
					});

					var customizationsHandle = instance._customizationsHandle;

					if (customizationsHandle) {
						customizationsHandle.detach();

						instance._customizationsHandle = null;
					}
				},

				_bindUI() {
					var instance = this;

					var manageCustomization = instance._manageCustomization;

					if (
						manageCustomization &&
						!manageCustomization.hasClass('disabled')
					) {
						manageCustomization.on(
							EVENT_CLICK,
							instance._onManageCustomization,
							instance
						);

						var columns = A.all('.portlet-column');

						Liferay.publish('updatedLayout', {
							defaultFn(event) {
								columns.each(function(item, index) {
									var overlayMask = item.getData(
										'customizationControls'
									);

									if (overlayMask) {
										item.setData(
											'customizationControls',
											null
										);
									}
								});
							}
						});
					}
				},

				_createCustomizationMask(column) {
					var instance = this;

					var columnId = column.attr('id');

					var customizable = !!column.one(
						'.portlet-column-content.customizable'
					);

					if (customizable && !column.hasClass('customizable')) {
						column.addClass('customizable');
					}

					var cssClass = 'customizable-layout-column';

					var overlayMask = new A.OverlayMask({
						cssClass,
						target: column,
						zIndex: 10
					}).render();

					var boundingBox = overlayMask.get(BOUNDING_BOX);

					var columnDropZone = column.one('.portlet-dropzone');

					if (columnDropZone) {
						columnDropZone.insertBefore(boundingBox);

						columnDropZone.setData(
							'customizationControls',
							overlayMask
						);
					}

					if (customizable) {
						boundingBox.addClass('customizable');
					}

					var columnControls = instance._controls.clone();

					var input = columnControls.one(
						'.layout-customizable-checkbox'
					);

					if (input) {
						var oldName = input.attr('name');

						var newName = oldName.replace('[COLUMN_ID]', columnId);

						input.attr({
							checked: customizable,
							id: newName,
							name: newName
						});

						boundingBox.prepend(columnControls);

						columnControls.show();

						input.setData('customizationControls', overlayMask);
					}

					var label = columnControls.one('label');

					if (label) {
						label.attr('for', newName);
					}

					column.setData('customizationControls', overlayMask);

					return overlayMask;
				},

				_onChangeCustomization(event) {
					var instance = this;

					var checkbox = event.currentTarget;

					var overlayMask = checkbox.getData('customizationControls');

					var boundingBox = overlayMask.get(BOUNDING_BOX);

					var column = overlayMask.get('target');

					var columnDropZone = column.one('.portlet-dropzone');

					if (columnDropZone) {
						columnDropZone.toggleClass('customizable');
					}

					boundingBox.toggleClass('customizable');
					column.toggleClass('customizable');

					var data = {
						cmd: 'update_type_settings',
						doAsUserId: themeDisplay.getDoAsUserIdEncoded(),
						p_auth: Liferay.authToken,
						p_l_id: themeDisplay.getPlid(),
						p_v_l_s_g_id: themeDisplay.getSiteGroupId()
					};

					data[checkbox.attr('name')] = checkbox.attr('checked');

					A.io.request(
						themeDisplay.getPathMain() + '/portal/update_layout',
						{
							data
						}
					);
				},

				_onManageCustomization(event) {
					var instance = this;

					var customizationsHandle = instance._customizationsHandle;

					if (!customizationsHandle) {
						customizationsHandle = BODY.delegate(
							EVENT_CLICK,
							instance._onChangeCustomization,
							'.layout-customizable-checkbox',
							instance
						);

						instance._customizationsHandle = customizationsHandle;
					} else {
						customizationsHandle.detach();

						instance._customizationsHandle = null;
					}

					var columns = A.all('.portlet-column');

					columns.each(function(item, index) {
						var overlayMask = item.getData('customizationControls');

						if (!overlayMask) {
							overlayMask = instance._createCustomizationMask(
								item
							);
						}

						overlayMask.toggle();
					});
				}
			}
		});

		Liferay.LayoutCustomizationSettings = LayoutCustomizationSettings;
	},
	'',
	{
		requires: [
			'aui-base',
			'aui-io-request',
			'aui-overlay-mask-deprecated',
			'liferay-portlet-base'
		]
	}
);
