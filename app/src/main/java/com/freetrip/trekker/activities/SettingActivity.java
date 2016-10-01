package com.freetrip.trekker.activities;

import com.freetrip.trekker.R;
import com.freetrip.trekker.widget.SettingItemView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.ServiceState;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private SharedPreferences sp;
	private SettingItemView siv_update;
	private SettingItemView siv_wifipic;
	private TextView tv_cleancache;
	ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initActionBar();
		// 获取自动更新对象
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		// 获取智能无图对象
		siv_wifipic = (SettingItemView) findViewById(R.id.siv_wifipic);
		// 获取缓存清理对象
		tv_cleancache = (TextView) findViewById(R.id.tv_cleancache);


		tv_cleancache.setOnClickListener(this);

		// 获取sp对象
		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 设置中心页面出现时 动态获取sp中的auto_update数据 如果获取不到数据就默认是true
		Boolean autoupdate = sp.getBoolean("auto_update", true);
		if (autoupdate) {
			// 如果获取到的数据是true // === siv_update.setDesc("自动更新已经开启");//文本信息变为开启
			siv_update.setChecked(true);// checkbox已经勾选
		} else { // === siv_update.setDesc("自动更新已经关闭");
			siv_update.setChecked(false);// checkbox没有勾选
		}

		Boolean autounopic = sp.getBoolean("auto_nopic", true);
		if (autounopic) {
			// 如果获取到的数据是true // === siv_update.setDesc("自动更新已经开启");//文本信息变为开启
			siv_wifipic.setChecked(true);// checkbox已经勾选
		} else { // === siv_update.setDesc("自动更新已经关闭");
			siv_wifipic.setChecked(false);// checkbox没有勾选
		}

		initUpdateView();
		initNoPicView();
		
	}
	
	private void initActionBar() {
		// TODO Auto-generated method stub
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.local_titile));
		actionBar.setTitle("设置中心");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initNoPicView() {
		siv_wifipic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果现在当前是已经勾选
				if (siv_wifipic.isChecked()) {
					siv_wifipic.setChecked(false);
					// === siv_update.setDesc("自动更新已经关闭");
					sp.edit().putBoolean("auto_nopic", false).commit();

				} else {
					siv_wifipic.setChecked(true);
					// === siv_update.setDesc("自动更新已经开启");
					sp.edit().putBoolean("auto_nopic", true).commit();

				}

			}
		});

	}

	// 设置自动更新开关
	public void initUpdateView() {

		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果现在当前是已经勾选
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					// === siv_update.setDesc("自动更新已经关闭");
					sp.edit().putBoolean("auto_update", false).commit();

				} else {
					siv_update.setChecked(true);
					// === siv_update.setDesc("自动更新已经开启");
					sp.edit().putBoolean("auto_update", true).commit();

				}

			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_cleancache:
			startActivity(new Intent(SettingActivity.this, CleanCacheActivity.class));
			// finish();
			break;

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if(item.getItemId()==android.R.id.home){
			 finish();
		 }
		return super.onOptionsItemSelected(item);
	}
}
