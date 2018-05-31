package com.keeplibrary;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/3/20 11:33
 * <p>
 * KeepShutdownReceiver
 */
public class KeepShutdownReceiver extends BroadcastReceiver {
    private static final String TAG = KeepShutdownReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SHUTDOWN.equals(action)) {
            executeShutdownTask(context);
            Log.d(TAG, "KeepShutdownReceiver：Intent.ACTION_SHUTDOWN");
        }
    }

    /**
     * 执行关机任务
     */
    private void executeShutdownTask(Context context) {
        Intent intent = new Intent(KeepConstant.ACTION_BOOT_COMPLETED);
        intent.setComponent(new ComponentName(context.getPackageName(), KeepActionReceiver.class.getName()));
        context.sendBroadcast(intent);
    }

}
