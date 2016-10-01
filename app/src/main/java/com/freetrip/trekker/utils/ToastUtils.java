package com.freetrip.trekker.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 弹吐司的工具类，
 * 不管是在子线程还是主线程，都可以直接谈
 * 因为会有很多地方会用到，所以封装一下，避免写太多没有意义的代码
 * @author mwp
 *
 */
public class ToastUtils {
	
	public static Toast mToast;

    /**  
     * 单例的Toast，只创建一次对象，下一个show会将上一个show冲刷掉
     * 无论是在主线程还是在子线程都可以直接刷新UI
     * @param context 
     * @param text
     */
	public static void show(final String text){
		
		if (mToast==null) {
			mToast = Toast.makeText(UIUtils.getContext(), "", Toast.LENGTH_SHORT);
		}
		
		//如果是在主线程直接谈
		if ("main".equals(Thread.currentThread().getName())) {
			mToast.setText(text);
			mToast.show();
		}else {
			//如果是在子线程，也可以刷新
			UIUtils.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mToast.setText(text);
					mToast.show();
				}
			});
		}
		
		
	}
}
