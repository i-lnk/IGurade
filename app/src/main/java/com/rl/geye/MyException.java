package com.rl.geye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.rl.geye.constants.Constants;
import com.rl.geye.ui.aty.LauncherAty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2017/9/15 17:38
 * <p>
 * App未知异常
 */

public class MyException implements Thread.UncaughtExceptionHandler {
    private static final String TAG = MyException.class.getSimpleName();
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    private Context context;

    private MyException(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, ex.getMessage(), ex);
        if (uncaughtExceptionHandler != null && !isHandlerException(ex)) {
            uncaughtExceptionHandler.uncaughtException(thread, ex);
        } else {
            saveExceptionLog(ex);
        }
    }

    /**
     * 启动未知异常捕获
     */
    public static void start(Context context) {
        MyException myException = new MyException(context);
        if (myException.uncaughtExceptionHandler == null) {
            myException.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        }
        Thread.setDefaultUncaughtExceptionHandler(myException);
    }

    /**
     * 是否处理过未知异常
     *
     * @param throwable
     * @return
     */
    private boolean isHandlerException(final Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        return true;
    }

    /**
     * 保存异常日志
     *
     * @param ex
     */
    @SuppressLint("SimpleDateFormat")
    private void saveExceptionLog(Throwable ex) {
        StringBuffer sbLog = new StringBuffer();
        OutputStream outputStream = null;
        try {
            File pathFile = new File(Environment.getExternalStorageDirectory(), Constants.DEVBUG_PATH);
            if (!pathFile.exists() && pathFile.mkdirs()) {
            }
            File logFile = new File(pathFile, "log.log");
            outputStream = new FileOutputStream(logFile, true);
            if (logFile.exists() && logFile.length() == 0) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
                sbLog.append("Application info:").append("\n");
                sbLog.append("PACKAGE_NAME").append(":").append(String.valueOf(packageInfo.packageName)).append("\n");
                sbLog.append("VERSION_NAME").append(":").append(String.valueOf(packageInfo.versionName)).append("\n");
                sbLog.append("VERSION_CODE").append(":").append(String.valueOf(packageInfo.versionCode)).append("\n\n");
                sbLog.append("Device info:").append("\n");
                Field[] fields = Build.class.getFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    if (i == fields.length - 1) {
                        sbLog.append(field.getName()).append(":").append(String.valueOf(field.get(null))).append("\n\n");
                    } else {
                        sbLog.append(field.getName()).append(":").append(String.valueOf(field.get(null))).append("\n");
                    }
                }
                sbLog.append("Exception info:").append("\n");
            }
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            sbLog.append(getCurrentTime()).append("\n");
            sbLog.append(writer.toString()).append("\n");
            outputStream.write(sbLog.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            Intent intent = new Intent(context, LauncherAty.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前时间
     *
     * @return System time
     */
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
