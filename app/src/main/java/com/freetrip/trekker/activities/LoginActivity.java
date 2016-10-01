package com.freetrip.trekker.activities;

import java.util.HashMap;

import cn.sharesdk.tpl.OnLoginListener;
import cn.sharesdk.tpl.ThirdPartyLogin;
import cn.sharesdk.tpl.UserInfo;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class LoginActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDemo();
	}

	private void showDemo() {
		ThirdPartyLogin tpl = new ThirdPartyLogin();
		tpl.setOnLoginListener(new OnLoginListener() {
			public boolean onSignin(String platform, HashMap<String, Object> res) {
				// 在这个方法填写尝试的代码，返回true表示还不能登录，需要注册
				// 此处全部给回需要注册
				return true;
			}

			public boolean onSignUp(UserInfo info) {
				// 填写处理注册信息的代码，返回true表示数据合法，注册页面可以关闭
				return true;
			}
		});
		tpl.show(this);
	}
	
	@Override
	protected void onPause() {
		finish();
		super.onPause();
	}

	@Override
	protected void onStop() {
		finish();
		super.onStop();
	}
}
