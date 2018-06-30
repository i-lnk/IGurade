package com.rl.geye.ui.aty;

import android.Manifest;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.edwintech.vdp.jni.Avapi;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.DateUtil;
import com.rl.commons.utils.NetUtil;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.PopupAdapter;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.bean.StreamItem;
import com.rl.geye.constants.Constants;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.db.bean.DeviceBg;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.db.bean.SubDeviceDao;
import com.rl.geye.image.ImageUtil;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.ui.widget.CameraMenuWin;
import com.rl.geye.ui.widget.CtrlLayout;
import com.rl.geye.ui.widget.XPopupWindow;
import com.rl.geye.util.CallAlarmUtil;
import com.rl.geye.util.MyRenderV2;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.p2plib.bean.CallAnswer;
import com.rl.p2plib.bean.PicMode;
import com.rl.p2plib.constants.P2PConstants;

import net.sqlcipher.database.SQLiteConstraintException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/9/12.
 * 摇头机监视页
 */
public class IpcVideoAty extends BaseP2PAty implements CtrlLayout.AnimationListener {

    private static final int REQUEST_CODE_FOR_AUDIO_RECORD = 4561;
    /**
     * handler 消息类型
     */
    private static final int H264_DATA = 1;//
    private static final int VIDEO_LOST = 3311;//
    private static final int STREAM_CHANGED = 3344;//码流变更
    private static final int CONNECT_TIME_UPDATE = 1234;//
    private static final int RECORD_TIME_UPDATE = 4321;//
    private static IpcVideoAty instance;
    @BindView(R.id.ly_all)
    View lyAll;
    @BindView(R.id.sv_video)
    GLSurfaceView svVideo;
    @BindView(R.id.ly_bg)
    FrameLayout lyBg;
    @BindView(R.id.pb_video)
    CircularProgressBar pbVideo;
    @BindView(R.id.ly_progress)
    LinearLayout lyProgress;
    @BindView(R.id.ic_status_record)
    ImageView icStatusRecord;
    @BindView(R.id.tv_time_connect)
    TextView tvTimeConnect;
    @BindView(R.id.tv_time_record)
    TextView tvTimeRecord;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ly_stream)
    View lyStream;
    @BindView(R.id.tv_stream)
    TextView tvStream;
    @BindView(R.id.ly_resolution)
    View lyResolution;
    @BindView(R.id.tv_resolution)
    TextView tvResolution;
    @BindView(R.id.iv_mic)
    ImageView ivMic;
    @BindView(R.id.iv_speaker)
    ImageView ivSpeaker;
    @BindView(R.id.iv_mirror)
    ImageView ivMirror;
    @BindView(R.id.iv_flip)
    ImageView ivFlip;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.ly_video_ctrl_land)
    CtrlLayout lyVideoCtrlLand;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.iv_photo)
    ImageView ivPhoto;
    @BindView(R.id.ly_ipc_photo_video)
    CtrlLayout lyPhotoVideo;
    @BindView(R.id.iv_mode_all)
    ImageView ivModeAll;
    @BindView(R.id.ly_ipc_bottom)
    CtrlLayout lyBottom;
    @BindView(R.id.img_guide)
    ImageView imgGuide;
    @BindView(R.id.tv_guide)
    TextView tvGuide;
    @BindView(R.id.btn_guide)
    Button btnGuide;
    @BindView(R.id.layout_guide)
    RelativeLayout layoutGuide;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    private CameraMenuWin mMenuWin;
    private int FLING_MIN_DISTANCE = 50;//最小滑动距离(上下左右判定)
    private int FLING_MIN_VELOCITY = 150;//最小滑动速度(上下左右判定)
    private GestureDetector mGestureDetector; //手势监听
    private boolean showGuide = true;
    private PicMode mPicMode; //图片翻转模式
    private int popupHeight = 0; //横屏时弹出框需要
    private XPopupWindow mStreamPopupWin; //码流弹出框
    private PopupAdapter mStreamAdapter; //码流弹出框适配器
    private XPopupWindow mResolutionPopupWin; //
    private PopupAdapter mResolutionAdapter; //
    private StreamItem mStreamType = new StreamItem();//码流类型
    private StreamItem mResolution = new StreamItem();
    private List<StreamItem> mStreamList;
    private List<StreamItem> mResolutionList;
    /**
     * @see P2PConstants.PushType
     */
    private int pushType; // 1表示呼叫 2代表移动侦测 7代表防拆报警

    /** 传递来的参数 */
    private Long recordId;
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
    private boolean needRestart = false;
    private int restartTimes = 0;
    /**
     * 麦克风、扩音器
     */
    private int mAudioStatus = 0x11; //扩音 和 麦克风 状态
    private boolean isSpeakerEnabled = false;
    private boolean isMicEnabled = false;
    private boolean origSpeakerEnabled = false; //备份暂停前状态
    private boolean origMicEnabled = false;//备份暂停前状态
    /**
     * 拍照、录像
     */
    private SoundPool mPhotoSoundPool;
//    private boolean needSetAudio = false;//是否需要设置音频
    private boolean isTakeVideo = false; //是否正在录像
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
    private boolean isFirstShow = true;
    private boolean bDisplayFinished = true; //当前视频帧是否显示完毕
    private byte[] videoData = null; //视频帧数据
    private int videoDataLen = 0;
    private int nVideoWidth = 0;
    private int nVideoHeight = 0;
    //    private boolean isAnswered = false;//是否已接听
//    private boolean needAnswer = false;//是否需要接听
//    private boolean answerOK = false;//是否成功接听
    private boolean isHangUped = false;//是否已挂断
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
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if (isFinishing()) {
                return;
            }
            switch (msg.what) {
//                case BATTERY_UPDATE:
//                    if(mBattery!=null){
//                        int level = (mBattery.getBattery()-1)/20 + 1;
//                        if(ivBattery!= null)
//                            ivBattery.setImageLevel( level  );
//                    }
//                    break;

                case H264_DATA: // h264
                {
//                    pbVideo.progressiveStop();
                    lyProgress.setVisibility(View.GONE);
                    svVideo.setVisibility(View.VISIBLE);
                    lyBg.setVisibility(View.GONE);

                    if (isFirstShow) {
                        if (isMonitor)
                            startConnectTimeThread();
                        saveBG();
                        isFirstShow = false;
                    }
                    if (myRender == null) {
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
//                    if (!isMonitor && !isAnswered && timeConnectSec >= MAX_WAITING) {
//                        hangUp();
//                    }
                    break;

                case RECORD_TIME_UPDATE:
                    if (!threadPauseFlag) {
                        tvTimeRecord.setText(getFormatTime(timeRecordSec));
                    }
                    break;

                case STREAM_CHANGED:
                    onStreamChanged();
                    break;
            }
        }
    };

    public static IpcVideoAty getInstance() {
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

    }

    @Override
    protected int getLayoutId() {
        return R.layout.aty_video_ipc;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void finish() {
        if (!isFinishing()) {
            if (lyPhotoVideo != null)
                lyPhotoVideo.autoHideStop();
            if (lyVideoCtrlLand != null)
                lyVideoCtrlLand.autoHideStop();
            if (lyBottom != null)
                lyBottom.autoHideStop();
            super.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (lyPhotoVideo != null)
            lyPhotoVideo.autoHideStop();
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.autoHideStop();
        if (lyBottom != null)
            lyBottom.autoHideStop();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Logger.t(TAG).i("---------onDestroy------");
        mHandler.removeMessages(H264_DATA);
        mHandler.removeMessages(VIDEO_LOST);
        CallAlarmUtil.getInstance().stopRingAndVibrator();
        stopConnectTimeThread();
        stopVideoMonitorThread();
        myRender.destroyShaders();
        super.onDestroy();
        hangUp();
        SystemValue.isCallRunning = false;
        instance = null;

    }

    private void initStream() {
        StreamItem item;
        mStreamList = new ArrayList<>();
        item = new StreamItem(getString(R.string.stream_main), true,
                R.drawable.ic_choose_small_selector, getString(R.string.stream_main_simple), P2PConstants.StreamType.MAIN);
        mStreamList.add(item);
        item = new StreamItem(getString(R.string.stream_secondary), true, R.drawable.ic_choose_small_selector, getString(R.string.stream_secondary_simple),
                P2PConstants.StreamType.SECONDARY);
        mStreamList.add(item);

        mStreamType = mStreamList.get(0);
        mStreamAdapter = new PopupAdapter(this, mStreamList);
        mStreamAdapter.chooseItem(mStreamType);
        tvStream.setText(mStreamType.getSimpleName());
        mStreamPopupWin = new XPopupWindow(this, mStreamAdapter);
        mStreamPopupWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                lyPhotoVideo.autoHideStart();
                lyVideoCtrlLand.autoHideStart();
                lyBottom.autoHideStart();
            }
        });
        mStreamPopupWin.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                executeStreamCtrl(mStreamList.get(position), mResolution);
                onStreamChanged();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mStreamPopupWin.dismiss();
                    }
                }, 100);
            }
        });


        mResolutionList = new ArrayList<>();
        item = new StreamItem(getString(R.string.resolution_h), true,
                R.drawable.ic_choose_small_selector, getString(R.string.resolution_h_simple), P2PConstants.Resolution.HIGH);
        mResolutionList.add(item);
        item = new StreamItem(getString(R.string.resolution_m), true, R.drawable.ic_choose_small_selector, getString(R.string.resolution_m_simple),
                P2PConstants.Resolution.MIDDLE);
        mResolutionList.add(item);
        item = new StreamItem(getString(R.string.resolution_l), true, R.drawable.ic_choose_small_selector, getString(R.string.resolution_l_simple),
                P2PConstants.Resolution.LOW);
        mResolutionList.add(item);

        mResolution = mResolutionList.get(1);
        mResolutionAdapter = new PopupAdapter(this, mResolutionList);
        mResolutionAdapter.chooseItem(mResolution);
        tvResolution.setText(mResolution.getSimpleName());
        mResolutionPopupWin = new XPopupWindow(this, mResolutionAdapter);
        mResolutionPopupWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                lyPhotoVideo.autoHideStart();
                lyVideoCtrlLand.autoHideStart();
                lyBottom.autoHideStart();
            }
        });
        mResolutionPopupWin.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mResolution = mResolutionList.get(position);
                executeStreamCtrl(mStreamType, mResolution);
                onStreamChanged();
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mResolutionPopupWin.dismiss();
                    }
                }, 100);
            }
        });
    }

    @Override
    protected void initViewsAndEvents() {
        SystemValue.isCallRunning = true;
        // 设置为全屏模式
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        int sysUiVisible = getWindow().getDecorView().getSystemUiVisibility();
        getWindow().getDecorView().setSystemUiVisibility(sysUiVisible | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


        FLING_MIN_DISTANCE = dp2px(20);
        FLING_MIN_VELOCITY = dp2px(50);
        mGestureDetector = new GestureDetector(this, new MySimpleGesture());

        showGuide = DataLogic.isShowGuide();

        initStream();
        mMenuWin = new CameraMenuWin(this, mDevice.getDevId());
        mMenuWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                lyPhotoVideo.autoHideStart();
                lyVideoCtrlLand.autoHideStart();
                lyBottom.autoHideStart();
            }
        });

        tvName.setText(mDevice.getName());
        ivMic.setSelected(isMicEnabled);
        ivSpeaker.setSelected(isSpeakerEnabled);
        ivVideo.setSelected(false);
        ivMic.setOnClickListener(this);
        ivSpeaker.setOnClickListener(this);
        ivMirror.setOnClickListener(this);
        ivFlip.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivVideo.setOnClickListener(this);
        ivPhoto.setOnClickListener(this);
        ivModeAll.setOnClickListener(this);
        lyStream.setOnClickListener(this);
        lyResolution.setOnClickListener(this);
        ivMenu.setOnClickListener(this);


        if (lyPhotoVideo != null)
            lyPhotoVideo.setAnimationListener(this);
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.setAnimationListener(this);
        if (lyBottom != null)
            lyBottom.setAnimationListener(this);

        if (showGuide) {
            layoutGuide.setVisibility(View.VISIBLE);
            btnGuide.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showGuide = false;
                    DataLogic.saveShowGuide(showGuide);
                    showNormalLayout();
                }
            });
        } else {
            showNormalLayout();
        }
        tvTimeConnect.setTextColor(ContextCompat.getColor(this, R.color.white));
        if (tvTips != null) {

            if (isMonitor) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_video_green));
                tvTips.setText(R.string.tips_monitoring);
            } else if (pushType == P2PConstants.PushType.CALL) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_video_green));
            } else if (pushType == P2PConstants.PushType.DETECTION) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_red));
                tvTips.setText(R.string.tips_alarm_detect);
            } else if (pushType == P2PConstants.PushType.ALARM_DISMANTLE) {
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_red));
                tvTips.setText(R.string.tips_alarm_dismantle);
            } else if (pushType >= P2PConstants.PushType.ALARM_433) {
//                EdwinRecord record = MyApp.getDaoSession().getEdwinRecordDao().load(recordId);
                List<SubDevice> subList = MyApp.getDaoSession().getSubDeviceDao().queryBuilder()
                        .where(SubDeviceDao.Properties.Pid.eq(mDevice.getDevId()),
                                SubDeviceDao.Properties.Id.eq(String.valueOf(pushType - 10000))
                        ).list();
                if (subList != null && !subList.isEmpty()) {
                    SubDevice subDev = subList.get(0);
                    tvTips.setText(getString(R.string.desc_alarm_433, subDev.getName()));
                }
                tvTips.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_red));
            }
        }
        if (isMonitor) {


        } else {
            startConnectTimeThread();
//            startRingAndVibrator();
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

                /** 开启视频流  */
                startStreamLive();

            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });

    }

    private void onStreamChanged() {
        mStreamAdapter.chooseItem(mStreamType);
        mResolutionAdapter.chooseItem(mResolution);
        tvStream.setText(mStreamType.getSimpleName());
        tvResolution.setText(mResolution.getSimpleName());
    }

    private boolean isCtrlMenu(View v) {
        return v.getId() == R.id.iv_mic || v.getId() == R.id.iv_speaker
                || v.getId() == R.id.iv_mirror || v.getId() == R.id.iv_flip
                || v.getId() == R.id.iv_video || v.getId() == R.id.iv_photo
                || v.getId() == R.id.iv_mode_all;
    }

    @Override
    protected void onClickView(View v) {
        if (lyPhotoVideo != null && lyVideoCtrlLand != null && lyBottom != null &&
                (lyPhotoVideo.isAnimationRunning()
                        || lyBottom.isAnimationRunning()
                        || lyVideoCtrlLand.isAnimationRunning())) {
            return;
        }
        if (isCtrlMenu(v) && lyPhotoVideo != null && lyVideoCtrlLand != null && lyBottom != null) {
            lyPhotoVideo.autoHideRestart();
            lyVideoCtrlLand.autoHideRestart();
            lyBottom.autoHideRestart();
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

            case R.id.iv_mirror:
                if (isVideoStarted && lyProgress.getVisibility() == View.GONE && mPicMode != null) {
                    mPicMode.toggleMirror();
                    executePicCtrl(mPicMode.getFlip(), mPicMode.getMirrow());
                }
                break;
            case R.id.iv_flip:
                if (isVideoStarted && lyProgress.getVisibility() == View.GONE && mPicMode != null) {
                    mPicMode.toggleRotate();
                    executePicCtrl(mPicMode.getFlip(), mPicMode.getMirrow());
                }
                break;

            case R.id.iv_video:
                takeVideo();
                break;
            case R.id.iv_photo:
                takePhoto();
                break;

            case R.id.iv_hangup:
            case R.id.iv_close:
                hangUp();
                break;

            case R.id.iv_mode_all:
                if (isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    ivModeAll.setSelected(!ivModeAll.isSelected());
                    executePtzCtrl(ivModeAll.isSelected() ? P2PConstants.PtzCmd.AVIOCTRL_PTZ_AUTO
                            : P2PConstants.PtzCmd.AVIOCTRL_PTZ_STOP);
                }
                break;

            case R.id.ly_stream:
                if (mStreamPopupWin != null && isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    lyVideoCtrlLand.autoHideStop();
                    lyPhotoVideo.autoHideStop();
                    lyBottom.autoHideStop();
//                    if( popupHeight==0 )
//                    {
//                        int[] location = new int[2];
//                        lyVideoCtrlLand.getLocationOnScreen(location);
//                        int[] location2 = new int[2];
//                        tvStream.getLocationOnScreen(location2);
//                        popupHeight = location2[1] - location[1] ;
//                    }
//                    mStreamPopupWin.showAsDropDown(v,0,popupHeight);//, 0, -21);
                    mStreamPopupWin.showAsDropDown(v);
                }
                break;

            case R.id.ly_resolution:
                if (mResolutionPopupWin != null && isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    lyVideoCtrlLand.autoHideStop();
                    lyPhotoVideo.autoHideStop();
                    lyBottom.autoHideStop();
//                    if( popupHeight==0 )
//                    {
//                        int[] location = new int[2];
//                        lyVideoCtrlLand.getLocationOnScreen(location);
//                        int[] location2 = new int[2];
//                        tvResolution.getLocationOnScreen(location2);
//                        popupHeight = location2[1] - location[1] ;
//                    }
//                    mResolutionPopupWin.showAsDropDown(v,0,popupHeight);//, 0, -21);
                    mResolutionPopupWin.showAsDropDown(v);
                }
                break;
            case R.id.iv_menu:
//                ivMenu.setSelected( );
                if (mMenuWin != null && isVideoStarted && lyProgress.getVisibility() == View.GONE) {
                    lyVideoCtrlLand.autoHideStop();
                    lyPhotoVideo.autoHideStop();
                    lyBottom.autoHideStop();
                    mMenuWin.showWindow(v);
                }


                break;

        }
    }

    /**********************************************************************************************
     *****************************************接听、挂断、拍照、录像*********************************
     /**********************************************************************************************/

    private void showNormalLayout() {
        layoutGuide.setVisibility(View.GONE);
        svVideo.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        svVideo.setFocusable(true);
        svVideo.setClickable(true);
        svVideo.setLongClickable(true);

        lyBg.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
        lyBg.setFocusable(true);
        lyBg.setClickable(true);
        lyBg.setLongClickable(true);

        lyPhotoVideo.setAnimationListener(this);
        lyBottom.setAnimationListener(this);
        lyVideoCtrlLand.setAnimationListener(this);

        if (lyPhotoVideo != null && lyVideoCtrlLand != null && lyBottom != null) {
            lyPhotoVideo.autoHideStart();
            lyVideoCtrlLand.autoHideStart();
            lyBottom.autoHideStart();
        }

    }

    /**
     * 接听
     */
    private void answer() {
//        isAnswered = true;
        EdwinRecord record = MyApp.getDaoSession().getEdwinRecordDao().load(recordId);
        if (record != null) {
            record.setAnswered(true);
            MyApp.getDaoSession().getEdwinRecordDao().update(record);
            postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_UPDATE);
        }
        CallAlarmUtil.getInstance().stopRingAndVibrator();
//        onModeChanged();
        //TODO---Nicky

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
        if (lyPhotoVideo != null)
            lyPhotoVideo.autoHideStop();
        if (lyVideoCtrlLand != null)
            lyVideoCtrlLand.autoHideStop();
        if (lyBottom != null)
            lyBottom.autoHideStop();
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


        isHangUped = true;
        finish();
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

        p2pStreamThread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (startStreamRunFlag) {
                    int ret = -1;
                    //start pppp 为阻塞
                    if (needRestart) {
                        needRestart = false;
                        restartTimes++;
                        Logger.t(TAG).e("----------------------> P2P restartTimes  " + restartTimes);
                        if (restartTimes > 10) {
                            if (!isFinishing())
                                finish();
                            startStreamRunFlag = false;
                            p2pStreamThread = null;
                            return;
                        } else if (restartTimes > 5) {
                            for (int i = 0; i < 20; i++) {
                                try {
                                    sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                    Logger.t(TAG).e("----------------------> P2P StartPPPP  ");
                    if (!startStreamRunFlag)
                        return;
//                    if( BridgeService.getInstance().getDeviceStatus(mDevice.getDevId())==P2PConstants.P2PStatus.CONNECTING ){
//                        Avapi.ClosePPPP(mDevice.getDevId());
//                    }
//                    if(!startStreamRunFlag)
//                        return;
                    ret = Avapi.StartPPPP(mDevice.getDevId(), mDevice.getUser(), mDevice.getPwd(), P2PConstants.P2P_SERVER, P2PConstants.P2P_TYPE);
                    Logger.t(TAG).e("----------------------> P2P StartPPPP  ret : " + ret);
                    if (!startStreamRunFlag)
                        return;
                    if (ret == 0) {
                        //start pppp OK
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
                        Logger.t(TAG).e("----------------------> StartPPPPLivestream   ");
                        ret = Avapi.StartPPPPLivestream(mDevice.getDevId(), "", rate, 1, recvCodec, sendCodec, 0);
                        Logger.t(TAG).e("----------------------> StartPPPPLivestream  ret : " + ret);
                        if (!startStreamRunFlag)
                            return;
                        if (ret >= 0) {
                            isVideoStarted = true;
                            updateAudioStatus();

                            if (NetUtil.NET_WIFI == NetUtil.getNetWorkType(getActivity())) {
                                executeStreamCtrl(mStreamList.get(0), mResolutionList.get(1));
                                mHandler.sendEmptyMessage(STREAM_CHANGED);
                            } else {
                                executeStreamCtrl(mStreamList.get(1), mResolutionList.get(2));
                                mHandler.sendEmptyMessage(STREAM_CHANGED);
                            }

                            ApiMgrV2.getPicMode(mDevice.getDevId(), null, -1);
                            recvDataTime = SystemClock.elapsedRealtime();
                            startVideoMonitorThread();
                            startStreamRunFlag = false;
                            p2pStreamThread = null;
                            return;
                        }
                    } else if (ret == P2PConstants.P2PStatus.SLEEP) {//待机
                        for (int i = 0; i < 20; i++) {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (!startStreamRunFlag)
                                return;
                        }
                    } else if (ret == P2PConstants.P2PStatus.ERR_USER_PWD) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BaseApp.showToastLong(R.string.pppp_status_err_user_pwd);
                            }
                        });
                        for (int i = 0; i < 20; i++) {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (!isFinishing())
                            finish();
                        startStreamRunFlag = false;
                        p2pStreamThread = null;
                        return;
                    }
                    p2pStreamThread = null;
                    needRestart = true;
                }
//                startStreamLive();
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
            videoMonitorThread.start();
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
            }

            @Override
            public void onGetPicMode(String did, int msgType, PicMode picMode) {
                super.onGetPicMode(did, msgType, picMode);
                if (isSameDevice(did)) {
                    mPicMode = picMode;
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
                if (!bDisplayFinished) {
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

            }
        };
    }

    /*******************************************************************/
    @Override
    public void onAnimationStart() {

    }

    @Override
    public void onAnimationFinish(boolean isShow) {
        if (isFinishing())
            return;
        if (lyPhotoVideo != null && lyVideoCtrlLand != null && lyBottom != null && !lyPhotoVideo.isAnimationRunning()
                && !lyVideoCtrlLand.isAnimationRunning() && !lyBottom.isAnimationRunning()) {
            if (isShow) {
                lyVideoCtrlLand.autoHideStart();
                lyPhotoVideo.autoHideStart();
                lyBottom.autoHideStart();
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
     * PTZ控制
     */
    protected void executePtzCtrl(int ptzCmd) {
        ApiMgrV2.setPtzCtrl(mDevice.getDevId(), ptzCmd);
    }

    /**
     * 图像控制
     */
    protected void executePicCtrl(int flip, int mirror) {
        ApiMgrV2.setPicMode(mDevice.getDevId(), flip, mirror);
    }

    /**
     * 码流控制
     *
     * @param resolution 0：高清 1：均衡 2：流畅
     */
    protected void executeStreamCtrl(StreamItem stream, StreamItem resolution) {
        ApiMgrV2.setStream(mDevice.getDevId(), stream.getVal(), resolution.getVal());
        if (!mStreamType.equals(stream)) {
            mStreamType = stream;
        }
        if (!mResolution.equals(resolution)) {
            mResolution = resolution;
        }
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
     * @author NickyHuang
     * @ClassName: MySimpleGesture
     * @Description: 处理全屏手势
     * @date 2016-5-6 上午11:06:54
     */
    private class MySimpleGesture extends GestureDetector.SimpleOnGestureListener {
        // 双击的第二下Touch down时触发
        public boolean onDoubleTap(MotionEvent e) {
//            Log.i("MyGesture", "onDoubleTap");
            return super.onDoubleTap(e);
        }

        // 双击的第二下Touch down和up都会触发，可用e.getAction()区分
        public boolean onDoubleTapEvent(MotionEvent e) {
//            Log.i("MyGesture", "onDoubleTapEvent");
            return super.onDoubleTapEvent(e);
        }

        // Touch down时触发
        public boolean onDown(MotionEvent e) {
//            Log.i("MyGesture", "onDown");
            return super.onDown(e);
        }

        // Touch了滑动一点距离后，up时触发
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.i("MyGesture", "onFling-------speed x: "+velocityX +", speed y: "+velocityY + " , min speed: "+FLING_MIN_VELOCITY );
            float xDistance = Math.abs(e1.getX() - e2.getX());
            float yDistance = Math.abs(e1.getY() - e2.getY());
            if (xDistance >= yDistance) {
                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
//                    Log.i("MyGesture", "Fling left");
//                    showToast("Trun Left");
                    executePtzCtrl(P2PConstants.PtzCmd.AVIOCTRL_PTZ_LEFT);
                } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
//                    Log.i("MyGesture", "Fling right");
//    	            showToast("Trun Right");
                    executePtzCtrl(P2PConstants.PtzCmd.AVIOCTRL_PTZ_RIGHT);
                }
            } else {
                if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
//                    Log.i("MyGesture", "Fling up");
//                    showToast("Trun Up");
                    executePtzCtrl(P2PConstants.PtzCmd.AVIOCTRL_PTZ_UP);
                } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityY) > FLING_MIN_VELOCITY) {
//                    Log.i("MyGesture", "Fling down");
//    	            showToast("Trun Down");
                    executePtzCtrl(P2PConstants.PtzCmd.AVIOCTRL_PTZ_DOWN);
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        // Touch了不移动一直Touch down时触发
        public void onLongPress(MotionEvent e) {
//            Log.i("MyGesture", "onLongPress");
            super.onLongPress(e);
        }

        // Touch了滑动时触发
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.i("MyGesture", "onScroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /*
         * Touch了还没有滑动时触发
         * (1)onDown只要Touch Down一定立刻触发
         * (2)Touch Down后过一会没有滑动先触发onShowPress再触发onLongPress
         * So: Touch Down后一直不滑动，onDown -> onShowPress -> onLongPress这个顺序触发。
         */
        public void onShowPress(MotionEvent e) {
//            Log.i("MyGesture", "onShowPress");
            super.onShowPress(e);
        }

        /*
         * 两个函数都是在Touch Down后又没有滑动(onScroll)，又没有长按(onLongPress)，然后Touch Up时触发
         * 点击一下非常快的(不滑动)Touch Up: onDown->onSingleTapUp->onSingleTapConfirmed
         * 点击一下稍微慢点的(不滑动)Touch Up: onDown->onShowPress->onSingleTapUp->onSingleTapConfirmed
         */
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.i("MyGesture", "onSingleTapConfirmed");
            if (lyVideoCtrlLand != null && !lyVideoCtrlLand.isAnimationRunning()
                    && lyPhotoVideo != null && !lyPhotoVideo.isAnimationRunning()
                    && lyBottom != null && !lyBottom.isAnimationRunning()) {
                lyVideoCtrlLand.toggleShowHide();
                lyPhotoVideo.toggleShowHide();
                lyBottom.toggleShowHide();
            }
            return super.onSingleTapConfirmed(e);
        }

        public boolean onSingleTapUp(MotionEvent e) {
//            Log.i("MyGesture", "onSingleTapUp");
            return super.onSingleTapUp(e);
        }
    }


}
