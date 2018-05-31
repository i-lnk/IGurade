package com.rl.geye.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.rl.commons.bean.EdwinEvent;
import com.rl.geye.R;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.ui.aty.BellVideoAty;
import com.rl.geye.ui.aty.IpcVideoAty;
import com.rl.p2plib.BridgeService;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;


/**
 * Created by Nicky on 2017/3/2.
 * P2P Activity 基类(处理P2P回调)
 */
public abstract class BaseP2PAty extends BaseMyAty {
    protected boolean isOnline = true; //设备是否在线
    protected EdwinDevice mDevice;
    protected MyP2PCallBack mP2pCallBack;
    private Handler mP2PHandler;

    protected abstract void onP2PStatusChanged();

    private void initP2PCallBack(MyP2PCallBack callBack) {
        mP2pCallBack = callBack;
    }

    protected MyP2PCallBack getP2PCallBack() {
        return null;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mDevice = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO);
            isOnline = fromIntent.getBooleanExtra(Constants.BundleKey.KEY_DEV_ONLINE, true);
        }
        return mDevice != null && super.initPrepareData();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void prepareInitView(Bundle savedInstanceState) {
        super.prepareInitView(savedInstanceState);

        /** 唤醒屏幕级全屏  */
        if (BellVideoAty.getInstance() != null || IpcVideoAty.getInstance() != null)
            wakeUpScreen();
        mP2PHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case R.id.msg_refresh_p2p_aty:
                        onP2PStatusChanged();
                        invalidateOptionsMenu();
                        break;
                }
            }
        };

        initP2PCallBack(getP2PCallBack());
        BridgeService.addP2PAppCallBack(mP2pCallBack);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BridgeService.removeP2PAppCallBack(mP2pCallBack);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        for (int i=0 ;i<menu.size();i++ ){
//            MenuItem item =  menu.getItem(i);
//            if( item.getItemId()==R.id.item_delete  ){
//                break;
//            }
//            if( isOnline ){
//                item.setEnabled(true);
//                item.getIcon().setAlpha(255);
//            }else{
//                item.setEnabled(false);
//                item.getIcon().setAlpha(99);
//            }
//        }
        MenuItem item = menu.findItem(R.id.item_save);
        if (item != null) {
            if (isOnline) {
                item.setEnabled(true);
                item.getIcon().setAlpha(255);
            } else {
                item.setEnabled(false);
                item.getIcon().setAlpha(99);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    protected boolean isSameDevice(String did) {
        return (mDevice != null && IdUtil.isSameId(mDevice.getDevId(), did));
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected boolean ignoreEventSelf() {
        return true;
    }

    @Override
    protected void onXEventRecv(EdwinEvent<?> event) {
        super.onXEventRecv(event);
        switch (event.getEventCode()) {

            case Constants.EdwinEventType.EVENT_FINISH_P2P_PAGE:
                if (!isFinishing())
                    finish();
                break;

            default:
                break;
        }
    }

    /**
     * 唤醒屏幕级全屏
     */
    protected void wakeUpScreen() {
        final Window win = getWindow();
        // WindowManager.LayoutParams.FLAG_FULLSCREEN |
        // WindowManager.LayoutParams.FLAG_FULLSCREEN |
        win.setFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
//                | PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "BELL_VIDEO");
//        if (mWakelock != null)
//            mWakelock.acquire();
//        KeyguardManager mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("BELL_VIDEO");
//        mKeyguardLock.disableKeyguard();//解锁屏幕，也就是 关闭 屏幕 锁定 功能
    }

    public class MyP2PCallBack extends SimpleP2PAppCallBack {
        private boolean isRefresh = false;

        @Override
        public void onStatusChanged(String did, int type, int param) {
            super.onStatusChanged(did, type, param);
            Log.e("onStatusChanged：", "param：" + param);
            if (isSameDevice(did)) {
                if (param == P2PConstants.P2PStatus.ON_LINE) {
                    isOnline = true;
                } else {
                    isOnline = false;
                }
                if (!isRefresh) {
                    isRefresh = true;
                    mP2PHandler.sendEmptyMessageDelayed(R.id.msg_refresh_p2p_aty, 1000);
                }
            }
        }
    }

}
