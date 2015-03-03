package com.ckt.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ckt.util.Utils;

public class DrawerLayout extends FrameLayout {
	private int leftDistance = Utils.ENDPOSITON;
	private Context context;

	public DrawerLayout(Context context) {
		this(context, null);
	}

	public DrawerLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	        int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		for (int i = 0; i < getChildCount(); i++) {
			if (i == getChildCount() - 1) {
				View child = getChildAt(i);
				child.layout(leftDistance, top, right, bottom);
			}
		}
	}

	public void setLeftDistance(int leftDistance, boolean flag) {
		if (flag) {
			this.leftDistance = leftDistance < Utils.CENTER ? Utils.STARTPOSITON
			        : Utils.ENDPOSITON;
		} else {
			this.leftDistance = leftDistance;
		}

		requestLayout();
		invalidate();

	}
}
