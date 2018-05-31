package com.rl.geye.ui.frag;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edwintech.vdp.jni.Avapi;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.nicky.framework.slidinglayout.SlidingLayout;
import com.orhanobut.logger.Logger;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.DeviceAddAdapter;
import com.rl.geye.base.BaseDevAddFrag;
import com.rl.geye.bean.EdwinWifiInfo;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;

import net.sqlcipher.database.SQLiteConstraintException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;

import static com.rl.commons.BaseApp.showToast;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 搜索
 */
public class AddStepSearchFrag extends BaseDevAddFrag implements View.OnClickListener {


    public static final String ARG_WIFI_INFO = "arg_wifi_info";
    public static final String ARG_IS_AP = "arg_is_ap";
    private static final int PROGRESS_UPDATE = 21;//进度条随时间更新
    private static final int LIST_UPDATE = 23;//
    @BindView(R.id.rv_dev)
    RecyclerView rvDev;
    @BindView(R.id.ly_list)
    SlidingLayout lyList;
    @BindView(R.id.pb_search)
    CircularProgressBar pbSearch;
    @BindView(R.id.ly_bg_enable)
    LinearLayout lyBgEnable;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.ly_empty)
    LinearLayout emptyView;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;


//    private OnStepSearchEvents mListener;
    private EdwinWifiInfo mWifiInfo;//WiFi信息
    private boolean isApMode = false; //是否为AP模式
    private boolean devFound = false; //搜到设备

    private DeviceAddAdapter mAdapter;

    private List<EdwinDevice> mDbDevList; //数据库已添加的设备
    private Thread searchThread;//搜索线程
    private boolean searchStop = true; //搜索线程已停止

    private Thread timeThread; //计时线程
    private boolean timeRunFlag = true;
    private Handler mHandler;

    private int progressVal = 0;

    //    public interface OnStepSearchEvents {
//        void retry();
//        void add();
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnStepSearchEvents) {
//            mListener = (OnStepSearchEvents) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnStep3Events");
//        }
//    }
    @Override
    protected void onAttachToContext(Context context) {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_step_search;
    }

    @Override
    public View getVaryTargetView() {
        return null;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {

        mWifiInfo = getArguments().getParcelable(ARG_WIFI_INFO);
        isApMode = getArguments().getBoolean(ARG_IS_AP);

        mDbDevList = MyApp.getDaoSession().getEdwinDeviceDao().loadAll();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDev.setLayoutManager(layoutManager);
        rvDev.setItemAnimator(new DefaultItemAnimator());


        mAdapter = new DeviceAddAdapter(new ArrayList<EdwinDevice>());//TODO

//        LayoutInflater mInflater = LayoutInflater.from(getContext());
//        LinearLayout headerView = (LinearLayout) mInflater.inflate(R.layout.header_empty_view, null);
//        mAdapter.addHeaderView( headerView );


        rvDev.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mAdapter.getItem(position).toggleCheck();
                mAdapter.notifyItemChanged(position);
                btnAdd.setEnabled(!mAdapter.getAllChecked().isEmpty());
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case PROGRESS_UPDATE:
                        progressVal += 1; //
                        updateProgress();
                        break;
                    case LIST_UPDATE:
                        onNewDevSearched((String) msg.obj, msg.arg1);
                        break;
                }
            }
        };

        startTimeThread();
        startSearch();


        btnAdd.setVisibility(View.VISIBLE);
        btnAdd.setEnabled(false);
        btnAdd.setOnClickListener(this);
        btnRetry.setVisibility(View.GONE);
        btnRetry.setOnClickListener(this);

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick(getActivity(), v))
            return;
        switch (v.getId()) {
            case R.id.btn_add:
                List<EdwinDevice> chooseDevices = mAdapter.getAllChecked();
                if (!chooseDevices.isEmpty()) {

                    stopTimeThread();
                    stopSearch();
                    progress.setVisibility(View.GONE);
//                  pbSearch.progressiveStop();
                    lyBgEnable.setVisibility(View.GONE);

                    try {
                        MyApp.getDaoSession().getEdwinDeviceDao().insertInTx(chooseDevices);
                    } catch (SQLiteConstraintException sqlE) {
                        sqlE.printStackTrace();
                    }
                    postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD, chooseDevices);
                    postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_ADD_COMPLETE);
                    getActivity().finish();
                }
                break;
            case R.id.btn_retry:
                //TODO
                progress.setProgress(0);
                progressVal = 0;
                btnRetry.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                progress.setVisibility(View.VISIBLE);
                lyBgEnable.setVisibility(View.VISIBLE);
//                pbSearch.progressiveStop();

                startTimeThread();
                startSearch();

                break;
        }
    }


    @Override
    public void onDestroyView() {
        mHandler.removeMessages(PROGRESS_UPDATE);
        mHandler.removeMessages(LIST_UPDATE);
        super.onDestroyView();
        stopSearch();
        stopTimeThread();
        isApMode = false;
        devFound = false;
    }


    @Override
    protected SimpleP2PAppCallBack getP2PCallBack() {
        return new SimpleP2PAppCallBack() {
            @Override
            public void onSearchResult(int cameraType, String mac, String name, String strDevId, String ipAddress, int port, int deviceType) {
                if (!devTypeGroup.getTypeList().contains(Integer.valueOf(deviceType))) {
                    return;
                }
                if (isApMode && !devFound) {
                    devFound = true;
                    stopSearch();
                    startSearch();
                }

                if (isDevExist(strDevId.replace("-", ""))) {
                    Logger.t(TAG).i("---Device is Exist");
                    return;
                }
                Message msg = Message.obtain();
                msg.obj = strDevId;
                msg.arg1 = deviceType;
                msg.what = LIST_UPDATE;
                mHandler.sendMessage(msg);
            }
        };
    }


    private boolean isDevExist(String devId) {

        for (EdwinDevice dev : mAdapter.getData()) {
            if (IdUtil.isSameId(dev.getDevId(), devId)) {
                return true;
            }
        }
        return false;
    }

    private synchronized void onNewDevSearched(String did, int devType) {
        if (isDevExist(did.replace("-", ""))) {
            Logger.t(TAG).i("---Device is Exist");
            return;
        }
        if (StringUtils.isEmpty(did)) {
            Logger.t(TAG).e("invalid id: " + did);
//            getContextDelegate().showToast(R.string.invalid_id);
            return;
        }
        String devName;
        String didLast4Str;
        String didFirst2Str;
        if (did.length() > 6) {
            didLast4Str = did.substring(did.length() - 4, did.length());
            didFirst2Str = did.substring(0, 2);
        } else {
            didLast4Str = did;
            didFirst2Str = "";
        }
        switch (devType) {

            case P2PConstants.DeviceType.IPC:
                devName = getString(R.string.type_ipc) + didFirst2Str + didLast4Str;
                break;

            case P2PConstants.DeviceType.IPCC:
                devName = getString(R.string.type_ipcc) + didFirst2Str + didLast4Str;
                break;

            case P2PConstants.DeviceType.IPFC:
                devName = getString(R.string.type_ipfc) + didFirst2Str + didLast4Str;
                break;

            case P2PConstants.DeviceType.BELL_BI_DIRECTIONAL:
                devName = getString(R.string.type_bell_bi_directional) + didFirst2Str + didLast4Str;
                break;

            case P2PConstants.DeviceType.BELL_UNIDIRECTIONAL:
                devName = getString(R.string.type_bell_unidirectional) + didFirst2Str + didLast4Str;
                break;
            case P2PConstants.DeviceType.CAT_SING_EYE:
            case P2PConstants.DeviceType.CAT_DOUBLE_EYE:
                devName = getString(R.string.type_cat_eye) + didFirst2Str + didLast4Str;
                break;
            default:
                devName = didFirst2Str + didLast4Str;
                break;
        }


        boolean isIdValid;
        String idRegex = "^[A-Z0-9]+$";
        Pattern p = Pattern.compile(idRegex);
        Matcher m = p.matcher(did);
        isIdValid = m.matches();
        if (!isIdValid) {
            showToast(R.string.invalid_id);
            return;
        }
        EdwinDevice device = new EdwinDevice();
        device.setDevId(did);
        device.setName(devName);
        device.setType(devType);
        device.setShareable(true);
        device.setUser("");
        device.setPwd("");
        device.setUserPwdOK(false);
        device.setAdded(false);
        //TODO
        for (EdwinDevice dev : mDbDevList) {
            if (IdUtil.isSameId(dev.getDevId(), did)) {
                device.setAdded(true);
                break;
            }
        }

        mAdapter.addData(device);//TODO---尚未排序--可在插入的时候给定位置
    }


    /**
     * 配置中,循环发送search
     */
    private void startSearch() {
        if (searchThread == null) {
            searchThread = new Thread() {
                public void run() {
                    Logger.w("--------------------------mWifiInfo: " + mWifiInfo);
                    if (isApMode && !devFound) {
                        Avapi.StartSearch("", "");
                    } else {
                        Avapi.StartSearch(mWifiInfo.getWifiName(), mWifiInfo.getWifiPwd());
                    }
                    searchThread = null;
                }
            };
        }
        searchThread.start();
        searchStop = false;
    }

    /**
     * 终止搜索线程
     */
    private void stopSearch() {

        if (!searchStop) {
            searchStop = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    Avapi.CloseSearch();
                }
            }.start();
        }

    }


    private Thread getTimeThread() {
        if (timeThread == null) {
            return new Thread() {
                @Override
                public void run() {
                    while (timeRunFlag) {
                        try {
//                            if (mNeedConfig) {
//                                /** 多次sleep 以便快速中断   */
//                                for (int i = 0; i < 3; i++) {
//                                    Thread.sleep(100);
//                                    if (!timeRunFlag) {
//                                        break;
//                                    }
//                                }
//
//                            } else {
//                                Thread.sleep(100); // sleep 100ms
//                            }
                            Thread.sleep(100); // sleep 100ms
                            mHandler.sendEmptyMessage(PROGRESS_UPDATE);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        if (timeThread == null) {
            return;
        }
        if (timeThread.isAlive()) {
            try {
                timeRunFlag = false;
                timeThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        timeThread = null;
        mHandler.removeMessages(PROGRESS_UPDATE);
        progressVal = 0;
    }


    private synchronized void updateProgress() {
//        XLog.i(TAG,"----------->updateProgress : "+progressVal);
        if (progress == null)
            return;
        if (progressVal <= 100) {
            progress.setProgress(progressVal);
        }
        if (progressVal >= 100) {
            stopTimeThread();
            stopSearch();
            progress.setVisibility(View.GONE);
//            pbSearch.progressiveStop();
            lyBgEnable.setVisibility(View.GONE);

            if (!mAdapter.getData().isEmpty()) {
                emptyView.setVisibility(View.GONE);
                if (mAdapter.hasNewDevice()) {
                    btnRetry.setVisibility(View.GONE);
                    btnAdd.setVisibility(View.VISIBLE);
                } else {
                    btnRetry.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.GONE);
                }
            } else {
                btnRetry.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
                btnAdd.setVisibility(View.GONE);
            }

        }
//        XLog.i(TAG,"----------->updateProgress end");
    }


}
