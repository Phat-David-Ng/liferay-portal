/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.service.persistence;

import com.liferay.portal.model.ModelListener;
import com.liferay.portal.util.PropsUtil;

import com.liferay.util.GetterUtil;
import com.liferay.util.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="WebsiteUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class WebsiteUtil {
	public static com.liferay.portal.model.Website create(long websiteId) {
		return getPersistence().create(websiteId);
	}

	public static com.liferay.portal.model.Website remove(long websiteId)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		ModelListener listener = _getListener();

		if (listener != null) {
			listener.onBeforeRemove(findByPrimaryKey(websiteId));
		}

		com.liferay.portal.model.Website website = getPersistence().remove(websiteId);

		if (listener != null) {
			listener.onAfterRemove(website);
		}

		return website;
	}

	public static com.liferay.portal.model.Website remove(
		com.liferay.portal.model.Website website)
		throws com.liferay.portal.SystemException {
		ModelListener listener = _getListener();

		if (listener != null) {
			listener.onBeforeRemove(website);
		}

		website = getPersistence().remove(website);

		if (listener != null) {
			listener.onAfterRemove(website);
		}

		return website;
	}

	public static com.liferay.portal.model.Website update(
		com.liferay.portal.model.Website website)
		throws com.liferay.portal.SystemException {
		ModelListener listener = _getListener();
		boolean isNew = website.isNew();

		if (listener != null) {
			if (isNew) {
				listener.onBeforeCreate(website);
			}
			else {
				listener.onBeforeUpdate(website);
			}
		}

		website = getPersistence().update(website);

		if (listener != null) {
			if (isNew) {
				listener.onAfterCreate(website);
			}
			else {
				listener.onAfterUpdate(website);
			}
		}

		return website;
	}

	public static com.liferay.portal.model.Website update(
		com.liferay.portal.model.Website website, boolean saveOrUpdate)
		throws com.liferay.portal.SystemException {
		ModelListener listener = _getListener();
		boolean isNew = website.isNew();

		if (listener != null) {
			if (isNew) {
				listener.onBeforeCreate(website);
			}
			else {
				listener.onBeforeUpdate(website);
			}
		}

		website = getPersistence().update(website, saveOrUpdate);

		if (listener != null) {
			if (isNew) {
				listener.onAfterCreate(website);
			}
			else {
				listener.onAfterUpdate(website);
			}
		}

		return website;
	}

	public static com.liferay.portal.model.Website findByPrimaryKey(
		long websiteId)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByPrimaryKey(websiteId);
	}

	public static com.liferay.portal.model.Website fetchByPrimaryKey(
		long websiteId) throws com.liferay.portal.SystemException {
		return getPersistence().fetchByPrimaryKey(websiteId);
	}

	public static java.util.List findByCompanyId(long companyId)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByCompanyId(companyId);
	}

	public static java.util.List findByCompanyId(long companyId, int begin,
		int end) throws com.liferay.portal.SystemException {
		return getPersistence().findByCompanyId(companyId, begin, end);
	}

	public static java.util.List findByCompanyId(long companyId, int begin,
		int end, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByCompanyId(companyId, begin, end, obc);
	}

	public static com.liferay.portal.model.Website findByCompanyId_First(
		long companyId, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByCompanyId_First(companyId, obc);
	}

	public static com.liferay.portal.model.Website findByCompanyId_Last(
		long companyId, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByCompanyId_Last(companyId, obc);
	}

	public static com.liferay.portal.model.Website[] findByCompanyId_PrevAndNext(
		long websiteId, long companyId,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByCompanyId_PrevAndNext(websiteId,
			companyId, obc);
	}

	public static java.util.List findByUserId(long userId)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByUserId(userId);
	}

	public static java.util.List findByUserId(long userId, int begin, int end)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByUserId(userId, begin, end);
	}

	public static java.util.List findByUserId(long userId, int begin, int end,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByUserId(userId, begin, end, obc);
	}

	public static com.liferay.portal.model.Website findByUserId_First(
		long userId, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByUserId_First(userId, obc);
	}

	public static com.liferay.portal.model.Website findByUserId_Last(
		long userId, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByUserId_Last(userId, obc);
	}

	public static com.liferay.portal.model.Website[] findByUserId_PrevAndNext(
		long websiteId, long userId,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByUserId_PrevAndNext(websiteId, userId, obc);
	}

	public static java.util.List findByC_C(long companyId, long classNameId)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C(companyId, classNameId);
	}

	public static java.util.List findByC_C(long companyId, long classNameId,
		int begin, int end) throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C(companyId, classNameId, begin, end);
	}

	public static java.util.List findByC_C(long companyId, long classNameId,
		int begin, int end, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C(companyId, classNameId, begin, end,
			obc);
	}

	public static com.liferay.portal.model.Website findByC_C_First(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_First(companyId, classNameId, obc);
	}

	public static com.liferay.portal.model.Website findByC_C_Last(
		long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_Last(companyId, classNameId, obc);
	}

	public static com.liferay.portal.model.Website[] findByC_C_PrevAndNext(
		long websiteId, long companyId, long classNameId,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_PrevAndNext(websiteId, companyId,
			classNameId, obc);
	}

	public static java.util.List findByC_C_C(long companyId, long classNameId,
		long classPK) throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C(companyId, classNameId, classPK);
	}

	public static java.util.List findByC_C_C(long companyId, long classNameId,
		long classPK, int begin, int end)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C(companyId, classNameId, classPK,
			begin, end);
	}

	public static java.util.List findByC_C_C(long companyId, long classNameId,
		long classPK, int begin, int end,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C(companyId, classNameId, classPK,
			begin, end, obc);
	}

	public static com.liferay.portal.model.Website findByC_C_C_First(
		long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_First(companyId, classNameId,
			classPK, obc);
	}

	public static com.liferay.portal.model.Website findByC_C_C_Last(
		long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_Last(companyId, classNameId,
			classPK, obc);
	}

	public static com.liferay.portal.model.Website[] findByC_C_C_PrevAndNext(
		long websiteId, long companyId, long classNameId, long classPK,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_PrevAndNext(websiteId, companyId,
			classNameId, classPK, obc);
	}

	public static java.util.List findByC_C_C_P(long companyId,
		long classNameId, long classPK, boolean primary)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C_P(companyId, classNameId, classPK,
			primary);
	}

	public static java.util.List findByC_C_C_P(long companyId,
		long classNameId, long classPK, boolean primary, int begin, int end)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C_P(companyId, classNameId, classPK,
			primary, begin, end);
	}

	public static java.util.List findByC_C_C_P(long companyId,
		long classNameId, long classPK, boolean primary, int begin, int end,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findByC_C_C_P(companyId, classNameId, classPK,
			primary, begin, end, obc);
	}

	public static com.liferay.portal.model.Website findByC_C_C_P_First(
		long companyId, long classNameId, long classPK, boolean primary,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_P_First(companyId, classNameId,
			classPK, primary, obc);
	}

	public static com.liferay.portal.model.Website findByC_C_C_P_Last(
		long companyId, long classNameId, long classPK, boolean primary,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_P_Last(companyId, classNameId,
			classPK, primary, obc);
	}

	public static com.liferay.portal.model.Website[] findByC_C_C_P_PrevAndNext(
		long websiteId, long companyId, long classNameId, long classPK,
		boolean primary, com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException, 
			com.liferay.portal.NoSuchWebsiteException {
		return getPersistence().findByC_C_C_P_PrevAndNext(websiteId, companyId,
			classNameId, classPK, primary, obc);
	}

	public static java.util.List findWithDynamicQuery(
		com.liferay.portal.kernel.dao.DynamicQueryInitializer queryInitializer)
		throws com.liferay.portal.SystemException {
		return getPersistence().findWithDynamicQuery(queryInitializer);
	}

	public static java.util.List findWithDynamicQuery(
		com.liferay.portal.kernel.dao.DynamicQueryInitializer queryInitializer,
		int begin, int end) throws com.liferay.portal.SystemException {
		return getPersistence().findWithDynamicQuery(queryInitializer, begin,
			end);
	}

	public static java.util.List findAll()
		throws com.liferay.portal.SystemException {
		return getPersistence().findAll();
	}

	public static java.util.List findAll(int begin, int end)
		throws com.liferay.portal.SystemException {
		return getPersistence().findAll(begin, end);
	}

	public static java.util.List findAll(int begin, int end,
		com.liferay.portal.kernel.util.OrderByComparator obc)
		throws com.liferay.portal.SystemException {
		return getPersistence().findAll(begin, end, obc);
	}

	public static void removeByCompanyId(long companyId)
		throws com.liferay.portal.SystemException {
		getPersistence().removeByCompanyId(companyId);
	}

	public static void removeByUserId(long userId)
		throws com.liferay.portal.SystemException {
		getPersistence().removeByUserId(userId);
	}

	public static void removeByC_C(long companyId, long classNameId)
		throws com.liferay.portal.SystemException {
		getPersistence().removeByC_C(companyId, classNameId);
	}

	public static void removeByC_C_C(long companyId, long classNameId,
		long classPK) throws com.liferay.portal.SystemException {
		getPersistence().removeByC_C_C(companyId, classNameId, classPK);
	}

	public static void removeByC_C_C_P(long companyId, long classNameId,
		long classPK, boolean primary)
		throws com.liferay.portal.SystemException {
		getPersistence().removeByC_C_C_P(companyId, classNameId, classPK,
			primary);
	}

	public static void removeAll() throws com.liferay.portal.SystemException {
		getPersistence().removeAll();
	}

	public static int countByCompanyId(long companyId)
		throws com.liferay.portal.SystemException {
		return getPersistence().countByCompanyId(companyId);
	}

	public static int countByUserId(long userId)
		throws com.liferay.portal.SystemException {
		return getPersistence().countByUserId(userId);
	}

	public static int countByC_C(long companyId, long classNameId)
		throws com.liferay.portal.SystemException {
		return getPersistence().countByC_C(companyId, classNameId);
	}

	public static int countByC_C_C(long companyId, long classNameId,
		long classPK) throws com.liferay.portal.SystemException {
		return getPersistence().countByC_C_C(companyId, classNameId, classPK);
	}

	public static int countByC_C_C_P(long companyId, long classNameId,
		long classPK, boolean primary)
		throws com.liferay.portal.SystemException {
		return getPersistence().countByC_C_C_P(companyId, classNameId, classPK,
			primary);
	}

	public static int countAll() throws com.liferay.portal.SystemException {
		return getPersistence().countAll();
	}

	public static void initDao() {
		getPersistence().initDao();
	}

	public static WebsitePersistence getPersistence() {
		return _getUtil()._persistence;
	}

	public void setPersistence(WebsitePersistence persistence) {
		_persistence = persistence;
	}

	private static WebsiteUtil _getUtil() {
		if (_util == null) {
			_util = (WebsiteUtil)com.liferay.portal.kernel.bean.BeanLocatorUtil.locate(_UTIL);
		}

		return _util;
	}

	private static ModelListener _getListener() {
		if (Validator.isNotNull(_LISTENER)) {
			try {
				return (ModelListener)Class.forName(_LISTENER).newInstance();
			}
			catch (Exception e) {
				_log.error(e);
			}
		}

		return null;
	}

	private static final String _UTIL = WebsiteUtil.class.getName();
	private static final String _LISTENER = GetterUtil.getString(PropsUtil.get(
				"value.object.listener.com.liferay.portal.model.Website"));
	private static Log _log = LogFactory.getLog(WebsiteUtil.class);
	private static WebsiteUtil _util;
	private WebsitePersistence _persistence;
}