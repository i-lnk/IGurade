package com.rl.geye.base;


import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.nicky.framework.base.BaseActivity;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Nicky on 2017/3/2.
 */
public abstract class BaseMyAty extends BaseActivity {

    Unbinder unbinder;

    @Override
    protected boolean useLightBarStatus() {
        return true;
    }

    protected void initCommonToolBar(Toolbar _toolbar) {
        _toolbar.setNavigationIcon(R.mipmap.ic_back);
        _toolbar.setTitle("");
        setSupportActionBar(_toolbar);
        _toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                onBackPressed();
            }
        });
    }

    @Override
    protected void initBind() {
        unbinder = ButterKnife.bind(this);
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


    /**
     * 唤醒屏幕并解锁
     */
    protected void wakeUpScreen() {
        final Window win = getWindow();
        win.setFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,

                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
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
