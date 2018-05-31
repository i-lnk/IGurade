package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rl.geye.ui.aty.LauncherAty;
import com.rl.geye.ui.aty.MainAty;
import com.rl.p2plib.BridgeService;

public class BootBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (!MainAty.isReady()) {
                Intent i = new Intent(context, LauncherAty.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else if (!BridgeService.isReady()) {
                Intent service = new Intent(context, BridgeService.class);
                context.startService(service);
            }
        }

    }

}
