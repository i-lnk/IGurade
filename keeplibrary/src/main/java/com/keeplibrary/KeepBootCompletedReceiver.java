package com.keeplibrary;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/3/20 11:35
 * <p>
 * KeepBootCompletedReceiver
 */
public class KeepBootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = KeepBootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            executeBootTask(context);
            Log.d(TAG, "KeepBootCompletedReceiver：Intent.ACTION_BOOT_COMPLETED");
        }
    }

    /**
     * 执行开机任务
     */
    private void executeBootTask(Context context) {
        Intent intent = new Intent(KeepConstant.ACTION_BOOT_COMPLETED);
        intent.setComponent(new ComponentName(context.getPackageName(), KeepActionReceiver.class.getName()));
        context.sendBroadcast(intent);
    }
}
