package com.rl.geye.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rl.geye.R;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;

/**
 * Created by Nicky on 2017/8/12.
 * P2P Fragment 基类(处理P2P回调)
 */
public abstract class BaseP2PFrag extends BaseMyFrag {
    protected boolean isOnline = true;
    protected EdwinDevice mDevice;
    private Handler mP2PHandler;

    protected abstract void onP2PStatusChanged();

    protected MyP2PCallBack getP2PCallBack() {
        return null;
    }

    @Override
    protected boolean initPrepareData() {
        Bundle argBundle = getArguments();
        if (argBundle != null) {
            mDevice = argBundle.getParcelable(Constants.BundleKey.KEY_DEV_INFO);
            isOnline = argBundle.getBoolean(Constants.BundleKey.KEY_DEV_ONLINE, true);
        }
        return mDevice != null && super.initPrepareData();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        initP2PCallBack(getP2PCallBack());

//        BridgeService.addP2PAppCallBack(mP2pCallBack);

        mP2PHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case R.id.msg_refresh_p2p_frag:
                        onP2PStatusChanged();
                        break;
                }
            }
        };
    }

    protected boolean isSameDevice(String did) {
        return (mDevice != null && IdUtil.isSameId(mDevice.getDevId(), did));
    }

    public class MyP2PCallBack extends SimpleP2PAppCallBack {
        @Override
        public void onStatusChanged(String did, int type, int param) {
            super.onStatusChanged(did, type, param);

            if (isSameDevice(did)) {
                if (param == P2PConstants.P2PStatus.ON_LINE) {
                    isOnline = true;
                } else {
                    isOnline = false;
                }
                mP2PHandler.sendEmptyMessage(R.id.msg_refresh_p2p_frag);
            }
        }
    }
}
