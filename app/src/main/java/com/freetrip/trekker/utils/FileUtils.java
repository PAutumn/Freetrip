package com.freetrip.trekker.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import android.os.Environment;

/**
 * 文件操作工具类
 * 
 * @author mwp
 *
 */
public class FileUtils {
	/** 本应用在SD卡的cache目录 */
	public static final String CACHE = "cache";
	/** 本应用在SD卡的icon目录 */
	public static final String ICON = "img";
	/** 本应用在SD卡的根目录 */
	public static final String ROOT = "freetrip";

	/**
	 * 获取图片的缓存的路径
	 * 
	 * @return
	 */
	public static File getIconDir() {
		return getDir(ICON);
	}

	/**
	 * 获取缓存文件的路径
	 * 
	 * @return
	 */
	public static File getCacheDir() {
		return getDir(CACHE);
	}

	public static File getDir(String path) {

		StringBuilder fullPath = new StringBuilder();
		if (isSDAvailable()) {
			fullPath.append(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			fullPath.append(File.separator);// '/'
			fullPath.append(ROOT);// /mnt/sdcard/GooglePlay
			fullPath.append(File.separator);
			fullPath.append(path);// /mnt/sdcard/GooglePlay/cache
		} else {
			// 如果SD卡不可用，将数据缓存到data/data 目录
			File cacheDir = UIUtils.getContext().getCacheDir(); // cache
																// getFileDir
																// file
			fullPath.append(cacheDir.getAbsolutePath());// /data/data/com.mwp.googleplay/cache
			fullPath.append(File.separator);
			fullPath.append(path);// /data/data/com.itheima.googleplay/cache/cache
		}

		// 如果文件夹不存在，创建文件夹
		File file = new File(fullPath.toString());
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}

		return file;
	}

	// 获取SD卡是否可用
	private static boolean isSDAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	
	public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
