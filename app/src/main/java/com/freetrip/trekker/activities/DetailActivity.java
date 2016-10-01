package com.freetrip.trekker.activities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.Escaper;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;

import com.freetrip.trekker.R;
import com.freetrip.trekker.bean.DetailBean;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.FileUtils;
import com.freetrip.trekker.utils.MD5Utils;
import com.freetrip.trekker.utils.ToastUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 详情页
 * @author mwp
 *
 */
public class DetailActivity extends BaseActivity implements OnClickListener{

	private ImageView iv_back;//顶部返回
	private ViewPager vp_tops;//ViewPager
	private RelativeLayout rl_map;//百度地图
	private TextView tv_map;//百度地图的文字描述
	private TextView tv_tel;//电话号码
	private WebView webView;//网页描述
	private DetailBean deatilInfo;
	private TextView tv_detail_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initViews();
		initDatas();
	}

	private void initViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_detail);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
		vp_tops = (ViewPager) findViewById(R.id.vp_tops);
		dot_layout = (LinearLayout) findViewById(R.id.dot_layout);
		rl_map = (RelativeLayout) findViewById(R.id.rl_map);
		tv_map = (TextView) findViewById(R.id.tv_map);
		tv_tel = (TextView) findViewById(R.id.tv_tel);
		iv_share = (ImageView) findViewById(R.id.iv_share);
		webView = (WebView) findViewById(R.id.wv);
		
		iv_want = (ImageView) findViewById(R.id.iv_want);
		iv_visited = (ImageView) findViewById(R.id.iv_visited);
		tv_want = (TextView) findViewById(R.id.tv_want);
		tv_visited = (TextView) findViewById(R.id.tv_visited);
		
		
		myPagerAdapter = new MyPagerAdapter();
		
		iv_back.setOnClickListener(this);
		rl_map.setOnClickListener(this);
		tv_tel.setOnClickListener(this);
		iv_share.setOnClickListener(this);
		
		iv_want.setOnClickListener(this);
		iv_visited.setOnClickListener(this);
	}
	
	private void initDatas() {
		detail_url = getIntent().getStringExtra("detail_url");

		if (TextUtils.isEmpty(detail_url)) {
			detail_url = "/10003/item_0/item_0.txt";
		}
		detail_url = GlobalContants.BASE_URL+detail_url;
	    //detail_url = "http://192.168.3.45:8080/aa/10003/item_0/item_0.txt";
		
	    ThreadManager.getInstance().getThreadFromLongPool().execute(new Runnable() {
			
			@Override
			public void run() {
				loadLocal();
			}
		});

	}

	private void loadLocal() {
		try {
			File cacheDir = FileUtils.getCacheDir();
			File file = new File(cacheDir, MD5Utils.encode(detail_url));
			if (!file.exists()) {
				System.out.println("读取网络数据");
				LoadServer();
			}else {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				long outOfDate = Long.parseLong(br.readLine());
				if (System.currentTimeMillis()>outOfDate) {
					System.out.println("缓存过期读取网络");
					LoadServer();
				}else {
					String str = null;
					StringWriter sw = new StringWriter();
					while((str=br.readLine())!=null){
						sw.write(str);
					}
					System.out.println("读取本地缓存");
					parseData(sw.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void LoadServer() {
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, detail_url, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastUtils.show("网络连接失败");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				String json = arg0.result;
				savelocal(json);
				System.out.println(json);
				parseData(json);
			}
		});
		
	}

	protected void savelocal(String json) {
		BufferedWriter bw = null;
		try {
			File cacheDir = FileUtils.getCacheDir();
			File file = new File(cacheDir, MD5Utils.encode(detail_url));
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(System.currentTimeMillis() + 1000*60*60*24 + "");
			bw.newLine();
			bw.write(json);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			FileUtils.close(bw);
		}
	}

	protected void parseData(String json) {
		deatilInfo = new DetailBean();
		try {
			JSONObject root = new JSONObject(json);
			JSONArray tops = root.getJSONArray("tops");
			ArrayList<String> imgs = new ArrayList<String>();
			for(int i=0;i<tops.length();i++){
				imgs.add(tops.getJSONObject(i).getString("img_"+i));
			}
			deatilInfo.tops = imgs;
			deatilInfo.wantto = root.getString("wantto");
			deatilInfo.visited = root.getString("visited");
			JSONObject map = root.getJSONObject("map");
			deatilInfo.text = map.getString("text");
			deatilInfo.latitude = map.getDouble("latitude");
			deatilInfo.longitude = map.getDouble("longitude");
			deatilInfo.tel = root.getString("tel");
			deatilInfo.url = root.getString("url");
				
			myPagerAdapter.notifyDataSetChanged();
			
			Message message = Message.obtain();
			message.what = 1;
			handler.sendMessage(message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				vp_tops.setCurrentItem(vp_tops.getCurrentItem() + 1);
				Message message = Message.obtain();
				message.what = 0;
				handler.sendMessageDelayed(message, 3000);
				break;
			case 1:
				System.out.println("是耗时吗？");
				initDots();
				refreshViews();
				break;
			default:
				break;
			}
			
			
		};
	};
	private LinearLayout dot_layout;
	private MyPagerAdapter myPagerAdapter;
	private ImageView iv_share;
	private String detail_url;
	private ImageView iv_want;
	private ImageView iv_visited;
	private TextView tv_want;
	private TextView tv_visited;
	private void refreshViews() {
        tv_detail_title.setText(deatilInfo.text);
		
        tv_want.setText(deatilInfo.wantto);
        tv_visited.setText(deatilInfo.visited);
       
		vp_tops.setAdapter(myPagerAdapter);
		vp_tops.setCurrentItem(100 - 100 % deatilInfo.tops.size());
		Message msg = Message.obtain();
		msg.what = 0;
		handler.sendMessageDelayed(msg, 3000);
//
		vp_tops.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				int currentPage = vp_tops.getCurrentItem() % deatilInfo.tops.size();

				for (int i = 0; i < dot_layout.getChildCount(); i++) {
					dot_layout.getChildAt(i).setEnabled(i == currentPage);
				}
			}
		});
		
		tv_tel.setText(deatilInfo.tel);
		webView.loadUrl(GlobalContants.BASE_URL+deatilInfo.url);
		//在当前WebView中响应内部点击
		webView.setWebViewClient(new WebViewClient() {
	         @Override
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	             view.loadUrl(url);
	             return true;
	         }
	     });
		
//		((ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0).invalidate();
		
		System.out.println("刷新UI成功");
	}
	
	private void initDots() {
		if (deatilInfo!=null) {
			for (int i = 0; i < deatilInfo.tops.size(); i++) {
				View view = new View(this);
				LayoutParams params = new LayoutParams(UIUtils.dip2px(8),
						UIUtils.dip2px(8));
				params.rightMargin = UIUtils.dip2px(5);
				view.setLayoutParams(params);
				view.setBackgroundResource(R.drawable.selector_dot);
				dot_layout.addView(view);
			}
			System.out.println("初始化小圆点成功");
		}
		
	}

	private boolean iswantclick = false;
	private boolean isvisitedclick = false;
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_map:
			//TODO 启动百度地图
			if (deatilInfo!=null) {
				Intent intent = new Intent(DetailActivity.this, BaiduMapActivity.class);
				intent.putExtra("latitude", deatilInfo.latitude);
				intent.putExtra("longitude", deatilInfo.longitude);
				intent.putExtra("title", deatilInfo.text);
				startActivity(intent);
			}
			break;
		case R.id.tv_tel:
			//TODO 打电话
			if (deatilInfo!=null) {
				showCallDailog();
			}
			break;

		case R.id.iv_share:
		    showShare();
			break;
		
		case R.id.iv_want:
			if (deatilInfo!=null) {
				if (!iswantclick) {
					iswantclick = true;
					iv_want.setImageResource(R.drawable.detail_haswant_hold);
					tv_want.setText((Integer.parseInt(deatilInfo.wantto)+1)+"");
				}else {
					iswantclick = false;
					iv_want.setImageResource(R.drawable.detail_haswant);
					tv_want.setText(Integer.parseInt(deatilInfo.wantto)+"");
				}
				
			}
			break;
		case R.id.iv_visited:
			if (deatilInfo!=null) {
				if (!isvisitedclick) {
					isvisitedclick = true;
					iv_visited.setImageResource(R.drawable.detail_hasbeen_hold);
					tv_visited.setText((Integer.parseInt(deatilInfo.visited)+1)+"");
				}else {
					isvisitedclick = false;
					iv_visited.setImageResource(R.drawable.detail_hasbeen);
					tv_visited.setText(Integer.parseInt(deatilInfo.visited)+"");
				}
			}
				
			break;
		
		default:
			break;
		}
	}

	/**一键分享*/
	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		
		oks.setTheme(OnekeyShareTheme.SKYBLUE);//设置天蓝色的主题
		
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(getString(R.string.ssdk_oks_share));
		// text是分享文本，所有平台都需要这个字段
		oks.setText("说点什么吧...");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");
		
		// 启动分享GUI
		oks.show(this);
	}

	/**用一个对话框打电话*/
	private void showCallDailog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("打电话");
		builder.setMessage("打电话:"+deatilInfo.tel);
		builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:"+deatilInfo.tel));
				startActivity(intent);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	class MyPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			View view = View.inflate(DetailActivity.this,
					R.layout.item_home_viewpager, null);
			ImageView image = (ImageView) view.findViewById(R.id.image);
			BitmapUtils bitmapUtils = new BitmapUtils(getApplicationContext());
			bitmapUtils.display(image, GlobalContants.BASE_URL+deatilInfo.tops.get(position % deatilInfo.tops.size()));

			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
	}

}
