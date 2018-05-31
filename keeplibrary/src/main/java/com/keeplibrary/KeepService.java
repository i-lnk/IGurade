package com.keeplibrary;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.lang.ref.SoftReference;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/4/22 18:39
 * <p>
 * KeepService
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepService extends JobService {
    private static final String TAG = KeepService.class.getName();
    public static final int JOB_ID = 0x1201;
    private JobHandler jobHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (jobHandler == null) {
            jobHandler = new JobHandler(this);
        }
        Log.d(TAG, "onCreate");
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        setRebootJobScheduler();
        // Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Message message = Message.obtain();
        message.what = params.getJobId();
        message.obj = params;
        jobHandler.sendMessage(message);
        // Log.d(TAG, "onStartJob");
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobHandler.removeCallbacksAndMessages(null);
        // Log.d(TAG, "onStopJob");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 设置自动重启程序调度
     */
    public void setRebootJobScheduler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            long executeTime = 10 * 1000;
            if (jobScheduler != null) {
                ComponentName componentName = new ComponentName(getPackageName(), KeepService.class.getName());
                JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                        .setMinimumLatency(executeTime)
                        .setOverrideDeadline(executeTime)
                        .setPersisted(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setRequiresCharging(true)
                        .setRequiresDeviceIdle(true)
                        .build();
                int request = jobScheduler.schedule(jobInfo);
                if (request > 0) {
                    Log.d(TAG, "JobScheduler：JOB_ID");
                    return;
                }
                Log.d(TAG, "JobScheduler：Not supported");
            }
        }
    }

    /**
     * 任务处理
     */
    private static class JobHandler extends Handler {
        private SoftReference<KeepService> reference;

        private JobHandler(KeepService service) {
            this.reference = new SoftReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            KeepService service = reference.get();
            if (service == null) {
                return;
            }
            JobParameters jobParameters = (JobParameters) msg.obj;
            service.jobFinished(jobParameters, false);
            switch (msg.what) {
                case JOB_ID:
                    service.setRebootJobScheduler();
                    service.sendBroadcast(new Intent(KeepConstant.ACTION_KEEP_SERVICE));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }


}
