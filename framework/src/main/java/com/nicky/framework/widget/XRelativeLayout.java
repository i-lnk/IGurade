package com.nicky.framework.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * 
 * 相对布局基类
 * @author NickyHuang
 *
 */

public class XRelativeLayout extends RelativeLayout {
	
	private AspectRatio ratio;
	
	protected int mViewType = 0;
	
	public XRelativeLayout(Context context,int viewType) {
		super(context);
		mViewType = viewType;
		init(context, LayoutInflater.from(context), null);
	}
	
	public XRelativeLayout(Context context) {
		super(context);
		init(context, LayoutInflater.from(context), null);
	}

	public XRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, LayoutInflater.from(context), attrs);
	}

	public XRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, LayoutInflater.from(context), attrs);
	}
	
	
	protected void init(Context context, LayoutInflater layoutInflater, AttributeSet attrs) {}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(ratio != null) {
			int[] arrayOfInt = ratio.makeMeasureSpec(this);
			super.onMeasure(arrayOfInt[0], arrayOfInt[1]);
		}
	}
	
	public void setRatio(AspectRatio aspectRatio) {
		this.ratio = aspectRatio;
	}

}
