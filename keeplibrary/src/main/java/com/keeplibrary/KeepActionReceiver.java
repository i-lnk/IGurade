package com.keeplibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import com.keeplibrary.util.IntentUtil;
import com.keeplibrary.util.SystemUtil;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/4/22 18:58
 * <p>
 * KeepActionReceiver
 */
public class KeepActionReceiver extends BroadcastReceiver {
    private static final String TAG = KeepActionReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (KeepConstant.ACTION_KEEP_SERVICE.equals(action)) {
            restart(context);
            Log.d(TAG, "KeepActionReceiver KeepConstant.ACTION_KEEP_SERVICE");
            return;
        }

        if (KeepConstant.ACTION_BOOT_COMPLETED.equals(action)) {
            restart(context);
            Log.d(TAG, "KeepActionReceiver KeepConstant.ACTION_BOOT_COMPLETED");
            return;
        }

        if (KeepConstant.ACTION_SHUTDOWN.equals(action)) {
            restart(context);
            Log.d(TAG, "KeepActionReceiver KeepConstant.ACTION_SHUTDOWN");
            return;
        }

        restart(context);
        Log.d(TAG, "KeepActionReceiver unknown");
    }

    /**
     * Restart
     *
     * @param context
     */
    public void restart(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!SystemUtil.isServiceRunning(context, "com.keeplibrary.KeepService")) {
                Intent intent = new Intent(KeepConstant.ACTION_KEEP_SERVICE);
                context.startService(IntentUtil.createExplicitFromImplicitIntent(context, intent));
                Log.d(TAG, "Keep service start");
                return;
            }
            Log.d(TAG, "Keep service running");
        }
    }


}
