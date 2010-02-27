/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.documentlibrary.service.persistence;

/**
 * <a href="DLFolderFinder.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public interface DLFolderFinder {
	public int countFE_FS_ByG_F_S(long groupId, java.util.List<Long> folderIds,
		int status) throws com.liferay.portal.kernel.exception.SystemException;

	public int countF_FE_FS_ByG_F_S(long groupId,
		java.util.List<Long> folderIds, int status)
		throws com.liferay.portal.kernel.exception.SystemException;

	public java.util.List<Object> findFE_FS_ByG_F_S(long groupId,
		java.util.List<Long> folderIds, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;

	public java.util.List<Object> findF_FE_FS_ByG_F_S(long groupId,
		java.util.List<Long> folderIds, int status, int start, int end)
		throws com.liferay.portal.kernel.exception.SystemException;
}