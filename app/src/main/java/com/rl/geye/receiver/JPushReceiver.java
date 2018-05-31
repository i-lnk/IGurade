package com.rl.geye.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.bean.PushData;
import com.rl.geye.util.CallAlarmUtil;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.JSONUtil;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {


    private static final String TAG = "JPushReceiver";
    private NotificationManager mCustomMgr;

    //    https://docs.jiguang.cn/jpush/client/Android/android_senior/
    @Override
    public void onReceive(Context context, Intent intent) {
        mCustomMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();

        Log.e("JPush", "call onReceive");
//        for (String key : bundle.keySet()) {
//            Logger.t(TAG).i("Key=" + key + ", content=" + bundle.get(key));
//        }
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//            String received = bundle.getString(JPushInterface.ACTION_MESSAGE_RECEIVED);
//            Logger.t(TAG).d("[JPushReceiver] 接收RECEIVED : " + received);
//            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//            mCustomMgr.cancel(notificationId);//--消除通知
//            String pushStr = bundle.getString(JPushInterface.EXTRA_ALERT);
//            String pushExt = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Logger.t(TAG).i("pushExt:"+ pushExt);
//            Logger.t(TAG).i("pushStr:"+ pushStr);
//            if (!StringUtils.isEmpty(pushExt)) {
//                try {
//                    PushData pushData = JSONUtil.fromJson(pushExt,PushData.class);
//                    Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的透传 pushData:" + pushData );
//                    if(pushData!=null && pushData.getDev()!=null)
//                        CallAlarmUtil.getInstance().onNewPush( context,pushData);
//                }catch (Exception e) {
//                    Log.e(TAG, "parse json data failed:[" + pushExt + "]");
//                    return;
//                }
//            }

        } else if (JPushInterface.EXTRA_MSG_ID.equals(intent.getAction())) {
//            String pushExt=bundle.getString()
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的通知");
//            String regId = bundle.getString(JPushInterface.ACTION_NOTIFICATION_RECEIVED);
//            Logger.t(TAG).d("[JPushReceiver] 接收Registration Id : " + regId);
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            mCustomMgr.cancel(notificationId);//--消除通知
            String pushStr = bundle.getString(JPushInterface.EXTRA_ALERT);
            String pushExt = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logger.t(TAG).i("pushExt:" + pushExt);
            Logger.t(TAG).i("pushStr:" + pushStr);
            if (!StringUtils.isEmpty(pushExt)) {
                try {
//                    //TODO---TEST
//                    PushData pushData = new PushData();
//                    PushData.PushDev pushDev = new PushData.PushDev();
//                    pushDev.setTime(1503996890);
//                    pushDev.setType(P2PConstants.PushType.DETECTION);
//                    pushDev.setUuid("P6FLAKWW6WVVH8PW111A");
//                    pushData.setDev(pushDev);

                    PushData pushData = JSONUtil.fromJson(pushExt, PushData.class);

                    Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的通知 pushData:" + pushData);
//                    if(2!=pushData.getDev().getType()){
//                        int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//                        mCustomMgr.cancel(notificationId);//--消除通知
//                    }


//                    boolean noDisturb = DataLogic.isNoDisturb();
//                    if(noDisturb){
//                        return;
//                    }
                    if (pushData != null && pushData.getDev() != null)
                        CallAlarmUtil.getInstance().onNewPush(context, pushStr, pushData);

                } catch (Exception e) {
                    Log.e(TAG, "parse json data failed:[" + pushExt + "]");
                    return;
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logger.t(TAG).d("[JPushReceiver] 用户点击打开了通知");
            toJieTing1(context, bundle);
//            if (!MainAty.isReady()) {
            //打开自定义的Activity
//                Intent i = new Intent(context, LauncherAty.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);
//            }
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Logger.t(TAG).d("[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Logger.t(TAG).w("[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
//            toJieTing(context,bundle);
            if (!connected) {
                Logger.t(TAG).w("[JPushReceiver]" + intent.getAction() + " isPushStopped " + JPushInterface.isPushStopped(context));
//				JPushInterface.stopPush(context);
//				JPushInterface.resumePush(context);
//				Set<String> tags = new HashSet<>();
//				tags.add("11" );
//				tags.add("22" );
//				//2EBAZVM55Y463VF2111A, 8S8NFKPY7DHVW325111A, H6EU7EJH37L4A8RV111A
//				JPushInterface.setTags( context, tags, null );
            }
        } else {
            Logger.t(TAG).d("[JPushReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void toJieTing1(Context context, Bundle bundle) {
        String pushStr = bundle.getString(JPushInterface.EXTRA_ALERT);
        String pushExt = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Logger.t(TAG).i("pushExt:" + pushExt);
        Logger.t(TAG).i("pushStr:" + pushStr);
        if (!StringUtils.isEmpty(pushExt)) {
            try {
                PushData pushData = JSONUtil.fromJson(pushExt, PushData.class);
                Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的消息 pushData:" + pushData);
                if (pushData != null && pushData.getDev() != null)
                    CallAlarmUtil.getInstance().onNewPush(context, pushStr, pushData);
            } catch (Exception e) {
                Log.e(TAG, "parse json data failed:[" + pushExt + "]");
                return;
            }
        }
    }

    private void toJieTing(Context context, Bundle bundle) {
        String pushStr = bundle.getString(JPushInterface.EXTRA_ALERT);
        Toast.makeText(context, "pushStr=" + pushStr, Toast.LENGTH_LONG).show();
//            String pushExt = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Logger.t(TAG).i("pushExt:"+ pushExt);
        Logger.t(TAG).i("pushStr:" + pushStr);
//            if (!StringUtils.isEmpty(pushExt)) {
        try {
//                    //TODO---TEST
            PushData pushData = new PushData();
            PushData.PushDev pushDev = new PushData.PushDev();
            pushDev.setTime(1493625603);
            pushDev.setType(P2PConstants.PushType.CALL);
            pushDev.setUuid("PD9DNR95CVAL8NH4111A");
            pushData.setDev(pushDev);

//                    PushData pushData = JSONUtil.fromJson(pushExt,PushData.class);


//                    if(2!=pushData.getDev().getType()){
//                        int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//                        mCustomMgr.cancel(notificationId);//--消除通知
//                    }


//                    boolean noDisturb = DataLogic.isNoDisturb();
//                    if(noDisturb){
//                        return;
//                    }
            if (pushData != null && pushData.getDev() != null && pushStr != null)
                Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的通知 pushData:" + pushData);
            CallAlarmUtil.getInstance().onNewPush(context, pushStr, pushData);

        } catch (Exception e) {
//                    Log.e(TAG, "parse json data failed:[" + pushExt + "]");
            return;
        }
//            }
    }

}