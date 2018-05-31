package com.keeplibrary;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;

import android.util.Log;
import com.keeplibrary.util.SystemUtil;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/4/22 19:04
 * <p>
 * Keep
 */
public class Keep {

    private static final String TAG = Keep.class.getName();

    private Keep() {
    }

    /**
     * Initialization Keep
     *
     * @param application Application
     */
    public static synchronized void initialization(Application application) {
        if (!SystemUtil.isProessRunning(application.getApplicationContext(), application.getPackageName() + ":keep")) {
            Intent intent = new Intent(KeepConstant.ACTION_KEEP_SERVICE);
            intent.setComponent(new ComponentName(application.getPackageName(), KeepActionReceiver.class.getName()));
            application.sendBroadcast(intent);
            Log.d(TAG, "Keep send start service");
        }
        Log.d(TAG, "Keep initialization");
    }

}
