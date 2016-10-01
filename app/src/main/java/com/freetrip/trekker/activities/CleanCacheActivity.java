package com.freetrip.trekker.activities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.freetrip.trekker.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends BaseActivity {
	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager pm;
	private LinearLayout ll;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleancache);
		initActionBar();
		pb = (ProgressBar) findViewById(R.id.pb);
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll = (LinearLayout) findViewById(R.id.ll);

		scanCache();
	}
	
	private void initActionBar() {
		// TODO Auto-generated method stub
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.local_titile));
		actionBar.setTitle("缓存清理");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	/**
	 * 扫描手机里面所有应用程序的缓存信息
	 */
	private void scanCache() {
		pm = getPackageManager();
		// 遍历每一个应用程序是耗时操作
		new Thread() {
			public void run() {
				Method getPackageSizeInfo = null;
				Method[] methods = PackageManager.class.getMethods();
				for (Method method : methods) {
					System.out.println(method.getName());
					if ("getPackageSizeInfo".equals(method.getName())) {
						// 说明找到了这个方法
						getPackageSizeInfo = method;
					}
				}
				// 拿到所有在手机上安装的应用程序信息
				List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
				
				pb.setMax(installedPackages.size());
				int progress = 0;
				for (PackageInfo packinfo : installedPackages) {
					// 由pm来执行getPackageSizeInfo这个方法
					try {
						getPackageSizeInfo.invoke(pm, packinfo.packageName, new MyDataObserver());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tv_scan_status.setText("扫描完毕");
						}
					});
				}
				
			};

		}.start();
	}

	// 远程服务接口实现类
	private class MyDataObserver extends IPackageStatsObserver.Stub {

		private ApplicationInfo appInfo;

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			final long cache = pStats.cacheSize;
			long code = pStats.codeSize;
			long data = pStats.dataSize;
			final String packname = pStats.packageName;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);

				runOnUiThread(new Runnable() {

					private TextView tv_appname;
					private TextView tv_appcache;
					private ImageView iv_delete;

					@Override
					public void run() {
						tv_scan_status.setText("正在扫描：" + appInfo.loadLabel(pm));
						if(cache>0){
							//说明当前应用是有缓存的
							View view=View.inflate(getApplicationContext(), R.layout.item_list_cache, null);
							tv_appname = (TextView) view.findViewById(R.id.tv_appname);
							tv_appcache= (TextView) view.findViewById(R.id.tv_appcache); 
							iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
							tv_appname.setText(appInfo.loadLabel(pm));
							tv_appcache.setText("缓存大小："+Formatter.formatFileSize(getApplicationContext(), cache));
							iv_delete.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
								try {
									Method deletemethod=PackageManager.class.getMethod("deleteApplicationCacheFiles", 
											String.class,IPackageDataObserver.class);
									deletemethod.invoke(pm, packname,new MyDataObserver());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
									
								}
							});
							
							ll.addView(view);
						}
					}
				});

			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

	}
	
	private class MyPackageDataObserver extends IPackageDataObserver.Stub{

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
			System.out.println(packageName+succeeded);
			
		}
		
	}
	/**清理手机的全部缓存
	 * 原理：利用安卓的bug，告诉系统你需要200G的空间，那么系统就会自己把缓存全部清掉以腾出空间来
	 */
	public void deleteAll(View v){
		Method[] methods = PackageManager.class.getMethods();
		for(Method  method:methods){
			 if("freeStorageAndNotify".equals(method.getName())){
				 try {
					method.invoke(pm, Integer.MAX_VALUE,
							new MyPackageDataObserver());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				
			 }
		}
	}
}
