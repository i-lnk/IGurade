package com.nicky.framework.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @Description: 线性布局基类
 * @author NickyHuang
 */

public class XLinearLayout extends LinearLayout {

	private AspectRatio ratio;
	
	protected int mViewType = 0;

	public XLinearLayout(Context context,int viewType) {
		super(context);
		mViewType = viewType;
		init(context, LayoutInflater.from(context), null);
	}
	
	public XLinearLayout(Context context) {
		super(context);
		init(context, LayoutInflater.from(context), null);
	}

	public XLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, LayoutInflater.from(context), attrs);
	}

	@SuppressLint("NewApi")
	public XLinearLayout(Context context, AttributeSet attrs, int theme) {
		super(context, attrs, theme);
		init(context, LayoutInflater.from(context), attrs);
	}

	protected EditText editText(int paramInt) {
		return (EditText) findViewById(paramInt);
	}

	protected void init(Context context, LayoutInflater layoutInflater,
			AttributeSet attrs) {}

	protected void onMeasure(int paramInt1, int paramInt2) {
		super.onMeasure(paramInt1, paramInt2);
		if (this.ratio != null) {
			int[] arrayOfInt = this.ratio.makeMeasureSpec(this);
			super.onMeasure(arrayOfInt[0], arrayOfInt[1]);
		}
	}

	public void setRatio(AspectRatio paramAspectRatio) {
		this.ratio = paramAspectRatio;
	}

}
