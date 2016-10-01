package com.freetrip.trekker.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LocalComparetor.CommonHotComparetor;
import LocalComparetor.CommonSearchComparetor;
import LocalComparetor.CommonStarsComparetor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.freetrip.trekker.R;
import com.freetrip.trekker.adapters.CommonListAdapters;
import com.freetrip.trekker.adapters.MyBaseAdapter;
import com.freetrip.trekker.bean.CommonListBean;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.MyHttpUtils;
import com.freetrip.trekker.utils.SearchUtils;
import com.freetrip.trekker.utils.ToastUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.freetrip.trekker.widget.NoBGListView;
import com.google.gson.Gson;

public class Activity_CommonList extends BaseActivity implements OnClickListener{
	 private FrameLayout fl_activity_common;
	   private String url="";
	   
	private ImageButton ib_common;
	private NoBGListView lv;
	private LinearLayout ll_common_sort;
	private TextView tv_common_default;
	private TextView tv_common_hot;
	private TextView tv_common_stars;
	
	// 当先选择分组条目的id
	private TextView currentItemSort;
	//是否使用默认的排序
	private boolean isDefault=true;
	
	private List<CommonInfo> mCloneDatas;

	private List<CommonInfo> datas;

   @Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_common_list);
	url=getIntent().getStringExtra("url");
	 initView();
	 initdata();
	 getActionBar().setDisplayHomeAsUpEnabled(true);//设置返回建
}

private void initView() {
	fl_activity_common = (FrameLayout) findViewById(R.id.fl_activity_common);
	ib_common = (ImageButton) findViewById(R.id.ib_common);
	ib_common.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(lv!=null){
				lv.setSelection(0);
			//lv.smoothScrollToPosition(0);
			//ll_common_sort
			LinearLayout.LayoutParams params_up = (android.widget.LinearLayout.LayoutParams) ll_common_sort.getLayoutParams();
			params_up.topMargin=0;
			ll_common_sort.setLayoutParams(params_up);

			
			}
		}
	});
	ll_common_sort = (LinearLayout) findViewById(R.id.ll_common_sort);
	ll_common_sort.measure(0, 0);
	height = ll_common_sort.getMeasuredHeight();
	
	tv_common_default = (TextView) findViewById(R.id.tv_common_default);
	tv_common_hot = (TextView) findViewById(R.id.tv_common_hot);
	tv_common_stars = (TextView) findViewById(R.id.tv_common_stars);
	currentItemSort=tv_common_default;
	tv_common_default.setOnClickListener(this);
	tv_common_hot.setOnClickListener(this);
	tv_common_stars.setOnClickListener(this);
	
	
}
private void initdata(){
	 View loadpage_loading = UIUtils.infalte(R.layout.loadpage_loading);
	 fl_activity_common.removeAllViews();
	 fl_activity_common.addView(loadpage_loading);
	 ll_common_sort.setVisibility(View.INVISIBLE);
	
	 ThreadManager.getInstance().getThreadFromLongPool().execute(new Runnable() {
		@Override
		public void run() {
			String json = MyHttpUtils.loadJson(url);
			Gson gson=new Gson();
			CommonListBean	itemBean = gson.fromJson(json, CommonListBean.class);
			if(itemBean!=null){
			datas = itemBean.data.items;
			//复制一份用于排序
			 mCloneDatas=new ArrayList<CommonInfo>(datas);
			lv = new NoBGListView(Activity_CommonList.this);
			//设置adapter
			UIUtils.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setAdapter(datas);
					 fl_activity_common.removeAllViews();
					fl_activity_common.addView(lv);
					ll_common_sort.setVisibility(View.VISIBLE);
				}
			});
			//设置监听
			setSrcollListener();
			setTouchLis();
			}else{
				//没有网络
				final View loadpage_error = UIUtils.infalte(R.layout.loadpage_error);
				View btn = loadpage_error.findViewById(R.id.page_bt);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						initdata();
					}
				});
				UIUtils.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						fl_activity_common.removeAllViews();
						fl_activity_common.addView(loadpage_error);
						 ll_common_sort.setVisibility(View.INVISIBLE);
					}
				});
			}
		}
	});
 }
private int startY;
private int height;
protected void setTouchLis() {
	lv.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				if(startY==0)
				startY = (int) event.getRawY();
			   int currentY=(int) event.getRawY();
				int deletY=currentY-startY;
				LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) ll_common_sort.getLayoutParams();
				params.topMargin+=deletY;
				if(params.topMargin<-height){
					params.topMargin=-height;
					// getActionBar().hide();//不管actionbar
				}
				if(params.topMargin>0){
					params.topMargin=0;
					//getActionBar().show();   //不管actionbar
				}
				ll_common_sort.setLayoutParams(params);
				startY=currentY;
				break;
			case MotionEvent.ACTION_UP:
				startY=0;
				LinearLayout.LayoutParams params_up = (android.widget.LinearLayout.LayoutParams) ll_common_sort.getLayoutParams();
				if(params_up.topMargin>-height/2){
					params_up.topMargin=0;
				}else{
					params_up.topMargin=-height;
				}
				ll_common_sort.setLayoutParams(params_up);
				break;
			}
			return false;
		}
	});
}
protected void setSrcollListener() {
	lv.setOnScrollListener(new OnScrollListener() {
		private int position;
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch(scrollState){
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				position = view.getLastVisiblePosition();
				break ;
			case OnScrollListener.SCROLL_STATE_IDLE:
				int curr= view.getLastVisiblePosition();
			}
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if(firstVisibleItem<2){
				ib_common.setVisibility(View.INVISIBLE);
			}else{
				ib_common.setVisibility(View.VISIBLE);
			}
			
		if(view.getLastVisiblePosition()==datas.size()-1){
				//加载更多
//				System.out.println("//加载更多");
//				String json = MyHttpUtils.loadJson(url);
//				Gson gson=new Gson();
//				CommonListBean	itemBean = gson.fromJson(json, CommonListBean.class);
//				if(itemBean!=null){
//					datas.addAll(itemBean.data.items);
//					 mCloneDatas=new ArrayList<CommonInfo>(datas);
//					ListView v= (ListView) view;
//					MyBaseAdapter<CommonInfo> adapter = (MyBaseAdapter<CommonInfo>)v.getAdapter();
//				//	adapter.setItems(datas);                     
//					//adapter.notifyDataSetChanged();
//					adapter.addItems(itemBean.data.items);
//				}else{
//					Toast.makeText(mActivity, "没有更多的数据了", 0).show();
			ToastUtils.show("没有更多的数据了");
   		    	}
			}
	});
}


private void setAdapter(List<CommonInfo> data) {
	CommonListAdapters  adapter=new CommonListAdapters(this);
	if(data!=null&&data.size()>0){
	adapter.setItems(data);
	lv.setAdapter(adapter);
	}else{
		// 错误页面
	}
}



// 创建actionBar 上面的搜索按钮
@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  getActionBar().setBackgroundDrawable(new ColorDrawable(UIUtils.getResources().getColor(R.color.local_titile)));
	  MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.local_actionbar, menu);
      SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
      searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
			//  Toast.makeText(Activity_CommonList.this, query, Toast.LENGTH_SHORT).show();
				  //搜索提交的
				if(mCloneDatas!=null&&mCloneDatas.size()>0){
					  for(int i = 0;i<mCloneDatas.size();i++){
						  String desc = mCloneDatas.get(i).desc;
						  mCloneDatas.get(i).similarity = SearchUtils.calculateSimilarity(desc, query);
					  }
					  Collections.sort(mCloneDatas, new CommonSearchComparetor());
			    	  setAdapter(mCloneDatas);
				}else{
				 Toast.makeText(Activity_CommonList.this, "没有网络连接！", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return true;
	}
//菜单条目的点击事件 	
@Override
public boolean onOptionsItemSelected(MenuItem item) {
	 if(item.getItemId()==android.R.id.home){
		 finish();
	 }
	return super.onOptionsItemSelected(item);
}

@Override
public void onClick(View v) {
	setSortTextViewState((TextView)v);
	 sortItems(v.getId());
}

	//根据不同选择排序
    private void sortItems(int id){
    	switch(id){
    	case R.id.tv_common_default:
    		setAdapter(datas);
    		isDefault=true;
    		break;
    	case R.id.tv_common_hot:
    		Collections.sort(mCloneDatas, new CommonHotComparetor());
    		setAdapter(mCloneDatas);
    		isDefault=false;
    		break;
    	case R.id.tv_common_stars:
    		Collections.sort(mCloneDatas, new CommonStarsComparetor());
    		setAdapter(mCloneDatas);
    		isDefault=false;
    		break;
    	}
}

private void setSortTextViewState(TextView clickView){
	currentItemSort.setBackgroundResource(R.drawable.local_rectangle_normal);
	clickView.setBackgroundResource(R.drawable.local_rectangle_selected);
	currentItemSort=clickView;
}

@Override
protected void onDestroy() {
	super.onDestroy();
}

}
