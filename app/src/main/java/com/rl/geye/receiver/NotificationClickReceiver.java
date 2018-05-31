package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rl.geye.ui.aty.LauncherAty;
import com.rl.geye.ui.aty.MainAty;

/**
 * @author NickyHuang
 * @ClassName: NotificationClickReceiver
 * @Description: 通知栏点击响应
 * @date 2016-6-28 下午4:19:37
 */
public class NotificationClickReceiver extends BroadcastReceiver {

    private final static String TAG = "NotificationClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//		XLog.e(TAG, "---------------------------->onReceive");

        if (MainAty.isReady()) {
            Intent newIntent = new Intent(context, MainAty.class)
                    .addCategory(Intent.CATEGORY_LAUNCHER)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(newIntent);
        } else {
            Intent newIntent = new Intent(context, LauncherAty.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(newIntent);
        }


    }


}
