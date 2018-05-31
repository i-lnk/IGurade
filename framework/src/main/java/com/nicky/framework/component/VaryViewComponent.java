package com.nicky.framework.component;

import com.nicky.framework.varyview.VaryViewHelperController;

/**
 * Created by Nicky on 2017/8/28.
 * 支持Loading View、Empty View、Error View 组件
 */

public interface VaryViewComponent {

    /**
     * show loading
     */
    void showLoading();

    void showLoading(int resId);

    void showLoading(String msg);


    /**
     * show empty
     */
    void showEmpty(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener);
    void showEmpty(String msg,int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener);

    /**
     * show error
     */
    void showError(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener);

    void showError(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener);

//    /**
//     * show network error
//     */
//    void showNetworkError(VaryViewHelperController.OnMsgViewClickListener onClickListener);
//
//    void showNetworkError(int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener);

    /** hide */
    void hideMsgView();

}
