package com.nicky.framework.component;

import android.support.annotation.NonNull;
import android.view.View;
import com.nicky.framework.R;
import com.nicky.framework.varyview.VaryViewHelperController;

/**
 * Created by Nicky on 2017/8/28.
 * 支持Loading View、Empty View、Error View 组件的具体实现
 */

public class MyVaryViewComponent implements VaryViewComponent {

    /** empty view 、loading view 、error view controller */
    private VaryViewHelperController mVaryViewHelperController = null;

    public MyVaryViewComponent(@NonNull View TargetView){
        mVaryViewHelperController = new VaryViewHelperController(TargetView);
    }

    public void setTargetView( @NonNull View TargetView ){
        mVaryViewHelperController = new VaryViewHelperController(TargetView);
    }


    @Override
    public void showLoading() {
        showLoading(R.string.common_loading_message);
    }

    @Override
    public void showLoading(int resId) {
        if (null == mVaryViewHelperController ) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        try {
            mVaryViewHelperController.showLoading(resId);
        } catch (Exception e) {
            //TODO
        }
    }

    @Override
    public void showLoading(String msg) {
        if (null == mVaryViewHelperController ) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        try {
            mVaryViewHelperController.showLoading(msg);
        } catch (Exception e) {
            //TODO
        }
    }

    @Override
    public void showEmpty(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        showEmpty(msg,-1,onClickListener);
    }

    @Override
    public void showEmpty(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (null == mVaryViewHelperController ) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.showMsgView(VaryViewHelperController.MsgType.MSG_EMPTY, msg,iconId, onClickListener);
    }

    @Override
    public void showError(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        showError(msg,-1,onClickListener);
    }

    @Override
    public void showError(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.showMsgView(VaryViewHelperController.MsgType.ERROR_NORMAL, msg,iconId, onClickListener);
    }

//    @Override
//    public void showNetworkError(VaryViewHelperController.OnMsgViewClickListener onClickListener) {
//        showNetworkError(-1,onClickListener);
//    }
//
//    @Override
//    public void showNetworkError(int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
//        if (null == mVaryViewHelperController ) {
//            throw new IllegalArgumentException("You must return a right target view for loading");
//        }
//        mVaryViewHelperController.showMsgView(VaryViewHelperController.MsgType.ERROR_NET, "",iconId, onClickListener);
//    }

    @Override
    public void hideMsgView() {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }
        mVaryViewHelperController.restore();
    }


}
