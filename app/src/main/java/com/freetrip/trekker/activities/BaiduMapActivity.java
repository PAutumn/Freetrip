package com.freetrip.trekker.activities;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.freetrip.trekker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class BaiduMapActivity extends BaseActivity {
	
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private double latitude;//=39.914690;// 39.9146900000,116.4039750000
	private double longitude;//=116.403975;//
	private LatLng homePosition;// =new LatLng(latitude, longitude);
	private Button btn_star;
	private Button btn_compass;
	private View pop;
	private TextView title;
	private String text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		//initManager();
		setContentView(R.layout.activity_baidumap);
		
		init();
		draw();
		initPop();	
	}

/*	private void initManager() {
		SDKInitializer.initialize(getApplicationContext());
			
	}
	*/
	/*
	 * 绘制地图，默认为
	 * */
	private void init() {

		mMapView = (MapView) findViewById(R.id.mv_mapview);
		mBaiduMap = mMapView.getMap();

		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		
		
		Intent intent = getIntent();
		latitude=intent.getDoubleExtra("latitude", 39.914690);
		longitude=intent.getDoubleExtra("longitude", 116.403975);
		text=intent.getStringExtra("title");
		homePosition = new LatLng(latitude, longitude);
		
		
		//System.out.println(latitude+":"+longitude+":"+text);
		
		MapStatusUpdate newLatLng = MapStatusUpdateFactory.newLatLng(homePosition);
		mBaiduMap.setMapStatus(newLatLng);
		
	}
	
	private void initPop() {
		// 加载pop 添加到mapview 设置为隐藏
		
		pop = View.inflate(getApplicationContext(), R.layout.map_pop, null);
		LayoutParams params = new MapViewLayoutParams.Builder()
		.layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)// 按照经纬度设置位置
		.position(homePosition)// 不能传null 设置为mapMode时 必须设置position
		.width(MapViewLayoutParams.WRAP_CONTENT)
		.height(MapViewLayoutParams.WRAP_CONTENT)
		.yOffset(-10)
		.build();
		mMapView.addView(pop, params);
		pop.setVisibility(View.VISIBLE);
		title = (TextView) pop.findViewById(R.id.title);
		title.setText(text);
	}
	
	/*
	 * 绘制坐标
	 * */
	private void draw() {

		BitmapDescriptor bitmapDes = BitmapDescriptorFactory
				.fromResource(R.drawable.map_icon_route_selected);

		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(homePosition)// 设置位置
				.icon(bitmapDes)// 设置图标
				.draggable(true)// 设置是否可以拖拽 默认是否
				.title(text);// 设置标题
		mBaiduMap.addOverlay(markerOptions);
		
	}
	
	
	
	

	public void show_compass(View v){
		
		mBaiduMap.getUiSettings().setCompassEnabled(true);
		
	}
	public void show_star(View v){
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		mBaiduMap.setTrafficEnabled(false);
		
	}
	public void normal(View v){
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mBaiduMap.setTrafficEnabled(false);
		
	}

	

	@Override
	protected void onResume() {

		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mMapView.onDestroy();
		super.onDestroy();
	}
}
