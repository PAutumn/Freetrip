package com.freetrip.trekker.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 获取手机系统信息的工具类
 * @author mwp
 *
 */
public class SystemInfoUtils {
	/**
	 * 判断一个服务是否处于运行状态
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static boolean isServiceRunning(Context context, String className) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(200);
		for (RunningServiceInfo info : infos) {
			String serviceClassName = info.service.getClassName();
			if (className.equals(serviceClassName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回进程的总个数
	 * 
	 * @param context
	 * @return
	 */
	public static int getProcessCount(Context context) {
		// 得到进程管理者
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 获取到当前手机上面所有运行的进程
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();

		// 获取手机上面一共有多少个进程
		return runningAppProcesses.size();
	}

	/**获取可用内存大小*/
	public static long getAvailMem(Context context) {
		// 得到进程管理者
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

		// 获取到内存的基本信息
		activityManager.getMemoryInfo(memoryInfo);
		// 获取到剩余内存
		return memoryInfo.availMem;
	}

	/**获取总内存*/
	public static long getTotalMem(Context context) {
		// 获取到总内存
		/*
		 * 这个地方不能直接跑到低版本的手机上面 MemTotal: 344740 kB "/proc/meminfo"
		 */
		try {
			// /proc/meminfo 配置文件的路径
			FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					fis));

			String readLine = reader.readLine();

			StringBuffer sb = new StringBuffer();

			for (char c : readLine.toCharArray()) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}
	
	/**判断是否是WiFi联网*/
	public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
	
	/**
	 * 判断当前网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

}
