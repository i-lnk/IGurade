package com.rl.commons;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rl.commons.log.XLog;
import com.rl.commons.utils.AppDevice;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nicky on 2016/9/8.
 */
public abstract class BaseApp extends Application{

    private static final String TAG = "BaseApp";

    protected static Context _context;
    private static long lastToastTime;
    private static String lastToast = "";

    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;

    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 30*1000; // 判断是否处于后台(30秒后最上层页面不是BaseActivity的话，则判定为后台)

    public boolean wasInBackground;



    @Override
    public void onCreate() {
        super.onCreate();
        _context = getApplicationContext();

		int pid = android.os.Process.myPid();
		String processAppName = AppDevice.getProcessName(this, pid);
		if (processAppName == null|| !processAppName.equalsIgnoreCase(this.getPackageName())) {
			return;
		}
        onCreateProcess();

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        super.onTerminate();
    }

    protected abstract void onCreateProcess();

    public static synchronized BaseApp context() {
        return (BaseApp) _context;
    }

    protected abstract void onGotoBackground(); //转至后台
    protected abstract void onGotoForeground(); //转至前台


    public void startActivityTransitionTimer() {

        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                BaseApp.this.wasInBackground = true;
                onGotoBackground();
            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask, MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }
        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        if(wasInBackground ){
            onGotoForeground();
        }

        this.wasInBackground = false;

    }

    /**
     * 当前Acitity个数
     */
    private int activityCount = 0;


    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks(){

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            XLog.i(TAG,"onActivityCreated  : "+activity.getClass().getName() );
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if ( activityCount == 0 ){
//                wasInBackground = false;
                XLog.w(TAG,"--------------->goto Foreground  ");
            }
            activityCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            if (activityCount == 0) {
//                wasInBackground = true;
                XLog.w(TAG,"--------------->goto Background  ");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            XLog.e(TAG,"onActivityDestroyed  : "+activity.getClass().getName() );
        }
    };




    public static void showToast(int message) {
        showToast(message, Toast.LENGTH_SHORT, 0);
    }

    public static void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
    }

    public static void showToast(int message, int icon) {
        showToast(message, Toast.LENGTH_SHORT, icon);
    }

    public static void showToast(String message, int icon) {
        showToast(message, Toast.LENGTH_SHORT, icon, Gravity.BOTTOM);
    }

    public static void showToastLong(int message) {
        showToast(message, Toast.LENGTH_LONG, 0);
    }

    public static void showToastLong(String message) {
        showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
    }

    public static void showToastShortLong(int message, Object... args) {
        showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM, args);
    }

    public static void showToast(int message, int duration, int icon) {
        showToast(message, duration, icon, Gravity.BOTTOM);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity) {
        showToast(context().getString(message), duration, icon, gravity);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity, Object... args) {
        showToast(context().getString(message, args), duration, icon, gravity);
    }


    public static void showToast(String message, int duration, int icon,
                                 int gravity) {
        if (message != null && !message.equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.equalsIgnoreCase(lastToast)
                    || Math.abs(time - lastToastTime) > 2000) {
                View view = LayoutInflater.from(context()).inflate(
                        R.layout.comm_lay_toast, null);
                ((TextView) view.findViewById(R.id.title_tv)).setText(message);
                if (icon != 0) {
                    ImageView iv = (ImageView) view.findViewById(R.id.icon_iv);
                    iv.setImageResource(icon);
                    iv.setVisibility(View.VISIBLE);
                }
                Toast toast = new Toast(context());
                toast.setView(view);
                if (gravity == Gravity.CENTER) {
                    toast.setGravity(gravity, 0, 0);
                } else {
                    toast.setGravity(gravity, 0, 35);
                }

                toast.setDuration(duration);
                toast.show();
                lastToast = message;
                lastToastTime = System.currentTimeMillis();
            }
        }
    }


}
