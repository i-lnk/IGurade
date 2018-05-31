package com.rl.geye.ui.aty;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.nicky.framework.widget.AspectRatio;
import com.nicky.framework.widget.XRelativeLayout;
import com.rl.commons.BaseApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.ui.widget.RandomView;
import com.rl.geye.ui.widget.SearchDevicesView;
import com.rl.p2plib.bean.SetResult;
import com.rl.p2plib.constants.CmdConstant;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/11/7.
 * 433 对码
 */
public class CheckCodeAty extends BaseP2PAty {

    private static final int PROGRESS_UPDATE_AUTO = 21;//进度条随时间更新
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    SearchDevicesView searchView;
    @BindView(R.id.search_bg)
    View searchBg;
    @BindView(R.id.random_view)
    RandomView randomView;
    @BindView(R.id.layout_search)
    XRelativeLayout layoutSearch;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.snackbar_container)
    CoordinatorLayout snackBarContainer;
    private int subType; // 433 子设备类型
    private String subName;// 433 子设备名称
    private String resTips = ""; // 配置结果
    private boolean isConfiging = false; //是否在配置中
    private boolean isFirstShow = true;
    private Thread timeThread; //计时线程
    private boolean timeRunFlag = true;
    private Handler mHandler;
    private int progressVal = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_check_code;
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
//            mDevId = fromIntent.getStringExtra(Constants.BundleKey.KEY_DEV_ID);
            subName = fromIntent.getStringExtra(Constants.BundleKey.KEY_SUB_NAME);
            subType = fromIntent.getIntExtra(Constants.BundleKey.KEY_SUB_TYPE, 0);
        }
        return super.initPrepareData();
    }

    @Override
    protected View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.check_code);
    }

    @Override
    protected void initViewsAndEvents() {

        SystemValue.is433Matching = true;

        layoutSearch.setRatio(AspectRatio.makeAspectRatio(408.0f / 480.0f, true));

        searchView.setWillNotDraw(false);

        randomView.setOnRandomClickListener(new RandomView.OnRandomClickListener() {

            @Override
            public void onRandomClicked(View view) {

            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case PROGRESS_UPDATE_AUTO:
                        progressVal += 1; //
                        updateProgress();
                        break;

                }
            }
        };


    }

    @Override
    protected void onClickView(View v) {

    }

    @Override
    protected void onResume() {
        searchView.setSearching(true);
        super.onResume();
        if (isFirstShow) {
            isFirstShow = false;
            startConfig433(subName, subType);
            startTimeThread();
        }
    }

    @Override
    protected void onPause() {
        searchView.setSearching(false);
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimeThread();
        if (isConfiging) {
            stopConfig433();
            isConfiging = false;
        }
        SystemValue.is433Matching = false;
    }


    /*********************************************************************************
     * 							计时线程(超时时间暂定为20s)
     *********************************************************************************/
    private Thread getTimeThread() {
        if (timeThread == null) {
            return new Thread() {
                @Override
                public void run() {
                    while (timeRunFlag) {
                        try {
                            Thread.sleep(200);
                            Message message = new Message();
                            message.what = PROGRESS_UPDATE_AUTO;
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                        }
                    }
                }
            };
        }
        return timeThread;
    }

    // 开启计时线程
    private void startTimeThread() {
        timeRunFlag = true;
        timeThread = getTimeThread();
        if (!timeThread.isAlive()) {
            timeThread.start();
        }

    }

    // 关闭计时线程
    private void stopTimeThread() {
        timeRunFlag = false;
        if (timeThread != null) {
            timeThread.interrupt();
            timeThread = null;
        }
        mHandler.removeMessages(PROGRESS_UPDATE_AUTO);
        progressVal = 0;
    }


    private synchronized void updateProgress() {
        if (progressVal <= 100) {
            progressBar.setProgress(progressVal);
        }
        if (progressVal >= 100) {
            stopTimeThread();
            resTips = getString(R.string.cfg_433_timeout);
            BaseApp.showToast(resTips);
            if (isConfiging) {
                stopConfig433();
                isConfiging = false;
                finish();
            }
        }
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {

        return new MyP2PCallBack() {

            @Override
            public void onSetResult(String did, int msgType, SetResult setRes) {
                super.onSetResult(did, msgType, setRes);
                if (mDevice != null && IdUtil.isSameId(did, mDevice.getDevId()) && setRes != null && CmdConstant.CmdType.USER_IPCAM_CFG_433_RESP == msgType) {
                    resTips = "";
                    switch (setRes.getResult()) {
                        case P2PConstants.SetResVal.RES_OK:
                            resTips = getString(R.string.cfg_433_ok);
                            break;
                        case P2PConstants.SetResVal.RES_CFG_433_TIMEOUT:
                            resTips = getString(R.string.cfg_433_timeout);
                            break;
                        case P2PConstants.SetResVal.RES_CFG_433_MAX:
                            resTips = getString(R.string.cfg_433_max);
                            break;
                        case P2PConstants.SetResVal.RES_CFG_433_EXISTS:
                            resTips = getString(R.string.cfg_433_exists);
                            break;
                        case P2PConstants.SetResVal.RES_CFG_433_WAITING:
                            resTips = getString(R.string.cfg_433_waiting);
                            return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseApp.showToast(resTips);
                        }
                    });
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isConfiging) {
                                stopConfig433();
                                isConfiging = false;
                                finish();
                            }
                        }
                    }, 200);
                }
            }

        };
    }


    /**
     * 开始配对
     */
    private void startConfig433(String name, int type) {
        isConfiging = true;
        ApiMgrV2.startConfig433(mDevice.getDevId(), name, type);
    }

    /**
     * 停止配对
     */
    private void stopConfig433() {
        ApiMgrV2.stopConfig433(mDevice.getDevId());
    }


}
