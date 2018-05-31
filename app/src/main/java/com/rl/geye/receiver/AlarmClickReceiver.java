package com.rl.geye.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.ui.aty.BellVideoAty;
import com.rl.geye.ui.aty.IpcVideoAty;
import com.rl.geye.util.CallAlarmUtil;
import com.rl.p2plib.constants.P2PConstants;


/**
 * @author NickyHuang
 * @ClassName: NotificationClickReceiver
 * @Description: 报警通知栏点击响应
 * @date 2016-6-28 下午4:19:37
 */
public class AlarmClickReceiver extends BroadcastReceiver {

    private final static String TAG = "AlarmClickReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
//		XLog.e(TAG, "---------------------------->onReceive");

        if ("action_click".equals(intent.getAction())) {

//            Logger.t(TAG).i("click dev: "+intent.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO));

            CallAlarmUtil.getInstance().stopAlarmNotify();
            if (BellVideoAty.getInstance() != null) {
                BellVideoAty.getInstance().finish();
            }
            if (IpcVideoAty.getInstance() != null) {
                IpcVideoAty.getInstance().finish();
            }
            EdwinDevice device = intent.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO);
            Intent clickIntent = new Intent(context, device.isIpc() ? IpcVideoAty.class : BellVideoAty.class);
            clickIntent.putExtra(Constants.BundleKey.KEY_AUTO_ANSWER, true);
            clickIntent.putExtra(Constants.BundleKey.KEY_DEV_INFO, device);
            clickIntent.putExtra(Constants.BundleKey.KEY_PUSH_TYPE, intent.getIntExtra(Constants.BundleKey.KEY_PUSH_TYPE, P2PConstants.PushType.CALL));
//      clickIntent.putExtra(Constants.BundleKey.KEY_TRIGGER_TIME, time);
            clickIntent.putExtra(Constants.BundleKey.KEY_RECORD_ID, intent.getLongExtra(Constants.BundleKey.KEY_RECORD_ID, -1));
            clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(clickIntent);
        } else if ("action_dismiss".equals(intent.getAction())) {
            CallAlarmUtil.getInstance().stopAlarmNotify();
        }

    }


}
