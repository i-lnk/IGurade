package com.rl.p2plib.callback;

import android.content.Context;

/**
 * Created by Nicky on 2017/6/16.
 */

public interface PushCallback {

    /**
     * 推送处理
     * @param context
     * @param did
     * @param pushType
     * @param time
     */
    void onPushCallback(Context context, String did, int pushType, long time);


}
