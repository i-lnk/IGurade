package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rl.p2plib.BridgeService;

/**
 * Created by PYH on 2017/11/28.
 */

public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()) || Intent.ACTION_BATTERY_LOW.equals(intent.getAction()) || Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())) {
            if (!BridgeService.isReady()) {
                Intent service = new Intent(context, BridgeService.class);
                context.startService(service);
            }
        }
    }

}
