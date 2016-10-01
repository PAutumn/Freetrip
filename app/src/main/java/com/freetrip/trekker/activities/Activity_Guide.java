package com.freetrip.trekker.activities;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freetrip.trekker.R;
import com.freetrip.trekker.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class Activity_Guide extends BaseActivity {
     @ViewInject(R.id.vp_guide)
	 ViewPager vp;
     @ViewInject(R.id.ll_guide)
     LinearLayout ll_guide;
     @ViewInject(R.id.fl_guide)
     FrameLayout fl_guide;
     @ViewInject(R.id.btn_guide)
     Button  btn_guide ;
     // 向导页的图片资源
     int[]  image={R.drawable.guide_01,R.drawable.guide_02,R.drawable.guide_03};
     
     List<ImageView> iv_list;
     
     // 小红点
     View redview;
// 2个小灰点的距离     
private int distance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		ViewUtils.inject(this);
		iv_list=new ArrayList<ImageView>();
		// 计算该手机上10个dip 多少个像素
		int a=UIUtils.dip2px(10);
		// 初始化图片
		for (int i = 0; i <image.length; i++) {
			ImageView iv=new ImageView(this);
			//iv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			iv.setBackgroundResource(image[i]);
			iv_list.add(iv);
			// 初始化小圆点
			View  view=new View(this);
			view.setBackgroundResource(R.drawable.guide_oval);
			//注意屏幕适配
			LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(a, a);
			if(i>0){
			params.leftMargin=a;
			}
			view.setLayoutParams(params);
			ll_guide.addView(view);
		}
		redview=new View(this);
		redview.setBackgroundResource(R.drawable.red_guide_oval);
		FrameLayout.LayoutParams redparams=new FrameLayout.LayoutParams(a, a);
		redview.setLayoutParams(redparams);
		fl_guide.addView(redview);
		
		MyViewPagerAdapter adapter=new MyViewPagerAdapter();
		vp.setAdapter(adapter);
		
		
		// 计算2个小圆点的距离

		// 监听LinearLayout 布局完成之后调用
		ll_guide.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
		
			@Override
			public void onGlobalLayout() {
		distance = ll_guide.getChildAt(1).getLeft()-ll_guide.getChildAt(0).getLeft();
			}
		});
		
		
		
		vp.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(position==iv_list.size()-1){
					btn_guide.setVisibility(View.VISIBLE);
				}else{
					btn_guide.setVisibility(View.INVISIBLE);
				}
			}
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) redview.getLayoutParams();
				
			//	System.out.println("position:"+position+"positionOffset:"+positionOffset);
				
				layoutParams.leftMargin=(int) (distance*position+distance*positionOffset);
				redview.setLayoutParams(layoutParams);
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		
	}
	
	@OnClick(R.id.btn_guide)
	public void tomain(View v){
		//进入主页面
		// 记录已经看过向导页面  下次进来不用开
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		sp.edit().putBoolean("isShowGuide", false).commit();
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	class MyViewPagerAdapter extends PagerAdapter{
		@Override
		public int getCount() {
			return iv_list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			container.addView(iv_list.get(position));
			
			return iv_list.get(position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
	}
	
}

