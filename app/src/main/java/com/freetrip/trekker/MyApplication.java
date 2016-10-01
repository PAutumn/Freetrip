package com.freetrip.trekker;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * 自定义Application
 * 
 * @author mwp
 *
 */
public class MyApplication extends Application {

	private static MyApplication application;

	private static int mainTid;

	private static Handler handler;

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		mainTid = android.os.Process.myTid();
		handler = new Handler();

		initManager();
		// 全局捕获异常
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());

	}

	private void initManager() {
		SDKInitializer.initialize(getApplicationContext());
			
	}
	
	public static Context getApplication() {
		return application;
	}

	public static int getMainTid() {
		return mainTid;
	}

	public static Handler getHandler() {
		return handler;
	}
}
