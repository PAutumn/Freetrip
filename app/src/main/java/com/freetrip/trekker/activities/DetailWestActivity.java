package com.freetrip.trekker.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.freetrip.trekker.R;
import com.freetrip.trekker.adapters.MyBaseAdapter;
import com.freetrip.trekker.bean.ListItemBean;
import com.freetrip.trekker.bean.ListItemBean.Item;
import com.freetrip.trekker.common.GlobalContants;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class DetailWestActivity extends BaseActivity {
	ListView mDetailWestListView;
	List<Item> detailWestBeanList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_detail_west);
		init();
		// initView();
	}

	private void init() {
		String url = GlobalContants.urlRoot + "testList.txt";
		Log.i("aaa", "url:" + url);
		detailWestBeanList = new ArrayList<Item>();
		// String json = "aaaaaaaaa";
		final String jsonArray[] = new String[1];
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("aaa", "shibaile");// 失败了
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				jsonArray[0] = arg0.result;
				Log.i("aaa", jsonArray[0]);
				initArrayList(jsonArray);
				initView();
			}
		});
		
	}

	private void initArrayList(final String[] jsonArray) {
		Gson gson = new Gson();
		Log.i("aaa", "jsonHere:" + jsonArray[0]);
		ListItemBean listItemBean = gson.fromJson(jsonArray[0],
				ListItemBean.class);
		if ("200".equals(listItemBean.getRetcode())) {
			for (Item item : listItemBean.getData().getItems()) {
				detailWestBeanList.add(item);
			}
		}
	}


	private void initView() {
		mDetailWestListView = (ListView) findViewById(R.id.lv_detail_west);
		DetailWestAdapter adapter = new DetailWestAdapter(this);
		adapter.addItems(detailWestBeanList);
		mDetailWestListView.setAdapter(adapter);
	}

	/**
	 * detailwest的适配器
	 * 
	 * @author Administrator
	 *
	 */
	class DetailWestAdapter extends MyBaseAdapter<Item> {

		public DetailWestAdapter(Context context) {
			super(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			// itemList.get(position).get
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_item_west, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tv_place = (TextView) convertView
						.findViewById(R.id.tv_place);
				holder.tv_des = (TextView) convertView
						.findViewById(R.id.tv_des);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			BitmapUtils bitmapUtils = new BitmapUtils(DetailWestActivity.this);
			bitmapUtils.display(holder.iv, GlobalContants.urlRoot
					+ itemList.get(position).getImg());
			holder.tv_title.setText(itemList.get(position).getText());
			holder.tv_place.setText(itemList.get(position).getLocation());
			holder.tv_des.setText(itemList.get(position).getDesc());

			return convertView;
		}

		class ViewHolder {
			ImageView iv;
			TextView tv_title;
			TextView tv_place;
			TextView tv_des;
		}
	}

}
