package com.freetrip.trekker.activities;


import com.freetrip.trekker.R;
import com.freetrip.trekker.utils.MyBitmapUtils;
import com.freetrip.trekker.utils.UIUtils;
import com.lidroid.xutils.BitmapUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
/**
 * 这是专门用来显示大图的activity,支持双点触控,手势放大与缩放
 * @author Administrator
 *
 */
public class BigPicActivity extends BaseActivity implements OnTouchListener {

	private ImageView mImageView;

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// 第一个按下的手指的点
	private PointF startPoint = new PointF();
	// 两个按下的手指的触摸点的中点
	private PointF midPoint = new PointF();
	// 初始的两个手指按下的触摸点的距离
	private float oriDis = 1f;
	//记录是否进入彩蛋
	private float  is_go=1f;
	
	private boolean has_go=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		this.setContentView(R.layout.activity_big_pic);
		mImageView = (ImageView) this.findViewById(R.id.imageView);
		try{
			//从intent中拿到url
			Intent intent = getIntent();
			String picUrl = intent.getStringExtra("url");
			
			//给mImageView添加图片
			MyBitmapUtils.loadBitmap(mImageView, picUrl);
			mImageView.setOnTouchListener(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//触摸的时候,要先将imageview的scaletype设置为matrix
		mImageView.setScaleType(ScaleType.MATRIX);
		ImageView view = (ImageView) v;

		// 进行与操作是为了判断多点触摸
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 第一个手指按下事件
			matrix.set(view.getImageMatrix());
			savedMatrix.set(matrix);
			startPoint.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// 第二个手指按下事件
			oriDis = distance(event);
			if (oriDis > 10f) {
				savedMatrix.set(matrix);
				midPoint = middle(event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			// 手指放开事件
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			// 手指滑动事件
			if (mode == DRAG) {
				// 是一个手指拖动
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - startPoint.x, event.getY()
						- startPoint.y);
			} else if (mode == ZOOM) {
				// 两个手指滑动
				float newDist = distance(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oriDis;
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					is_go*=scale;
						System.out.println("is_go=="+is_go);
						
							if(is_go<=0.0000001){
								if(!has_go){
									has_go=true;
								Intent intent =new Intent(BigPicActivity.this,Amazing_Activity.class);
								UIUtils.startActivity(intent);
								finish();
								}
							}
					
					
					
				}
			}
			break;
		}

		// 设置ImageView的Matrix
		view.setImageMatrix(matrix);
		return true;
	}

	// 计算两个触摸点之间的距离
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算两个触摸点的中点
	private PointF middle(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x / 2, y / 2);
	}

}
