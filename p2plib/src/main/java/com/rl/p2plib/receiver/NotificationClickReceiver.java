package com.rl.p2plib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.rl.commons.log.XLog;


/**
 *
 * @ClassName: NotificationClickReceiver
 * @Description: 通知栏点击响应
 * @author NickyHuang
 * @date 2016-6-28 下午4:19:37
 *
 */
public class NotificationClickReceiver extends BroadcastReceiver{

	private final static String TAG = "NotificationClickReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		XLog.v(TAG, "---------------------------->onReceive");

//		if( MainActivity.isReady() ){
//			XLog.i(TAG, "---------------------------->MainActivity is Ready");
//			Intent newIntent = new Intent(context, MainActivity.class)
//					.addCategory(Intent.CATEGORY_LAUNCHER)
//					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//			context.startActivity(newIntent);
//		}else{
//			XLog.i(TAG, "---------------------------->MainActivity not Ready");
//			Intent newIntent = new Intent(context, LauncherAty.class);
//			newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			context.startActivity(newIntent);
//		}
	}


}
