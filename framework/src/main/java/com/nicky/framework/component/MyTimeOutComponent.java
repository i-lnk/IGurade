package com.nicky.framework.component;

import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.log.XLog;

/**
 * Created by Nicky on 2017/8/28.
 */

public class MyTimeOutComponent implements TimeOutComponent {

    private EdwinTimeoutCallback timeoutCallBack;
    private boolean mCheckTimeout;// 超时线程退出标记
    private Thread mTimeoutThread;// 超时线程

    /**
     * 超时5秒
     */
    private static final int REQUEST_TIMEOUT = 5 * 1000; // total timeout = REQUEST_TIMEOUT * 100ms

    private int mTimeoutVal = REQUEST_TIMEOUT;

    @Override
    public void startTimeoutThread(EdwinTimeoutCallback callback) {
        if (callback != null) {
            clearTimeoutThread();
            mCheckTimeout = true;
            mTimeoutVal = REQUEST_TIMEOUT;
            timeoutCallBack = callback;
            mTimeoutVal = (int) timeoutCallBack.timeout;
            mTimeoutThread = new Thread(mTimeoutRunnable);
            mTimeoutThread.start();
//            mTimeoutTask = ThreadPoolMgr.getCustomThreadPool().submit(mTimeoutRunnable);
        }
    }

    @Override
    public void clearTimeoutThread() {
        if (mTimeoutThread == null) {
            mCheckTimeout = false;
            timeoutCallBack = null;
            return;
        }
        if (mTimeoutThread.isAlive()) {
            try {
                mCheckTimeout = false;
                mTimeoutThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mTimeoutThread = null;
        mCheckTimeout = false;
        timeoutCallBack = null;
        /*
        if( mTimeoutTask==null ){
            return;
        }
        mCheckTimeout = false;
        sleepFuture(mTimeoutTask,150);
        if( !mTimeoutTask.isCancelled() && !mTimeoutTask.isDone() )
        {
            mTimeoutTask.cancel(true);
        }
        mTimeoutTask = null;
        timeoutCallBack = null;
        */
    }

    /**
     * 超时处理
     */
    private Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            XLog.i("timeout thread start");
            int mElapsed = 0;
            while (mCheckTimeout) {
                sleep(100);
                mElapsed += 100;
                if (mCheckTimeout) {
                    if (mElapsed > mTimeoutVal) {
                        XLog.w("-------------->[timeout]");
                        mCheckTimeout = false;
                        mTimeoutThread = null;
//                        mTimeoutTask = null;
                        if (timeoutCallBack != null)
                            timeoutCallBack.onTimeOut();
                        break;
                    }
                }
            }
            XLog.i("timeout thread stop");
        }
    };

    public void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
