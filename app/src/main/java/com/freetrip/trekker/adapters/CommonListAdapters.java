package com.freetrip.trekker.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freetrip.trekker.R;
import com.freetrip.trekker.Dao.CollectionDao;
import com.freetrip.trekker.activities.BigPicActivity;
import com.freetrip.trekker.activities.DetailActivity;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.utils.MyBitmapUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

public class CommonListAdapters extends MyBaseAdapter<CommonInfo> {
	private String url = GlobalContants.BASE_URL;
	private BitmapUtils bitmaputils;
	int[] pics = { R.drawable.test_pic, R.drawable.default_pic,
			R.drawable.test_pic03 };
	private CollectionDao dao;

	public CommonListAdapters(Context context) {
		super(context);
		bitmaputils = new BitmapUtils(context);
		dao = new CollectionDao();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		final CommonInfo info = itemList.get(i);
		CommonHolder holder;
		if (view == null) {
			holder = new CommonHolder();
			view = View.inflate(context, R.layout.item_common, null);
			holder.item_local_titile = (TextView) view
					.findViewById(R.id.item_local_titile);
			holder.item_local_desc = (TextView) view
					.findViewById(R.id.item_local_desc);
			holder.item_local_rating = (RatingBar) view
					.findViewById(R.id.item_local_rating);
			holder.item_local_icon = (ImageView) view
					.findViewById(R.id.item_local_icon);
			holder.tv_local_praise = (CheckBox) view
					.findViewById(R.id.tv_local_praise);
			view.setTag(holder);
		} else {
			holder = (CommonHolder) view.getTag();
		}
		holder.item_local_titile.setText(info.text);
		holder.item_local_desc.setText(info.desc);
		holder.item_local_rating.setRating(info.star);
		holder.tv_local_praise.setChecked(dao.findData(info.img));
		holder.tv_local_praise.setText((info.praise+(holder.tv_local_praise.isChecked()?1:0))+"");
		
		bitmaputils.configDefaultLoadingImage(pics[1]);
		MyBitmapUtils.loadBitmap(holder.item_local_icon, url + info.img);

		holder.tv_local_praise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// System.out.println("dianle RadioButton");
				CheckBox b = (CheckBox) v;
				if (b.isChecked()) {
					b.setText((info.praise + 1) + "");
					// 添加到数据库
					dao.addCollection(info);
				} else {
					b.setText(info.praise + "");
					// 删除数据库中的数据
					dao.deleteCollection(info.img);
				}
			}
		});

		// 设置双击查看大图点击事件
		holder.item_local_icon.setOnClickListener(new OnClickListener() {
			private long first;
			private boolean isDoubleClicked = false;
			@Override
			public void onClick(View v) {
				long now=System.currentTimeMillis();
				new Thread() {
					public void run() {
//							isDoubleClicked = false;
							SystemClock.sleep(300);
							if(!isDoubleClicked){//按照单击事件处理
							 String partPath=info.url;
							 Intent intent=new Intent(context,DetailActivity.class);
							 intent.putExtra("detail_url", partPath);
							UIUtils.startActivity((intent));
							}
					};
				}.start();
				
				
				if(now-first<300){
					isDoubleClicked = true;
				}else{
					isDoubleClicked = false;
				}
				if(isDoubleClicked){
				//双击了
					Intent intent=new Intent(context,BigPicActivity.class);
					intent.putExtra("url", url+info.img);
					UIUtils.startActivity((intent));
				}
				 first=now;
			}
		});
		return view;
	}

	class CommonHolder {
		TextView item_local_desc;
		RatingBar item_local_rating;
		TextView item_local_titile;
		ImageView item_local_icon;
		CheckBox tv_local_praise;
	}

}
