package com.rl.geye.ui.aty;

import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.edwintech.vdp.jni.Avapi;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.utils.DateUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.util.MyRenderV2;
import com.rl.p2plib.bean.EdwinVideo;
import com.rl.p2plib.constants.P2PConstants;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/3/24.
 */

public class PlayBackAty extends BaseP2PAty {

    private static final int H264_DATA = 1;//
    protected EdwinVideo mVideo;
    protected MyRenderV2 myRender = null; //视频渲染
    protected boolean bDisplayFinished = true; //当前视频帧是否显示完毕
    protected byte[] videoData = null; //视频帧数据
    protected int videoDataLen = 0;
    protected int nVideoWidth = 0;
    protected int nVideoHeight = 0;
    protected int mAudioStatus = 0x11; //扩音 和 麦克风 状态
    @BindView(R.id.sv_video)
    GLSurfaceView svVideo;
    @BindView(R.id.pb_video)
    CircularProgressBar pbVideo;
    @BindView(R.id.ly_progress)
    LinearLayout lyProgress;
    @BindView(R.id.ly_video)
    RelativeLayout lyVideo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.ic_play_pause)
    ImageView btnPlayPause;
    @BindView(R.id.tv_play_time)
    TextView tvPlayTime;
    @BindView(R.id.tv_video_time)
    TextView tvVideoTime;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    private MediaPlayer mPlayer;
    private Thread mStartStreamThread; //视频流启动任务
    private volatile boolean startStreamRunFlag = true; //视频流线程标记
    private volatile boolean isVideoStarted = false; //视频流是否已经开启
    /**
     * 开启视频线程
     */
    protected Runnable mStartStreamRunnable = new Runnable() {
        @Override
        public void run() {
            int sendCodec;
            int recvCodec;
            int rate;
            switch (mDevice.getType()) {
                case P2PConstants.DeviceType.BELL_LTK6112C:
                case P2PConstants.DeviceType.BELL_BI_DIRECTIONAL:
                case P2PConstants.DeviceType.BELL_UNIDIRECTIONAL:
                case P2PConstants.DeviceType.IPC:
                case P2PConstants.DeviceType.IPCC:
                case P2PConstants.DeviceType.IPFC:
                case P2PConstants.DeviceType.CAT_SING_EYE:
                case P2PConstants.DeviceType.CAT_DOUBLE_EYE:
//                        sendCodec = Constants.AudioCodec.OPUS;
//                        recvCodec = Constants.AudioCodec.PCM;
                    sendCodec = P2PConstants.AudioCodec.G711A;
                    recvCodec = P2PConstants.AudioCodec.G711A;
                    rate = 8000;
                    break;

                default:
                    sendCodec = P2PConstants.AudioCodec.OPUS;
                    recvCodec = P2PConstants.AudioCodec.OPUS;
                    rate = 8000;
                    break;
            }
            Log.e("replay",mVideo.getStart());

            if (BellVideoAty.getInstance() != null) {
                Avapi.ClosePPPPLivestream(mDevice.getDevId());
                BellVideoAty.getInstance().restartP2PLive();
            }
            Avapi.StartPPPPLivestream(mDevice.getDevId(), mVideo.getStart(), rate, 1, recvCodec, sendCodec, 0);
            updateAudioStatus();
            isVideoStarted = true;
            mStartStreamThread = null;
            startStreamRunFlag = false;
        }
    };
    private boolean isFirstShow = true;
    private int startTimeFromDev = 0;
    private int maxProgress = 0;
    private int curTimeFromDev = 0;
    protected Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case H264_DATA: // h264
                {
                    lyProgress.setVisibility(View.GONE);
                    svVideo.setVisibility(View.VISIBLE);
                    if (myRender == null)
                        return;
                    myRender.writeSample(videoData, nVideoWidth, nVideoHeight);
                    if (isFirstShow) {
                        isFirstShow = false;
                        btnPlayPause.setSelected(true);
                        btnPlayPause.setEnabled(true);
                        seekBar.setEnabled(true);
                        seekBar.setMax(maxProgress);
//                        XLog.i(TAG,"");
                    }
                    seekBar.setProgress(curTimeFromDev - startTimeFromDev);
                    tvPlayTime.setText(getPlayTime(curTimeFromDev - startTimeFromDev));
                    bDisplayFinished = true;

                }
                break;

            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.aty_play_back;
    }

    @Override
    protected View getVaryTargetView() {
        return null;
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mVideo = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DEV_SD_VIDEO);
        }
        return super.initPrepareData() && mVideo != null;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvName.setText(mVideo.getName());
        ivClose.setOnClickListener(this);
        btnPlayPause.setOnClickListener(this);
        btnPlayPause.setEnabled(false);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new PlayerSeekBarChangeListener());

        tvVideoTime.setText(getPlayTime((int) mVideo.getTotalTime()));
        initRender();
        executeStartStreamThread();
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onBackPressed();
                break;
            case R.id.ic_play_pause:
                ApiMgrV2.palybackPausePlay(mDevice.getDevId());
                btnPlayPause.setSelected(!btnPlayPause.isSelected());
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelStartStreamThread();
        //TODO
        if (mDevice != null) {
            ThreadPoolMgr.getCustomThreadPool2().submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            Avapi.ClosePPPPLivestream(mDevice.getDevId());
                        }
                    }
            );
        }
//        ApiMgr.stopPlayBack(mDevice.getDevId());
    }

    protected void initRender() {
        if (myRender == null) {
            myRender = new MyRenderV2(svVideo);
            svVideo.setRenderer(myRender);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
        }
    }

    /*********************************************************************************************
     * 											P2P回调
     * *******************************************************************************************/
    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {

            @Override
            public void onStatusChanged(String did, int type, int param) {
                super.onStatusChanged(did, type, param);
                if (isVideoStarted && isSameDevice(did)) {
                    if (param != P2PConstants.P2PStatus.ON_LINE) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.p2p_disconnect);
                            }
                        });
                        onBackPressed();
                        return;
                    }
                }
            }


            @Override
            public void onVideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time) {
                super.onVideoData(did, videoBuf, h264Data, len, width, height, time);
                Logger.i(" onVideoData :----------------> " + did);
                if (isFinishing()) {
                    return;
                }
                if (!isSameDevice(did)) {
                    return;
                }
                if (!bDisplayFinished)//|| isConfigurationChanging)
                {
                    return;
                }
                if (h264Data != 1) {
                    return;
                }

                bDisplayFinished = false;
                videoData = videoBuf;
                videoDataLen = len;
                nVideoWidth = width;
                nVideoHeight = height;
                curTimeFromDev = time;
                if (isFirstShow) {
                    startTimeFromDev = time;
                    maxProgress = mVideo.getTotalTime();
                }
                mHandler.sendEmptyMessage(H264_DATA);

            }
        };
    }

    protected void executeStartStreamThread() {
        cancelStartStreamThread();
        startStreamRunFlag = true;
        mStartStreamThread = new Thread(mStartStreamRunnable);
        mStartStreamThread.start();
    }

    protected synchronized void cancelStartStreamThread() {
        isVideoStarted = false;
        if (mStartStreamThread == null) {
            return;
        }
        if (mStartStreamThread.isAlive()) {
            try {
                startStreamRunFlag = false;
                mStartStreamThread.join(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        startStreamRunFlag = false;
        mStartStreamThread = null;
    }

    private String getPlayTime(int ms) {
        int hour = 0;
        int minute = 0;
        int sec = 0;
        if (ms > 0) {
            int tmpSec = ms / 1000;
            hour = tmpSec / (60 * 60);
            minute = (tmpSec % (60 * 60)) / 60;
            sec = (tmpSec % (60 * 60)) % 60;
        }
        return String.format("%02d:%02d", minute, sec);
    }

    /**
     * 麦克风、扩音 设置变动
     */
    protected void updateAudioStatus() {
        boolean isMicEnabled = false;
        boolean isSpeakerEnabled = true;

        int status = 0;
        status += (isMicEnabled ? 2 : 0);
        status += (isSpeakerEnabled ? 1 : 0);
        mAudioStatus = status;
//        XLog.i(TAG, "updateAudioStatus------------->Audio Status: " + mAudioStatus);
        ThreadPoolMgr.getCustomThreadPool2().submit(
                new Runnable() {
                    @Override
                    public void run() {
                        Avapi.SetAudioStatus(mDevice.getDevId(), mAudioStatus);
                    }
                }
        );
    }

    class PlayerSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            ApiMgrV2.setPlaybackProgress(mDevice.getDevId(), DateUtil.getCommTimeStr2(mVideo.getStartTimeMills() + progress));
            tvPlayTime.setText(getPlayTime(progress));
        }
    }


}
