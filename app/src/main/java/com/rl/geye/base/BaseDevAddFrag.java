package com.rl.geye.base;

import android.content.Context;
import android.os.Bundle;

import com.rl.geye.bean.DevTypeGroupBean;
import com.rl.geye.constants.Constants;
import com.rl.geye.ui.aty.DevAddAty;

/**
 * Created by Nicky on 2016/9/21.
 * 添加设备--页面基类
 */
public abstract class BaseDevAddFrag extends BaseMyFrag {
    protected DevTypeGroupBean devTypeGroup;
    protected DevAddAty mAty;

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof DevAddAty) {
            mAty = (DevAddAty) context;
        } else {
            throw new RuntimeException(context.toString() + " must be DevAddAty");
        }
    }

    @Override
    protected boolean initPrepareData() {
        Bundle argBundle = getArguments();
        if (argBundle != null) {
            devTypeGroup = argBundle.getParcelable(Constants.BundleKey.KEY_DEV_TYPE_GROUP);
        }
        return devTypeGroup != null;
    }
}
