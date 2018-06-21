package com.rl.geye.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.jpush.TagBean;
import com.rl.geye.jpush.TagUtil;
import com.rl.geye.logic.DataLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 后台服务
 *
 * @author Nicky
 * created at 2017/8/31 9:18
 */
public class TaskWorkServer extends IntentService {
    public static final String ACTION_SET_PUSH_TAG = "action_set_push_tag";
    //	public static final String ACTION_SET_DEV_PUSH   = "action_set_dev_push";
    public static final String ACTION_ADD_FCM_TOPIC = "action_add_fcm_topic";
    public static final String ACTION_DEL_FCM_TOPIC = "action_del_fcm_topic";
    private static final String SERVICE_NAME = "UploadDataService";
    private static final int NOTIFICATION_ID = 0x1080;
    public static boolean isTagSetting = false; //极光TAG不允许同时请求设置
    public static boolean needSetTag = false; //需要设置

    private static List<String> pendingTasks = new ArrayList<String>();

    public TaskWorkServer() {
        this(SERVICE_NAME);
    }

    public TaskWorkServer(String name) {
        super(name);
    }

    public static void startService(Context context, String action, String value) {
        Intent intent = new Intent(action);
        if (!StringUtils.isEmpty(value)) {
            Bundle bundle = new Bundle();
            bundle.putString(action, value);
            intent.putExtras(bundle);
        }
        intent.setPackage(BaseApp.context().getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static void startService(Context context, String action) {
        startService(context, action, null);
    }

//	private synchronized void addPendingTask(String key) {
//		pendingTasks.add(key);
//		Logger.e("后台服务 增加PenddingTask-----------");
//	}
//
//	public synchronized void removePendingTask(String key) {
//		pendingTasks.remove(key);
//		Logger.e("后台服务 移除PenddingTask-----------");
//	}
//
//	public synchronized void tryToStopServie() {
//		if (pendingTasks == null || pendingTasks.size() == 0) {
//			stopSelf();
//			Logger.e("后台服务 关闭-----------");
//		}
//	 }

    /***************** 极光TAG处理 ********************/


    public static void onTagSetFinished(final int errorCode) {

        boolean needRetry = errorCode == 6002 || errorCode == 6021 || errorCode == 6014;
        isTagSetting = false;
        if (needRetry)
            needSetTag = true;
        if (needSetTag) {
            needSetTag = false;
            new Thread(new RetryTagSetThread(errorCode == 6002 || errorCode == 6014)).start();
        }
    }


//	/***************** 设备Push设置 ********************/
//
//	private void setDevPush(){
//		List<PushSet> pushDevList = MyApp.getDaoSession().getPushSetDao().queryBuilder()
//				.where(PushSetDao.Properties.DevPushOk.eq(false), PushSetDao.Properties.IsDeleted.eq(false) )
//				.list();
//
//		Logger.d("--------->  pushDevList: "+pushDevList);
//		for ( PushSet push: pushDevList)
//		{
//			Logger.d("后台服务---------> 设置 DevPush: "+push);
//			ApiMgrV2.setPush( push.getDevId() );
//		}
//	}

    public static void setTagSetting(boolean tagSetting) {
        isTagSetting = tagSetting;
    }

    public static void mySleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createNotification(){
        String CHANNEL_ID = getString(R.string.pkg_name);
        String CHANNEL_NAME = "PUSH REGIST SERVICE";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }

        //使用兼容版本
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.app_geye);
        //设置通知栏横条的图标
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_geye));
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(false);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle(getString(R.string.app_running));
        builder.setContentText(getString(R.string.app_name));
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_ID,notification);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Logger.e("后台服务 开启---------> action:" + action);
        if (ACTION_SET_PUSH_TAG.equals(action)) {
            if (!isTagSetting) {
                needSetTag = false;
                setPushTag();
            } else {
                needSetTag = true;
            }
        } else if (ACTION_ADD_FCM_TOPIC.equals(action)) {
            Logger.d("--------->  subscribeToTopic: " + intent.getStringExtra(action));
            FirebaseMessaging.getInstance().subscribeToTopic(intent.getStringExtra(action));
        } else if (ACTION_DEL_FCM_TOPIC.equals(action)) {
            Logger.d("--------->  unsubscribeFromTopic: " + intent.getStringExtra(action));
            FirebaseMessaging.getInstance().unsubscribeFromTopic(intent.getStringExtra(action));
        }

//		else if( ACTION_SET_DEV_PUSH.equals(action)){
//			setDevPush();
//		}
    }

    private void setPushTag() {

        if (!DataLogic.getJpushTagOk()) {
            isTagSetting = true;
            List<EdwinDevice> devs = MyApp.getDaoSession().getEdwinDeviceDao().loadAll();
            final Set<String> tags = new HashSet<>();
            for (EdwinDevice dev : devs) {
                tags.add(dev.getDevId().replace("-", ""));
            }
            if (tags.isEmpty()) {
                tags.add(TagUtil.EMPTY_JPUSH_TAG);
            }
            TagBean tagBean = new TagBean();
            tagBean.setAction(Constants.OpAction.ACTION_SET);
            tagBean.setTags(tags);
            int sequence = (int) (System.currentTimeMillis() / 1000);

            Logger.e("后台服务---------> 设置 TAGS: " + tagBean);
//				addPendingTask(ACTION_SET_PUSH_TAG);
            TagUtil.getInstance().setTags(this, sequence, tagBean);
        }
    }

    public static class RetryTagSetThread implements Runnable {

        boolean needDelay = false;

        public RetryTagSetThread(boolean needDelay) {
            this.needDelay = needDelay;
        }

        @Override
        public void run() {
            if (needDelay) {
                for (int i = 0; i < 600; i++) {
                    mySleep(100);
                }
            }
            startService(MyApp.context(), ACTION_SET_PUSH_TAG);
        }
    }
}
