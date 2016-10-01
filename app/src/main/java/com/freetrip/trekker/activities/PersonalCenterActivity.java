package com.freetrip.trekker.activities;

import com.freetrip.trekker.R;
import com.freetrip.trekker.utils.SpfUtils;
import com.freetrip.trekker.utils.ToastUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonalCenterActivity extends BaseActivity implements OnClickListener {
	
	private TextView tvUserName;
	private TextView tvUserGender;
	private TextView tvUserNote;
	private ImageView ivUserIcon;
	private LinearLayout ll_back;
	private RelativeLayout rl_quit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tpl_page_signup);
		
		tvUserName = (TextView)findViewById(R.id.tv_user_name);
		tvUserGender = (TextView)findViewById(R.id.tv_user_gender);
		tvUserNote = (TextView)findViewById(R.id.tv_user_note);
		ivUserIcon = (ImageView)findViewById(R.id.iv_user_icon);
		
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
	    rl_quit = (RelativeLayout) findViewById(R.id.rl_quit);
		
		ll_back.setOnClickListener(this);
		rl_quit.setOnClickListener(this);
		
		tvUserName.setText(SpfUtils.getString(this, "user_name", ""));
		tvUserGender.setText(SpfUtils.getString(this, "user_gender", ""));
		tvUserNote.setText("USER ID : "+SpfUtils.getString(this, "user_id", ""));
		String user_icon = SpfUtils.getString(this, "user_icon", "");
		if (!TextUtils.isEmpty(user_icon)) {
			ivUserIcon.setImageURI(Uri.parse(user_icon));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back:
			finish();
			break;

		case R.id.rl_quit:
			showQuitdialog();
			
		default:
			break;
		}
	}

	private void showQuitdialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("退出");
		builder.setMessage("是否确定退出当前账号登录");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SpfUtils.setBoolean(PersonalCenterActivity.this, "is_login", false);
				ToastUtils.show("注销成功！");
				MainActivity.currentMainActivity.finish();
				startActivity(new Intent(PersonalCenterActivity.this, MainActivity.class));
				finish();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

}
