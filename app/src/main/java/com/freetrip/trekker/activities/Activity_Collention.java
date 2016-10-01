package com.freetrip.trekker.activities;

import java.util.List;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.freetrip.trekker.R;
import com.freetrip.trekker.Dao.CollectionDao;
import com.freetrip.trekker.adapters.CommonListAdapters;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.UIUtils;
import com.freetrip.trekker.widget.NoBGListView;

public class Activity_Collention extends BaseActivity {
	
	// 查询获取的数据
	List<CommonInfo> datas ;
	 NoBGListView lv;
	// 布局
	private FrameLayout fl_collection;
	ActionBar actionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initdata();
		initView();
		initActionBar();
	}
	
	private void initActionBar() {
		// TODO Auto-generated method stub
		actionBar = getActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.color.local_titile));
		actionBar.setTitle("我的收藏");
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
    private void initView() {
    	setContentView(R.layout.activity_collection);
	fl_collection = (FrameLayout) findViewById(R.id.fl_collection);
	}
	//查询数据库
	private void initdata() {
		ThreadManager.getInstance().getThreadFromShortPool().execute(new Runnable() {
			@Override
			public void run() {
				CollectionDao dao=new CollectionDao();
				 datas = dao.findAllCollection();
				  lv=new NoBGListView(UIUtils.getContext());
				 UIUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						 if(datas!=null&&datas.size()>0){
						CommonListAdapters adapter=new CommonListAdapters(UIUtils.getContext());
						adapter.setItems(datas);
						lv.setAdapter(adapter);
						fl_collection.addView(lv);
						 }else{
							 //
							 Toast.makeText(Activity_Collention.this, "当前没有收藏,快去收藏吧", 30).show();
						 }
					}
				});
			}
		});
		
		
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
