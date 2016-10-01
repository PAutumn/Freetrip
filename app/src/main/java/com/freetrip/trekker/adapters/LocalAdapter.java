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
import com.freetrip.trekker.bean.CommonListBean;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.bean.LocalItemBean.LocalInfo;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.utils.MyBitmapUtils;
import com.lidroid.xutils.BitmapUtils;

public class LocalAdapter extends MyBaseAdapter<LocalInfo> {
	   private String url=GlobalContants.BASE_URL;

	private BitmapUtils bitmaputils;
    int[] pics={R.drawable.test_pic,R.drawable.default_pic,R.drawable.test_pic03};

	private CollectionDao dao;
	public LocalAdapter(Context context) {
		super(context);
		bitmaputils = new BitmapUtils(context);
		dao = new CollectionDao();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		final LocalInfo info = itemList.get(i);
		Holder holder;
		if(view==null){
			holder=new Holder();
		view= View.inflate(context, R.layout.item_local_listview, null);
		holder.item_local_titile=(TextView) view.findViewById(R.id.item_local_titile);
		holder.item_local_desc=(TextView) view.findViewById(R.id.item_local_desc);
		holder.item_local_rating=(RatingBar) view.findViewById(R.id.item_local_rating);
		holder.item_local_icon=(ImageView) view.findViewById(R.id.item_local_icon);
		holder.tv_local_instance=(TextView) view.findViewById(R.id.tv_local_instance);
		holder.cb_local=(CheckBox) view.findViewById(R.id.cb_local);
		view.setTag(holder);
		}else{
			holder=(Holder) view.getTag();
		}
		holder.item_local_titile.setText(info.text); 
		holder.item_local_desc.setText(info.desc);
		holder.item_local_rating.setRating(info.star);
		holder.tv_local_instance.setText(info.instance+"km");
		bitmaputils.configDefaultLoadingImage(pics[1]);
		MyBitmapUtils.loadBitmap(holder.item_local_icon, url+info.img);
			holder.cb_local.setChecked(dao.findData(info.img));
			holder.cb_local.setText((info.praise+(holder.cb_local.isChecked()?1:0))+"");
		holder.cb_local.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox b = (CheckBox) v;
				if (b.isChecked()) {
					b.setText((info.praise + 1) + "");
					// 添加到数据库
					CommonInfo commonInfo=new CommonListBean().new CommonInfo();
					commonInfo.img =info.img;
					commonInfo.text = info.text;
					commonInfo.desc = info.desc;
					commonInfo.url = info.url;
					commonInfo.praise = info.praise;
					commonInfo.star =info.star;
					dao.addCollection(commonInfo);
					
				} else {
					b.setText(info.praise + "");
					// 从数据库删除
					dao.deleteCollection(info.img);
				}
			}
		});
		
		
		// 设置双击查看大图点击事件

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
							SystemClock.sleep(500);
							if(!isDoubleClicked){
							 String partPath=info.url;
							 Intent intent=new Intent(context,DetailActivity.class);
							 intent.putExtra("detail_url", partPath);
							 context.startActivity(intent);
							}
					};
				}.start();
				if(now-first<500){
					isDoubleClicked = true;
				}else{
					isDoubleClicked = false;
				}
				if(isDoubleClicked){
				// //双击了
					Intent intent=new Intent(context,BigPicActivity.class);
					intent.putExtra("url", url+info.img);
					context.startActivity(intent);
				}
				// }else{
				
				// }
				 first=now;
			}
		});
		
		//int m=i%3;
		//holder.item_local_icon.setImageResource(pics[m]);
		return view;
	}
	class Holder {
		TextView item_local_desc;
		RatingBar item_local_rating;
		TextView item_local_titile;
		ImageView item_local_icon;
		TextView tv_local_instance;
		CheckBox cb_local;
	}
}
