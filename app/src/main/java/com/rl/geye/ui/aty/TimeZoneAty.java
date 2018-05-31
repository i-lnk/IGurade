package com.rl.geye.ui.aty;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.rl.commons.BaseApp;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.adapter.TimeZoneAdapter;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.p2plib.bean.DevTimeZone;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Nicky on 2016/10/20.
 * 时区选择
 */
public class TimeZoneAty extends BaseP2PAty {


    @BindView(R.id.rv_timezone)
    RecyclerView rvZone;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ly_all)
    View lyAll;

    private List<DevTimeZone> mZoneList;
    private TimeZoneAdapter mAdapter;

    private DevTimeZone curTimeZone;

    private Handler mHandler;
    private BaseQuickAdapter.OnItemClickListener onItemClickListener;

    private volatile long opTime = 0; // 命令执行时间

    @Override
    protected int getLayoutId() {
        return R.layout.aty_time_zone;
    }

    @Override
    protected void onP2PStatusChanged() {
        mAdapter.setOnline(isOnline);
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            curTimeZone = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_TIME_ZONE);
        }
        return curTimeZone != null && super.initPrepareData();
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.time_zone);
    }

    @Override
    protected void initViewsAndEvents() {
        int[] zones = getResources().getIntArray(R.array.time_zone_val);
        initHandler();
        mZoneList = new ArrayList<>();
        for (int zone : zones) {
            DevTimeZone timeZone = new DevTimeZone(zone);
            mZoneList.add(timeZone);
        }
        if (mZoneList.contains(curTimeZone)) {
            mZoneList.remove(curTimeZone);
            mZoneList.add(0, curTimeZone);
        }

        mAdapter = new TimeZoneAdapter(mZoneList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvZone.setLayoutManager(layoutManager);
        rvZone.setItemAnimator(new DefaultItemAnimator());

        rvZone.setAdapter(mAdapter);
        mAdapter.chooseItem(curTimeZone);


        onItemClickListener = new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                if (!isOnline)
                    return;
//                mAdapter.chooseItem(position);
                if (!curTimeZone.equals(mZoneList.get(position))) {
                    setTimeZone(mZoneList.get(position).getTimezone());
                    opTime = SystemClock.elapsedRealtime();
                    showLoadDialog(new EdwinTimeoutCallback(5000) {
                        @Override
                        public void onTimeOut() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoadDialog();
                                    BaseApp.showToast(R.string.time_out);
                                }
                            });

                        }
                    }, null);
                }


            }
        };
        mAdapter.setOnItemClickListener(onItemClickListener);

        onP2PStatusChanged();

    }

    @Override
    protected void onClickView(View v) {

    }

    /**
     * 设置时区
     */
    private void setTimeZone(int timeZone) {
        ApiMgrV2.setTimeZone(mDevice.getDevId(), timeZone);
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {

        return new MyP2PCallBack() {


            @Override
            public void onSetTimeZone(String did, int msgType, DevTimeZone timeZone) {
                super.onSetTimeZone(did, msgType, timeZone);
                if (timeZone != null) {
                    Message msg = Message.obtain();
                    msg.what = R.id.msg_update_time_zone;
                    msg.obj = timeZone;

                    if (SystemClock.elapsedRealtime() - opTime < 800)
                        mHandler.sendMessageDelayed(msg, 500);
                    else
                        mHandler.sendMessage(msg);
                }
            }
        };
    }


    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case R.id.msg_update_time_zone:
                        if (mAdapter != null) {
                            hideLoadDialog();
                            mAdapter.chooseItem((DevTimeZone) msg.obj);
                            fromIntent.putExtra(Constants.BundleKey.KEY_TIME_ZONE, (DevTimeZone) msg.obj);
                            setResult(RESULT_OK, fromIntent);
                            finish();
                        }
                        break;

                }
            }
        };
    }

}
