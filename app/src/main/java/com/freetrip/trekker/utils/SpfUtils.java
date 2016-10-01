package com.freetrip.trekker.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 对SharedPreference进行简单封装的工具类
 * 
 * @author mwp
 *
 */
public class SpfUtils {

	public static final String SPF_NAME = "config";

	/**
	 * 向SharedPreference中保存一个布尔值
	 * 
	 * @param context
	 *            上下文
	 * @param key
	 *            键
	 * @param value
	 *            值
	 */
	public static void setBoolean(Context context, String key, boolean value) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		spf.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean(key, defValue);
	}
	
	public static void setString(Context context, String key, String value) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		spf.edit().putString(key, value).commit();
	}
	
	public static String getString(Context context, String key,
			String defValue) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		return spf.getString(key, defValue);
	}
	
	public static void setInt(Context context, String key, int value) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		spf.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context, String key,
			int defValue) {
		SharedPreferences spf = context.getSharedPreferences(SPF_NAME,
				Context.MODE_PRIVATE);
		return spf.getInt(key, defValue);
	}
}
