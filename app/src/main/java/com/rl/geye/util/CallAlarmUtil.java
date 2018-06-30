package com.rl.geye.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.nicky.framework.component.MyTimeOutComponent;
import com.nicky.framework.component.TimeOutComponent;
import com.orhanobut.logger.Logger;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.utils.DateUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.bean.PushData;
import com.rl.geye.bean.RingBean;
import com.rl.geye.constants.Constants;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.db.bean.SubDeviceDao;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.receiver.AlarmClickReceiver;
import com.rl.geye.service.TaskWorkServer;
import com.rl.geye.ui.aty.BellVideoAty;
import com.rl.p2plib.BridgeService;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;

import net.sqlcipher.database.SQLiteConstraintException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.POWER_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Nicky on 2017/8/28.
 * 呼叫、警报推送处理帮助类
 */
public class CallAlarmUtil implements TimeOutComponent {

    private static final String TAG = "CallAlarmUtil";
    private static final Object handleCall = new Object();
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static CallAlarmUtil instance = null;
    /* 由于mAppContext为Application的Context 因此并不会造成memory leak. mAppContext生命周期结束,app也就退出了 */
    private Context mAppContext; //application context
    private MyTimeOutComponent mTimeOutComponent;
    /**
     * 来电铃声及振动
     */
    private boolean vibrateEnable;
    private RingBean curRing;
    private SoundPool mSoundPool;
    private int playStreamId = -1;
    //    private boolean isRinging = false;
//    private boolean isRingStarting = false;
    private HashMap<Integer, Integer> soundID = new HashMap<>();
    private Vibrator mVibrator;
    //    private AudioManager mAudioManager;
    private PowerManager.WakeLock mWakelock;

    private NotificationManager mCustomMgr;
    private volatile int notifyId = 23654;

    private boolean isAlarming = false;//正在报警
    private String alarmDevId = "";//正在报警设备ID

    private ServiceWaitThread mServiceWaitThread; // 等待服务启动线程
    private AppStartWaitThread mAppStartWaitThread; //等待APP启动线程
    private String mMessage;
    private PushData mCurPushData; //当前处理的推送数据
//    private Context mCurPushContext; //当前推送的Context

    /* 私有构造方法，防止被实例化 */
    private CallAlarmUtil(Context context) {
        mAppContext = context;
        mTimeOutComponent = new MyTimeOutComponent();
        mCustomMgr = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 判断某个Activity 界面是否在前台
     * @param context
     * @param className 某个界面名称
     * @return
     */
    public static boolean  isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /* 1:懒汉式，静态工程方法，创建实例 */
    public static CallAlarmUtil getInstance() {
        if (instance == null) {
            instance = new CallAlarmUtil(MyApp.context());
        }
        return instance;
    }

    /**
     * 处理新推送(新的推送到来)
     */
    public void onNewPush(@NonNull Context pushContext, String msg, @NonNull PushData pushData) {
        if (pushData.getDev() != null) {
            if (mCurPushData == null) { //当前没有出来任何推送
                mMessage = msg;
                mCurPushData = pushData;
//                mCurPushContext = pushContext;
                if (SystemValue.appIsStarting) {
                    if (mAppStartWaitThread == null) {
                        mAppStartWaitThread = new AppStartWaitThread();
                        mAppStartWaitThread.start();
                    }
                } else {
                    onAppStartReady();
                }
            }
        }
    }

    /**
     * 设备删除 移除报警通知
     */
    public void onDeviceDeleted(String did) {
//        isAlarming &&
        if (alarmDevId.equals(did)) {
            stopAlarmNotify();
        }
    }

    /**
     * 处理推送内容
     * time：时间戳(秒数)
     */
    private void handlePush(final Context context, String did, int pushType, long time) {
        synchronized (handleCall) {
            if (!BridgeService.isReady())
                return;
            Logger.t(TAG).i("-------------> handleCall pushType: " + pushType + " , time: " + time);
            if (pushType == P2PConstants.PushType.CALL
                    || pushType == P2PConstants.PushType.PIR
                    || pushType == P2PConstants.PushType.DETECTION
                    || pushType == P2PConstants.PushType.ALARM_DISMANTLE
                    || pushType == P2PConstants.PushType.BELL_ON_LINE
                    || (pushType >= 10000 && !SystemValue.is433Matching)) {

                EdwinDevice dev = getDevice(did);
                if (dev == null) {
                    Logger.t(TAG).e("-------------> device not exist");
                    TaskWorkServer.startService(mAppContext, TaskWorkServer.ACTION_ADD_FCM_TOPIC, did.replace("-", ""));
                    return;
                }
                if (!dev.isUserPwdOK() && StringUtils.isEmpty(dev.getUser()) && StringUtils.isEmpty(dev.getPwd())) {
                    Logger.t(TAG).e("-------------> user not login ");
                    return;
                }
                Logger.t(TAG).e("-------------> SystemValue.isCallRunning :  " + SystemValue.isCallRunning);


                // 双向门铃唤醒上线
                if (pushType == P2PConstants.PushType.BELL_ON_LINE) {
                    if (!SystemValue.isCallRunning) {
                        if (BridgeService.isReady()) {
                            BridgeService.getInstance().refreshDevice(dev);
                            // BridgeService.getInstance().connectDevice(dev);
                        }
                    }
                    EdwinRecord record = new EdwinRecord();
                    record.setDid(dev.getId());
                    record.setType(pushType);
                    record.setAnswered(false);
                    record.setTriggerTime(time);
                    record.setDevice(dev);
                    record.setDate(DateUtil.getDateStr(time * 1000));
                    return;
                }

                EdwinRecord record = new EdwinRecord();
                record.setDid(dev.getId());
                record.setType(pushType);
                record.setAnswered(false);
                record.setTriggerTime(time);
                record.setDevice(dev);
                record.setDate(DateUtil.getDateStr(time * 1000));
                if (pushType >= 10000) {
                    record.setType(P2PConstants.PushType.ALARM_433);
                    SubDevice subDev = null;
                    List<SubDevice> subList = MyApp
                            .getDaoSession()
                            .getSubDeviceDao()
                            .queryBuilder()
                            .where(SubDeviceDao.Properties.Pid.eq(did), SubDeviceDao.Properties.Id.eq(String.valueOf(pushType - 10000)))
                            .list();
                    if (subList != null && !subList.isEmpty()) {
                        subDev = subList.get(0);
                    }
                    Logger.t(TAG).i("-------------> handleCall callType getSubDevice: " + subDev);
                    if (subDev == null || subDev.getType() == P2PConstants.SubDevType.REMOTE_CONTROL) {
                        if (subDev == null)
                            Logger.t(TAG).e("-------------> handleCall no SubDev ");
                        else
                            Logger.t(TAG).e("-------------> handleCall SubDev type is Control ");
                        return;
                    }
                    record.setSubDevId(subDev.getId());
                    createAlarmNotify(dev, pushType, record.getId());
                }
                long res;
                try {
                    res = ((MyApp) mAppContext).getDaoSession().getEdwinRecordDao().insert(record);
                } catch (SQLiteConstraintException sqlE) {
                    sqlE.printStackTrace();
                    res = -1;
                }
                if (res == -1) {
                    Logger.t(TAG).e("-------------> add record failed");
                    record.setId(-1L);
//                    return;
                } else {
                    postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_ADD);
                }
                boolean noDisturb = DataLogic.isNoDisturb();
                if (noDisturb) {
                    return;
                }
                // if( pushType == P2PConstants.PushType.CALL ){
                // && MainActivity.isReady()
                if (!SystemValue.isCallRunning) {
                    postEdwinEvent(Constants.EdwinEventType.EVENT_FINISH_P2P_PAGE);
                    Intent intent = new Intent(context, BellVideoAty.class);
                    intent.putExtra(Constants.BundleKey.KEY_DEV_INFO, dev);
                    intent.putExtra(Constants.BundleKey.KEY_PUSH_TYPE, pushType);
                    intent.putExtra(Constants.BundleKey.KEY_RECORD_ID, record.getId());
                    // intent.putExtra(Constants.BundleKey.KEY_TRIGGER_TIME, time);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    }catch (Exception e){
                        Log.e("CallAlarmUtil","create BellVideoAty failed.");
                    }
                    createAlarmNotify(dev, pushType, record.getId());
                } else {
                    Logger.t(TAG).e("-------------> BellVideoAty is Running....");
                    createAlarmNotify(dev, pushType, record.getId());
                }
//                }else{
//                    if(!isAlarming){
//                        createAlarmNotify(dev,pushType,record.getId());
//                                startRingAndVibrator(pushType);
//                        startTimeoutThread(new EdwinTimeoutCallback(10*1000) {
//                            @Override
//                            public void onTimeOut() {
//                                stopAlarmNotify();
//                            }
//                        });
//                    }
//                }
            } else if (pushType == P2PConstants.PushType.LOW_CHARGE) {
                EdwinDevice dev = getDevice(did);
                EdwinRecord record = new EdwinRecord();
                record.setDid(dev.getId());
                record.setType(pushType);
                record.setAnswered(false);
                record.setTriggerTime(time);
                record.setDevice(dev);
                record.setDate(DateUtil.getDateStr(time * 1000));

                long res;
                try {
                    res = ((MyApp) mAppContext).getDaoSession().getEdwinRecordDao().insert(record);
                } catch (SQLiteConstraintException sqlE) {
                    sqlE.printStackTrace();
                    res = -1;
                }
                if (res == -1) {
                    Logger.t(TAG).e("-------------> add record failed");
                    record.setId(-1L);
//                    return;
                } else {
                    postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_ADD);
                }
                createAlarmNotify(dev, pushType, record.getId());
                //startRingAndVibrator(pushType);
                startTimeoutThread(new EdwinTimeoutCallback(30 * 1000) {
                    @Override
                    public void onTimeOut() {
                        stopAlarmNotify();
                    }
                });
            }
        }
    }

    private synchronized void createAlarmNotify(EdwinDevice dev, int pushType, Long recordId) {
//      isAlarming = true;
        alarmDevId = dev.getDevId();
        PowerManager pm = (PowerManager) mAppContext.getSystemService(POWER_SERVICE);
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "BELL_VIDEO");
        if (mWakelock != null)
            mWakelock.acquire();
        Logger.t(TAG).i("notify dev: " + dev);

        Intent clickIntent = new Intent("action_click", null, mAppContext, AlarmClickReceiver.class);
        Intent dismissIntent = new Intent("action_dismiss", null, mAppContext, AlarmClickReceiver.class);

        clickIntent.putExtra("Notification", true);
        clickIntent.putExtra(Constants.BundleKey.KEY_DEV_INFO, dev);
        clickIntent.putExtra(Constants.BundleKey.KEY_RECORD_ID, recordId);
        clickIntent.putExtra(Constants.BundleKey.KEY_PUSH_TYPE, pushType);
//      intent.putExtra(Constants.BundleKey.KEY_TRIGGER_TIME, time);
//      intent.putExtra(Constants.BundleKey.KEY_RECORD_ID, record.getId());
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(mAppContext, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(mAppContext, 0, dismissIntent, 0);
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeResource(mAppContext.getResources(), com.rl.p2plib.R.mipmap.app_geye);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String alarmMsg;
        String alarmTitle;
        switch (pushType) {
            case P2PConstants.PushType.CALL:
                alarmTitle = mAppContext.getString(R.string.record_call);
                alarmMsg = mAppContext.getString(R.string.record_call);
                break;
            case P2PConstants.PushType.PIR:
                alarmTitle = mAppContext.getString(R.string.record_call);
                alarmMsg = mAppContext.getString(R.string.record_call);
                break;
            case P2PConstants.PushType.DETECTION:
                alarmTitle = mAppContext.getString(R.string.record_detect);
                alarmMsg = mAppContext.getString(R.string.desc_movement);
                break;
            case P2PConstants.PushType.ALARM_DISMANTLE:
                alarmTitle = mAppContext.getString(R.string.record_dismantle);
                alarmMsg = mAppContext.getString(R.string.desc_alarm_dismantle);
                break;
            case P2PConstants.PushType.LOW_CHARGE:
                alarmTitle = mAppContext.getString(R.string.charge_alarm);
                alarmMsg = mAppContext.getString(R.string.charge_alarm);
                break;
            case P2PConstants.PushType.BELL_ON_LINE:
                alarmTitle = mAppContext.getString(R.string.new_online);
                alarmMsg = mAppContext.getString(R.string.new_online);
                break;
            default:
                alarmTitle = mAppContext.getString(R.string.desc_alarm_other);
                alarmMsg = mAppContext.getString(R.string.desc_alarm_other);
                break;
        }
        alarmTitle = mMessage;
        alarmMsg = mMessage;
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(notifyId),
                    mAppContext.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            mCustomMgr.createNotificationChannel(channel);
            builder = new Notification.Builder(mAppContext, String.valueOf(notifyId));
        } else {
            builder = new Notification.Builder(mAppContext);
        }
        builder.setContentTitle(alarmTitle)
                .setContentText(alarmMsg)
                .setSmallIcon(R.mipmap.app_geye)
                .setAutoCancel(true)
                .setLargeIcon(bm)
                // .setSound( Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.ring_alarm_default) )
                .setVibrate(new long[]{0, 1000, 1000})
                .setDeleteIntent(dismissPendingIntent)
                .setContentIntent(clickPendingIntent);
        Notification notification = builder.build();
        //使用默认的声音
        notification.defaults |= Notification.DEFAULT_SOUND;
        //使用默认的震动
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        mCustomMgr.notify(notifyId, notification);
        notifyId++;
    }

    public void stopAlarmNotify() {
//        stopRingAndVibrator();
//        mCustomMgr.cancel(NOTIFY_ID_ALARM);
        mCustomMgr.cancelAll();
        clearTimeoutThread();
//        isAlarming = false;
        alarmDevId = "";
        try {
            if (mWakelock != null) {//&& mWakelock.isHeld()
                mWakelock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始响铃和震动
     */
    public synchronized void startRingAndVibrator(final int pushType) {

        vibrateEnable = DataLogic.isVibrateEnable();
        if (pushType == P2PConstants.PushType.CALL) {
            curRing = DataLogic.getRingBell();
        } else {
            curRing = DataLogic.getRingAlarm();
        }
        mVibrator = (Vibrator) mAppContext.getSystemService(VIBRATOR_SERVICE);
//        mAudioManager = ((AudioManager) mAppContext.getSystemService(AUDIO_SERVICE));
//        isRingStarting = true;
        if (vibrateEnable) {
            long[] pattern = {1000, 1000};
            mVibrator.vibrate(pattern, 1);

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSoundPool == null) {
                        Logger.t(TAG).i("------------startRinging：");
//                        Thread.sleep(100);
                        //当前系统的SDK版本大于等于21(Android 5.0)时
                        if (Build.VERSION.SDK_INT >= 21) {
                            SoundPool.Builder builder = new SoundPool.Builder();
                            //传入音频数量
                            builder.setMaxStreams(1);
                            //AudioAttributes是一个封装音频各种属性的方法
                            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                            //设置音频流的合适的属性
                            attrBuilder.setLegacyStreamType(AudioManager.STREAM_RING);
                            //加载一个AudioAttributes
                            builder.setAudioAttributes(attrBuilder.build());
                            mSoundPool = builder.build();
                        }
                        //当系统的SDK版本小于21时
                        else {
                            //设置最多可容纳1个音频流，音频的品质为50
                            mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 50);
                        }

                        int sourceId = -1;
                        if (curRing.getRemindType() == RingBean.RingRemindType.DEFAULT) {
                            sourceId = mSoundPool.load(mAppContext, curRing.getRingDefaultResId(), 0);
                        } else if (curRing.getRemindType() == RingBean.RingRemindType.MUTE) {

                        } else {
                            File file = new File(curRing.getRingUrl());
                            if (file.exists()) {
                                sourceId = mSoundPool.load(file.getAbsolutePath(), 0);
                            } else {
                                sourceId = mSoundPool.load(mAppContext, curRing.getRingDefaultResId(), 0);
                            }
                        }
                        playRing(sourceId);
                    } else {
                        Logger.t(TAG).w("already ringing");
                    }
                } catch (Exception e) {
                    Logger.t(TAG).e("cannot handle incoming call" + e);
                }
//                isRinging = true;
//                isRingStarting = false;
            }
        }).start();
    }

    private void playRing(final int sourceId) {
        if (sourceId == -1)
            return;
        try {
//            soundID.put(1, mSoundPool.load(this, curRing.getRingDefaultResId(), 1));
            mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (mSoundPool != null)
                        playStreamId = mSoundPool.play(sourceId, 2, 2, 0, -1, 1);
                }
            });
        } catch (Exception ex) {
            Logger.t(TAG).e("playDefault failed:", ex);
        }
    }

    /**
     * 停止响铃和震动
     */
    public synchronized void stopRingAndVibrator() {
        if (mSoundPool != null) {
            if (playStreamId != -1)
                mSoundPool.stop(playStreamId);
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }


    @Override
    public void startTimeoutThread(EdwinTimeoutCallback callback) {
        mTimeOutComponent.startTimeoutThread(callback);
    }

    @Override
    public void clearTimeoutThread() {
        mTimeOutComponent.clearTimeoutThread();
    }

    /**
     * 设备
     */
    private synchronized EdwinDevice getDevice(String did) {
        List<EdwinDevice> list = ((MyApp) mAppContext).getDaoSession().getEdwinDeviceDao().loadAll();
        if (list != null && !list.isEmpty()) {
            for (EdwinDevice device : list) {
                String strDid = device.getDevId();
                if (IdUtil.isSameId(did, strDid)) {
                    return device;
                }
            }
        }
        return null;
    }

    protected void postEdwinEvent(EdwinEvent<?> event) {
        if (null != event) {
            EventBus.getDefault().post(event);
        }
    }

    protected void postEdwinEvent(int eventCode) {
        postEdwinEvent(new EdwinEvent<>(eventCode, CallAlarmUtil.class));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onMessage(EdwinEvent event) {
        if (event == null)
            return;
    }

    /************************************************** 推送处理等待线程 ************************************************/

    private void onAppStartReady() {
        if (BridgeService.isReady()) {
            onServiceReady();
        } else {
            if (mServiceWaitThread == null) {
                mAppContext.startService(new Intent(Intent.ACTION_MAIN).setClass(mAppContext, BridgeService.class));
                mServiceWaitThread = new ServiceWaitThread();
                mServiceWaitThread.start();
            }
        }
    }

    private void onServiceReady() {
        Logger.t(TAG).i(" onServiceReady--------------------------------------");
//        BridgeService.getInstance().CallBack_AlarmNotifyDoorBell(did, did, type, time);
        if (mCurPushData != null && mCurPushData.getDev() != null)
            CallAlarmUtil.getInstance().handlePush(mAppContext, mCurPushData.getDev().getUuid(), mCurPushData.getDev().getType(), mCurPushData.getDev().getTime());
        mCurPushData = null;
//        mCurPushContext = null;
    }

    /**
     * BridgeService正在启动 --等待启动完成
     */
    private class ServiceWaitThread extends Thread {
        public ServiceWaitThread() {
        }

        public void run() {
            while (!BridgeService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            onServiceReady();
            mServiceWaitThread = null;
        }
    }


    /**
     * app正在启动 --等待启动完成
     */
    private class AppStartWaitThread extends Thread {

        public AppStartWaitThread() {

        }

        public void run() {
            while (SystemValue.appIsStarting) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            onAppStartReady();
            mAppStartWaitThread = null;
        }
    }


}
