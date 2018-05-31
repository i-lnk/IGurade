package com.rl.geye.ui.aty;//package com.rl.geye.ui.aty;
//
///**
// * Created by PYH on 2018/1/5.
// */
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.widget.TextView;
//
//import com.orhanobut.logger.Logger;
//import com.rl.commons.utils.StringUtils;
//import com.rl.geye.bean.PushData;
//import com.rl.geye.util.CallAlarmUtil;
//import com.rl.p2plib.utils.JSONUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import cn.jpush.android.api.JPushInterface;
//
///**
// * Created by jiguang on 17/9/26.
// */
//
//public class OpenClickActivity extends Activity {
//    private static final String TAG = "OpenClickActivity";
//    /**消息Id**/
//    private static final String KEY_MSGID = "msg_id";
//    /**该通知的下发通道**/
//    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
//    /**通知标题**/
//    private static final String KEY_TITLE = "n_title";
//    /**通知内容**/
//    private static final String KEY_CONTENT = "n_content";
//    /**通知附加字段**/
//    private static final String KEY_EXTRAS = "n_extras";
//    private TextView mTextView;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTextView = new TextView(this);
//        setContentView(mTextView);
//        handleOpenClick();
//    }
//
//
//    /**
//     * 处理点击事件，当前启动配置的Activity都是使用
//     * Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
//     * 方式启动，只需要在onCreat中调用此方法进行处理
//     */
//    private void handleOpenClick() {
//        Log.d(TAG, "用户点击打开了通知");
//        if (getIntent().getData() == null) return;
//        String data = getIntent().getData().toString();
//        Log.w(TAG, "msg content is " + String.valueOf(data));
//        if (TextUtils.isEmpty(data)) return;
//        try {
//            JSONObject jsonObject = new JSONObject(data);
//            String msgId = jsonObject.optString(KEY_MSGID);
//            byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
//            String title = jsonObject.optString(KEY_TITLE);
//            String content = jsonObject.optString(KEY_CONTENT);
//            String extras = jsonObject.optString(KEY_EXTRAS);
//            StringBuilder sb = new StringBuilder();
//            sb.append("msgId:");
//            sb.append(String.valueOf(msgId));
//            sb.append("\n");
//            sb.append("title:");
//            sb.append(String.valueOf(title));
//            sb.append("\n");
//            sb.append("content:");
//            sb.append(String.valueOf(content));
//            sb.append("\n");
//            sb.append("extras:");
//            sb.append(String.valueOf(extras));
//            sb.append("\n");
//            sb.append("platform:");
//            sb.append(getPushSDKName(whichPushSDK));
//            mTextView.setText(sb.toString());
//            //上报点击事件
//            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
//            if (!StringUtils.isEmpty(extras)) {
//                try {
////                    //TODO---TEST
////                    PushData pushData = new PushData();
////                    PushData.PushDev pushDev = new PushData.PushDev();
////                    pushDev.setTime(1503996890);
////                    pushDev.setType(P2PConstants.PushType.DETECTION);
////                    pushDev.setUuid("P6FLAKWW6WVVH8PW111A");
////                    pushData.setDev(pushDev);
//
//                    PushData pushData = JSONUtil.fromJson(extras,PushData.class);
//
//                    Logger.t(TAG).i("[JPushReceiver] 接收到推送下来的通知 pushData:" + pushData );
////                    if(2!=pushData.getDev().getType()){
////                        int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
////                        mCustomMgr.cancel(notificationId);//--消除通知
////                    }
//
//
////                    boolean noDisturb = DataLogic.isNoDisturb();
////                    if(noDisturb){
////                        return;
////                    }
//                    if(pushData!=null && pushData.getDev()!=null)
//                        CallAlarmUtil.getInstance().onNewPush( getApplicationContext(),pushData);
//
//                }catch (Exception e) {
//                    Log.e(TAG, "parse json data failed:[" + extras + "]");
//                    return;
//                }
//            }
//        } catch (JSONException e) {
//            Log.w(TAG, "parse notification error");
//        }
//
//
//    }
//
//    private String getPushSDKName(byte whichPushSDK) {
//        String name;
//        switch (whichPushSDK){
//            case 0:
//                name = "jpush";
//                break;
//            case 1:
//                name = "xiaomi";
//                break;
//            case 2:
//                name = "huawei";
//                break;
//            case 3:
//                name = "meizu";
//                break;
//            case 8:
//                name = "fcm";
//                break;
//            default:
//                name = "jpush";
//        }
//        return name;
//    }
//}
//
