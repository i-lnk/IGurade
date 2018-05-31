package com.rl.commons.utils;

import android.content.Context;
import android.view.View;

/**
 * 
 * @ClassName: ClickUtil 
 * @Description: 快速点击帮助类
 * @author NickyHuang
 * @date 2016-4-20 上午10:04:04 
 *
 */
public class ClickUtil {
	private ClickUtil(){
	}
	
	private static long lastClickTime = 0;
	
	private static View mView = null;
	
	private static Object clickObj = new Object();
	private static Context mContext;
	

	public synchronized static boolean isFastClick(Context context,View view) {
		synchronized(clickObj){
			//界面已跳转,忽略原界面剩余点击
			if( context!=mContext ){
				return true;
			}
			long time = System.currentTimeMillis();
			
			if( mView==view )
			{
				if ( time - lastClickTime < 500 ) {
					return true;
				}
			}else{
				mView = view;
				if (time - lastClickTime < 300) {
					return true;
				}
			}
			lastClickTime = time;
			return false;
		}
	}
	
	public synchronized static void init(Context context){
		synchronized(clickObj)
		{
			lastClickTime = 0;
			mView = null;
			mContext = context;
//			XLog.e( "#########$$$$$$$$$$$$$$$$$$$$$$$$$$$$$--------->context: "+context );
		}
	}
	
}