package com.rl.p2plib;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.edwintech.vdp.jni.Avapi;
import com.rl.commons.BaseApp;
import com.rl.commons.log.XLog;
import com.rl.commons.utils.StringUtils;
import com.rl.p2plib.bean.Battery;
import com.rl.p2plib.bean.CallAnswer;
import com.rl.p2plib.bean.DetectInfo;
import com.rl.p2plib.bean.DevSdCard;
import com.rl.p2plib.bean.DevSysSet;
import com.rl.p2plib.bean.DevTimeZone;
import com.rl.p2plib.bean.EdwinVideoGroup;
import com.rl.p2plib.bean.MonthRecord;
import com.rl.p2plib.bean.OsdInfo;
import com.rl.p2plib.bean.PicMode;
import com.rl.p2plib.bean.PowerData;
import com.rl.p2plib.bean.RecordTime;
import com.rl.p2plib.bean.SetResult;
import com.rl.p2plib.bean.SysInfo;
import com.rl.p2plib.bean.UpdateProgress;
import com.rl.p2plib.bean.VoiceData;
import com.rl.p2plib.bean.WakeUpData;
import com.rl.p2plib.bean.WifiData;
import com.rl.p2plib.callback.P2PJniCallBack;
import com.rl.p2plib.callback.PushCallback;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;
import com.rl.p2plib.constants.CmdConstant;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.interf.IP2PDevice;
import com.rl.p2plib.p2pconnect.P2PConnectThreads;
import com.rl.p2plib.utils.JSONUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@SuppressLint("NewApi")
public class BridgeService extends Service implements P2PJniCallBack {

    private final static String TAG = "BridgeService";

    private static final Object handleForInit = new Object();

//	WakeLock wakeLock = null;
//	private PendingIntent mkeepAlivePendingIntent;

    private boolean p2pLibStartOK = false; //P2P库初始化成功与否

    private static BridgeService instance;

    public static BridgeService getInstance() {
        return instance;
    }


    private HashMap<String, Integer> statusMap = new HashMap<>();

    private Lock devLock = new ReentrantLock(); //设备状态变化
//	private List<EdwinDevice> mDeviceList;//数据库中的设备列表


//	private PendingIntent mNotifContentIntent;
//
//	private final static int NOTIF_ID = 1;
//	private NotificationManager mCustomMgr;
//	private Notification mNotif;
//
//	private static final Class<?>[] mSetFgSign = new Class[] {boolean.class};
//	private static final Class<?>[] mStartFgSign = new Class[] {int.class, Notification.class};
//	private static final Class<?>[] mStopFgSign = new Class[] {boolean.class};
//	private Method mSetForeground;
//	private Method mStartForeground;
//	private Method mStopForeground;
//	private Object[] mSetForegroundArgs = new Object[1];
//	private Object[] mStartForegroundArgs = new Object[2];
//	private Object[] mStopForegroundArgs = new Object[1];


    public int getDeviceStatus(String did) {
        int status = P2PConstants.P2PStatus.UNKNOWN;
        String key = did.replace("-", "");
        if (!StringUtils.isEmpty(did) && statusMap.containsKey(key)) {
            status = statusMap.get(key);
        }
        return status;
    }


    /**
     * 服务是否已启动
     */
    public static boolean isReady() {
        return instance != null;
    }

    private static PushCallback mPushCallback;
    private static Class<?> mClickReceiverCls;


    /***** 推送处理 *****************/
    public static synchronized void init(Class<?> clickReceiverCls, PushCallback callback) {
        mPushCallback = callback;
        mClickReceiverCls = clickReceiverCls;
    }

    @Override
    public IBinder onBind(Intent intent) {
        XLog.i(TAG, "------------------> onBind()");
        return new ControllerBinder();
    }


    /** */
    class ControllerBinder extends Binder {
        public BridgeService getBridgeService() {
            return BridgeService.this;
        }
    }

    @Override
    public void onCreate() {
        XLog.i(TAG, "------------------> onCreate() ");
        super.onCreate();
//		BootstrapService.startForeground(this);
        // start BootstrapService to remove notification
//		Intent intent = new Intent(this, BootstrapService.class);
//		startService(intent);

        statusMap = new HashMap<>();
        if (devLock == null)
            devLock = new ReentrantLock(); //设备状态变化
//		initNotification();
        initP2P();
//		startForegroundCompat(NOTIF_ID, mNotif);
        registerReceiver();
        instance = this;

        XLog.i(TAG, "------------------> onCreate() END");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//		return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        XLog.e(TAG, "------------------> onDestroy()");
        super.onDestroy();
        destroyP2P();
        stopForeground(true);
        // Make sure our notification is gone.
//		stopForegroundCompat(NOTIF_ID);
//		mCustomMgr.cancel(NOTIF_ID);

        unregisterReceiver();
        statusMap.clear();
        instance = null;
        XLog.e(TAG, "------------------> onDestroy() END");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        XLog.e(TAG, "------------------> onUnbind()");
        return super.onUnbind(intent);
    }

    /*************************************** P2P初始化 ******************************************/


    public boolean isP2pLibStartOK() {
        return p2pLibStartOK;
    }

    /**
     * 重新启动P2P
     */
    public void restartP2P(boolean forceRestart) {
        XLog.i(TAG, "restartP2P..............");
        if (!p2pLibStartOK || forceRestart) {
            destroyP2P();
            initP2P();
        }
    }

    private void initP2P() {

        synchronized (handleForInit) {
            XLog.i(TAG, "startP2P..............");
            p2pLibStartOK = false;
            try {
                Avapi.PPPPInitialize(P2PConstants.P2P_SERVER);
                Avapi.PPPPManagementInit();
                Avapi.PPPPSetCallbackContext(this);
                XLog.i(TAG, "startP2P [ OVER ]..............");
                p2pLibStartOK = true;
            } catch (Exception e) {
                e.printStackTrace();
                XLog.e(TAG, "Cannot start lib p2p");
                BaseApp.showToast("Cannot start lib p2p");
                p2pLibStartOK = false;
            }
            if (!p2pLibStartOK) {
                //TODO
//                postEdwinEvent(P2PConstants.P2PEventType.EVENT_P2P_INIT_FAILED);
            }
        }
    }

    private void destroyP2P() {
        synchronized (handleForInit) {
            try {
                XLog.i(TAG, "------------------->PPPPManagementFree...");
                Avapi.PPPPManagementFree();
                XLog.i(TAG, "------------------->PPPPManagementFree [ OVER ]");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            p2pLibStartOK = false;
        }
    }


    /*********************************************************************************************/

//	private RebootReceiver mRebootReceiver;
    private void registerReceiver() {

//		if(!EventBus.getDefault().isRegistered(this))
//		{
//			EventBus.getDefault().register(this);
//		}
//		mRebootReceiver = new RebootReceiver();
//		IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_TIME_TICK);
//        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
//		registerReceiver(mRebootReceiver, filter);
    }

    private void unregisterReceiver() {

//		EventBus.getDefault().unregister(this);
//		if(mRebootReceiver!=null)
//			unregisterReceiver(mRebootReceiver);
    }

    /********************************************P2P回连接*************************************************/

    /**
     * 启动设备PPPP(多个设备)
     */
    public <T extends IP2PDevice> void connectDevices(@NonNull List<T> list) {
        if (!p2pLibStartOK)
            return;
        if (list == null)
            return;
        for (T device : list) {
            new Thread(new P2PConnectThreads.ConnectDeviceThread<>(device)).start();
            mySleep(50);
        }
//		new Thread( new P2PConnectThreads.ConnectDevicesThread<>(list)).start();
    }

    /**
     * 启动设备PPPP(具体某个设备)
     */
    public <T extends IP2PDevice> void connectDevice(@NonNull T dev) {
        if (!p2pLibStartOK)
            return;
        new Thread(new P2PConnectThreads.ConnectDeviceThread<>(dev)).start();
    }

    /**
     * 重启设备PPPP(具体某个设备)
     */
    public <T extends IP2PDevice> void refreshDevice(@NonNull T dev) {
        if (!p2pLibStartOK)
            return;
        new Thread(new P2PConnectThreads.RefreshDeviceThread<>(dev, true)).start();
    }

//	/**  重启设备PPPP(具体某个设备)  */
//	public<T extends IP2PDevice>  void refreshDeviceDelay(@NonNull T dev ,long mills ){
//		if(!p2pLibStartOK)
//			return;
//		new Thread( new P2PConnectThreads.RefreshDeviceThread<>(dev,true) ).start();
//	}

    /**
     * 重启设备PPPP(多个设备)
     **/
    public <T extends IP2PDevice> void refreshDevices(List<T> list) {
        if (!p2pLibStartOK)
            return;
        if (list == null)
            return;
        for (T device : list) {
            new Thread(new P2PConnectThreads.RefreshDeviceThread(device, true)).start();
            mySleep(50);
        }
//		new P2PConnectThreads.RefreshDevicesMainThread( list ).start();
    }

    /**
     * 关闭设备PPPP(具体某个设备)
     */
    public void stopDevice(@NonNull String devId) {
        if (!p2pLibStartOK)
            return;
        new Thread(new P2PConnectThreads.StopDeviceThread(devId)).start();
    }


    /**
     * 关闭设备PPPP连接(多个设备)
     */
    public <T extends IP2PDevice> void stopDevices(@NonNull List<T> list) {
        if (!p2pLibStartOK)
            return;
        if (list == null)
            return;
        for (T device : list) {
            new Thread(new P2PConnectThreads.StopDeviceThread(device.getDevId())).start();
            mySleep(50);
        }
//		new Thread( new P2PConnectThreads.StopDevicesThread<>(list)).start();
    }


    /*************************************** P2P回调 ******************************************/


    private static Set<SimpleP2PAppCallBack> mP2PAppCallBack = new LinkedHashSet<>();
    private static final Object handleCallBack = new Object();

    public static void addP2PAppCallBack(SimpleP2PAppCallBack callback) {
        if (callback != null) {
            synchronized (handleCallBack) {
                if (mP2PAppCallBack == null) {
                    mP2PAppCallBack = new LinkedHashSet<>();
                }
                mP2PAppCallBack.add(callback);
            }
        }
    }

    public static void removeP2PAppCallBack(SimpleP2PAppCallBack callback) {
        if (callback != null) {
            synchronized (handleCallBack) {
                if (mP2PAppCallBack != null && !mP2PAppCallBack.isEmpty()) {
                    mP2PAppCallBack.remove(callback);
                }
            }
        }
    }


    @Override
    public void VideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time) {
        XLog.v(TAG, "----------->PPPPMsgNotify did: " + did);
        synchronized (handleCallBack) {
            if (mP2PAppCallBack != null) {
                for (SimpleP2PAppCallBack callback : mP2PAppCallBack) {
                    callback.onVideoData(did, videoBuf, h264Data, len, width, height, time);
                }
            }
        }
    }

    @Override
    public void PPPPMsgNotify(String did, int type, int status) {
        XLog.e(TAG, "----------->PPPPMsgNotify did: " + did + "PPPPMsgNotify did: " + type + "status: " + status + " BEGIN");
        synchronized (handleCallBack) {
            if (type == P2PConstants.PPPPMsgType.PPPP_STATUS) {
                statusMap.put(did.replace("-", ""), status);
                if (mP2PAppCallBack != null) {
                    for (SimpleP2PAppCallBack callback : mP2PAppCallBack) {
                        callback.onStatusChanged(did, type, status);
                    }
                }
            }
        }
        XLog.e(TAG, "----------->PPPPMsgNotify did: " + did + " ,status: " + status + " END");
    }

    @Override
    public void SearchResult(int cameraType, String mac, String name, String did, String ipAddress, int port, int deviceType) {
        XLog.e(TAG, "SearchResult: did:" + did + " , Name: " + name + " , deviceType: " + deviceType);
        if (StringUtils.isEmpty(did)) {
            return;
        }
        synchronized (handleCallBack) {
            if (mP2PAppCallBack != null) {
                for (SimpleP2PAppCallBack callback : mP2PAppCallBack) {
                    callback.onSearchResult(cameraType, mac,
                            name, did, ipAddress, port, deviceType);
                }
            }
        }
    }

    @Override
    public void CallBack_AlarmNotifyDoorBell(String did, String dbDid, String dbType, String dbTime) {
        XLog.e(TAG, "----------->PPPPMsgNotify did: " + did + "dbDid：" + dbDid + "dbDid：" + dbType);
//		if (StringUtils.isEmpty(dbDid) || dbDid.length() < 3) {
//			return;
//		}
////      if ( dev.getStatus() == Constants.DeviceStatus.PPPP_STATUS_NOT_LOGIN
////      || dev.getStatus() == Constants.DeviceStatus.PPPP_STATUS_ERR_USER_PWD)
//		int devStatus = getDeviceStatus(dbDid);
//		if (devStatus == P2PConstants.P2PStatus.NOT_LOGIN || devStatus == P2PConstants.P2PStatus.ERR_USER_PWD) {
//			return;
//		}
//		long callTime;
//		if (dbTime != null && dbTime.length() >= 21 && !dbTime.startsWith("1970")) {
//			String dateTime = dbTime.substring(0, 10) + " "
//					+ dbTime.substring(15, 17) + ":" + dbTime.substring(18, 20)
//					+ ":" + dbTime.substring(21);
//			callTime = DateUtil.getDateMills(dateTime) / 1000;
//		} else {
//			callTime = System.currentTimeMillis() / 1000;
//		}
//		if (dbType != null) {
//			handleCall(dbDid, dbType, callTime);
//		}

        int pushType = StringUtils.toInt(dbType);
        String msg = "------------->pushType ：" + pushType + " , did: " + did + " , dbDid : " + dbDid;
        Toast.makeText(BridgeService.this, msg, Toast.LENGTH_LONG).show();
        Log.i(TAG, "------------->pushType ：" + pushType + " , did: " + did + " , dbDid : " + dbDid);
        synchronized (handleCallBack) {
            if (mP2PAppCallBack != null) {
                for (SimpleP2PAppCallBack callback : mP2PAppCallBack) {
                    if (callback != null)
                        callback.onPushTypeChanged(dbDid, pushType);
                }
            }

        }

    }

    @Override
    public void UILayerNotify(String did, int msgType, byte[] data, int len) {
        byte[] d = new byte[len];
        System.arraycopy(data, 0, d, 0, len);
        String msgData = byte2Str(data, len);
        XLog.e(TAG, "-----------------> msgType: " + msgType);
        synchronized (handleCallBack) {
            if (mP2PAppCallBack != null) {
                for (SimpleP2PAppCallBack callback : mP2PAppCallBack) {
                    switch (msgType) {
                        case CmdConstant.CmdType.USER_IPCAM_SETPASSWORD_RESP:
                            if (callback != null)
                                callback.onSetResult(did, msgType, JSONUtil.fromJson(msgData, SetResult.class));
                        case CmdConstant.CmdType.USER_IPCAM_CFG_433_RESP:
                        case CmdConstant.CmdType.USER_IPCAM_SET_433_RESP:
                        case CmdConstant.CmdType.USER_IPCAM_DEL_433_RESP:
                            if (callback != null)
                                callback.onSetResult(did, msgType, JSONUtil.fromJson(msgData, SetResult.class));
                            break;
                        case CmdConstant.CmdType.RESP_GET_PIC_MODE:
                            if (callback != null)
                                callback.onGetPicMode(did, msgType, JSONUtil.fromJson(msgData, PicMode.class));
                            break;
                        case CmdConstant.CmdType.GET_MONTH_RECORD_RESP:
                            if (callback != null)
                                callback.onGetMonthRecord(did, msgType, JSONUtil.fromJson(msgData, MonthRecord.class));
                            break;
                        case CmdConstant.CmdType.VIDEOLIST_GET_RESP:
                            if (callback != null) {
                                msgData = StringUtils.replaceBlank(msgData);
                                callback.onGetVideoGroup(did, msgType, JSONUtil.fromJson(msgData, EdwinVideoGroup.class));
                            }
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_GET_SDCARD_RESP:
                            if (callback != null)
                                callback.onGetSdCard(did, msgType, JSONUtil.fromJson(msgData, DevSdCard.class));
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_SET_SDCARD_RESP:
                            if (callback != null)
                                callback.onSdSetAck(did, msgType);
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_GET_OSD_RESP:
                            if (callback != null)
                                callback.onGetOsdInfo(did, msgType, JSONUtil.fromJson(msgData, OsdInfo.class));
                            break;
                        case CmdConstant.CmdType.RESP_GET_DETECT_YTJ:
                            if (callback != null)
                                callback.onGetDetectInfo(did, msgType, JSONUtil.fromJson(msgData, DetectInfo.class));
                            break;
                        case CmdConstant.CmdType.RESP_GET_RECORD_TIME:
                            if (callback != null)
                                callback.onGetRecordTime(did, msgType, JSONUtil.fromJson(msgData, RecordTime.class));
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_POWER_FREQUENCY_RESP:
                            Log.e(TAG, "IOTYPE_USER_IPCAM_GET_POWER_FREQUENCY_RESP" + msgData);
                            if (callback != null)
                                callback.onGetPower(did, msgType, JSONUtil.fromJson(msgData, PowerData.class));
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_AUDIO_VOLUME_RESP:
                            if (callback != null)
                                callback.onGetVoice(did, msgType, JSONUtil.fromJson(msgData, VoiceData.class));
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_GET_TIMEZONE_RESP:
                            if (callback != null)
                                callback.onGetTimeZone(did, msgType, JSONUtil.fromJson(msgData, DevTimeZone.class));
                            break;
                        case CmdConstant.CmdType.GET_SYS_SET_RESP:
                        case CmdConstant.CmdType.SET_SYS_SET_RESP:
                            if (callback != null)
                                callback.onGetDevSysSet(did, msgType, JSONUtil.fromJson(msgData, DevSysSet.class));
                            if (CmdConstant.CmdType.SET_SYS_SET_RESP == msgType)
                                if (callback != null)
                                    callback.onSetDevSysSet(did, msgType, JSONUtil.fromJson(msgData, DevSysSet.class));
                            break;
                        case CmdConstant.CmdType.DEV_SYS_GET_RESP:
                            if (callback != null)
                                callback.onGetSysInfo(did, msgType, JSONUtil.fromJson(msgData, SysInfo.class));
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_GET_433_RESP:
                            if (callback != null) {
                                String strDecoder;
                                try {
                                    strDecoder = URLDecoder.decode(msgData, "UTF-8");
                                } catch (Exception e) {
                                    strDecoder = msgData;
                                }
                                callback.onGetSubList(did, msgType, strDecoder);
                            }
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_BATTERY_RESP:
                            if (callback != null)
                                callback.onGetBattery(did, msgType, JSONUtil.fromJson(msgData, Battery.class));
                            break;
//						case CmdConstant.CmdType.DEV_SYS_UPDATE_RESP:
//							callback.onUpdateAck( did,msgType );
//							break;
                        case CmdConstant.CmdType.DEV_SYS_UPDATE_PROGRESS_RESP:
                            if (callback != null)
                                callback.onUpdateProgress(did, msgType, JSONUtil.fromJson(msgData, UpdateProgress.class));
                            break;
                        case CmdConstant.CmdType.CALL_OTHER_ANSWERED:
                            if (callback != null)
                                callback.onOtherUserAnswered(did, msgType, JSONUtil.fromJson(msgData, CallAnswer.class));
                            break;
                        case CmdConstant.CmdType.USER_IPCAM_SET_PUSH_RESP:
                            XLog.i(TAG, "-----------111--->set push: " + did);
                            if (callback != null)
                                callback.onPushSetOk(did, msgType);
                            break;
                        case CmdConstant.CmdType.RESP_GET_WIFI:
                        case CmdConstant.CmdType.RESP_GET_WIFI2:
                            if (callback != null)
                                callback.onGetWifiInfo(did, msgType, JSONUtil.fromJson(msgData, WifiData.class));
                            break;

                        case CmdConstant.CmdType.USER_IPCAM_SET_TIMEZONE_RESP:
                            if (callback != null)
                                callback.onSetTimeZone(did, msgType, JSONUtil.fromJson(msgData, DevTimeZone.class));
                            break;
                        case CmdConstant.CmdType.RESP_SET_DETECT:
                            if (callback != null)
                                callback.onSetDetect(did, msgType, JSONUtil.fromJson(msgData, DetectInfo.class));
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_SET_POWER_FREQUENCY_RESP:
                            Log.e(TAG, "IOTYPE_USER_IPCAM_SET_POWER_FREQUENCY_RESP" + msgData);
                            if (callback != null) {
                                callback.onSetPower(did, msgType, JSONUtil.fromJson(msgData, PowerData.class));
                            }
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_SET_AUDIO_VOLUME_RESP:
                            if (callback != null)
                                callback.onSetVoice(did, msgType, JSONUtil.fromJson(msgData, VoiceData.class));
                            break;
                        case CmdConstant.CmdType.IOTYPE_USER_IPCAM_GET_WAKEUP_FUN_RESP:
                            if (callback != null)
                                callback.onGetWakeUp(did, msgType, JSONUtil.fromJson(msgData, WakeUpData.class));
                            break;
                    }
                }
            }

        }
    }


    /****************************************来电(或警报)处理 ******************************************/

//	public synchronized void handleCall( String did, String callType, long callTime ) {
//		int pushType = StringUtils.toInt(callType);
//		if(mPushCallback !=null){
//			mPushCallback.onPushCallback(this, did ,pushType, callTime);
//		}
////		synchronized (handleCallBack) {
////			if (mP2PAppCallBack != null) {
////				for (SimpleP2PCallBack callback : mP2PAppCallBack) {
////					callback.onPushTypeChanged( did, pushType );
////				}
////			}
////		}
//	}


    /*************************************** 通知栏 Start ******************************************/

//	/** 初始化通知栏 */
//	private void initNotification(){
//		mCustomMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////		mCustomMgr.cancel(INCALL_NOTIF_ID); // in case of crash the icon is not removed
//		if(mClickReceiverCls==null)
//			mClickReceiverCls = NotificationClickReceiver.class;
//		Intent clickIntent = new Intent(this,mClickReceiverCls); //点击通知之后要发送的广播
//		clickIntent.putExtra("Notification", true);
//		mNotifContentIntent = PendingIntent.getBroadcast(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//		String title = getResources().getString(R.string.app_name);
//		String message = getResources().getString(R.string.app_running);
//		Bitmap bm = null;
//		int iconId = R.mipmap.app_geye;
//		try {
//			bm = BitmapFactory.decodeResource(getResources(), R.mipmap.app_geye);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		mNotif = Compatibility.createNotification(this, title, message, iconId, 0, bm, mNotifContentIntent, true, 0);
//
//
//		// Retrieve methods to publish notification and keep Android
//		// from killing us and keep the audio quality high.
//		if (Version.sdkStrictlyBelow(Version.API05_ECLAIR_20)) {
//			try {
//				mSetForeground = getClass().getMethod("setForeground", mSetFgSign);
//			} catch (NoSuchMethodException e) {
//				XLog.e(TAG, "Couldn't find foreground method");
//			}
//		} else {
//			try {
//				mStartForeground = getClass().getMethod("startForeground", mStartFgSign);
//				mStopForeground = getClass().getMethod("stopForeground", mStopFgSign);
//			} catch (NoSuchMethodException e) {
//				XLog.e(TAG, "Couldn't find startForeground or stopForeground");
//			}
//		}
//	}
//
//	/**
//	 * This is a wrapper around the new startForeground method, using the older
//	 * APIs if it is not available.
//	 */
//	void startForegroundCompat(int id, Notification notification) {
//		// If we have the new startForeground API, then use it.
//		if (mStartForeground != null) {
//			mStartForegroundArgs[0] = Integer.valueOf(id);
//			mStartForegroundArgs[1] = notification;
//			invokeMethod(mStartForeground, mStartForegroundArgs);
//			return;
//		}
//
//		// Fall back on the old API.
//		if (mSetForeground != null) {
//			mSetForegroundArgs[0] = Boolean.TRUE;
//			invokeMethod(mSetForeground, mSetForegroundArgs);
//			// continue
//		}
//
//		notifyWrapper(id, notification);
//	}
//
//	/**
//	 * This is a wrapper around the new stopForeground method, using the older
//	 * APIs if it is not available.
//	 */
//	void stopForegroundCompat(int id) {
//		// If we have the new stopForeground API, then use it.
//		if (mStopForeground != null) {
//			mStopForegroundArgs[0] = Boolean.TRUE;
//			invokeMethod(mStopForeground, mStopForegroundArgs);
//			return;
//		}
//
//		// Fall back on the old API.  Note to cancel BEFORE changing the
//		// foreground state, since we could be killed at that point.
//		mCustomMgr.cancel(id);
//		if (mSetForeground != null) {
//			mSetForegroundArgs[0] = Boolean.FALSE;
//			invokeMethod(mSetForeground, mSetForegroundArgs);
//		}
//	}
//
//
//	/**
//	 * Wrap notifier to avoid setting the icons while the service
//	 * is stopping. When the (rare) bug is triggered, the icon is
//	 * present despite the service is not running. To trigger it one could
//	 * stop app as soon as it is started. Transport configured with TLS.
//	 */
//	private synchronized void notifyWrapper(int id, Notification notification) {
//		if (instance != null && notification != null) {
//			mCustomMgr.notify(id, notification);
//		} else {
//			XLog.i(TAG,"Service not ready, discarding notification");
//		}
//	}
//
//	void invokeMethod(Method method, Object[] args) {
//		try {
//			method.invoke(this, args);
//		} catch (InvocationTargetException e) {
//			// Should not happen.
//			XLog.w(TAG, "Unable to invoke method");
//		} catch (IllegalAccessException e) {
//			// Should not happen.
//			XLog.w(TAG, "Unable to invoke method");
//		}
//	}

    /*************************************** 通知栏 End ******************************************/

    private static String byte2Str(byte[] buf, int len) {
        String strByte = "";
        try {
            strByte = new String(buf, 0, len, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        XLog.i(TAG, "-----------------> byte2Str: " + strByte);
        return strByte;
    }


    public static void mySleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
