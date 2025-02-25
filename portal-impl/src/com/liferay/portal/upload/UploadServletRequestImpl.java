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

package com.liferay.portal.upload;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.ByteArrayFileInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.upload.UploadServletRequestConfigurationHelperUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author Brian Wing Shun Chan
 * @author Zongliang Li
 * @author Harry Mark
 * @author Raymond Augé
 */
public class UploadServletRequestImpl
	extends HttpServletRequestWrapper implements UploadServletRequest {

	public static File getTempDir() {
		return _getTempDir(null);
	}

	public static void setTempDir(File tempDir) {
		_tempDir = tempDir;
	}

	public UploadServletRequestImpl(HttpServletRequest httpServletRequest) {
		this(httpServletRequest, 0, null, 0, 0);
	}

	public UploadServletRequestImpl(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location, long maxRequestSize, long maxFileSize) {

		super(httpServletRequest);

		_fileParameters = new LinkedHashMap<>();
		_regularParameters = new LinkedHashMap<>();

		LiferayServletRequest liferayServletRequest = null;

		try {
			HttpSession httpSession = httpServletRequest.getSession();

			httpSession.removeAttribute(ProgressTracker.PERCENT);

			ServletFileUpload servletFileUpload;

			if (fileSizeThreshold > 0) {
				servletFileUpload = new ServletFileUpload(
					new LiferayFileItemFactory(
						_getTempDir(location), fileSizeThreshold));
			}
			else {
				servletFileUpload = new ServletFileUpload(
					new LiferayFileItemFactory(getTempDir()));
			}

			long uploadServletRequestImplMaxSize =
				UploadServletRequestConfigurationHelperUtil.getMaxSize();

			if (maxRequestSize > 0) {
				servletFileUpload.setSizeMax(maxRequestSize);
			}
			else {
				servletFileUpload.setSizeMax(uploadServletRequestImplMaxSize);
			}

			if (maxFileSize > 0) {
				servletFileUpload.setFileSizeMax(maxFileSize);
			}
			else {
				servletFileUpload.setFileSizeMax(
					uploadServletRequestImplMaxSize);
			}

			liferayServletRequest = new LiferayServletRequest(
				httpServletRequest);

			List<org.apache.commons.fileupload.FileItem> fileItems =
				servletFileUpload.parseRequest(liferayServletRequest);

			liferayServletRequest.setFinishedReadingOriginalStream(true);

			long uploadServletRequestImplSize = 0;

			int contentLength = httpServletRequest.getContentLength();

			if ((uploadServletRequestImplMaxSize > 0) &&
				((contentLength == -1) ||
				 (contentLength > uploadServletRequestImplMaxSize))) {

				fileItems = sort(fileItems);
			}

			for (org.apache.commons.fileupload.FileItem fileItem : fileItems) {
				LiferayFileItem liferayFileItem = (LiferayFileItem)fileItem;

				if (uploadServletRequestImplMaxSize > 0) {
					long itemSize = liferayFileItem.getSize();

					if ((uploadServletRequestImplSize + itemSize) >
							uploadServletRequestImplMaxSize) {

						UploadException uploadException = new UploadException(
							StringBundler.concat(
								"Request reached the maximum permitted size ",
								"of ", uploadServletRequestImplMaxSize,
								" bytes"));

						uploadException.setExceededUploadRequestSizeLimit(true);

						httpServletRequest.setAttribute(
							WebKeys.UPLOAD_EXCEPTION, uploadException);

						continue;
					}

					uploadServletRequestImplSize += itemSize;
				}

				if (liferayFileItem.isFormField()) {
					liferayFileItem.setString(
						httpServletRequest.getCharacterEncoding());

					String fieldName = liferayFileItem.getFieldName();

					if (!_regularParameters.containsKey(fieldName)) {
						_regularParameters.put(
							fieldName, new ArrayList<String>());
					}

					List<String> values = _regularParameters.get(fieldName);

					if (liferayFileItem.getSize() >
							LiferayFileItem.THRESHOLD_SIZE) {

						UploadException uploadException = new UploadException(
							StringBundler.concat(
								"The field ", fieldName,
								" exceeds its maximum permitted size of ",
								LiferayFileItem.THRESHOLD_SIZE, " bytes"));

						uploadException.setExceededLiferayFileItemSizeLimit(
							true);

						httpServletRequest.setAttribute(
							WebKeys.UPLOAD_EXCEPTION, uploadException);
					}

					values.add(liferayFileItem.getEncodedString());

					continue;
				}

				FileItem[] liferayFileItems = _fileParameters.get(
					liferayFileItem.getFieldName());

				if (liferayFileItems == null) {
					liferayFileItems = new LiferayFileItem[] {liferayFileItem};
				}
				else {
					LiferayFileItem[] newLiferayFileItems =
						new LiferayFileItem[liferayFileItems.length + 1];

					System.arraycopy(
						liferayFileItems, 0, newLiferayFileItems, 0,
						liferayFileItems.length);

					newLiferayFileItems[newLiferayFileItems.length - 1] =
						liferayFileItem;

					liferayFileItems = newLiferayFileItems;
				}

				_fileParameters.put(
					liferayFileItem.getFieldName(), liferayFileItems);
			}
		}
		catch (Exception exception) {
			UploadException uploadException = new UploadException(exception);

			if (exception instanceof
					FileUploadBase.FileSizeLimitExceededException) {

				uploadException.setExceededFileSizeLimit(true);
			}
			else if (exception instanceof
						FileUploadBase.SizeLimitExceededException) {

				uploadException.setExceededUploadRequestSizeLimit(true);
			}

			httpServletRequest.setAttribute(
				WebKeys.UPLOAD_EXCEPTION, uploadException);

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse upload request: " +
						exception.getMessage());
			}
		}

		_liferayServletRequest = liferayServletRequest;
	}

	public UploadServletRequestImpl(
		HttpServletRequest httpServletRequest,
		Map<String, FileItem[]> fileParameters,
		Map<String, List<String>> regularParameters) {

		super(httpServletRequest);

		_fileParameters = new LinkedHashMap<>();
		_regularParameters = new LinkedHashMap<>();

		if (fileParameters != null) {
			_fileParameters.putAll(fileParameters);
		}

		if (regularParameters != null) {
			_regularParameters.putAll(regularParameters);
		}

		_liferayServletRequest = null;
	}

	@Override
	public void cleanUp() {
		if ((_fileParameters != null) && !_fileParameters.isEmpty()) {
			for (FileItem[] liferayFileItems : _fileParameters.values()) {
				for (FileItem liferayFileItem : liferayFileItems) {
					liferayFileItem.delete();
				}
			}
		}

		if (_liferayServletRequest != null) {
			_liferayServletRequest.cleanUp();
		}
	}

	@Override
	public String getContentType(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getContentType();
		}

		return null;
	}

	@Override
	public File getFile(String name) {
		return getFile(name, false);
	}

	@Override
	public File getFile(String name, boolean forceCreate) {
		if (getFileName(name) == null) {
			return null;
		}

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isEmpty(liferayFileItems)) {
			return null;
		}

		FileItem liferayFileItem = liferayFileItems[0];

		long size = liferayFileItem.getSize();

		if ((size > 0) && (size <= liferayFileItem.getSizeThreshold())) {
			forceCreate = true;
		}

		File file = liferayFileItem.getStoreLocation();

		if (liferayFileItem.isInMemory() && forceCreate) {
			try {
				FileUtil.write(file, liferayFileItem.getInputStream());
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to write temporary file " +
							file.getAbsolutePath(),
						ioException);
				}
			}
		}

		return file;
	}

	@Override
	public InputStream getFileAsStream(String name) throws IOException {
		return getFileAsStream(name, true);
	}

	@Override
	public InputStream getFileAsStream(String name, boolean deleteOnClose)
		throws IOException {

		if (getFileName(name) == null) {
			return null;
		}

		InputStream inputStream = null;

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			inputStream = getInputStream(liferayFileItem, deleteOnClose);
		}

		return inputStream;
	}

	@Override
	public String getFileName(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getFileName();
		}

		return null;
	}

	@Override
	public String[] getFileNames(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			String[] fileNames = new String[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				fileNames[i] = liferayFileItem.getFileName();
			}

			return fileNames;
		}

		return null;
	}

	@Override
	public File[] getFiles(String name) {
		String[] fileNames = getFileNames(name);

		if (fileNames == null) {
			return null;
		}

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			File[] files = new File[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				if (Validator.isNotNull(liferayFileItem.getFileName())) {
					files[i] = liferayFileItem.getStoreLocation();
				}
			}

			return files;
		}

		return null;
	}

	@Override
	public InputStream[] getFilesAsStream(String name) throws IOException {
		return getFilesAsStream(name, true);
	}

	@Override
	public InputStream[] getFilesAsStream(String name, boolean deleteOnClose)
		throws IOException {

		String[] fileNames = getFileNames(name);

		if (fileNames == null) {
			return null;
		}

		InputStream[] inputStreams = null;

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			inputStreams = new InputStream[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				if (Validator.isNotNull(liferayFileItem.getFileName())) {
					inputStreams[i] = getInputStream(
						liferayFileItem, deleteOnClose);
				}
			}
		}

		return inputStreams;
	}

	@Override
	public String getFullFileName(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getFullFileName();
		}

		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (_liferayServletRequest != null) {
			return _liferayServletRequest.getInputStream();
		}

		return super.getInputStream();
	}

	@Override
	public Map<String, FileItem[]> getMultipartParameterMap() {
		return _fileParameters;
	}

	@Override
	public String getParameter(String name) {
		List<String> values = _regularParameters.get(name);

		if ((values != null) && !values.isEmpty()) {
			return values.get(0);
		}

		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<>();

		Enumeration<String> enumeration = getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String[] values = getParameterValues(name);

			if (values != null) {
				map.put(name, values);
			}
		}

		return map;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Set<String> parameterNames = new LinkedHashSet<>();

		Enumeration<String> enumeration = super.getParameterNames();

		while (enumeration.hasMoreElements()) {
			parameterNames.add(enumeration.nextElement());
		}

		parameterNames.addAll(_regularParameters.keySet());
		parameterNames.addAll(_fileParameters.keySet());

		return Collections.enumeration(parameterNames);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] parameterValues = null;

		List<String> values = _regularParameters.get(name);

		if (values != null) {
			parameterValues = values.toArray(new String[0]);
		}

		String[] parentParameterValues = super.getParameterValues(name);

		if (parameterValues == null) {
			return parentParameterValues;
		}
		else if (parentParameterValues == null) {
			return parameterValues;
		}

		return ArrayUtil.append(parameterValues, parentParameterValues);
	}

	@Override
	public Map<String, List<String>> getRegularParameterMap() {
		return _regularParameters;
	}

	@Override
	public Long getSize(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return Long.valueOf(liferayFileItem.getSize());
		}

		return null;
	}

	@Override
	public Boolean isFormField(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.isFormField();
		}

		return null;
	}

	protected InputStream getInputStream(
			FileItem liferayFileItem, boolean deleteOnClose)
		throws IOException {

		InputStream inputStream = null;

		if (liferayFileItem.isInMemory() && (liferayFileItem.getSize() > 0)) {
			inputStream = liferayFileItem.getInputStream();
		}
		else if (!liferayFileItem.isInMemory()) {
			inputStream = new ByteArrayFileInputStream(
				liferayFileItem.getStoreLocation(),
				liferayFileItem.getSizeThreshold(), deleteOnClose);
		}

		return inputStream;
	}

	protected List<org.apache.commons.fileupload.FileItem> sort(
		List<org.apache.commons.fileupload.FileItem> fileItems) {

		Map<String, GroupedFileItems> groupedFileItemsMap = new HashMap<>();

		for (org.apache.commons.fileupload.FileItem fileItem : fileItems) {
			String fieldName = fileItem.getFieldName();

			GroupedFileItems groupedFileItems = groupedFileItemsMap.get(
				fieldName);

			if (groupedFileItems == null) {
				groupedFileItems = new GroupedFileItems(fieldName);

				groupedFileItemsMap.put(fieldName, groupedFileItems);
			}

			groupedFileItems.addFileItem(fileItem);
		}

		Set<GroupedFileItems> groupedFileItemsList = new TreeSet<>(
			groupedFileItemsMap.values());

		List<org.apache.commons.fileupload.FileItem> sortedFileItems =
			new ArrayList<>();

		for (GroupedFileItems groupedFileItems : groupedFileItemsList) {
			sortedFileItems.addAll(groupedFileItems.getFileItems());
		}

		return sortedFileItems;
	}

	private static File _getTempDir(String configuredTempDir) {
		if (Validator.isNotNull(configuredTempDir)) {
			return new File(configuredTempDir);
		}

		if (_tempDir == null) {
			_tempDir = new File(
				UploadServletRequestConfigurationHelperUtil.getTempDir());
		}

		return _tempDir;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadServletRequestImpl.class);

	private static File _tempDir;

	private final Map<String, FileItem[]> _fileParameters;
	private final LiferayServletRequest _liferayServletRequest;
	private final Map<String, List<String>> _regularParameters;

	private static class GroupedFileItems
		implements Comparable<GroupedFileItems> {

		public GroupedFileItems(String key) {
			_key = key;
		}

		public void addFileItem(
			org.apache.commons.fileupload.FileItem fileItem) {

			_fileItems.add(fileItem);

			_fileItemsSize += fileItem.getSize();
		}

		@Override
		public int compareTo(GroupedFileItems groupedFileItems) {
			if (groupedFileItems == null) {
				return 1;
			}

			if (equals(groupedFileItems)) {
				return 0;
			}

			if (_key.equals(groupedFileItems._key) ||
				(getFileItemsSize() >= groupedFileItems.getFileItemsSize())) {

				return 1;
			}

			return -1;
		}

		public List<org.apache.commons.fileupload.FileItem> getFileItems() {
			return _fileItems;
		}

		public int getFileItemsSize() {
			return _fileItemsSize;
		}

		private final List<org.apache.commons.fileupload.FileItem> _fileItems =
			new ArrayList<>();
		private int _fileItemsSize;
		private final String _key;

	}

}