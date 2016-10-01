package com.freetrip.trekker.widget;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 侧滑面板
 * 
 * @author mwp
 *
 */
public class DragLayout extends FrameLayout {

	private ViewDragHelper mDragHelper;

	private Status mStatus = Status.Close;

	/**
	 * 状态枚举
	 * 
	 * @author mwp
	 *
	 */
	public static enum Status {
		/** 关闭状态 */
		Close,
		/** 打开状态 */
		Open,
		/** 滑动状态 */
		Draging;
	}

	public void setmStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public Status getmStatus() {
		return mStatus;
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DragLayout(Context context) {
		this(context, null);
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mDragHelper = ViewDragHelper.create(this, mCallback);
	}

	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View arg0, int arg1) {
			return true;
		}

		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}

		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}

		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mMainContent) {
				left = fixLeft(left);
			}

			return left;
		}

		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			int newLeft = left;
			if (changedView == mLeftContent) {
				// 把当前变化量传递给mLeftContent
				newLeft = mMainContent.getLeft() + dx;
			}

			// 进行修正
			newLeft = fixLeft(newLeft);

			if (changedView == mLeftContent) {
				// 当左面板移动之后，在强制放回去
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);

				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}

			// 更新状态，执行动画
			dispatchDragEvent(newLeft);

			// 为了兼容低版本，每次修改值之后，进行重绘
			invalidate();
		}

		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			// 判断执行 关闭/开启
			if (xvel == 0 && mMainContent.getLeft() > mRange / 2.0f) {
				open();
			} else if (xvel > 0) {
				open();
			} else {
				close();
			}
		}

		public void onViewDragStateChanged(int state) {
			super.onViewDragStateChanged(state);
		}

	};

	/**
	 * 根据范围修正左边值
	 * 
	 * @param left
	 * @return
	 */
	protected int fixLeft(int left) {
		if (left < 0) {
			return 0;
		} else if (left > mRange) {
			return mRange;
		}

		return left;
	}

	/** 更新状态， 执行动画 */
	protected void dispatchDragEvent(int newLeft) {
		// 当前已移动的百分比 0-1
		float percent = newLeft * 1.0f / mRange;

		if (listener != null) {
			listener.onDraging(percent);
		}

		// 更新状态，执行回调
		Status preStatus = mStatus;
		mStatus = updateStatus(percent);
		if (mStatus != preStatus) {
			// 如果状态发生变化
			if (mStatus == Status.Close) {
				if (listener != null) {
					listener.onClose();
				}
			} else if (mStatus == Status.Open) {
				if (listener != null) {
					listener.onOpen();
				}
			}
		}

		// 伴随动画
		animViews(percent);

	}

	private void animViews(float percent) {
		// 1.左面板： 缩放动画，平移动画，透明度动画
		// 缩放动画
		ViewHelper.setScaleX(mLeftContent, evaluate(percent, 0.5f, 1.0f));
		ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f * percent);// 这两种写法是等价的

		// 平移动画
		ViewHelper.setTranslationX(mLeftContent,
				evaluate(percent, -mWidth / 2.0f, 0));

		// 透明度动画
		ViewHelper.setAlpha(mLeftContent, evaluate(percent, 0.5f, 1.0f));

		// 2.主面板：缩放动画 1.0 - 0.8
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f));

		// 3.背景动画：亮度变化（颜色变化）
		getBackground().setColorFilter(
				(Integer) (evaluateColor(percent, Color.BLACK,
						Color.TRANSPARENT)), Mode.SRC_OVER);
	}

	/**
	 * 估值器
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
		float startFloat = startValue.floatValue();
		return startFloat + fraction * (endValue.floatValue() - startFloat);
	}

	/**
	 * 颜色变化过度
	 * 
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	public Object evaluateColor(float fraction, Object startValue,
			Object endValue) {
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}

	@Override
	public void computeScroll() {
		super.computeScroll();

		if (mDragHelper.continueSettling(true)) {
			// 如果返回true ，动画还需要继续执行
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	private Status updateStatus(float percent) {
		if (percent == 0) {
			return Status.Close;
		} else if (percent == 1.0f) {
			return Status.Open;
		}

		return Status.Draging;
	}

	public void close() {
		close(true);
	}

	/** 关闭的时候是否使用平滑动画 */
	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if (isSmooth) {
			// 触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置，需要刷新界面
				// 参数传this（child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	public void open() {
		open(true);
	}

	/** 打开面板的时候是否是使用平滑动画 */
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if (isSmooth) {
			// 触发一个平滑动画
			if (mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)) {
				// 返回true代表还没有移动到指定位置，需要刷新界面
				// 参数传this（child所在的ViewGroup）
				ViewCompat.postInvalidateOnAnimation(this);
			}
		} else {
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
	}

	private int mHeight;

	private int mWidth;

	private onDragStatusChangeListener listener;

	private int mRange;

	private ViewGroup mLeftContent;

	private ViewGroup mMainContent;

	// b.传递触摸事件
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		// 将事件是否拦截直接交给ViewDragHelper处理
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;// 自己处理完之后，拦截事件，不继续传递
	}

	// 当布局xml初始化完布局对象后调用
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 容错性检查（至少有两个子View，子View必须是ViewGroup的子类）
		if (getChildCount() < 2) {
			throw new IllegalArgumentException(
					"布局至少有俩孩子. Your ViewGroup must have 2 children at least.");
		}

		if (!(getChildAt(0) instanceof ViewGroup && getChildAt(1) instanceof ViewGroup)) {
			throw new IllegalArgumentException(
					"子View必须是ViewGroup的子类. Your children must be an instance of ViewGroup");
		}

		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);

	}

	// 当当前view的尺寸发生变化时回调
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();
		// 移动的范围
		mRange = (int) (mWidth * 0.6f);
	}

	/** 侧滑面板状态的监听回调 */
	public interface onDragStatusChangeListener {
		void onClose();

		void onOpen();

		void onDraging(float percent);
	}

	/** 设置侧滑面板的状态监听 */
	public void setDragStatusListener(onDragStatusChangeListener listener) {
		this.listener = listener;

	}
}
