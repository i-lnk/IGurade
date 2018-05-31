package com.nicky.framework.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * 组合TextView
 * 可设置其部分Text的点击事件,颜色以及字体大小等
 * @author NickyHuang
 *
 */
public class CompoundText extends TextView{

	private SpannableString mSpannableString;
	private Context mContext;

	public CompoundText(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public CompoundText(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    mContext = context;
	    init();
	}
	
	/**
	 * 初始化（内容改变后需调用）
	 */
	private void init()
	{				
		setMovementMethod(LinkMovementMethod.getInstance());  
	    setFocusable(false);  
	    setClickable(false);  
	    setLongClickable(false); 
	    //setLinkTextColor(Color.BLUE); 
	}

	public void setCompoundText(CharSequence text)
	{
		mSpannableString = new SpannableString(ToDBC(text.toString()));
		setText(ToDBC(text.toString()));		
	}
	

	/**
	 * 半角转为全角
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {  
		   char[] c = input.toCharArray();  
		   for (int i = 0; i< c.length; i++) {  
		       if (c[i] == 12288) {  
		         c[i] = (char) 32;  
		         continue;  
		       }if (c[i]> 65280&& c[i]< 65375)  
		          c[i] = (char) (c[i] - 65248);  
		       }  
		   return new String(c);  
	} 
	
	/**
	 * 设置部分内容点击事件
	 * @param start 	点击起始位置(包含)
	 * @param end   	点击结束位置(不包含)
	 * @param color   	点击部分颜色
	 * @param listener 	点击响应事件
	 */
	public void setSpecialClick(int start,int end,int color,boolean withUnderline,OnClickListener listener)
	{
		if(mSpannableString==null)
			mSpannableString = new SpannableString(getText());
		mSpannableString.setSpan(new MyClickSpan(withUnderline,listener), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		setSpecialColor(start,end,color);
	}
	
	/**
	 * 设置部分内容颜色
	 * @param start 起始位置(包含)
	 * @param end   结束位置(不包含)
	 * @param color    
	 */
	public void setSpecialColor(int start,int end, int color )
	{
		if(mSpannableString==null)
			mSpannableString = new SpannableString(getText());
		mSpannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		setText(mSpannableString);
	}
	 
	/**
	 * 设置部分内容大小
	 * @param start 起始位置(包含)
	 * @param end   结束位置(不包含)
	 * @param size  字体大小
	 */
	public void setSpecialSize(int start,int end, int size )
	{
		if(mSpannableString==null)
			mSpannableString = new SpannableString(getText());
		mSpannableString.setSpan(new AbsoluteSizeSpan(size, true), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		setText(mSpannableString);
	}

	/**
	 * //无下划线超链接，使用textColorLink、textColorHighlight分别修改超链接前景色和按下时的颜色
	 * @author NickyHuang
	 *
	 */
	private class MyClickSpan extends ClickableSpan {
		//private String text;
		private OnClickListener mListener;
		private boolean withUnderline; //是否需要下划线
//	    public MyClickSpan(String text) {
//	        super();
//	        this.text = text;
//	    }

		public MyClickSpan(boolean withUnderline,OnClickListener listener)
		{
			super();
			mListener = listener;
			this.withUnderline = withUnderline;
		}


		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setColor(ds.linkColor);
			ds.setUnderlineText(withUnderline);//下划线
		}

		@Override
		public void onClick(View widget) {
			// processHyperLinkClick(text);//点击超链接时调用</span>
			if(mListener!=null)
				mListener.onClick(widget);
		}
	}




	

}
