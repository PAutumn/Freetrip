package com.freetrip.trekker.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.freetrip.trekker.R;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PhotographActivity extends BaseActivity {

	private int mPointWidth;

	//private String url = "http://192.168.3.18:8080";
	private String url = GlobalContants.BASE_URL;
	private ViewPager vp_show;
	private MyPagerAdapter adapter;

	private View view_red_point;
	private LinearLayout ll_point_group;
	private LinearLayout ll_home;

	private List<Object> mImageList;

	int a=UIUtils.dip2px(10);
	Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1:

				System.out.println("解析完成");
				fillViews();

				break;

			default:
				break;
			}

		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();

	}

	private void initView() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photograph);

		vp_show = (ViewPager) findViewById(R.id.vp_show);
		ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
		ll_home = (LinearLayout) findViewById(R.id.ll_home);
		
		
		view_red_point = findViewById(R.id.view_red_point);
		

		ll_home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PhotographActivity.this,
						MainActivity.class);

				startActivity(intent);
				overridePendingTransition(R.anim.show_in, R.anim.show_out);
				// overridePendingTransition(R.anim.in_from_left,
				// R.anim.out_to_right);
			}
		});

		adapter = new MyPagerAdapter();

		initData();

	}

	private void initData() {
		HttpUtils httpUtils = new HttpUtils();
		String mUrl = url + "/10006/list_6.txt";
		httpUtils.send(HttpMethod.GET, mUrl, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {

				System.out.println(msg);
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				String result = responseInfo.result;
				System.out.println(result);

				parserData(result);

				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				mHandler.sendMessage(msg);

			}

		});
	}

	private void parserData(String result) {
		try {
			JSONObject root = new JSONObject(result);

			JSONArray images = root.getJSONArray("pic");

			mImageList = new ArrayList<Object>();
			for (int i = 0; i < images.length(); i++) {

				// System.out.println(images.getJSONObject(i).get("img_" + i));

				mImageList.add(images.getJSONObject(i).get("img_" + i));

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void fillViews() {

		vp_show.setAdapter(adapter);
		vp_show.setOnPageChangeListener(new GuidePageListener());

		// 初始化小圆点
		if (mImageList != null) {

			for (int i = 0; i < mImageList.size(); i++) {

				View point = new View(this);
				// 小灰点
				point.setBackgroundResource(R.drawable.shape_point_gray);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						a, a);
				if (i > 0) {
					params.leftMargin = a;
				}
				point.setLayoutParams(params);
				ll_point_group.addView(point);
			}

		}

		ll_point_group.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						ll_point_group.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);

						mPointWidth = ll_point_group.getChildAt(1).getLeft()
								- ll_point_group.getChildAt(0).getLeft();

					}
				});

		}

	/*
	 * viewPager适配器
	 */

	class GuidePageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int postion, float postionOffset,
				int positionOffsetPixels) {
			// "位置:" + position
			// "偏移量:" + positionOffset
			// "偏移百分比:" + positionOffsetPixels);

			int len = (int) (mPointWidth * postionOffset) + postion
					* mPointWidth;

			RelativeLayout.LayoutParams params = (LayoutParams) view_red_point
					.getLayoutParams();
			params.leftMargin = len;
			view_red_point.setLayoutParams(params);

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub

		}

	}

	/*
	 * 创建适配器
	 */
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View view = View.inflate(PhotographActivity.this,
					R.layout.item_photograph, null);
			ImageView imageView = (ImageView) view
					.findViewById(R.id.iv_photograph);

			BitmapUtils bitmapUtils = new BitmapUtils(getApplicationContext());

			bitmapUtils.display(imageView, url + mImageList.get(position));
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			// super.destroyItem(container, position, object);
		}

	}

}
