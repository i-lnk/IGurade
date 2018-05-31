package com.nicky.framework.component;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import com.nicky.framework.R;
import com.nicky.framework.base.LoadingDialog;
import com.rl.commons.BaseApp;
import com.rl.commons.interf.DlgCancelCallback;
import com.rl.commons.interf.EdwinTimeoutCallback;

/**
 * Created by Nicky on 2017/8/28.
 */
public class MyLoadingDlgComponent implements LoadingDlgComponent {


    private LoadingDialog dialog;
    private MyTimeOutComponent mTimeOutComponent;

    private Activity mActivity;
    private Fragment mFragment;

    public MyLoadingDlgComponent(Activity activity){
        this.mActivity = activity;
        mTimeOutComponent = new MyTimeOutComponent();
    }
    public MyLoadingDlgComponent(Fragment fragment){
        this.mFragment = fragment;
        mTimeOutComponent = new MyTimeOutComponent();
    }

    public Context getContext(){
        if(mActivity!=null)
            return mActivity;
        if( mFragment!=null )
            return  mFragment.getContext();
        return  BaseApp.context();
    }


    @Override
    public LoadingDialog showLoadDialog(EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return showLoadDialog( R.string.common_loading_message,timeoutCallback,cancelCallback );
    }

    @Override
    public LoadingDialog showLoadDialog(int resId, EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return showLoadDialog( BaseApp.context().getString(resId),timeoutCallback  ,cancelCallback);
    }

    @Override
    public LoadingDialog showLoadDialog(String text, EdwinTimeoutCallback timeoutCallback, final DlgCancelCallback cancelCallback ) {
        if(dialog==null)
            dialog = new LoadingDialog(getContext());
        dialog.setLoadMsg(text);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if( timeoutCallback!=null )
        {
            mTimeOutComponent.startTimeoutThread(timeoutCallback);
        }
        if( cancelCallback!=null )
        {
            dialog.setCancelable(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    hideLoadDialog();
                    cancelCallback.onCancel();
                }
            });
        }
        dialog.show();
        return dialog;
    }

    @Override
    public void hideLoadDialog() {
        mTimeOutComponent.clearTimeoutThread();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void setLoadingTips(String text) {
        if(dialog!=null){
            dialog.setLoadMsg(text);
        }
    }

}
