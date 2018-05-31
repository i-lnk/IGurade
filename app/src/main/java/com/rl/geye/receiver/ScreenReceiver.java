package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rl.p2plib.BridgeService;

/**
 * Created by PYH on 2017/11/28.
 * http://blog.csdn.net/chenghai2011/article/details/7219336
 */

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            if (!BridgeService.isReady()) {
                Intent service = new Intent(context, BridgeService.class);
                context.startService(service);
            }
        }
    }
}
