package com.nicky.framework.widget;

import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 按比例缩放工具类
 * @author NickyHuang
 *
 */
public class AspectRatio {
	
	private final double aspectRatio;
	private final boolean widthBased;
	private AspectRatio(double aspectRatio, boolean widthBased) {
		this.aspectRatio = aspectRatio;
		this.widthBased = widthBased;
	}
	
	public static AspectRatio makeAspectRatio(double aspectRatio, boolean widthBased) {
		return new AspectRatio(aspectRatio, widthBased);
	}
	
	private int makeMeasure(int size) {
		return (int)(size * this.aspectRatio);
	}
	
	public int[] makeMeasureSpec(View view) {
		int[] arrayOfInt = new int[2];
		if(widthBased) {
			int size = view.getMeasuredWidth();
			arrayOfInt[0] = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
		    arrayOfInt[1] = MeasureSpec.makeMeasureSpec(makeMeasure(size), MeasureSpec.EXACTLY);
		} else {
			int size = view.getMeasuredWidth();
			arrayOfInt[0] = MeasureSpec.makeMeasureSpec(makeMeasure(size), MeasureSpec.EXACTLY);
		    arrayOfInt[1] = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);

		}
		return arrayOfInt;
	}

}
