/**
 * @(#)Preferences.java 2013-9-5 Copyright 2013 it.kedacom.com, Inc. All rights
 *                      reserved.
 */

package com.yuqiaotech.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yuqiaotech.app.AppWatchApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public abstract class PcAbsPreferences {

	protected Editor mEditor;
	protected Context mContext;
	protected SharedPreferences mPreferences;

	public PcAbsPreferences(Context context) {
		this(context, "");
	}

	// Context.MODE_PRIVATE
	public PcAbsPreferences(Context context, String fileName, int mode) {
		mContext = context;
		if (mContext == null) {
			mContext = AppWatchApplication.getApplication();
		}
		initPreferencesEditor(mContext, fileName, mode);
	}

	public PcAbsPreferences(Context context, String fileName) {
		mContext = context;
		if (mContext == null) {
			mContext = AppWatchApplication.getApplication();
		}
		initPreferencesEditor(mContext, fileName);
	}

	public void initPreferencesEditor(Context context, String fileName) {
		if (null == context || null == fileName || 0 == fileName.length()) {
			return;
		}

		// Context.MODE_WORLD_WRITEABLE
		mPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
	}

	public void initPreferencesEditor(Context context, String fileName, int mode) {
		if (null == context || null == fileName || 0 == fileName.length()) {
			return;
		}

		mPreferences = context.getSharedPreferences(fileName, mode);
	}

	public synchronized void putValue(String key, Object obj) {
		if (obj == null) {
			return;
		}

		openEditor();

		try {
			if (obj instanceof Integer || obj instanceof Short) {
				putInt(key, (Integer) obj);
			} else if (obj instanceof Long) {
				putLong(key, (Long) obj);
			} else if (obj instanceof Float || obj instanceof Double) {
				putFloat(key, (Float) obj);
			} else if (obj instanceof Boolean) {
				putBoolean(key, (Boolean) obj);
			} else if (obj instanceof String) {
				putString(key, (String) obj);
			}
		} catch (Exception e) {
		}

		commit();
	}

	/**
	 * opent editor
	 */
	public void openEditor() {
		if (null == mPreferences) {
			return;
		}

		if (mEditor == null) {
			mEditor = mPreferences.edit();
		}
	}

	/**
	 * commit modify
	 */
	public void commit() {
		if (mEditor == null) {
			return;
		}

		mEditor.commit();
	}

	/**
	 * close editor
	 */
	public void closeEditor() {
		if (mEditor == null) {
			return;
		}

		mEditor.commit();
		mEditor = null;
	}

	public boolean isClosed() {
		if (mEditor != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * release the instance
	 */
	public void release() {
		if (!isClosed()) {
			closeEditor();
		}
	}

	public void putInt(String key, int value) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.putInt(key, value);
	}

	public void putLong(String key, long value) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.putLong(key, value);
	}

	public void putFloat(String key, float value) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.putFloat(key, value);
	}

	public void putString(String key, String value) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.putString(key, value);
	}

	public void putBoolean(String key, boolean value) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.putBoolean(key, value);
	}

	public boolean containsKey(String key) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return false;
		}

		if (null == mPreferences) {
			return false;
		}

		return mPreferences.contains(key);
	}

	public void removeKey(String key) {
		if (TextUtils.isEmpty(key) || null == mPreferences) {
			return;
		}

		if (null == mEditor) {
			mEditor = mPreferences.edit();
		}

		mEditor.remove(key);
	}

	public String getString(String key, String defValue) {
		String value = defValue;
		if (TextUtils.isEmpty(key) || mPreferences == null) {
			return value;
		}

		if (mPreferences.contains(key)) {
			value = mPreferences.getString(key, defValue);
		}

		return value;
	}

	public int getInt(String key, int defValue) {
		int value = defValue;
		if (TextUtils.isEmpty(key) || mPreferences == null) return value;

		if (mPreferences.contains(key)) {
			value = mPreferences.getInt(key, defValue);
		}

		return value;
	}

	public long getLong(String key, long defValue) {
		long value = defValue;
		if (TextUtils.isEmpty(key) || mPreferences == null) return value;

		if (mPreferences.contains(key)) {
			value = mPreferences.getLong(key, defValue);
		}

		return value;
	}

	public float getFloat(String key, float defValue) {
		float value = defValue;
		if (TextUtils.isEmpty(key) || mPreferences == null) {
			return value;
		}

		if (mPreferences.contains(key)) {
			value = mPreferences.getFloat(key, defValue);
		}

		return value;
	}

	public boolean getBoolean(String key, boolean defValue) {
		boolean value = defValue;
		if (TextUtils.isEmpty(key) || mPreferences == null) return value;

		if (mPreferences.contains(key)) {
			value = mPreferences.getBoolean(key, defValue);
		}

		return value;
	}

	/**
	 * 批量添加
	 * @param map
	 */
	public void batchValues(Map<String, Object> map) {
		if (null == map || map.isEmpty() || null == mPreferences) {
			return;
		}

		Iterator<Entry<String, Object>> it = map.entrySet().iterator();
		if (null == it) return;

		if (mEditor == null) {
			mEditor = mPreferences.edit();
		}

		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (null == entry) {
				continue;
			}

			String key = entry.getKey();
			Object obj = entry.getValue();

			if (null == key || key.length() == 0 || null == obj) {
				continue;
			}

			try {
				if (obj instanceof Integer) {
					mEditor.putInt(key, (Integer) obj);
				} else if (obj instanceof Long) {
					mEditor.putLong(key, (Long) obj);
				} else if (obj instanceof Float) {
					mEditor.putFloat(key, (Float) obj);
				} else if (obj instanceof Boolean) {
					mEditor.putBoolean(key, (Boolean) obj);
				} else if (obj instanceof String) {
					mEditor.putString(key, (String) obj);
				}
			} catch (Exception e) {
			}
		}

		mEditor.commit();
	}

	public Map<String, ?> getAll() {
		if (null == mPreferences) {
			return null;
		}

		return mPreferences.getAll();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getList() {
		Map<String, Object> map = (Map<String, Object>) getAll();
		if (null == map || map.isEmpty()) {
			return null;
		}

		List<Object> list = new ArrayList<Object>();
		Iterator<Entry<String, Object>> itMap = map.entrySet().iterator();
		if (itMap == null) {
			return null;
		}

		while (itMap.hasNext()) {
			Entry<String, Object> entry = itMap.next();
			if (null == entry) continue;
			list.add(entry.getValue());
		}

		return list;
	}

	public synchronized static void putValue(Editor editor, String key, Object obj) {
		if (obj == null || editor == null || TextUtils.isEmpty(key)) {
			return;
		}

		try {
			if (obj instanceof Integer || obj instanceof Short) {
				editor.putInt(key, (Integer) obj);
			} else if (obj instanceof Long) {
				editor.putLong(key, (Long) obj);
			} else if (obj instanceof Float || obj instanceof Double) {
				editor.putFloat(key, (Float) obj);
			} else if (obj instanceof Boolean) {
				editor.putBoolean(key, (Boolean) obj);
			} else if (obj instanceof String) {
				editor.putString(key, (String) obj);
			}
		} catch (Exception e) {
		}

		editor.commit();
	}

	/**
	 * 存储登录信息
	 * 
	 * @param context
	 */
	public static void putValue(Context context, String key, Object value) {
		if (TextUtils.isEmpty(key)) {
			return;
		}

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		putValue(sp.edit(), key, value);
	}

	public static int getInt(Context context, String key, int defValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(key, defValue);
	}

	public static long getLong(Context context, String key, long defValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getLong(key, defValue);
	}

	public static String getLong(Context context, String key, String defValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(key, defValue);
	}

	public static float getFloat(Context context, String key, float defValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getFloat(key, defValue);
	}

	public static boolean getBoolean(Context context, String key, boolean defValue) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 删除SharedPrefs文件
	 * @Description
	 * @param fileName
	 */
	@SuppressLint("SdCardPath")
	public static void delSharedPrefs(Context context, String fileName) {
		try {
			File file = new File("/data/data/" + context.getPackageName() + "/shared_prefs", fileName);
			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
		}
	}

}
