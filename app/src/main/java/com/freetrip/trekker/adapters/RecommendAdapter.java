package com.freetrip.trekker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freetrip.trekker.R;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.lidroid.xutils.BitmapUtils;

public class RecommendAdapter extends MyBaseAdapter<CommonInfo> {
private BitmapUtils bitmapUtil;
private String url = "http://192.168.191.1:8080";
	public RecommendAdapter(Context context) {
		super(context);
		bitmapUtil = new BitmapUtils(context);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		View v = view;
		if(view==null){
			v = inflater.inflate(R.layout.item_recommend_listview, viewGroup,false);
			holder = new ViewHolder();
			holder.ivRecommend = (ImageView) v.findViewById(R.id.iv_recommend);
			holder.tvTitle = (TextView) v.findViewById(R.id.tv_recommend_title);
			holder.tvDesc = (TextView) v.findViewById(R.id.tv_recommend_desc);
			v.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		CommonInfo item = itemList.get(i);
		bitmapUtil.display(holder.ivRecommend, url+item.img);
		holder.tvDesc.setText(item.desc);
		holder.tvTitle.setText(item.text);
		
		return v;
	}
	
	class ViewHolder{
		ImageView ivRecommend;
		TextView tvTitle;
		TextView tvDesc;
	}

}
