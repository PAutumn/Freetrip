package com.freetrip.trekker.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.freetrip.trekker.R;

public class FeedBackActivity extends BaseActivity{
	ActionBar actionBar;
	EditText mEditText2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_back);
		initActionBar();
		initView();
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.local_titile));
		actionBar.setTitle("意见反馈");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void initView() {
		// TODO Auto-generated method stub
		mEditText2 = (EditText) findViewById(R.id.editText2);
	}

	public void submit(View v) {
		String suggestion = mEditText2.getText().toString();
		if (TextUtils.isEmpty(suggestion)) {
			Toast.makeText(this, "请输入您的建议再提交", Toast.LENGTH_SHORT).show();
		}else{//跳转到下一个页面
			Toast.makeText(this, "提交中", 0).show();
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					Intent intent = new Intent(FeedBackActivity.this,FeedBackSuccessActivity.class);
					startActivity(intent);
					FeedBackActivity.this.finish();
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, 500);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
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
