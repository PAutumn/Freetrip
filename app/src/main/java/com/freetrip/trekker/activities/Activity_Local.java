package com.freetrip.trekker.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import LocalComparetor.CommonSearchComparetor;
import LocalComparetor.LocalHotComparetor;
import LocalComparetor.LocalInstanceComparetor;
import LocalComparetor.LocalSearchComparetor;
import LocalComparetor.LocalStarsComparetor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
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
import com.freetrip.trekker.adapters.LocalAdapter;
import com.freetrip.trekker.adapters.MyBaseAdapter;
import com.freetrip.trekker.bean.CommonListBean;
import com.freetrip.trekker.bean.LocalItemBean;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.MyHttpUtils;
import com.freetrip.trekker.utils.SearchUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.freetrip.trekker.widget.NoBGListView;
import com.google.gson.Gson;

public class Activity_Local extends BaseActivity implements OnClickListener{
   private FrameLayout fl_activity_local;
  //String url="http://cdn.sinacloud.net/cstour/testList.txt";
   private String url=GlobalContants.BASE_URL+"/10001/local.txt";
	private NoBGListView lv;
	// 获取的数据
	private List<LocalInfo> datas;
	
	private List<LocalInfo> mCloneDatas;
	//imageButton  默认不可见
	private ImageButton ib_local;
	
	//4个排序选项
	private TextView tv_sort_default;
	private TextView tv_sort_hot;
	private TextView tv_sort_instance;
	private TextView tv_sort_stars;
	// 当先选择分组条目的id
	private TextView currentItemSort;
	private LinearLayout ll_local_sort;
	// 排序标题的高和宽
	private int height;
	private int width;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_local);
	 initView();
	 initdata();
	 getActionBar().setDisplayHomeAsUpEnabled(true);//设置返回建
}
 private void  initView(){
	 fl_activity_local = (FrameLayout) findViewById(R.id.fl_activity_local);
		ib_local = (ImageButton) findViewById(R.id.ib_local);
		ib_local.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lv!=null){
				lv.smoothScrollToPosition(0);
				LinearLayout.LayoutParams params_up = (android.widget.LinearLayout.LayoutParams) ll_local_sort.getLayoutParams();
				params_up.topMargin=0;
				ll_local_sort.setLayoutParams(params_up);
				
				}
			}
		});
	ll_local_sort = (LinearLayout) findViewById(R.id.ll_local_sort);
	ll_local_sort.measure(0, 0);
	height = ll_local_sort.getMeasuredHeight();
	width = ll_local_sort.getMeasuredWidth();
	
	
		tv_sort_default = (TextView) findViewById(R.id.tv_sort_default);
	tv_sort_hot = (TextView) findViewById(R.id.tv_sort_hot);
	tv_sort_instance = (TextView) findViewById(R.id.tv_sort_instance);
	tv_sort_stars = (TextView) findViewById(R.id.tv_sort_stars);
	currentItemSort=tv_sort_default;

	tv_sort_hot.setOnClickListener(this);
	tv_sort_default.setOnClickListener(this);
	tv_sort_instance.setOnClickListener(this);
	tv_sort_stars.setOnClickListener(this);
 }
 private void initdata(){
	 
		 View loadpage_loading = UIUtils.infalte(R.layout.loadpage_loading);
		 fl_activity_local.removeAllViews();
			fl_activity_local.addView(loadpage_loading);
			ll_local_sort.setVisibility(View.INVISIBLE);

			
	 ThreadManager.getInstance().getThreadFromLongPool().execute(new Runnable() {
		@Override
		public void run() {
			String json = MyHttpUtils.loadJson(url);
			Gson gson=new Gson();
			LocalItemBean	itemBean = gson.fromJson(json, LocalItemBean.class);
			if(itemBean!=null){
			datas = itemBean.data.items;
			//复制一份用于排序
			 mCloneDatas=new ArrayList<LocalItemBean.LocalInfo>(datas);
		//	System.out.println(itemBean);
			lv = new NoBGListView(Activity_Local.this);
			//设置adapter
			UIUtils.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setAdapter(datas);
					fl_activity_local.removeAllViews();
					fl_activity_local.addView(lv);
					ll_local_sort.setVisibility(View.VISIBLE);

				}
			});
			//设置滑动监听
			setSrcollListener();
			//设置触摸监听
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
			fl_activity_local.removeAllViews();
			fl_activity_local.addView(loadpage_error);
			ll_local_sort.setVisibility(View.INVISIBLE);
				}
			});
		}
			 
		}
	});
  }
 private int startY;
protected void setTouchLis() {
	lv.setOnTouchListener(new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
			//	System.out.println("startY::::::"+startY);
				break;
			case MotionEvent.ACTION_MOVE:
				if(startY==0)
				startY = (int) event.getRawY();
			   int currentY=(int) event.getRawY();
				int deletY=currentY-startY;
			//	System.out.println("startY===="+startY+"height==="+height);
				LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) ll_local_sort.getLayoutParams();
				params.topMargin+=deletY;
			//	System.out.println("params.topMargin=="+params.topMargin+"deletY==="+deletY);
				if(params.topMargin<-height){
					params.topMargin=-height;
					// getActionBar().hide();//不管actionbar
				}
				if(params.topMargin>0){
					params.topMargin=0;
					//getActionBar().show();   //不管actionbar
				}
				ll_local_sort.setLayoutParams(params);
				startY=currentY;
				break;
			case MotionEvent.ACTION_UP:
				startY=0;
				LinearLayout.LayoutParams params_up = (android.widget.LinearLayout.LayoutParams) ll_local_sort.getLayoutParams();
				if(params_up.topMargin>-height/2){
					params_up.topMargin=0;
				}else{
					params_up.topMargin=-height;
				}
				ll_local_sort.setLayoutParams(params_up);
				//System.out.println("taiqi");
				break;
			}
			return false;
		}
	});
}
private void setSrcollListener(){
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
				if(position>curr){
				//	ll_local_sort.setVisibility(View.VISIBLE);
				//	getActionBar().show();
				}else{
			   //		ll_local_sort.setVisibility(View.GONE);
				//	getActionBar().hide();
				}
				break ;
			}
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if(firstVisibleItem<2){
				ib_local.setVisibility(View.INVISIBLE);
			}else{
				ib_local.setVisibility(View.VISIBLE);
			}
			
		//	if(view.getLastVisiblePosition()==datas.size()-1){
				//加载更多
//				System.out.println("//加载更多");
//				String json = MyHttpUtils.loadJson(url);
//				Gson gson=new Gson();
//				LocalItemBean	itemBean = gson.fromJson(json, LocalItemBean.class);
//				if(itemBean!=null){
//					datas.addAll(itemBean.data.items);
//					 mCloneDatas=new ArrayList<LocalInfo>(datas);
//					ListView v= (ListView) view;
//					MyBaseAdapter adapter = (MyBaseAdapter)v.getAdapter();
//					//adapter.notifyDataSetChanged();
//					adapter.addItems(itemBean.data.items);
//				}else{
//					Toast.makeText(mActivity, "没有更多的数据了", 0).show();
////				}
//			}
		}
	});
  }
	private void setAdapter(List<LocalInfo> data) {
		LocalAdapter adapter=new LocalAdapter(this);
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
					//  Toast.makeText(Activity_Local.this, query, Toast.LENGTH_SHORT).show();
					  if(mCloneDatas!=null&&mCloneDatas.size()>0){
					  //搜索提交的
					  for(int i = 0;i<mCloneDatas.size();i++){
						  String desc = mCloneDatas.get(i).desc;
						  mCloneDatas.get(i).similarity = SearchUtils.calculateSimilarity(desc, query);
					  }
					  Collections.sort(mCloneDatas, new LocalSearchComparetor());
			    	  setAdapter(mCloneDatas);
					  }else{
							 Toast.makeText(Activity_Local.this, "没有网络连接！", Toast.LENGTH_SHORT).show();
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
	  
	  //点击事件
	@Override
	public void onClick(View v) {
		//System.out.println(v.getId());
		setSortTextViewState((TextView)v);
		 sortItems(v.getId());
	}
	
	 // 全部enable false
	private void setSortTextViewState(TextView clickView){
		currentItemSort.setBackgroundResource(R.drawable.local_rectangle_normal);
		clickView.setBackgroundResource(R.drawable.local_rectangle_selected);
		currentItemSort=clickView;
	}
	//根据不同选择排序
    private void sortItems(int id){
    	switch(id){
    	case R.id.tv_sort_default:
    		setAdapter(datas);
    		break;
    	case R.id.tv_sort_hot:
    		Collections.sort(mCloneDatas, new LocalHotComparetor());
    		setAdapter(mCloneDatas);
    		break;
    	case R.id.tv_sort_instance:
    		Collections.sort(mCloneDatas, new LocalInstanceComparetor());
    		setAdapter(mCloneDatas);
    		break;
    	case R.id.tv_sort_stars:
    		Collections.sort(mCloneDatas, new LocalStarsComparetor());
    		setAdapter(mCloneDatas);
    		break;
    	}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
	
}
