package com.keeplibrary.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2017/12/18 0:21
 * <p>
 * SystemUtil
 */

public class SystemUtil {

    private SystemUtil() {
        throw new UnsupportedOperationException("SystemUtil cannot be instantiated");
    }

    /**
     * 判断当前应用的是否为前台task
     *
     * @param context
     * @return
     */
    public static boolean isAppForgroud(Context context) {
        if (context != null) {
            String packName = context.getPackageName();
            List<ActivityManager.RunningTaskInfo> rTasks = getRunningTask(context);
            ActivityManager.RunningTaskInfo task = rTasks.get(0);
            return packName.equalsIgnoreCase(task.topActivity.getPackageName());
        }
        return false;
    }

    /**
     * 判断当前应用的是否为后台task
     *
     * @param context
     * @return
     */
    public static boolean isAppBackgroud(Context context) {
        if (context != null) {
            String packName = context.getPackageName();
            List<ActivityManager.RunningTaskInfo> rTasks = getRunningTask(context);
            ActivityManager.RunningTaskInfo task = rTasks.get(0);
            return !packName.equalsIgnoreCase(task.topActivity.getPackageName());
        }
        return false;
    }

    /**
     * 判断Activity 是否在运行
     *
     * @param context
     * @param activityName
     * @return
     */
    public static boolean isActivityRunning(Context context, String activityName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getClassName().equals(activityName) ||
                    info.baseActivity.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 服务是否运行
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 进程是否运行
     *
     * @return
     */
    public static boolean isProessRunning(Context context, String proessName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processs = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processs) {
            if (process.processName.equals(proessName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取运行中的任务
     *
     * @param context
     * @return
     */
    public static List<ActivityManager.RunningTaskInfo> getRunningTask(Context context) {
        if (context != null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rTasks = am.getRunningTasks(1);
            return rTasks;
        }
        return null;
    }

    /**
     * 获取进程名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

}
