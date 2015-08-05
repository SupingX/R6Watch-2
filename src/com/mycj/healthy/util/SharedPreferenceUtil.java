package com.mycj.healthy.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreference工具类
 * 
 * @author Administrator
 * 
 */
public class SharedPreferenceUtil {

	public static SharedPreferences sharedPreferences;
	public final static String SHARED_NAME = "i2watch";

	public static SharedPreferences getInstance(Context context) {
		if (sharedPreferences == null) {
			// sharedPreferences = PreferenceManager
			// .getDefaultSharedPreferences(context);
			sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		}
		return sharedPreferences;
	}

	/**
	 * 存入数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void put(Context context, String key, Object value) {
		Editor editor = getEditor(context);
		if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else {
			editor.putString(key, (String) value);
		}
		commit(editor);
	}

	/**
	 * 读取数据
	 * 
	 * @param context
	 * @param key
	 *            键
	 * @param defaultValue
	 *            默认值
	 */
	public static Object get(Context context, String key, Object defaultValue) {
		SharedPreferences sp = getInstance(context);
		if (defaultValue instanceof String) {
			return sp.getString(key, (String) defaultValue);
		} else if (defaultValue instanceof Integer) {
			return sp.getInt(key, (Integer) defaultValue);
		} else if (defaultValue instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultValue);
		} else if (defaultValue instanceof Float) {
			return sp.getFloat(key, (Float) defaultValue);
		} else if (defaultValue instanceof Long) {
			return sp.getLong(key, (Long) defaultValue);
		}
		return null;
	}

	/**
	 * 可编辑
	 * 
	 * @param context
	 * @return
	 */
	public static Editor getEditor(Context context) {
		return getInstance(context).edit();
	}

	/**
	 * 提交
	 * 
	 * @param context
	 */
	public static void commit(Editor editor) {
		editor.commit();
	}

	/**
	 * 是否包含某键值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean contains(Context context, String key) {
		return getInstance(context).contains(key);
	}

	/**
	 * 清除某个key所对应的value值
	 * 
	 * @param context
	 * @param key
	 */
	public static void remove(Context context, String key) {
		getEditor(context).remove(key);
	}

	/**
	 * 清除所有存储的值
	 * 
	 * @param context
	 */
	public static void removeAll(Context context) {
		getEditor(context).clear();
	}
}
