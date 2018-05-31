package com.rl.geye.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.nicky.framework.base.BaseFragment;
import com.rl.commons.bean.EdwinEvent;
import com.rl.p2plib.BridgeService;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Nicky on 2016/9/9.
 */
public abstract class BaseMyFrag extends BaseFragment {

    protected SimpleP2PAppCallBack mP2pCallBack;
    Unbinder unbinder;

    private void initP2PCallBack(SimpleP2PAppCallBack callBack) {
        mP2pCallBack = callBack;
    }

    protected SimpleP2PAppCallBack getP2PCallBack() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initP2PCallBack(getP2PCallBack());
        BridgeService.addP2PAppCallBack(mP2pCallBack);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BridgeService.removeP2PAppCallBack(mP2pCallBack);
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected abstract void onAttachToContext(Context context);


    @Override
    protected void initBind() {
        unbinder = ButterKnife.bind(this, mView);
    }

    @Override
    protected void unBind() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    @Override
    protected void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }


    /**************************************** EdwinEvent 处理 ****************************************/

    /**
     * when event received
     */
    protected void onXEventRecv(EdwinEvent<?> event) {
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(EdwinEvent<?> event) {
        if (null != event) {
            if (ignoreEventSelf() && event.getFromCls() != null && event.getFromCls().equals(getXClass())) {
                return;
            }
            onXEventRecv(event);
        }
    }

    protected void postEdwinEvent(EdwinEvent<?> event) {
        if (null != event) {
            EventBus.getDefault().post(event);
        }
    }

    protected void postEdwinEvent(int eventCode) {
        postEdwinEvent(new EdwinEvent<>(eventCode, getXClass()));
    }


    protected void postEdwinEvent(int eventCode, Object data) {
        postEdwinEvent(new EdwinEvent<>(eventCode, data, getXClass()));
    }


}
