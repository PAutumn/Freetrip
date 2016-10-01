package com.freetrip.trekker.utils;

import com.freetrip.trekker.MyApplication;
import com.freetrip.trekker.activities.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 在开发UI过程中使用到的工具类
 * 
 * @author mwp
 *
 */
public class UIUtils {
	
	public static Resources getResources() {
		return MyApplication.getApplication().getResources();
	}

	/**
	 * 通过id将数组资源转换成一个字符串数组
	 * 
	 * @param id
	 * @return
	 */
	public static String[] getStringArray(int id) {
		return getResources().getStringArray(id);
	}

	/** dip转换px */
	public static int dip2px(int dip) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/** pxz转换dip */
	public static int px2dip(int px) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}
	
	/**返回从Application获取的上下文*/
	public static Context getContext(){
		return MyApplication.getApplication();
	}

	/**把Runable方法提交到主线程*/
	public static void runOnUiThread(Runnable runnable){
		//在主线程运行
		//方式一： "main".equals(Thread.currentThread().getName())
		if (android.os.Process.myTid() == MyApplication.getMainTid()) {
			runnable.run();
		}else {
			//如果不在主线程中，获取Handler
			MyApplication.getHandler().post(runnable);
		}
	}
	
	/**
	 * 将view的inflate的方法简化成一个参数
	 * @param id 布局文件的id
	 * @return
	 */
	public static View infalte(int id){
		return View.inflate(getContext(), id, null);
	}
	
	/**
	 * 将一个图片id转化成一个Drawable对象
	 * @param id
	 * @return
	 */
	public static Drawable getDrawable(int id){
		return getResources().getDrawable(id);
	}

	public static int getDimens(int id) {
		return (int)getResources().getDimension(id);
	}

	
	/**
	 * 取消任务
	 * @param runnable
	 */
	public static void cancel(Runnable runnable) {
		MyApplication.getHandler().removeCallbacks(runnable);
	}

	/**
	 * 延迟执行任务
	 * @param runnable
	 * @param time 毫秒
	 */
	public static void postDelayed(Runnable runnable, long time) {
		MyApplication.getHandler().postDelayed(runnable, time);
	}
	
	/**
	 * 可以打开Activity
	 * @param intent
	 */
	public static void startActivity(Intent intent){
		//如果不在Activity里去打开Activity，需要指定任务栈
		if (BaseActivity.mActivity==null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);
		}else {
			//startActivity是Activity的方法
			BaseActivity.mActivity.startActivity(intent);
		}
	}
}
