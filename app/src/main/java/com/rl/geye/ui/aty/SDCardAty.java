package com.rl.geye.ui.aty;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.rl.commons.BaseApp;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.p2plib.bean.DevSdCard;
import com.rl.p2plib.constants.P2PConstants;

import java.text.DecimalFormat;

import butterknife.BindView;

/**
 * Created by Nicky on 2016/10/20.
 * SD卡设置
 */
public class SDCardAty extends BaseP2PAty {

    @BindView(R.id.tv_tips)
    TextView tvTips; //

    @BindView(R.id.tv_use)
    TextView tvUsed; //

    @BindView(R.id.tv_rest)
    TextView tvRest; //

    @BindView(R.id.btn_format)
    Button btnFormat; //

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ly_all)
    View lyAll;


    private DevSdCard sdCardInfo;
    private Handler mHandler;
    private boolean isFormating = false;
    private int mProgress = 0;

    private Thread refreshThread = null;
    private volatile boolean refreshRunFlag = true;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_sd_card;
    }


    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
    }

    @Override
    protected void initViewsAndEvents() {

//        btnFormat.getBackground().setLevel(2);
        mHandler = new Handler(Looper.getMainLooper());

        getDevSdCard();
        showLoadDialog(new EdwinTimeoutCallback(5000) {

            @Override
            public void onTimeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        BaseApp.showToast(R.string.time_out);

                        if (!isFinishing()) {
                            btnFormat.setEnabled(false);
                            hideLoadDialog();
                            sleep(100);
                            finish();
                        }

                    }
                });
            }
        }, null);
        executeRefresh();
        btnFormat.setOnClickListener(this);
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRefresh();
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {

            case R.id.btn_format:

                new MaterialDialog.Builder(getActivity())
                        .content(R.string.tips_format_sd_card)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                formatSdCard();
                            }
                        }).show();
                break;
        }
    }


    /**
     * 获取设备SD卡状态
     */
    private void getDevSdCard() {
        ApiMgrV2.getSdCard(mDevice.getDevId());
    }

    /**
     * 格式化SD卡
     */
    private void formatSdCard() {
        stopRefresh();
        ApiMgrV2.formatSdCard(mDevice.getDevId());
        isFormating = true;
        tvTips.setText(R.string.formating);
        btnFormat.setEnabled(false);
        showLoadDialog(R.string.formating, new EdwinTimeoutCallback(100 * 1000) {

            @Override
            public void onTimeOut() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseApp.showToast(R.string.time_out);
                        if (!isFinishing()) {
                            hideLoadDialog();
                            finish();
                        }

                    }
                });
            }
        }, null);
        getDevSdCard();
    }

    //2S刷新一次SD卡状态
    private void executeRefresh() {

        if (refreshThread == null) {
            refreshRunFlag = true;
            refreshThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (refreshRunFlag) {
                        try {
                            /** 多次sleep 以便快速中断 */
                            for (int i = 0; i < 20; i++) {
                                Thread.sleep(100);
                                if (!refreshRunFlag) {
                                    break;
                                }
                            }
                            if (!refreshRunFlag) {
                                break;
                            }
                            getDevSdCard();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            refreshThread.start();
        }
    }

    private void stopRefresh() {
        if (refreshThread == null) {
            return;
        }
        if (refreshThread.isAlive()) {
            try {
                refreshRunFlag = false;
                refreshThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        refreshThread = null;
    }


    /**
     * 转换文件大小
     */
    private String FormetFileSize(long mbZise) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0MB";
        if (mbZise == 0) {
            return wrongSize;
        }
        if (mbZise < 1024) {
            fileSizeString = df.format((double) mbZise) + "MB";
        } else if (mbZise < 1048576) {
            fileSizeString = df.format((double) mbZise / 1024) + "GB";
        } else if (mbZise < 1073741824) {
            fileSizeString = df.format((double) mbZise / 1048576) + "TB";
        }
        return fileSizeString;
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {
            @Override
            public void onGetSdCard(String did, int msgType, DevSdCard sdCard) {
                super.onGetSdCard(did, msgType, sdCard);
                sdCardInfo = sdCard;
                if (sdCardInfo == null) {
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            BaseApp.showToast(R.string.get_sd_failed);
                            hideLoadDialog();
                            sleep(200);
                            finish();
                        }
                    }, 1000);
                    return;
                }
                if (!sdCardInfo.isValidDev()) {
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            BaseApp.showToast(R.string.dev_no_support_sd);
                            hideLoadDialog();
                            sleep(200);
                            finish();
                        }
                    }, 1000);
                    return;
                }
                if (!isFormating) {
                    if (!sdCardInfo.isSdCardExist()) {
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.sd_no_exist);
                                hideLoadDialog();
                                sleep(200);
                                finish();
                            }
                        }, 1000);
                        return;
                    }
                    if (sdCardInfo.getStatus() == P2PConstants.SDStatus.SD_STATUS_FORMATING) {
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.formating);
                                hideLoadDialog();
                                sleep(200);
                                finish();
                            }
                        }, 1000);
                        return;
                    }

                    if (sdCardInfo.getStatus() == P2PConstants.SDStatus.SD_STATUS_NOTFORMAT
                            || sdCardInfo.getStatus() == P2PConstants.SDStatus.SD_STATUS_READONLY
                            ) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tvTips.setVisibility(View.VISIBLE);
                                if (sdCardInfo.getStatus() == P2PConstants.SDStatus.SD_STATUS_READONLY) {
                                    tvTips.setText(R.string.sdcard_read_only);
                                } else {
                                    tvTips.setText(R.string.sd_not_format);
                                }
                                hideLoadDialog();
//								tvUsed.setText(getString(R.string.sd_not_format));
//								tvRest.setText("");
                                tvUsed.setText(getString(R.string.storage_used, ""));
                                tvRest.setText(getString(R.string.storage_rest, ""));
                            }
                        });
                        return;
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            hideLoadDialog();
                            tvUsed.setText(getString(R.string.storage_used, sdCardInfo.getUsed() + "MB"));
                            tvRest.setText(getString(R.string.storage_rest, sdCardInfo.getFree() + "MB"));
                        }
                    });
                } else {
                    //正在格式化 需等待格式化结果
                    if (!sdCardInfo.isSdCardExist()) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.sd_no_exist_format);
                                hideLoadDialog();
                                sleep(200);
                                finish();
                            }
                        });
                        return;
                    }
                    if (!sdCardInfo.isFormatOk()) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (mProgress < sdCardInfo.getProgress()) {
                                    mProgress = sdCardInfo.getProgress();
                                } else if (mProgress < 100) {
                                    mProgress++;
                                }
                                setLoadingTips(getString(R.string.formating_with_progress, mProgress) + "%");
                            }
                        });
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                getDevSdCard();
                            }
                        }, 2000);
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    setLoadingTips(getString(R.string.format_success));
                                    sleep(200);
                                    hideLoadDialog();
                                    tvUsed.setText(getString(R.string.storage_used, FormetFileSize(sdCardInfo.getUsed())));
                                    tvRest.setText(getString(R.string.storage_rest, FormetFileSize(sdCardInfo.getFree())));
                                    BaseApp.showToast(R.string.format_success);
                                    finish();
                                }

                            }
                        });
                    }
                }


            }

            @Override
            public void onSdSetAck(String did, int msgType) {
                super.onSdSetAck(did, msgType);
            }
        };
    }

    //    @Override
//    public void onGetSetResult(String did, int msgType, String msgData) {
//        if(devId.equals(did) && !StringUtils.isEmpty(msgData) ){
//
//            switch (msgType) {
//                case ApiMgr.MsgType.TYPE_USER_IPCAM_GET_SDCARD_RESP:
//                    sdCardInfo = JSON.parseObject( msgData, DevSdCard.class );
//
//                    break;
//                case ApiMgr.MsgType.TYPE_USER_IPCAM_SET_SDCARD_RESP:
////				     hideLoadDialog();
//                    break;
//            }
//        }
//    }

}
