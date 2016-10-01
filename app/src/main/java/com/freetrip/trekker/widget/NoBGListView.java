package com.freetrip.trekker.widget;



import com.freetrip.trekker.R;
import com.freetrip.trekker.utils.UIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class NoBGListView extends ListView {

	public NoBGListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NoBGListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NoBGListView(Context context) {
		super(context);
		init();
	}
	
	void init(){
			this.setSelector(R.drawable.nothing);  // ʲô��û��
			this.setCacheColorHint(R.drawable.nothing);
			this.setDivider(UIUtils.getDrawable(R.drawable.nothing));
		}
	}

