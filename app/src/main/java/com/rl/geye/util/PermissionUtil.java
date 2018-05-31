package com.rl.geye.util;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2016/9/13 15:46
 * <p>
 * 权限工具
 */

public class PermissionUtil {

    /**
     * 外部存储卡权限
     */
    public static final int MARK_WRITE_EXTERNAL_STORAGE = 0;

    /**
     * 发送短信权限
     */
    public static final int MARK_SEND_SMS = 1;

    /**
     * 接收短信
     */
    public static final int MARK_RECEIVE_SMS = 2;

    /**
     * 位置权限
     */
    public static final int MARK_ACCESS_FINE_LOCATION = 3;

    /**
     * 照相机权限
     */
    public static final int MARK_CAMERA = 4;

    /**
     * 忽略省电优化
     */
    public static final int IGNOR_BATTERY = 5;

    /**
     * 日历权限
     */
    public static final int Calendar = 6;

    /**
     * 多个权限
     */
    public static final int MARK_MORE = 7;

    private PermissionUtil() {
        throw new UnsupportedOperationException("PermissionUtil cannot be instantiated");
    }


    /**
     * 判断申请外部存储所需权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestExternalStorage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MARK_WRITE_EXTERNAL_STORAGE);
        return false;
    }

    /**
     * 判断申请发送短信权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestSendSms(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, MARK_SEND_SMS);
        return false;
    }

    /**
     * 判断申请发送短信权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestSmsReceived(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECEIVE_SMS}, MARK_RECEIVE_SMS);
        return false;
    }

    /**
     * 判断申请网络位置权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestFineLocation(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean mayRequestCoarseLocation(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean checkRequestCalendar(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static void mayRequestCalendar(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_CALENDAR}, MARK_CAMERA);
    }


    /**
     * 检测申请照相机权限
     *
     * @param context
     * @return
     */
    public static boolean checkRequestCameara(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 申请照相机权限
     *
     * @param context
     */
    public static void mayRequestCameara(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MARK_CAMERA);
    }

    public static void mayIgnoringBatteryOptimizations(Context context, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                ((Activity) context).startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取电池优化权限状态
     *
     * @param context
     * @return
     */
    public static boolean checkIgnoringBatteryOptimizations(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return powerManager == null || powerManager.isIgnoringBatteryOptimizations(packageName);
        }
        return true;
    }

    /**
     * 判断悬浮窗权限状态
     *
     * @param context
     * @return
     */
    public static boolean checkCanDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    /**
     * 权限申请设置界面
     *
     * @param context
     */
    public static void mayRequestAllPermissionSetting(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }


    /**
     * 申请勿扰权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestNotifaicationPolicyAccessGranted(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, MARK_CAMERA);
        }
        return false;
    }

    /**
     * 获取勿扰权限状态
     *
     * @param context
     * @param mNotificationManager
     * @return
     */
    public static boolean checkNotifaicationPolicyAccessGranted(Context context, NotificationManager mNotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取通知权限状态
     *
     * @param context
     * @return
     */
    public static boolean checkAreNotificationEnabled(Context context) {
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= 19) {
            if (managerCompat.areNotificationsEnabled()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void mayRequestNotificationEnabled(Context context) {
        mayRequestAllPermissionSetting(context);
    }

    /**
     * 设置自启动
     *
     * @param phoneType 手机型号
     * @param context
     */
    public static void mayRequestSelfLaunching(String phoneType, Context context) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("HLQ_Struggle", "******************当前手机型号为：" + phoneType);
            ComponentName componentName = null;
            if (phoneType.equals("xiaomi") || phoneType.equals("Xiaomi")) { // 红米Note5x测试通过
                componentName = ComponentName.unflattenFromString("com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity");
                Intent intentXiaomi = new Intent();
                intent.setClassName("com.miui.securitycenter/com.miui.permcenter.autostart", "AutoStartManagementActivity");
                if (context.getPackageManager().resolveActivity(intentXiaomi, 0) == null) {
                    componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                }
            } else if (phoneType.equals("Letv")) { // 乐视2测试通过
                intent.setAction("com.letv.android.permissionautoboot");
            } else if (phoneType.equals("samsung")) { // 三星Note5测试通过
                Intent intentSs;
                try {
                    componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm/.ui.ram.RamActivity");
                    intentSs = new Intent();
                    intentSs.setComponent(componentName);
                    context.startActivity(intentSs);
                    return;
                } catch (Exception e) {
//                    componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm/.ui.ram.AutoRunActivity");
                    componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity");
                    intentSs = new Intent();
                    intentSs.setComponent(componentName);
                    context.startActivity(intentSs);
                    return;
                }
            } else if (phoneType.equals("HUAWEI") || phoneType.equals("HONOR")) {
                try {
                    Intent intentHw = new Intent();
                    componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                    intentHw.setComponent(componentName);
                    context.startActivity(intentHw);
                    return;
                } catch (Exception e) {
                    componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity");
                }
            } else if (phoneType.equals("vivo")) { // VIVO测试通过
                componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");
            } else if (phoneType.equals("Meizu")) { //魅族
                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
            } else if (phoneType.equals("OPPO")) { // OPPO A57 r9S R11S测试通过
                Intent intentOppo;
                try {
                    componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                    intentOppo = new Intent();
                    intentOppo.setComponent(componentName);
                    context.startActivity(intentOppo);
                    return;
                } catch (Exception e) {
                    try {
                        componentName = ComponentName.unflattenFromString("com.coloros.safecenter/.startupapp.StartupAppListActivity");
                        intentOppo = new Intent();
                        intentOppo.setComponent(componentName);
                        context.startActivity(intentOppo);
                        return;
                    } catch (Exception ex) {
                        componentName = ComponentName.unflattenFromString("com.coloros.safecenter/com.coloros.privacypermissionsentry.PermissionTopActivity");
                        intentOppo = new Intent();
                        intentOppo.setComponent(componentName);
                        context.startActivity(intentOppo);
                        return;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    /**
     * 判断申请多个权限
     *
     * @param context
     * @return
     */
    public static boolean mayRequestMore(Context context, String[] permission) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        List<String> ps = new ArrayList();
        boolean flag = false;
        boolean result = true;
        for (String p : permission) {
            flag = (ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED);
            if (!flag) {
                ps.add(p);
            }
            result = (result && flag);
        }
        if (result) {
            return true;
        }
        String[] current = new String[ps.size()];
        for (int i = 0; i < ps.size(); i++) {
            current[i] = ps.get(i);
        }
        ActivityCompat.requestPermissions((Activity) context, current, MARK_MORE);
        return false;
    }

}
