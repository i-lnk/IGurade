package com.rl.geye.ui.aty;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rl.commons.ThreadPoolMgr;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.PhotoVideo;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/10/27.
 * 视频播放页面
 */
public class VideoPlayAty extends BaseMyAty implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final static int TIME_UPDATE = 1;
    @BindView(R.id.sv_video)
    SurfaceView svVideo;
    @BindView(R.id.bg_top_ctrl)
    FrameLayout bgTopCtrl;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    //    @BindView(R.id.iv_share)
//    ImageView ivShare;
    @BindView(R.id.iv_del)
    ImageView ivDelete;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ly_top_ctrl)
    RelativeLayout lyTopCtrl;
    @BindView(R.id.bg_bottom_ctrl)
    FrameLayout bgBottomCtrl;
    @BindView(R.id.ly_bottom_ctrl)
    RelativeLayout lyBottomCtrl;
    @BindView(R.id.ic_play)
    ImageView btnPlay;
    @BindView(R.id.ic_pause)
    ImageView btnPause;
    @BindView(R.id.tv_play_time)
    TextView tvPlayTime;
    @BindView(R.id.tv_video_time)
    TextView tvVideoTime;
    @BindView(R.id.seekbar)
    SeekBar seekBar;
    @BindView(R.id.root_view)
    View rootView;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer player;
    private Handler mHandler;
    private PhotoVideo mData;
    private AsyncTask<Void, Boolean, Boolean> mDelTask;
    private Timer timer;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_video_play;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mData = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_VIDEO_DATA);
        }
        return mData != null;
    }

    @Override
    public View getVaryTargetView() {
        return rootView;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        mSurfaceHolder = svVideo.getHolder();
        mSurfaceHolder.addCallback(this);
//		// 为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case TIME_UPDATE:
                        tvPlayTime.setText(getPlayTime(msg.arg1));
                        break;
                }
            }
        };

        ivClose.setOnClickListener(this);
//        ivShare.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new PlayerSeekBarChangeListener());
    }

    @Override
    protected void onClickView(View v) {

        switch (v.getId()) {
            case R.id.ic_pause:
                pauseVideo();
                break;
            case R.id.ic_play:
                resumeVideo();
                break;
            case R.id.iv_close:
                onBackPressed();
                break;
//            case R.id.iv_share:
//                if(mData!=null){
//                    pauseVideo();
//                    ShareUtil.shareVideo(this,getString(R.string.qr_share_to),getString(R.string.qr_share),getString(R.string.qr_title),
//                            Uri.parse(mData.getPath()));
//                }
//                break;
            case R.id.iv_del:
                pauseVideo();
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.str_delete)
                        .content(R.string.tips_del_video)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                executeDel();
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        stopVideo();
        if (player != null) {
            player.release();
            player = null;
        }
        super.onBackPressed();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
//      player.setOnSeekCompleteListener(this);
//      player.setOnVideoSizeChangedListener(this);
//      player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //在这里我们指定MediaPlayer在当前的Surface中进行播放
        player.setDisplay(mSurfaceHolder);
        try {
            String dataPath = mData.getPath();
//            PhotoVideoUtil.getVideoDirPath()+"F7PGYA89TK9RXGMJ111A/1477554734.mp4";
            Log.e("VideoPlayAty", "dataPath---->" + dataPath);

            player.setDataSource(dataPath);
            //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了
            stopVideo();
            player.prepareAsync();
            initProgress();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("VideoPlayAty", "surfaceDestroyed");
        stopVideo();
        if (player != null)
            player.release();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        int max = player.getDuration();
        tvVideoTime.setText(getPlayTime(max));
        seekBar.setMax(max);
        player.start();
        btnPlay.setVisibility(View.GONE);
        btnPause.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        seekBar.setProgress(mp.getDuration());
        stopVideo();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if( !isFinishing() )
//                    finish();
//            }
//        },100);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("Play Error:::", "onError called ,error code : " + what + ", extra code:" + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
                break;
            default:
                break;
        }
        return false;
    }

    public void pauseVideo() {
        if (player != null && player.isPlaying()) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
            player.pause();
        }

    }

    public void resumeVideo() {
        if (player != null && !player.isPlaying()) {
            btnPlay.setVisibility(View.GONE);
            btnPause.setVisibility(View.VISIBLE);
            player.start();
        }
        if (timer == null) {
            initProgress();
        }
    }


    public void stopVideo() {
        Log.e(TAG, "########## stopVideo");
        if (btnPlay != null && btnPause != null) {
            btnPlay.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
        }
        if (player != null && player.isPlaying()) {
            player.stop();
            player.prepareAsync();
        }
        if (timer != null) {
            Log.e(TAG, "########## STOP timer");
            timer.purge();
            timer.cancel();
            timer = null;
//			curTimeVal = 0 ;
//			timeSec = 0 ;
        }
    }

    //	private int curTimeVal = 0; //没100毫秒+1
//	private int timeSec = 0 ;
    private void initProgress() {
        timer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (player == null)
                    return;
                try {
                    if (player.isPlaying()) {
                        int max = player.getDuration();
                        int current = player.getCurrentPosition();
                        Log.e("VideoPlayAty", "max:" + max + " , current:" + current);

//						curTimeVal++;
//						if( curTimeVal%10 ==0 ){
//							timeSec++;
//							Message message = new Message();
//							message.what = TIME_UPDATE;
//							message.arg1 = timeSec;
//							mHandler.sendMessage(message);
//						}

                        Message message = Message.obtain();
                        message.what = TIME_UPDATE;
                        message.arg1 = current;
                        mHandler.sendMessage(message);

                        seekBar.setProgress(current);
                        //current * 100 /

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        timer.schedule(mTimerTask, 100, 100);
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

    private void cancelTask() {
        if (mDelTask != null) {
            mDelTask.cancel(true);
            mDelTask = null;
        }
    }

    private void executeDel() {
        cancelTask();
        mDelTask = new AsyncTask<Void, Boolean, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadDialog(R.string.deleting, null, null);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                boolean res = false;
                try {
                    if (deleteNorFile(mData.getPath())) {
                        deleteNorFile(mData.getPathThumb());
                        MyApp.getDaoSession().getPhotoVideoDao().delete(mData);
                        res = true;
                    }
                } catch (Exception e) {
                    res = false;
                }
                return res;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                hideLoadDialog();
                if (result) {
                    setResult(RESULT_OK, fromIntent);
                    onBackPressed();
                }
            }
        };
        mDelTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
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
//            int max = seekBar.getMax();
//            XLog.e(TAG,"--------------- progress: "+progress + "  ----- max: "+max);
//            XLog.e(TAG,"--------------- progress: "+progress + "  ----- max: "+max);
//            XLog.e(TAG,"-------------------- player max: "+player.getDuration());
            if (player != null) {
                player.seekTo(progress);
                tvPlayTime.setText(getPlayTime(progress));
            }
        }
    }

}
