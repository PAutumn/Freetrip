package com.freetrip.trekker.widget;

import com.freetrip.trekker.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

//设置中心的自定义控件
public class SettingItemView extends RelativeLayout {

	private static final String NAMESPACE = 
			"http://schemas.android.com/apk/res/com.freetrip.trekker";
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_status;
	private String title;
	private String desc_on;
	private String desc_off;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 从自定义的属性中拿到属性的名称和值
		/*
		 * int count = attrs.getAttributeCount(); for(int i=0;i<count;i++){
		 * String attributeName = attrs.getAttributeName(i); String
		 * attributeValue = attrs.getAttributeValue(i);
		 * 
		 * System.out.println(attributeName+"="+attributeValue); }
		 */

		title = attrs.getAttributeValue(NAMESPACE, "title");
		desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");

		initView();// 注意执行顺序

	}

	public SettingItemView(Context context) {
		super(context);
		initView();
	}

	// 初始化布局
	private void initView() {
		View.inflate(getContext(), R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_titile);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_status = (CheckBox) findViewById(R.id.cb_status);

		setTitle(title);

	}
	public void setTitle(String title) {
		tv_title.setText(title);
	}
	public void setDesc(String desc) {
		tv_desc.setText(desc);
	}
	// 判断checkbox是否勾选并返回当前的勾选状态
	public boolean isChecked() {
		return cb_status.isChecked();
	}
	// 选择勾选或者不勾选
	public void setChecked(boolean check) {
		cb_status.setChecked(check);
		// 根据选择的状态，更新desc文本
		if (check) {
			tv_desc.setText(desc_on);
		} else {
			tv_desc.setText(desc_off);
		}
	}
}
