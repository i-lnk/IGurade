package com.rl.geye.ui.aty;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.DateUtil;
import com.rl.geye.R;
import com.rl.geye.adapter.VideoAdapter;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.p2plib.bean.EdwinVideo;
import com.rl.p2plib.bean.EdwinVideoGroup;
import com.rl.p2plib.utils.IdUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/3/22.
 */

public class FolderVideoAty extends BaseP2PAty implements AdapterView.OnItemClickListener {


    private static final int MSG_REFRESH = 32;
    private static final int MSG_REFRESH_LIST = 33;
    private static final int REQUEST_CODE_FOR_AUDIO_RECORD = 2002;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_video)
    ListView listVideo;
    @BindView(R.id.ly_video)
    View lyVideo;
    //    private String mPath = "";
    private VideoAdapter mAdapter;

    private List<EdwinVideo> mDataList = new ArrayList<>();
    private Handler mHandler;
    private boolean isGetting = false;

    private CalendarDay curDate;


    @Override
    protected int getLayoutId() {
        return R.layout.aty_folder_video;
    }

    @Override
    protected View getVaryTargetView() {
        return lyVideo;
    }

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            curDate = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_CAL_DATE);
        }
        return super.initPrepareData() && curDate != null;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.title_dev_video);
    }

    @Override
    protected void initViewsAndEvents() {


        mAdapter = new VideoAdapter(this, mDataList);
        listVideo.setAdapter(mAdapter);
        listVideo.setOnItemClickListener(this);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_REFRESH:
                        showVideo();
                        break;
                    case MSG_REFRESH_LIST:
                        EdwinVideoGroup group = (EdwinVideoGroup) msg.obj;
                        if (group.getRecord() != null && !group.getRecord().isEmpty()) {
                            mDataList.addAll(group.getRecord());
                            Collections.reverse(mDataList);
                        }
                        mAdapter.notifyDataSetChanged();
                        if (group.isEnd()) {
                            showVideo();
                        }
                        break;
                }
            }
        };
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isGetting = false;
    }

    @Override
    protected void onClickView(View v) {

    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {
            @Override
            public void onGetVideoGroup(String did, int msgType, EdwinVideoGroup videoGroup) {
                super.onGetVideoGroup(did, msgType, videoGroup);
                if (mDevice != null && IdUtil.isSameId(mDevice.getDevId(), did) && !isPaused() && isGetting) {
                    if (videoGroup != null) {
                        Message message = Message.obtain();
                        message.what = MSG_REFRESH_LIST;
                        message.obj = videoGroup;
                        mHandler.sendMessage(message);
                    }
                }
            }
        };
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final EdwinVideo item = mAdapter.getItem(position);
        if (item != null) {
            checkPermission(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_FOR_AUDIO_RECORD,
                    new PermissionResultCallback() {
                        @Override
                        public void onPermissionGranted() {

                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                            bundle.putParcelable(Constants.BundleKey.KEY_DEV_SD_VIDEO, item);
                            gotoActivity(PlayBackAty.class, bundle);

                        }

                        @Override
                        public void onPermissionDenied() {

                        }
                    });
        }
    }

    private void refreshData() {
        mDataList.clear();
        isGetting = true;

//        Calendar calMonthAgo = Calendar.getInstance();
//        calMonthAgo.add(Calendar.MONTH, -1);
//        Calendar startCal = Calendar.getInstance();
//        startCal.add(Calendar.DAY_OF_MONTH, -7);
        String startTime = DateUtil.getCommTimeStr2(DateUtil.getTimeStampDayStart(curDate.getCalendar()));
        String endTime = DateUtil.getCommTimeStr2(DateUtil.getTimeStampDayEnd(curDate.getCalendar()));

        ApiMgrV2.getVideoList(mDevice.getDevId(), startTime, endTime);
        showLoadDialog(R.string.tips_loading_video, new EdwinTimeoutCallback(45 * 1000) {
            @Override
            public void onTimeOut() {
                mHandler.sendEmptyMessage(MSG_REFRESH);
            }
        }, null);
//        new DlgCancelCallback() {
//            @Override
//            public void onCancel() {
//                mHandler.sendEmptyMessage(MSG_REFRESH);
//            }
//        }
    }

    private void showVideo() {
        isGetting = false;
        hideLoadDialog();
        if (mDataList.isEmpty()) {
            showEmpty(getString(R.string.tips_empty_file), R.mipmap.ic_empty_msg, null);
        } else {
            hideMsgView();
//            Collections.sort( mDataList, EdwinVideo.comparator );
            mAdapter.notifyDataSetChanged();
        }
    }


}
