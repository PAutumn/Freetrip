package com.freetrip.trekker.activities;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.freetrip.trekker.R;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.MyHttpUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends BaseActivity {
   private View iv_pic;
  private View iv_text;
  
  
  private String url="http://192.168.3.14:8080/examples/update.json";
  // private   String url="http://cdn.sinacloud.net/cstour/testList.txt";
//版本更新状态码
	private static final int CODE_UPDATE = 1;
	// 网络连接 JSON 解析异常状态码
	private static final int CODE_NET_ERROR = 2;
   //下载失败状态码
	private static final int CODE_FAILTOLOAD=3;
	private String mVersionName;
	private int mVersionCode;
	private String mDescribe;
	private String mUrl;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		//	Log.i("MYLOG", "处理页面");
			switch (msg.what) {
			case CODE_UPDATE:
                  AlertDialog.Builder builder=new Builder(SplashActivity.this);
                  builder.setTitle("发现新版本"+mVersionName);
                  builder.setMessage(mDescribe);
                  builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						toMainActivity();
					}
				});
                  builder.setPositiveButton("立即体验",new OnClickListener(){
  					@Override
  					public void onClick(DialogInterface dialog, int which) {
  						// 下载然后进入安装界面
  						if(mUrl!=null&&mUrl.trim()!=""){
  							// 使用xUtils 工具进行下载
  						updateandinstall(mUrl);
  						}
  					}
                    });
                  builder.setNegativeButton("以后再说", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 进入主页面
						toMainActivity();
					}
                  });
                  builder.show();
				break;
			case CODE_NET_ERROR:
				Toast.makeText(SplashActivity.this,"网络异常",0).show();
				// 跳到主页面
				toMainActivity();
				break;
			case CODE_FAILTOLOAD:
				toMainActivity();
				break;
			}
		}
	};
	private long mStartTime;
	private SharedPreferences sp;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_splash);
	iv_pic = findViewById(R.id.iv_splash_pic);
	iv_text = findViewById(R.id.iv_splash_text);
	sp = getSharedPreferences("config", MODE_PRIVATE);
	mStartTime = System.currentTimeMillis();
 if(sp.getBoolean("update", false)){
		CheckVersion();
		}
}

// 检测版本
private void CheckVersion(){
	ThreadManager.getInstance().getThreadFromLongPool().execute(new Runnable() {
		@Override
		public void run() {
			try {
			String json = MyHttpUtils.loadJson(url, false);
			System.out.println(json);
				xjson(json);
				if (mVersionCode > getVersionCode()) {
					// 更新版本 弹框提示是否更新版本
					//Log.i("MYLOG", "发送消息");
					handler.sendEmptyMessage(CODE_UPDATE);
				} else {
					// 进入主页面
					toMainActivity();
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(CODE_NET_ERROR);
			}
		
		}
	});
	
}
     @Override
	protected void onResume() {
	super.onResume();
	TranslateAnimation anim=new TranslateAnimation(1, -1.2f, 1, 0, 1, 0, 1, 0);
	anim.setInterpolator(new OvershootInterpolator() );
	anim.setDuration(2500);
	iv_pic.startAnimation(anim);
	ObjectAnimator oa=ObjectAnimator.ofFloat(iv_text,"RotationY", 0,360);
	oa.setDuration(2500); 
	oa.start();
	oa.addListener(new AnimatorListenerAdapter(){
		@Override
		public void onAnimationEnd(Animator animation) {
		 //	SystemClock.sleep(300);
			new Thread(new Runnable() {
				@Override
				public void run() {
					SystemClock.sleep(300);
					toMainActivity();
				}
			}).start();
			
		}
	});
	}
     
  // 解析json
 	private void xjson(String json) throws JSONException {
 		if (json != null && json.trim() != "") {
 			JSONObject jobject = new JSONObject(json);
 			// 版本名字 版本号 描述 url
 			mVersionName = jobject.getString("versionName");
 			mVersionCode = jobject.getInt("versionCode");
 			mDescribe = jobject.getString("describe");
 			mUrl = jobject.getString("URL");
 		}
 	}
     
//进入主页面
private void toMainActivity() {
	//设置向导或者主页面
	if(sp.getBoolean("isShowGuide", true)){
	   // 进入设置向导
		Intent intent=new Intent(this,Activity_Guide.class);
		startActivity(intent);
		finish();
	}else{
		Intent intent=new Intent(this,MainActivity.class);
		//Intent intent=new Intent(this,Activity_Local.class);
		startActivity(intent);
		finish();
	}
}

// 获取当前app版本状态信息版本号
private int getVersionCode() {
	PackageManager packageManager = getPackageManager();
	int versionCode = 0;
	try {
		PackageInfo packageInfo = packageManager.getPackageInfo(
				getPackageName(), PackageManager.GET_META_DATA);
		versionCode = packageInfo.versionCode;
	} catch (NameNotFoundException e) {
		e.printStackTrace();
	}
	return versionCode;
}
//下载应用 sd卡权限
private void updateandinstall(String url){
	HttpUtils xutils=new HttpUtils();
		// 设置存放位置 以及下载成功的callback
		xutils.download(mUrl,Environment.getExternalStorageDirectory()+"/update.apk" ,
				new RequestCallBack<File>(){
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						//下载失败发送一条状态消息
						handler.sendEmptyMessage(CODE_FAILTOLOAD);
					}
					@Override
					public void onSuccess(ResponseInfo arg0) {
						// 下载成功安装apk
						Intent in=new Intent();
						in.setAction(Intent.ACTION_VIEW);
						in.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/update.apk")),
								"application/vnd.android.package-archive");
						// 当用户取消更新在返回当前页面
						SplashActivity.this.startActivityForResult(in, 100);
					}
		});
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if(requestCode==100){
		toMainActivity();
	}
}


}
