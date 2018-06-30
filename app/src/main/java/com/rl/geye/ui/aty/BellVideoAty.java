package com.rl.geye.ui.aty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.edwintech.vdp.jni.Avapi;
import com.lzy.okgo.OkGo;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.nicky.framework.widget.AspectRatio;
import com.nicky.framework.widget.XRelativeLayout;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.DateUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.db.bean.DeviceBg;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.image.ImageUtil;
import com.rl.geye.net.NetUrl;
import com.rl.geye.net.callback.MyStringCallback;
import com.rl.geye.ui.widget.CtrlLayout;
import com.rl.geye.util.ByteUtil;
import com.rl.geye.util.CallAlarmUtil;
import com.rl.geye.util.MyRenderV2;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.p2plib.bean.Battery;
import com.rl.p2plib.bean.CallAnswer;
import com.rl.p2plib.bean.SysInfo;
import com.rl.p2plib.bean.SysVersion;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.JSONUtil;
import net.sqlcipher.database.SQLiteConstraintException;
import okhttp3.Call;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicky on 2017/8/11.
 * 门铃 接听与通话
 */

public class BellVideoAty extends BaseP2PAty implements CtrlLayout.AnimationListener {

    private final static int MAX_WAITING = 20;// 20秒 等待超时

    private static final int REQUEST_CODE_FOR_AUDIO_RECORD = 4561;

    private static final int REQUEST_CODE_FOR_SET = 1133;
    private static final int REQUEST_CODE_FOR_PLAY_BACK = 1144;
    /**
     * handler 消息类型
     */
    private static final int H264_DATA = 1;//
    private static final int VIDEO_LOST = 3311;//
    private static final int NET_SPEED = 2232;//
    private static final int BATTERY_UPDATE = 2233;//
    private static final int CONNECT_TIME_UPDATE = 1234;//
    private static final int RECORD_TIME_UPDATE = 4321;//
    private static BellVideoAty instance;
    GLSurfaceView svVideo;
    CircularProgressBar pbVideo;
    LinearLayout lyProgress;
    ImageView icStatusRecord;
    TextView tvTimeConnect;
    TextView tvTimeRecord;
    TextView tvName;
    TextView tvTips;
    //    TextView tvStream;
    ImageView ivMic;
    ImageView ivSpeaker;
    ImageView ivVideo;
    ImageView ivPhoto;
    ImageView ivZoomOut;
    ImageView ivZoomIn;
    RelativeLayout lyVideoCtrl;
    CtrlLayout lyVideoCtrlLand;
    View lyUnlock;
    View lyAnswer;
    ImageView ivUnlock;
    ImageView ivAnswer;
    ImageView ivHangup;
    CtrlLayout lyOtherCtrl;
    XRelativeLayout lyTop;
    View lyAll;
    View lyBG;
    Toolbar toolbar;
    TextView tvTitle;
    ImageView ivBattery;
    TextView tvNnetSpeed;
    ImageView ivSet;
    ImageView iv_red_point;
    ImageView ivFile;
    private boolean restartP2PLive = false;//重启视频流(用于回放)
    private boolean isP2PLivePaused = false;//视频流暂停
    private GestureDetector mGestureDetector; //手势监听
    /**
     * @see P2PConstants.PushType
     */
    private int pushType; // 1表示呼叫 2代表移动侦测 7代表防拆报警
    private Long recordId;


    /**
     * 传递来的参数
     */
    private boolean isMonitor;//是否是监视
    private boolean autoAnswer;//是否
    private MyRenderV2 myRender = null; //视频渲染
    private Thread videoMonitorThread; //视频检测线程
    private volatile long recvDataTime = 0; // 上次接收到视频数据时间
    //    private volatile boolean videoDataOk = true; // 视频数据OK
    private volatile boolean threadVideoRunFlag = true;
    /**
     * 开启视频流
     */
    private Thread p2pStreamThread; //视频流线程
    private volatile boolean startStreamRunFlag = true; //视频流线程标记
    private volatile boolean isVideoStarted = false; //视频流是否已经开启
    private SysVersion mSysVersion;
    private boolean needRestart = false;
    private int restartTimes = 0;
    private SysInfo mSysInfo;
    private Battery mBattery;
    /**
     * 麦克风、扩音器
     */
    private int mAudioStatus = 0x11; //扩音 和 麦克风 状态
    private boolean isSpeakerEnabled = false;
    //    private boolean needGetBattery = false;//是否需要获取电池电量
    private boolean isMicEnabled = false;
    private boolean origSpeakerEnabled = false; //备份暂停前状态
    private boolean origMicEnabled = false;//备份暂停前状态
    /**
     * 拍照、录像
     */
    private SoundPool mPhotoSoundPool;
    private boolean isTakeVideo = false; //是否正在录像
    //    private boolean needSetAudio = false;//是否需要设置音频
    private boolean needVideoThumb = false; //
    private PhotoVideo mVideo;//当前录像
    private Bitmap mBitmap; //抓拍的图片
    private boolean needPhoto = false; //是否需要抓拍当前帧图像
    private boolean isSavePhoto = false; //是否正在保存照片
    private boolean isNeedSavedPhone = false; //是否需要保存图片
    private AsyncTask<View, Void, Void> startRecordTask;//开始录像任务
    private AsyncTask<View, Void, Void> stopRecordTask;//停止录像任务
    /**
     * 视频流数据
     */
    private boolean isConfigurationChanging = false;
    private boolean isFirstShow = true;
    private boolean bDisplayFinished = true; //当前视频帧是否显示完毕
    private byte[] videoData = null; //视频帧数据
    private int videoDataLen = 0;
    private int nVideoWidth = 0;
    private int nVideoHeight = 0;
    private boolean isAnswered = false;//是否已接听
    //    private boolean needAnswer = false;//是否需要接听
    private boolean isHangUped = false;//是否已挂断
    private boolean answerOK = false;//是否成功接听
    private boolean isFirstPortrait = true;
    private long mLastDataLen = 0;
    private long mDataLen = 0;
    /**
     * 时间线程
     */
    private Thread timeConnectThread; //通话计时线程
    private Thread timeRecordThread; //录像计时线程
    private volatile boolean threadConnectRunFlag = true;
    private volatile boolean threadRecordRunFlag = true;
    private boolean threadPauseFlag = false; //线程暂停
    private long timeConnectSec = 0;
    private long timeRecordSec = 0;
    private boolean isRecordTimeShow = false; //录像时间显示
    private boolean ppppClosed = false;//pppp连接是否已关闭
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
                case NET_SPEED:
                    if (msg.obj != null) {
                        String speed = (String) msg.obj;
                        tvNnetSpeed.setText(speed);
                    }
                    break;
                case BATTERY_UPDATE:
                    if (mBattery != null) {
                        int level = (mBattery.getBattery() - 1) / 20 + 1;
                        if (ivBattery != null)
                            ivBattery.setImageLevel(level);
                    }
                    break;
                case H264_DATA: // h264
                {
//                  pbVideo.progressiveStop();
                    lyProgress.setVisibility(View.GONE);
                    svVideo.setVisibility(View.VISIBLE);
                    lyBG.setVisibility(View.GONE);

                    if (isFirstShow) {
                        if (isMonitor)
                            startConnectTimeThread();
                        saveBG();
                        isFirstShow = false;
                    }
                    if (myRender == null || isConfigurationChanging) {
                        bDisplayFinished = true;
                        return;
                    }
                    myRender.writeSample(videoData, nVideoWidth, nVideoHeight);
                    bDisplayFinished = true;

                    if (needPhoto && mBitmap != null && isNeedSavedPhone) {
                        savePhoto();
                    }
                    if (isTakeVideo && needVideoThumb && mBitmap != null && mVideo != null) {
                        needVideoThumb = false;
                        saveVideo();
                    }

                }
                break;
                case VIDEO_LOST:
                    if (lyProgress != null)
                        lyProgress.setVisibility(View.VISIBLE);
                    break;
                case CONNECT_TIME_UPDATE:
                    if (!threadPauseFlag) {
                        tvTimeConnect.setText(getFormatTime(timeConnectSec));
                    }
                    if (!isMonitor && !isAnswered && timeConnectSec >= MAX_WAITING) {
                        hangUp();
                    }
                    break;

                case RECORD_TIME_UPDATE:
                    if (!threadPauseFlag) {
                        tvTimeRecord.setText(getFormatTime(timeRecordSec));
                    }
                    break;
                case R.id.msg_update_version:
                    checkVersion();
                    break;
            }
        }
    };

    public static BellVideoAty getInstance() {
        return instance;
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time 时间 秒
     * @return 时：分：秒
     */
    private static String getFormatTime(long time) {
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public void restartP2PLive() {
        restartP2PLive = true;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            isMonitor = fromIntent.getBooleanExtra(Constants.BundleKey.KEY_IS_MONITOR, false);
            autoAnswer = fromIntent.getBooleanExtra(Constants.BundleKey.KEY_AUTO_ANSWER, false);
            pushType = fromIntent.getIntExtra(Constants.BundleKey.KEY_PUSH_TYPE, P2PConstants.PushType.CALL);
            recordId = fromIntent.getLongExtra(Constants.BundleKey.KEY_RECORD_ID, -1);
        }
        return super.initPrepareData();
    }

    @Override
    protected void onP2PStatusChanged() {
        getDevSys();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.aty_video_bell;
    }

    @Override
    protected void initToolBar() {
        if (toolbar != null) {
            toolbar.setNavigationIcon(null);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    private BroadcastReceiver homeAndLockReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                }
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                //屏幕亮了
                Log.e("lock-", "--on");
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //屏幕黑了
                Log.e("lock-", "--off");
                hangUp();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(homeAndLockReceiver,filter);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        if (!isFinishing()) {
            if (lyOtherCtrl != null)
                lyOtherCtrl.autoHideStop();
            if (lyVideoCtrlLand != null)
                lyVideoCtrlLand.autoHideStop();
            super.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (lyOtherCtrl != null)
            lyOtherCtrl.autoHideStop();
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.autoHideStop();
        super.onBackPressed();
    }

    @Override
    protected void initViewsAndEvents() {
        SystemValue.isCallRunning = true;
        ppppClosed = false;
        isHangUped = false;

        mGestureDetector = new GestureDetector(this, new MySimpleGesture());
        findViews();
        setListeners();

        //TODO----
//        if( !mDevice.isUserPwdOK() ){
//            BaseApp.showToastLong(R.string.pppp_status_err_user_pwd);
////
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(!isFinishing())
//                        finish();
//                }
//            },3000);
//            return;
//        }


        if (isMonitor) {


        } else {
            startConnectTimeThread();
            int pushType = fromIntent.getIntExtra(Constants.BundleKey.KEY_PUSH_TYPE,0);
            CallAlarmUtil.getInstance().startRingAndVibrator(pushType);
            SystemValue.isCallRunning = true;
            if (autoAnswer)
                answer();
        }

        checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_FOR_AUDIO_RECORD, new PermissionResultCallback() {
            @Override
            public void onPermissionGranted() {

                if (myRender == null) {
                    myRender = new MyRenderV2(svVideo);
                    svVideo.setRenderer(myRender);
                }

                if (mDevice.getPwd().startsWith("admin1") || mDevice.getPwd().startsWith("admin")) {
                    Toast toast = Toast.makeText(instance, R.string.tips_err_default_pwd, Toast.LENGTH_LONG);
                    LinearLayout layout = (LinearLayout) toast.getView();
                    layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.BLACK);
                    v.setTextSize(15);
                    toast.show();
                    Log.e(TAG, "当前使用默认密码登陆");
                }

                /** 开启视频流  */
                startStreamLive();
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });


    }

    private boolean isCtrlMenu(View v) {
        return v.getId() == R.id.iv_mic || v.getId() == R.id.iv_speaker
                || v.getId() == R.id.iv_video || v.getId() == R.id.iv_photo;
    }

    @Override
    protected void onClickView(View v) {

        if (lyOtherCtrl != null && lyVideoCtrlLand != null && (lyOtherCtrl.isAnimationRunning() || lyVideoCtrlLand.isAnimationRunning())) {
            return;
        }
        if (isCtrlMenu(v) && lyOtherCtrl != null && lyVideoCtrlLand != null) {
            lyOtherCtrl.autoHideRestart();
            lyVideoCtrlLand.autoHideRestart();
        }

        switch (v.getId()) {
            case R.id.iv_mic:
                if (isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    isMicEnabled = !isMicEnabled;
                    ivMic.setSelected(isMicEnabled);
                    updateAudioStatus();
                }

                break;
            case R.id.iv_speaker:
                if (isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    isSpeakerEnabled = !isSpeakerEnabled;
                    ivSpeaker.setSelected(isSpeakerEnabled);
                    updateAudioStatus();
                }
                break;
            case R.id.iv_video:
                takeVideo();
                break;
            case R.id.iv_photo:
                takePhoto();
                break;
            case R.id.iv_zoom_out:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.iv_zoom_in:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            case R.id.iv_unlock:
                break;
            case R.id.iv_answer:
                answer();
                break;
            case R.id.iv_hangup:
                hangUp();
                break;
            case R.id.iv_file:
                gotoSetOrPlayBack(false);
                break;
            case R.id.iv_set:
                gotoSetOrPlayBack(true);
                break;
        }
    }

    private void gotoSetOrPlayBack(boolean isSet) {
        isP2PLivePaused = true;
        stopVideoMonitorThread();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
        bundle.putBoolean(Constants.BundleKey.KEY_DEV_ONLINE, isOnline);

        origMicEnabled = isMicEnabled;
        origSpeakerEnabled = isSpeakerEnabled;
        isMicEnabled = false;
        isSpeakerEnabled = false;
        ivMic.setSelected(isMicEnabled);
        ivSpeaker.setSelected(isSpeakerEnabled);
        updateAudioStatus();
        pauseOrResumeVideo(true);
        if (isSet) {
            gotoActivityForResult(SetDevAty.class, REQUEST_CODE_FOR_SET, bundle);
        } else {
            gotoActivityForResult(ChooseDateAty.class, REQUEST_CODE_FOR_PLAY_BACK, bundle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //REQUEST_CODE_FOR_SET
        if (requestCode == REQUEST_CODE_FOR_SET || requestCode == REQUEST_CODE_FOR_PLAY_BACK) {
            isP2PLivePaused = false;
            isMicEnabled = origMicEnabled;
            isSpeakerEnabled = origSpeakerEnabled;
            ivMic.setSelected(isMicEnabled);
            ivSpeaker.setSelected(isSpeakerEnabled);

            if (restartP2PLive) {
                restartP2PLive = false;
                if (lyProgress != null)
                    lyProgress.setVisibility(View.VISIBLE);
                startStreamLive();
            } else {
                updateAudioStatus();
                pauseOrResumeVideo(false);
                startVideoMonitorThread();
            }

        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isConfigurationChanging = true;
        isFirstPortrait = false;
        super.onConfigurationChanged(newConfig);

        setContentView(R.layout.aty_video_bell);
        //注意，这里删除了init()，否则又初始化了，状态就丢失
        findViews();
        setListeners();
        ApiMgrV2.getBattery(mDevice.getDevId(), null, -1);
        myRender = new MyRenderV2(svVideo);
        svVideo.setRenderer(myRender);
        ivMic.setSelected(isMicEnabled);
        ivSpeaker.setSelected(isSpeakerEnabled);

        ivVideo.setSelected(isTakeVideo);
        if (isTakeVideo) {
            tvTimeConnect.setVisibility(View.GONE);
            tvTimeRecord.setVisibility(View.VISIBLE);
        } else {
            tvTimeConnect.setVisibility(View.VISIBLE);
            tvTimeRecord.setVisibility(View.GONE);
        }
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //退出为全屏模式
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            int sysUiVisible = getWindow().getDecorView().getSystemUiVisibility()
                    & ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION & ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE | sysUiVisible);
            tvTimeConnect.setTextColor(ContextCompat.getColor(this, R.color.text_black));
        } else {
            // 设置为全屏模式
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            int sysUiVisible = getWindow().getDecorView().getSystemUiVisibility();
            getWindow().getDecorView().setSystemUiVisibility(sysUiVisible | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            tvTimeConnect.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        isConfigurationChanging = false;
    }

    @Override
    public void onDestroy() {
        Logger.t(TAG).i("---------onDestroy------");
        mHandler.removeMessages(H264_DATA);
        mHandler.removeMessages(VIDEO_LOST);
        CallAlarmUtil.getInstance().stopRingAndVibrator();
        stopConnectTimeThread();
        stopVideoMonitorThread();
        super.onDestroy();
        hangUp();
        SystemValue.isCallRunning = false;
        instance = null;
        unregisterReceiver(homeAndLockReceiver);
    }

    private void findViews() {
        svVideo = (GLSurfaceView) findViewById(R.id.sv_video);
        pbVideo = (CircularProgressBar) findViewById(R.id.pb_video);
        lyProgress = (LinearLayout) findViewById(R.id.ly_progress);
        icStatusRecord = (ImageView) findViewById(R.id.ic_status_record);
        tvTimeConnect = (TextView) findViewById(R.id.tv_time_connect);
        tvTimeRecord = (TextView) findViewById(R.id.tv_time_record);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTips = (TextView) findViewById(R.id.tv_tips);
//        tvStream = (TextView) findViewById(R.id.tv_stream);

        ivMic = (ImageView) findViewById(R.id.iv_mic);
        ivSpeaker = (ImageView) findViewById(R.id.iv_speaker);
        ivVideo = (ImageView) findViewById(R.id.iv_video);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ivZoomOut = (ImageView) findViewById(R.id.iv_zoom_out);
        ivZoomIn = (ImageView) findViewById(R.id.iv_zoom_in);
        lyVideoCtrl = (RelativeLayout) findViewById(R.id.ly_video_ctrl);
        lyVideoCtrlLand = (CtrlLayout) findViewById(R.id.ly_video_ctrl_land);

        ivUnlock = (ImageView) findViewById(R.id.iv_unlock);
        ivAnswer = (ImageView) findViewById(R.id.iv_answer);
        ivHangup = (ImageView) findViewById(R.id.iv_hangup);
        lyOtherCtrl = (CtrlLayout) findViewById(R.id.ly_other_ctrl);
        lyUnlock = findViewById(R.id.ly_unlock);
        lyAnswer = findViewById(R.id.ly_answer);

        lyTop = (XRelativeLayout) findViewById(R.id.ly_top);
        lyAll = findViewById(R.id.ly_all);
        lyBG = findViewById(R.id.ly_bg);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvNnetSpeed = (TextView) findViewById(R.id.tv_net_speed);
        ivSet = (ImageView) findViewById(R.id.iv_set);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
        ivFile = (ImageView) findViewById(R.id.iv_file);
        tvTitle = (TextView) findViewById(R.id.tv_title);

        if (lyTop != null)
            lyTop.setRatio(AspectRatio.makeAspectRatio(3.0f / 4.0f, true));

        if (tvTitle != null) {
            tvTitle.setText(mDevice.getName());
        }
        if (tvName != null) {
            tvName.setText(mDevice.getName());
        }
        if (tvTips != null) {

            if (isMonitor) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_video_green));
                tvTips.setText(R.string.tips_monitoring);
            } else if (pushType == P2PConstants.PushType.CALL) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_video_green));
                if (isAnswered) {
                    tvTips.setText(R.string.tips_talking);
                } else {
//                    tvTips.stopLoading();
                    tvTips.setText(R.string.tips_calling);
//                    tvTips.startLoading();
                }
            } else if (pushType == P2PConstants.PushType.DETECTION) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_red));
                tvTips.setText(R.string.tips_alarm_detect);
            } else if (pushType == P2PConstants.PushType.ALARM_DISMANTLE) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_red));
                tvTips.setText(R.string.tips_alarm_dismantle);
            }

        }

        onModeChanged();
    }

    private void setListeners() {
//        setOnClickListener(tvStream,this);
        setOnClickListener(ivMic, this);
        setOnClickListener(ivSpeaker, this);

        setOnClickListener(ivZoomOut, this);
        setOnClickListener(ivZoomIn, this);
        setOnClickListener(ivUnlock, this);
        setOnClickListener(ivAnswer, this);
        setOnClickListener(ivHangup, this);
        setOnClickListener(ivSet, this);
        setOnClickListener(ivFile, this);
        if (mDevice.isBell() && isFirstPortrait) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setOnClickListener(ivVideo, BellVideoAty.this);
                    setOnClickListener(ivPhoto, BellVideoAty.this);
                }
            }, 3000);
        } else {
            setOnClickListener(ivVideo, this);
            setOnClickListener(ivPhoto, this);
        }
        svVideo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        svVideo.setFocusable(true);
        svVideo.setClickable(true);
        svVideo.setLongClickable(true);

        if (lyBG != null) {
            lyBG.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
            lyBG.setFocusable(true);
            lyBG.setClickable(true);
            lyBG.setLongClickable(true);
        }

        if (lyOtherCtrl != null)
            lyOtherCtrl.setAnimationListener(this);
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.setAnimationListener(this);
        if ((isAnswered || isMonitor) && lyOtherCtrl != null && lyVideoCtrlLand != null) {
            lyOtherCtrl.autoHideStart();
            lyVideoCtrlLand.autoHideStart();
        }


    }

    /**********************************************************************************************
     *****************************************接听、挂断、拍照、录像*********************************
     /**********************************************************************************************/

    private void onModeChanged() {
        if (isMonitor) {
            setVisibility(lyAnswer, View.GONE);
            setVisibility(lyUnlock, View.GONE);
            setVisibility(ivUnlock, View.GONE);

            setVisibility(ivAnswer, View.GONE);
            setVisibility(ivBattery, View.VISIBLE);
            setVisibility(ivSet, View.VISIBLE);
            setVisibility(ivFile, View.VISIBLE);
        } else {
            if (isAnswered) {
                setVisibility(lyAnswer, View.GONE);
                setVisibility(lyUnlock, View.GONE);
                setVisibility(ivUnlock, View.GONE);

                setVisibility(ivAnswer, View.GONE);
                setVisibility(lyVideoCtrl, View.VISIBLE);
                setVisibility(lyVideoCtrlLand, View.VISIBLE);
                setVisibility(ivBattery, View.VISIBLE);
                setVisibility(ivSet, View.VISIBLE);
                setVisibility(ivFile, View.VISIBLE);
            } else {
                setVisibility(lyAnswer, View.VISIBLE);
                setVisibility(lyUnlock, View.GONE);
                setVisibility(ivUnlock, View.GONE);
                setVisibility(ivAnswer, View.VISIBLE);
                setVisibility(lyVideoCtrl, View.GONE);
                setVisibility(lyVideoCtrlLand, View.GONE);
                setVisibility(ivBattery, View.GONE);
                setVisibility(ivSet, View.GONE);
                setVisibility(ivFile, View.GONE);
            }
        }
    }

    /**
     * 接听
     */
    private void answer() {
        isAnswered = true;
        EdwinRecord record = MyApp.getDaoSession().getEdwinRecordDao().load(recordId);
        if (record != null) {
            record.setAnswered(true);
            MyApp.getDaoSession().getEdwinRecordDao().update(record);
            postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_UPDATE);
        }
        CallAlarmUtil.getInstance().stopRingAndVibrator();
        onModeChanged();
        //TODO---Nicky
        if (tvTips != null && !isMonitor && pushType == P2PConstants.PushType.CALL) {
//            tvTips.stopLoading();
            tvTips.setText(R.string.tips_talking);
        }
        if (pushType == P2PConstants.PushType.CALL) {
            isMicEnabled = true;
            isSpeakerEnabled = true;
//            needSetAudio = true;
        } else {
            isMicEnabled = false;
            isSpeakerEnabled = false;
        }
        ivMic.setSelected(isMicEnabled);
        ivSpeaker.setSelected(isSpeakerEnabled);

        if (isVideoStarted && !answerOK && (isAnswered || isMonitor)) {
            ApiMgrV2.hangUpOrAnswer(mDevice.getDevId(), false, new ApiMgrV2.SendCmdCallBack() {
                @Override
                public void onSuccess() {
                    answerOK = true;
                }

                @Override
                public void onFailed() {

                }
            }, -1);
        }
        if (isVideoStarted) {
            updateAudioStatus();
        }


    }

    /**
     * 挂断
     */
    private void hangUp() {
        if (isHangUped)
            return;
        if (lyOtherCtrl != null)
            lyOtherCtrl.autoHideStop();
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.autoHideStop();
        CallAlarmUtil.getInstance().stopRingAndVibrator();
        if (mPhotoSoundPool != null) {
            mPhotoSoundPool.release();
            mPhotoSoundPool = null;
        }
        startStreamRunFlag = false;
        try {
            if (p2pStreamThread != null)
                p2pStreamThread.join(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mDevice != null) { //&& !ppppClosed
            stopRecord(); //TODO
            ThreadPoolMgr.getCustomThreadPool2().submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            Avapi.ClosePPPPLivestream(mDevice.getDevId());
                        }
                    }
            );
        }
        //TODO---Nicky
        if (isAnswered || isMonitor) {
            ApiMgrV2.hangUpOrAnswer(mDevice.getDevId(), true, null, 0);
        }

//        if( BridgeService.isReady() ){
//            if( BridgeService.getInstance().getDeviceStatus(mDevice.getDevId()) == P2PConstants.P2PStatus.ON_LINE){
//            }
//        }

        isHangUped = true;
        finish();
    }

    /**
     * 解锁
     */
    private void unlock() {


    }

    /**
     * 拍照
     */
    protected void takePhoto() {
        if (isVideoStarted && lyProgress.getVisibility() == View.GONE) {
            if (existSdcard()) {
                needPhoto = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mPhotoSoundPool == null) {

                                //当前系统的SDK版本大于等于21(Android 5.0)时
                                if (Build.VERSION.SDK_INT >= 21) {
                                    SoundPool.Builder builder = new SoundPool.Builder();
                                    //传入音频数量
                                    builder.setMaxStreams(1);
                                    //AudioAttributes是一个封装音频各种属性的方法
                                    AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                                    //设置音频流的合适的属性
                                    attrBuilder.setLegacyStreamType(AudioManager.STREAM_RING);
                                    //加载一个AudioAttributes
                                    builder.setAudioAttributes(attrBuilder.build());
                                    mPhotoSoundPool = builder.build();
                                }
                                //当系统的SDK版本小于21时
                                else {
                                    //设置最多可容纳1个音频流，音频的品质为50
                                    mPhotoSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 50);
                                }
                            }
                            final int sourceId = mPhotoSoundPool.load("/system/media/audio/ui/camera_click.ogg", 0);
                            try {
                                mPhotoSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                        if (mPhotoSoundPool != null)
                                            mPhotoSoundPool.play(sourceId, 2, 2, 0, 0, 1);
                                    }
                                });
                            } catch (Exception ex) {
                                Logger.t(TAG).e("play photo click failed:" + ex);
                            }
                        } catch (Exception e) {
                            Logger.t(TAG).e("cannot play photo click: " + e);
                        }
                    }
                }).start();

            } else {
                BaseApp.showToast(R.string.sd_not_exist_photo);
            }
        }
    }

    /**
     * 保存图片
     */
    private synchronized void savePhoto() {
        isNeedSavedPhone = false;
        needPhoto = false;
        if (isSavePhoto == false) {
            isSavePhoto = true;
            new Thread() {
                public void run() {
                    FileOutputStream fos = null;
                    try {
                        String filePath = PhotoVideoUtil.getPhotoDirPath(false) + mDevice.getDevId();
                        File picDir = new File(filePath);
                        if (!picDir.exists()) {
                            picDir.mkdirs();
                        }
                        String fileName = DateUtil.getFormatStr(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"),
                                System.currentTimeMillis()) + ".jpg";
                        File file = new File(picDir, fileName);
                        fos = new FileOutputStream(file);
                        if (mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                            fos.flush();
                            PhotoVideo photoData = new PhotoVideo();
                            photoData.setDid(mDevice.getId());
                            photoData.setType(P2PConstants.PhotoVideoType.PICTURE);
                            photoData.setName(fileName);
                            photoData.setPath(file.getAbsolutePath());
                            photoData.setTriggerTime(System.currentTimeMillis() / 1000);
                            photoData.setDate(DateUtil.getDateStr(System.currentTimeMillis()));
                            long res;
                            try {
                                res = MyApp.getDaoSession().getPhotoVideoDao().insert(photoData);
                            } catch (SQLiteConstraintException sqlE) {
                                sqlE.printStackTrace();
                                res = -1;
                            }
                            postEdwinEvent(Constants.EdwinEventType.EVENT_TAKE_PHOTO);
                            ImageUtil.scanFile(getActivity(), file.getAbsolutePath());
//                            MediaScannerConnection.scanFile(getActivity(), new String[]{
//                                            file.getAbsolutePath() }, null
//                                    , new MediaScannerConnection.OnScanCompletedListener() {
//                                        @Override
//                                        public void onScanCompleted(String path, Uri uri) {
//                                            Log.i("ExternalStorage", "Scanned " + path + ":");
//                                            Log.i("ExternalStorage", "-> uri=" + uri);
//
//                                        }
//                                    }
//                            );

                        }
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.photo_failed);
                            }
                        });
                        e.printStackTrace();
                    } finally {
                        isSavePhoto = false;
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fos = null;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BaseApp.showToast(R.string.photo_success);
                            }
                        });
                    }
                    // if (bmp != null) {
                    // bmp.recycle();
                    // }
                }
            }.start();
        }
    }

    /**
     * 保存视频
     */
    private void saveVideo() {
        if (mVideo == null)
            return;
        new Thread() {
            public void run() {
                FileOutputStream fos = null;
                try {

                    String filePath = PhotoVideoUtil.getVideoDirPath() + mDevice.getDevId();
                    File picDir = new File(filePath);
                    if (!picDir.exists()) {
                        picDir.mkdirs();
                    }
                    String fileName = mVideo.getName() + ".jpg";
                    File file = new File(picDir, fileName);
                    fos = new FileOutputStream(file);
                    if (mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                        fos.flush();
                        if (mVideo != null) {
                            mVideo.setPathThumb(file.getAbsolutePath());
                        }
                        try {
                            MyApp.getDaoSession().getPhotoVideoDao().update(mVideo);
                        } catch (SQLiteConstraintException sqlE) {
                            sqlE.printStackTrace();
                        }
                        postEdwinEvent(Constants.EdwinEventType.EVENT_PHOTO_VIDEO_UPDATE, mDevice);
                        ImageUtil.scanFile(getActivity(), file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();


    }

    /**
     * 保存背景
     */
    private void saveBG() {
        isNeedSavedPhone = false;
        new Thread() {
            public void run() {
                FileOutputStream fos = null;
                try {
                    String filePath = PhotoVideoUtil.getPhotoDirPath(false) + mDevice.getDevId();
                    File picDir = new File(filePath);
                    if (!picDir.exists()) {
                        picDir.mkdirs();
                    }
                    String fileName = Constants.DEVICE_BG_NAME;
                    File file = new File(picDir, fileName);
                    fos = new FileOutputStream(file);
                    if (mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
                        fos.flush();
                        DeviceBg devBg = new DeviceBg();
                        devBg.setDid(mDevice.getId());
                        devBg.setName(fileName);
                        devBg.setPath(file.getAbsolutePath());
                        devBg.setTriggerTime(System.currentTimeMillis() / 1000);
                        long res;
                        try {
                            res = MyApp.getDaoSession().getDeviceBgDao().insertOrReplace(devBg);
                        } catch (SQLiteConstraintException sqlE) {
                            sqlE.printStackTrace();
                            res = -1;
                        }

//                        TbBgPhoto.getInstance().saveBg(photoData);
                        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_UPDATE_BG, mDevice);
                        ImageUtil.scanFile(getActivity(), file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 录像
     */
    private void takeVideo() {
        if (isVideoStarted && lyProgress.getVisibility() == View.GONE) {
            if (existSdcard()) {
                if (isTakeVideo) {
                    isTakeVideo = false;
                    needVideoThumb = false;
                    ivVideo.setSelected(isTakeVideo);
                    stopRecordTimeThread();
                    executeStopRecordTask(ivVideo);
                } else {
                    StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                    long blockSize = stat.getBlockSize();
                    long availableBlocks = stat.getAvailableBlocks();
                    if ((availableBlocks * blockSize) / (1024 * 1024) < 50) {
                        BaseApp.showToast(R.string.sd_size_not_enough);
                        return;
                    }
                    isTakeVideo = true;
                    ivVideo.setSelected(isTakeVideo);
                    startRecordTimeThread();
                    executeStartRecordTask(ivVideo);
                }
            }
        }
    }

    /**********************************************************************************************
     ***************************************** app内部事件处理***************************************
     /**********************************************************************************************/

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected boolean ignoreEventSelf() {
        return true;
    }

    @Override
    protected void onXEventRecv(EdwinEvent<?> event) {
        super.onXEventRecv(event);
        switch (event.getEventCode()) {
            case Constants.EdwinEventType.EVENT_GOTO_BACKGROUND:
                ppppClosed = true;
//                onBackPressed();
                if (!isFinishing())
                    finish();
                break;

            case Constants.EdwinEventType.EVENT_GOTO_FOREGROUND:
                break;

            case Constants.EdwinEventType.EVENT_DEV_REBOOT:
                ppppClosed = true;
                if (!isFinishing())
                    finish();
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_NAME:
                EdwinDevice nameDev = (EdwinDevice) event.getData();
                if (nameDev != null) {
                    if (nameDev.getDevId().equals(mDevice.getDevId())) {
                        mDevice.setName(nameDev.getName());
                        if (tvTitle != null) {
                            tvTitle.setText(mDevice.getName());
                        }
                        if (tvName != null) {
                            tvName.setText(mDevice.getName());
                        }
                    }
                }
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_BG:
                EdwinDevice bgDev = (EdwinDevice) event.getData();
                if (bgDev != null) {
                    if (bgDev.getDevId().equals(mDevice.getDevId())) {
                        mDevice.setBgPath(bgDev.getBgPath());
                    }
                }
                break;
            case Constants.EdwinEventType.EVENT_DEV_UPDATE_PWD:
                EdwinDevice pwdDev = (EdwinDevice) event.getData();
                if (pwdDev != null) {
                    if (pwdDev.getDevId().equals(mDevice.getDevId())) {
                        mDevice.setPwd(pwdDev.getPwd());
                    }
                }
                break;

            default:
                break;
        }
    }

    /*************** 通话计时线程 ************/
    private Thread getConnectTimeThread() {
        if (timeConnectThread == null) {
            return new Thread() {
                @Override
                public void run() {
                    while (threadConnectRunFlag) {
                        try {
                            /** 多次sleep 以便快速中断   */
                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(100);
                                if (!threadConnectRunFlag) {
                                    break;
                                }
                            }
                            timeConnectSec++;
                            if (!threadPauseFlag) {
                                mHandler.sendEmptyMessage(CONNECT_TIME_UPDATE);
                            }

                        } catch (Exception e) {
                        }
                    }
                }
            };
        }
        return timeConnectThread;
    }

    // 开启时间线程
    private void startConnectTimeThread() {
        threadConnectRunFlag = true;
        timeConnectThread = getConnectTimeThread();
        if (!timeConnectThread.isAlive()) {
            timeConnectThread.start();
        }
    }

    // 关闭时间线程
    private void stopConnectTimeThread() {
        if (timeConnectThread == null) {
            return;
        }
        if (timeConnectThread.isAlive()) {
            try {
                threadConnectRunFlag = false;
                timeConnectThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timeConnectThread = null;
        timeConnectSec = 0;

        mHandler.removeMessages(CONNECT_TIME_UPDATE);
    }

    /****************录像计时线程*******************/
    protected Thread getRecordTimeThread() {
        if (timeRecordThread == null) {
            return new Thread() {
                @Override
                public void run() {
                    while (threadRecordRunFlag) {
                        try {
                            /** 多次sleep 以便快速中断   */
                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(100);
                                if (!threadConnectRunFlag) {
                                    break;
                                }
                            }
                            timeRecordSec++;
                            if (!threadPauseFlag) {
                                mHandler.sendEmptyMessage(RECORD_TIME_UPDATE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        return timeRecordThread;
    }

    // 开启线程
    protected void startRecordTimeThread() {
        threadRecordRunFlag = true;
        timeRecordThread = getRecordTimeThread();
        if (!timeRecordThread.isAlive()) {
            timeRecordThread.start();
        }
        tvTimeConnect.setVisibility(View.GONE);
        tvTimeRecord.setVisibility(View.VISIBLE);
        icStatusRecord.setVisibility(View.VISIBLE);
        isRecordTimeShow = true;
    }

    // 关闭线程
    protected void stopRecordTimeThread() {
        if (timeRecordThread == null) {
            return;
        }
        if (timeRecordThread.isAlive()) {
            try {
                threadRecordRunFlag = false;
                timeRecordThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timeRecordThread = null;
        timeRecordSec = 0;
        mHandler.removeMessages(RECORD_TIME_UPDATE);
        if (tvTimeConnect != null)
            tvTimeConnect.setVisibility(View.VISIBLE);
        if (tvTimeRecord != null) {
            tvTimeRecord.setVisibility(View.GONE);
            tvTimeRecord.setText("00:00:00");
        }
        if (icStatusRecord != null)
            icStatusRecord.setVisibility(View.INVISIBLE);
        isRecordTimeShow = false;
    }

    /****************  录像线程 ****************/
    protected void stopRecord() {
        if (isTakeVideo) {
            isTakeVideo = false;
            cancelStartRecordTask();
            if (ivVideo != null)
                ivVideo.setSelected(false);
            stopRecordTimeThread();
            executeStopRecordTask(null);
        }
    }

    protected void cancelStartRecordTask() {
        if (startRecordTask != null) {
            startRecordTask.cancel(true);
            startRecordTask = null;
        }
    }

    protected void executeStartRecordTask(View view) {
        cancelStartRecordTask();

        startRecordTask = new AsyncTask<View, Void, Void>() {

            private View mView;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mView != null)
                    mView.setEnabled(false);
            }

            @Override
            protected Void doInBackground(View... params) {
                mView = params[0];
                String filePath = PhotoVideoUtil.getVideoDirPath() + mDevice.getDevId();
                String fileName = String.valueOf(System.currentTimeMillis()) + ".mp4";
                File videoDir = new File(filePath);
                if (!videoDir.exists()) {
                    videoDir.mkdirs();
                }
                Avapi.StartRecorder(mDevice.getDevId(), filePath + "/" + fileName);
                //TODO
                mVideo = new PhotoVideo();
                mVideo.setDid(mDevice.getId());
                mVideo.setType(P2PConstants.PhotoVideoType.VIDEO);
                mVideo.setName(fileName);
                mVideo.setPath(filePath + "/" + fileName);
                mVideo.setTriggerTime(System.currentTimeMillis() / 1000);
                mVideo.setDate(DateUtil.getDateStr(System.currentTimeMillis()));
                long res;
                try {
                    res = MyApp.getDaoSession().getPhotoVideoDao().insert(mVideo);
                } catch (SQLiteConstraintException sqlE) {
                    sqlE.printStackTrace();
                    res = -1;
                }
                needVideoThumb = true;
//                postEdwinEvent(Constants.EdwinEventType.EVENT_TAKE_VIDEO);
                sleep(1000);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (mView != null)
                    mView.setEnabled(true);
                super.onPostExecute(result);
            }
        };
        startRecordTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool(), view);
    }

    protected void cancelStopRecordTask() {
        if (stopRecordTask != null) {
            stopRecordTask.cancel(true);
            stopRecordTask = null;
        }
    }

    protected void executeStopRecordTask(View view) {
        cancelStopRecordTask();

        stopRecordTask = new AsyncTask<View, Void, Void>() {

            private View mView;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (mView != null)
                    mView.setEnabled(false);
            }

            @Override
            protected Void doInBackground(View... params) {
                mView = params[0];
                Avapi.CloseRecorder(mDevice.getDevId());
                sleep(100);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (mView != null)
                    mView.setEnabled(true);
                postEdwinEvent(Constants.EdwinEventType.EVENT_TAKE_VIDEO);
                super.onPostExecute(result);
            }
        };
        stopRecordTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool(), view);
    }

    /**********************************************************************************************
     *****************************************   视频流   ****************************************
     /**********************************************************************************************/

    private void startStreamLive() {
        startStreamRunFlag = true;
        needRestart = false;
//        restartTimes  = 0;
        isVideoStarted = false;

        p2pStreamThread = new Thread() {
            @Override
            public void run() {
                super.run();
                int recvCodec;
                int sendCodec;
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

                Avapi.Wake(mDevice.getDevId());

                int result = 0;
                result = Avapi.StartPPPP(mDevice.getDevId(),
                        mDevice.getUser(),
                        mDevice.getPwd(),
                        P2PConstants.P2P_SERVER,
                        P2PConstants.P2P_TYPE);
                Log.e("PPPP", "result code:" + result);
                if (result != 0) {
                    Log.e("PPPP", "start pppp connection failed.");
                    startStreamRunFlag = false;
                    finish();
                    return;
                }
                if(startStreamRunFlag == false){
                    finish();
                    return;
                }
                result = Avapi.StartPPPPLivestream(
                        mDevice.getDevId(), "",
                        rate,
                        1,
                        recvCodec,
                        sendCodec,
                        0
                );
                Log.e("PPPP", "result code:" + result);
                if (result != 0) {
                    Log.e("PPPP", "start pppp connection failed.");
                    startStreamRunFlag = false;
                    finish();
                    return;
                }
                isVideoStarted = true;
                updateAudioStatus();
                if (mBattery == null) {
                    ApiMgrV2.getBattery(mDevice.getDevId(), new ApiMgrV2.SendCmdCallBack() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailed() {
                        }
                    }, -1);
                }
                if (!answerOK && (isAnswered || isMonitor)) {
                    ApiMgrV2.hangUpOrAnswer(mDevice.getDevId(), false, new ApiMgrV2.SendCmdCallBack() {
                        @Override
                        public void onSuccess() {
                            answerOK = true;
                        }

                        @Override
                        public void onFailed() {
                        }
                    }, -1);
                }
                recvDataTime = SystemClock.elapsedRealtime();
                startVideoMonitorThread();
                p2pStreamThread = null;
                startStreamRunFlag = false;
            }
        };
        p2pStreamThread.start();
    }

    /***************视频检测线程(5秒内无数据显示正在获取)************/
    private Thread getVideoMonitorThread() {
        if (videoMonitorThread == null) {
            return new Thread() {
                @Override
                public void run() {
                    while (threadVideoRunFlag) {
                        try {
                            for (int i = 0; i < 10; i++) {
                                sleep(100);
                                if (!threadVideoRunFlag) {
                                    return;
                                }
                            }
                            if (SystemClock.elapsedRealtime() - recvDataTime > 5 * 1000) {
                                mHandler.sendEmptyMessage(VIDEO_LOST);
                                if (mDevice != null) {
                                    stopRecord();
//                                    if( isVideoStarted )
//                                    {
//                                        Avapi.ClosePPPPLivestream(mDevice.getDevId());
//                                    }
                                }
                                threadVideoRunFlag = false;
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            };
        }
        return videoMonitorThread;
    }

    // 开启线程
    private void startVideoMonitorThread() {
        threadVideoRunFlag = true;
        videoMonitorThread = getVideoMonitorThread();
        if (!videoMonitorThread.isAlive()) {
            try {
                videoMonitorThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 关闭线程
    private void stopVideoMonitorThread() {
        if (videoMonitorThread == null) {
            return;
        }
        if (videoMonitorThread.isAlive()) {
            try {
                threadVideoRunFlag = false;
                videoMonitorThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        videoMonitorThread = null;
    }

    /**
     * 检查版本
     */
    private void checkVersion() {

        Map<String, String> params = new HashMap<>();
        params.put("devid", mDevice.getDevId());
        params.put("cloudid", mDevice.getDevId());
        params.put("curver", mSysInfo.getVersion());
        params.put("model", mSysInfo.getModel());
        params.put("odm", String.valueOf(mSysInfo.getOdm()));
        params.put("lang", String.valueOf(mSysInfo.getLanguage()));

        OkGo.<String>get(NetUrl.checkVersion()).tag(this)
                .params(params)
                .execute(new MyStringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        if (!StringUtils.isEmpty(s)) {
                            mSysVersion = JSONUtil.fromJson(s, SysVersion.class);
                            if (mSysVersion != null && !StringUtils.isEmpty(mSysVersion.getUrl()) && mSysVersion.getVer().compareTo(mSysInfo.getVersion()) > 0) {
                                iv_red_point.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

    }

    /*********************************************************************************************
     * 											自动隐藏
     * *******************************************************************************************/

    /*********************************************************************************************
     * 											P2P回调
     * *******************************************************************************************/
    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {

            @Override
            public void onOtherUserAnswered(String did, int msgType, CallAnswer callAnswer) {
                //TODO----
                if (!isFinishing() && isSameDevice(did) && callAnswer != null && callAnswer.getType() == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseApp.showToast(R.string.other_user_answered);
                        }
                    });
                    hangUp();
                }
            }

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
                        if (!isFinishing())
                            hangUp();
                        return;
                    }
                }
//                if(needAnswer){
//                    if( param==P2PConstants.P2PStatus.ON_LINE &&  !isFinishing() )
//                    {
//                        needAnswer = false;
//                        ApiMgrV2.hangUpOrAnswer(mDevice.getDevId(), false, new ApiMgrV2.SendCmdCallBack() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailed() {
//                                needAnswer = true;
//                            }
//                        });
//                    }
//                }
//                if(needGetBattery){
//                    needGetBattery = false;
//                    ApiMgrV2.getBattery(mDevice.getDevId(), new ApiMgrV2.SendCmdCallBack() {
//                        @Override
//                        public void onSuccess() {
//
//                        }
//
//                        @Override
//                        public void onFailed() {
//                            needGetBattery = true;
//                        }
//                    });
//                }
//                if(needSetAudio){
//                    needSetAudio = false;
//                    updateAudioStatus();
//                }
            }

            @Override
            public void onGetBattery(String did, int msgType, Battery battery) {
                if (isSameDevice(did)) {
                    mBattery = battery;
                    mHandler.sendEmptyMessage(BATTERY_UPDATE);
                }
            }

            @Override
            public void onVideoData(String did, byte[] videoBuf, int h264Data, int len, int width, int height, int time) {
                super.onVideoData(did, videoBuf, h264Data, len, width, height, time);
                if (isFinishing()) {
                    return;
                }
                if (!isSameDevice(did)) {
                    return;
                }
                if (!bDisplayFinished || isConfigurationChanging || isP2PLivePaused || !isVideoStarted) {
                    return;
                }
                if (h264Data != 1) {
                    return;
                }
                recvDataTime = SystemClock.elapsedRealtime();
                bDisplayFinished = false;
                videoData = videoBuf;
                videoDataLen = len;
                nVideoWidth = width;
                nVideoHeight = height;
                if (needPhoto || isFirstShow || needVideoThumb) {
                    isNeedSavedPhone = true;
                    byte[] rgb = new byte[width * height * 2];
                    Avapi.YUV4202RGB565(videoBuf, rgb, width, height);
                    ByteBuffer buffer = ByteBuffer.wrap(rgb);
                    mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    mBitmap.copyPixelsFromBuffer(buffer);
                }
                mHandler.sendEmptyMessage(H264_DATA);
                mDataLen += videoBuf.length / 1024;
                if (!mHandler.hasMessages(NET_SPEED)) {
                    long tempDataLen = mDataLen - mLastDataLen;
                    if (tempDataLen < 0) {
                        tempDataLen = 0;
                    }
                    mLastDataLen = mDataLen;
                    Message msg = Message.obtain();
                    msg.what = NET_SPEED;
                    msg.obj = ByteUtil.formatSize(tempDataLen) + "/s";
                    mHandler.sendMessageDelayed(msg, 1000);
                }
            }

            @Override
            public void onGetSysInfo(String did, int msgType, SysInfo sysInfo) {
                super.onGetSysInfo(did, msgType, sysInfo);
                if (sysInfo != null && isSameDevice(did)) {
                    mSysInfo = sysInfo;
                    mHandler.sendEmptyMessage(R.id.msg_update_version);
                }
            }
        };
    }

    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationFinish(boolean isShow) {
        if (isFinishing())
            return;
        if (lyOtherCtrl != null && lyVideoCtrlLand != null && !lyOtherCtrl.isAnimationRunning()
                && !lyVideoCtrlLand.isAnimationRunning()) {
            if (isShow) {
                lyVideoCtrlLand.autoHideStart();
                lyOtherCtrl.autoHideStart();
            } else {
//                resumeTimeThread();
            }
        }
    }

    /*********************************************************************************************
     * 											设备控制 指令
     * *******************************************************************************************/
    /**
     * 麦克风、扩音 设置变动
     */
    private void updateAudioStatus() {
        Logger.i(" **************  updateAudioStatus ");
        int status = 0;
        status += (isMicEnabled ? 2 : 0);
        status += (isSpeakerEnabled ? 1 : 0);
        mAudioStatus = status;
        ThreadPoolMgr.getCustomThreadPool2().submit(
                new Runnable() {
                    @Override
                    public void run() {
                        Avapi.SetAudioStatus(mDevice.getDevId(), mAudioStatus);
                    }
                }
        );
    }

    private void pauseOrResumeVideo(final boolean isPaused) {
        ThreadPoolMgr.getCustomThreadPool2().submit(
                new Runnable() {
                    @Override
                    public void run() {
                        Avapi.SetVideoStatus(mDevice.getDevId(), isPaused ? 0 : 1);
                    }
                }
        );
    }


    /**
     * 获取设备系统信息
     */
    private void getDevSys() {
        ApiMgrV2.getDevSys(mDevice.getDevId());
    }


    /*********************************************************************************************
     * 											其它
     * *******************************************************************************************/

    /**
     * SD卡是否存在
     */
    private boolean existSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 控件显示隐藏
     */
    private void setVisibility(View view, int visibility) {
        try {
            if (view != null)
                view.setVisibility(visibility);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控件点击事件
     */
    private void setOnClickListener(View view, View.OnClickListener l) {
        try {
            if (view != null)
                view.setOnClickListener(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @author NickyHuang
     * @ClassName: MySimpleGesture
     * @Description: 处理全屏手势
     * @date 2016-5-6 上午11:06:54
     */
    private class MySimpleGesture extends GestureDetector.SimpleOnGestureListener {


        /*
         * 两个函数都是在Touch Down后又没有滑动(onScroll)，又没有长按(onLongPress)，然后Touch Up时触发
         * 点击一下非常快的(不滑动)Touch Up: onDown->onSingleTapUp->onSingleTapConfirmed
         * 点击一下稍微慢点的(不滑动)Touch Up: onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
         */
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Logger.t("MyGesture").i("onSingleTapConfirmed");
            if ((isAnswered || isMonitor)
                    && lyVideoCtrlLand != null && !lyVideoCtrlLand.isAnimationRunning()
                    && lyOtherCtrl != null && !lyOtherCtrl.isAnimationRunning()) {// !lyTopCtrl.isAnimationRunning() &&
                lyVideoCtrlLand.toggleShowHide();
                lyOtherCtrl.toggleShowHide();
            }
            return super.onSingleTapConfirmed(e);
        }

    }


}
