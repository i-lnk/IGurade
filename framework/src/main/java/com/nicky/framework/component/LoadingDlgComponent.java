package com.nicky.framework.component;


import com.nicky.framework.base.LoadingDialog;
import com.rl.commons.interf.DlgCancelCallback;
import com.rl.commons.interf.EdwinTimeoutCallback;

/**
 * Created by Nicky on 2017/8/28.
 */

public interface LoadingDlgComponent {

     LoadingDialog showLoadDialog( EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) ;

     LoadingDialog showLoadDialog( int resId,EdwinTimeoutCallback timeoutCallback,DlgCancelCallback cancelCallback) ;

     LoadingDialog showLoadDialog( String text,final EdwinTimeoutCallback timeoutCallback,final DlgCancelCallback cancelCallback );

     void hideLoadDialog() ;

     void setLoadingTips(String text);

}
