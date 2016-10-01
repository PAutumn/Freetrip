package com.freetrip.trekker.activities;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;

/**
 * Activity的基类
 * 
 * @author mwp
 *
 */
public class BaseActivity extends Activity {

	/** 管理运行的所有Activity */
	public final static List<BaseActivity> mActivities = new LinkedList<BaseActivity>();// 使用LinkedList添加删除效率高，ArrayList查找效率高
	public static BaseActivity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		synchronized (mActivities) {
			// 使用monkey测试的时候容易产生Activity刚创建出来还没添加到集合中就要销毁，这样会导致程序崩掉，所以对于添加删除操作要添加同步
			mActivities.add(this);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mActivity = this;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mActivity = null;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();

		synchronized (mActivities) {
			mActivities.remove(this);
		}
	}


	/**
	 * 杀死所有Activity，退出应用
	 */
	public void killAll() {

		// 集合不能再遍历过程中改变长度,所以复制一份集合在遍历删除
		List<BaseActivity> copy;
		synchronized (mActivities) {
			copy = new LinkedList<BaseActivity>(mActivities);
		}

		for (BaseActivity activity : copy) {
			activity.finish();
		}

		// 杀死当前的进程
		android.os.Process.killProcess(Process.myPid());
	}
}
