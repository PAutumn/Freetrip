package cn.sharesdk.tpl;

import java.util.HashMap;

import com.freetrip.trekker.R;
import com.mob.tools.FakeActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

public class ThirdPartyLogin extends FakeActivity implements OnClickListener, Callback, PlatformActionListener {
	private static final int MSG_AUTH_CANCEL = 2;
	private static final int MSG_AUTH_ERROR= 3;
	private static final int MSG_AUTH_COMPLETE = 4;
	
	private OnLoginListener signupListener;
	private Handler handler;
	/** 设置授权回调，用于判断是否进入注册 */
	public void setOnLoginListener(OnLoginListener l) {
		this.signupListener = l;
	}
	
	public void onCreate() {
		// 初始化ui
		handler = new Handler(this);
		activity.setContentView(R.layout.tpl_login_page);
		(activity.findViewById(R.id.tvWeibo)).setOnClickListener(this);
		(activity.findViewById(R.id.tvQq)).setOnClickListener(this);
	}
		
	public void onStop() {
		finish();
		super.onStop();
	};
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.tvWeibo: {
				//新浪微博
				Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
				authorize(sina);
			} break;
			case R.id.tvQq: {
				//QQ空间
				Platform qzone = ShareSDK.getPlatform(QZone.NAME);
				authorize(qzone);
			} break;
		}
	}
	
	//执行授权,获取用户信息
	private void authorize(Platform plat) {
		if (plat == null) {
			return;
		}
		
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}
	
	
	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			Message msg = new Message();
			msg.what = MSG_AUTH_COMPLETE;
			msg.obj = new Object[] {platform.getName(), res};
			handler.sendMessage(msg);
		}
	}
	
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_ERROR);
		}
		t.printStackTrace();
	}
	
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(MSG_AUTH_CANCEL);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_AUTH_CANCEL: {
				//取消授权
				Toast.makeText(activity, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_ERROR: {
				//授权失败
				Toast.makeText(activity, R.string.auth_error, Toast.LENGTH_SHORT).show();
			} break;
			case MSG_AUTH_COMPLETE: {
				//授权成功
				Toast.makeText(activity, R.string.auth_complete, Toast.LENGTH_SHORT).show();
				Object[] objs = (Object[]) msg.obj;
				String platform = (String) objs[0];
				HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
				if (signupListener != null && signupListener.onSignin(platform, res)) {
					SignupPage signupPage = new SignupPage();
					signupPage.setOnLoginListener(signupListener);
					signupPage.setPlatform(platform);
					signupPage.show(activity, null);
				}
			} break;
		}
		return false;
	}
	
	public void show(Context context) {
		ShareSDK.initSDK(context);
		super.show(context, null);
	}
		
}
