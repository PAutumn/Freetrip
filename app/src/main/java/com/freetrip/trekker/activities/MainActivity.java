package com.freetrip.trekker.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.freetrip.trekker.R;
import com.freetrip.trekker.adapters.RecommendAdapter;
import com.freetrip.trekker.bean.CommonListBean;
import com.freetrip.trekker.bean.CommonListBean.CommonInfo;
import com.freetrip.trekker.common.GlobalContants;
import com.freetrip.trekker.manager.ThreadManager;
import com.freetrip.trekker.utils.MyHttpUtils;
import com.freetrip.trekker.utils.SpfUtils;
import com.freetrip.trekker.utils.ToastUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.freetrip.trekker.widget.DragLayout;
import com.freetrip.trekker.widget.DragLayout.Status;
import com.freetrip.trekker.widget.DragLayout.onDragStatusChangeListener;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;

/**
 * 主页
 * 
 * @author mwp
 *
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	private static final int LOGIN_REQUESET_CODE = 10;
	private ViewPager vp_home;
	private GridView gv_home;

	private boolean isOpen = false;// 侧滑菜单是否打开

	private int[] pagerList = new int[] { R.drawable.top1, R.drawable.top2,
			R.drawable.top3, R.drawable.top4 };

	private int[] gv_icons = new int[] { R.drawable.gv_01, R.drawable.gv_02,
			R.drawable.gv_03, R.drawable.gv_04, R.drawable.gv_05,
			R.drawable.gv_06 };

	private String[] gv_titles = new String[] { "不得不去", "淡季远行", "美食不负", "城市热门",
			"关外风情", "老城记事" };

	/**记录当前MainActivity中，当注销用户是需要使用到此对象*/
	public static MainActivity currentMainActivity;
	
	
    Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (isOpen) {
				handler.removeCallbacksAndMessages(null);
                vp_home.setEnabled(false);
			} else {
				vp_home.setCurrentItem(vp_home.getCurrentItem() + 1);
				vp_home.setEnabled(true);
			}
			handler.sendEmptyMessageDelayed(0, 3000);

		};
	};
	private DragLayout dragLayout;
	private LinearLayout dot_layout;
	private RelativeLayout user_layout;
	private RelativeLayout collect_layout;
	private RelativeLayout setting_layout;
	private RelativeLayout feedback_layout;
	private TextView tv_login;

	private String test_url = GlobalContants.BASE_URL;
	private boolean is_login;
	private ImageView iv_photo;
	private RelativeLayout exit_layout;
	
	private String rec_url = GlobalContants.BASE_URL + "/10008/list_8.txt";
	private List<CommonInfo> getedItems;
	private CommonInfo recommendItem;
	
	private RelativeLayout rl_recommend;
	private ImageView iv_recommend;
	private TextView tv_recommend_title;
	private TextView tv_recommend_desc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		currentMainActivity = this;
		
		initViews();
		initMenu();
		initDots();
		initrecomened();
		loadRecommend();
		
		if (isOpen) {
			gv_home.setEnabled(false);
		}else {
			gv_home.setEnabled(true);
		}
	}

	private void initrecomened() {
		rl_recommend = (RelativeLayout) findViewById(R.id.rl_recommend);
		iv_recommend = (ImageView) findViewById(R.id.iv_recommend);
		tv_recommend_title = (TextView) findViewById(R.id.tv_recommend_title);
		tv_recommend_desc = (TextView) findViewById(R.id.tv_recommend_desc);
	}

	@Override
	protected void onDestroy() {
		currentMainActivity = null;
		super.onDestroy();
	}
	
	// 初始化侧滑菜单
	private void initMenu() {
		user_layout = (RelativeLayout) findViewById(R.id.user_layout);
		collect_layout = (RelativeLayout) findViewById(R.id.collect_layout);
		setting_layout = (RelativeLayout) findViewById(R.id.setting_layout);
		feedback_layout = (RelativeLayout) findViewById(R.id.feedback_layout);
		
		exit_layout = (RelativeLayout) findViewById(R.id.exit_layout);

		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		tv_login = (TextView) findViewById(R.id.tv_login);

		tv_login.setOnClickListener(this);

		user_layout.setOnClickListener(this);
		collect_layout.setOnClickListener(this);
		setting_layout.setOnClickListener(this);
		feedback_layout.setOnClickListener(this);
		exit_layout.setOnClickListener(this);

		initUserInfo();
	}

	// 初始化用户登录信息
	private void initUserInfo() {
		is_login = SpfUtils.getBoolean(this, "is_login", false);

		if (is_login) {
			tv_login.setText(SpfUtils.getString(this, "user_name", ""));
			String user_cion = SpfUtils.getString(this,"user_icon", "");
			
			if (TextUtils.isEmpty(user_cion)) {
				Bitmap	bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
				ToastUtils.show("获取头像失败，刷新试试");
				iv_photo.setImageBitmap(toRoundCorner(bitmap, UIUtils.dip2px(200)));
			}else {
				File f = new File(user_cion);
				if (f.exists()&&f.length()>0) {
					Bitmap  bitmap = BitmapFactory.decodeFile(user_cion);
					iv_photo.setImageBitmap(toRoundCorner(bitmap, UIUtils.dip2px(200)));
				}else {
					Bitmap	bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.head);
					ToastUtils.show("获取头像失败，刷新试试");
					iv_photo.setImageBitmap(toRoundCorner(bitmap, UIUtils.dip2px(200)));
				}
				
				
			}
			
			
			// iv_photo.setImageURI(Uri.parse(SpfUtils.getString(this,
			// "user_icon", "")));
		} else {
			tv_login.setText("登录/注册");
			iv_photo.setImageResource(R.drawable.head);
		}
	}

	private void initViews() {
		setContentView(R.layout.activity_main);

		dragLayout = (DragLayout) findViewById(R.id.dl);

		dragLayout.setDragStatusListener(new onDragStatusChangeListener() {

			@Override
			public void onOpen() {
				updateMenuStatus();
			}

			@Override
			public void onDraging(float percent) {
				updateMenuStatus();
			}

			@Override
			public void onClose() {
				updateMenuStatus();
			}
		});

	
		LinearLayout ll_more = (LinearLayout) findViewById(R.id.ll_more);

		ll_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						PhotographActivity.class);

				startActivity(intent);
				finish();

				overridePendingTransition(R.anim.show_in, R.anim.show_out);
				// overridePendingTransition(R.anim.in_from_right,
				// R.anim.out_to_left);

			}
		});

		vp_home = (ViewPager) findViewById(R.id.vp_home);
		dot_layout = (LinearLayout) findViewById(R.id.dot_layout);
		vp_home.setAdapter(new MyViewPagerAdapter());
		int centerValue = Integer.MAX_VALUE / 2;
		vp_home.setCurrentItem(centerValue - centerValue % pagerList.length);
		handler.sendEmptyMessageDelayed(0, 3000);

		vp_home.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 更新小圆点
				int currentPage = vp_home.getCurrentItem() % pagerList.length;

				for (int i = 0; i < dot_layout.getChildCount(); i++) {
					dot_layout.getChildAt(i).setEnabled(i == currentPage);
				}
			}
		});

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < gv_icons.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", gv_icons[i]);
			map.put("title", gv_titles[i]);
			data.add(map);
		}

		gv_home = (GridView) findViewById(R.id.gv_home);
		gv_home.setAdapter(new SimpleAdapter(this, data,
				R.layout.item_home_gridview, new String[] { "icon", "title" },
				new int[] { R.id.iv_grid, R.id.tv_grid }));

		// =============================================
		// 在这里处理每个Item点击的响应
		// ================================================
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 3:
					Intent intent = new Intent(MainActivity.this,
							Activity_Local.class);
					startActivity(intent);
					break;
				case 5:
					Intent intent1 = new Intent(MainActivity.this,
							Activity_CommonList.class);
					intent1.putExtra("url", test_url + "/10000/list_0.txt");
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(MainActivity.this,
							Activity_CommonList.class);
					intent2.putExtra("url", test_url + "/10002/list_2.txt");
					startActivity(intent2);

					break;
				case 0:
					Intent intent3 = new Intent(MainActivity.this,
							Activity_CommonList.class);
					intent3.putExtra("url", test_url + "/10003/list_3.txt");
					startActivity(intent3);
					break;
				case 1:
					Intent intent4 = new Intent(MainActivity.this,
							Activity_CommonList.class);
					intent4.putExtra("url", test_url + "/10004/list_4.txt");
					startActivity(intent4);

					break;
				case 4:
					Intent intent5 = new Intent(MainActivity.this,
							Activity_CommonList.class);
					intent5.putExtra("url", test_url + "/10005/list_5.txt");
					startActivity(intent5);

					break;

				default:
					break;
				}
			}
		});

//		// ListView初始化
//		recommend_listview = (ListView) findViewById(R.id.lv_recommend);
//		setAdapter(recItems);
//		loadRecommend();
//		recommend_listview.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				String detail_url = recItems.get(position).url;
//				Intent intent = new Intent(MainActivity.this,
//						DetailActivity.class);
//				intent.putExtra("detail_url", detail_url);
//				startActivity(intent);
//			}
//		});
	}

	private void updateMenuStatus() {
		if (dragLayout.getmStatus() == Status.Close) {
			isOpen = false;
		} else {
			isOpen = true;
		}
		
		if (isOpen) {
			gv_home.setEnabled(false);
		}else {
			gv_home.setEnabled(true);
		}
	}

	private void initDots() {
		for (int i = 0; i < pagerList.length; i++) {
			View view = new View(this);
			LayoutParams params = new LayoutParams(UIUtils.dip2px(8),
					UIUtils.dip2px(8));
			params.rightMargin = UIUtils.dip2px(5);
			view.setLayoutParams(params);
			view.setBackgroundResource(R.drawable.selector_dot);
			dot_layout.addView(view);
		}
	}

	class MyViewPagerAdapter extends PagerAdapter {

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

			View view = View.inflate(MainActivity.this,
					R.layout.item_home_viewpager, null);
			ImageView image = (ImageView) view.findViewById(R.id.image);
			image.setImageResource(pagerList[position % pagerList.length]);

			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	/**
	 * 侧滑菜单条目的点击在此处理
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_login:
			if (is_login) {
				ToastUtils.show("您已经登录过了");
			} else {
				Intent login = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(login);
				//finish();
			}

			break;
		case R.id.user_layout:
			// ToastUtils.show("个人中心");
			if (is_login) {
				Intent intent = new Intent(MainActivity.this,PersonalCenterActivity.class);	
				startActivity(intent);
				
			} else {
				startActivity(new Intent(MainActivity.this, LoginActivity.class));
				//finish();
			}
			break;
		case R.id.collect_layout:
			//ToastUtils.show("我的收藏");
			Intent intent = new Intent(this, Activity_Collention.class);
			startActivity(intent);
			break;
		case R.id.setting_layout:
			//ToastUtils.show("设置中心");
			Intent intent1 = new Intent(MainActivity.this,
					SettingActivity.class);
			startActivity(intent1);
			break;
		case R.id.feedback_layout:
			// ToastUtils.show("意见反馈");
			Intent intent_feedback = new Intent(MainActivity.this,
					FeedBackActivity.class);
			startActivity(intent_feedback);
			break;
			
		case R.id.exit_layout:
             showExitDialog();
			break;
		default:
			break;
		}
	}

	private void showExitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确定退出应用吗？");
		builder.setNegativeButton("离开", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentMainActivity.killAll();
			}
		});
		builder.setPositiveButton("再看看", null);
		builder.show();
		
	}


	public void loadRecommend() {
		ThreadManager.getInstance().getThreadFromLongPool()
				.execute(new Runnable() {
					@Override
					public void run() {
						final String json = MyHttpUtils.getJsonFromNet(null,
								rec_url, false);
						Gson gson = new Gson();
						final CommonListBean itemBean = gson.fromJson(json,
								CommonListBean.class);
						// 设置布局
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (itemBean != null && json != null) {
									getedItems = itemBean.data.items;
									recommendItem = getedItems.get((int) (Math.random() * getedItems.size()));

									new BitmapUtils(MainActivity.this).display(iv_recommend, test_url + recommendItem.img);
									tv_recommend_desc.setText(recommendItem.desc);
									tv_recommend_title.setText(recommendItem.text);
									rl_recommend.setOnClickListener(new OnClickListener() {
												@Override
										public void onClick(View v) {
											goDetail(recommendItem);
										}
									});
								}else{//无网情况
									tv_recommend_title.setText("敦煌商旅");
									tv_recommend_desc.setText("来自楼兰古国的友人，千年的故事。");
									iv_recommend.setImageResource(R.drawable.smallshow);
									
									rl_recommend.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											Intent recommend = new Intent(MainActivity.this, DetailActivity.class);
											recommend.putExtra("detail_url", "/10004/item_7/item_7.txt");
											startActivity(recommend);
										}
									});
								}
							}
						});
					}
				});
	}

	private void goDetail(CommonInfo item) {
		Intent intent = new Intent(MainActivity.this, DetailActivity.class);
		intent.putExtra("detail_url", item.url);
		startActivity(intent);
	}
	
	
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;

		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		final RectF rectF = new RectF(rect);

		final float roundPx = pixels;

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;

	}
}