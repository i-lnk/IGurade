package com.rl.p2plib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;


/**
 * Created by PYH on 2017/12/11.
 */

public class BootstrapService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(this);
        // stop self to clear the notification
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startForeground(Service context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("I'm running")
                .setContentText("")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.mipmap.app_geye)
                .setAutoCancel(true);
        Notification notification = builder.build();

        context.startForeground(8888, notification);
    }
}
