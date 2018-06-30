package com.rl.geye.ui.frag;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.edwintech.vdp.jni.Avapi;
import com.nicky.framework.slidinglayout.SlidingLayout;
import com.nicky.framework.widget.RippleView;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.compatibility.Version;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.CameraUtil;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.FileUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.DeviceAdapter;
import com.rl.geye.base.BaseMyFrag;
import com.rl.geye.bean.DevTypeGroupBean;
import com.rl.geye.constants.Constants;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.db.bean.DeviceBgDao;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.db.bean.EdwinRecordDao;
import com.rl.geye.db.bean.PhotoVideoDao;
import com.rl.geye.db.bean.PushSet;
import com.rl.geye.db.bean.PushSetDao;
import com.rl.geye.db.bean.SubDevice;
import com.rl.geye.db.bean.SubDeviceDao;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.service.TaskWorkServer;
import com.rl.geye.ui.aty.BellVideoAty;
import com.rl.geye.ui.aty.CaptureActivity;
import com.rl.geye.ui.aty.ChooseDateAty;
import com.rl.geye.ui.aty.DevAddAty;
import com.rl.geye.ui.aty.DevQRCodeAty;
import com.rl.geye.ui.aty.IpcVideoAty;
import com.rl.geye.ui.aty.MainAty;
import com.rl.geye.ui.aty.SetDevAty;
import com.rl.geye.ui.dlg.LoginDialog;
import com.rl.geye.util.CallAlarmUtil;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.geye.util.SnackbarUtil;
import com.rl.p2plib.BridgeService;
import com.rl.p2plib.bean.Battery;
import com.rl.p2plib.bean.DevTimeZone;
import com.rl.p2plib.bean.SetResult;
import com.rl.p2plib.callback.SimpleP2PAppCallBack;
import com.rl.p2plib.constants.CmdConstant;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.IdUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;

/**
 * Created by Nicky on 2016/9/13.
 * 首页---设备列表
 */
public class HomeDevFrag extends BaseMyFrag implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener, DeviceAdapter.OnMenuClickListener {
    private static final int REQUEST_CODE_FOR_AUDIO_RECORD = 1001;
    private static final int REQUEST_CODE_FOR_CAMERA = 1002;
    private final int MSG_REFRESH_STATUS = 111;
    private final int MSG_SERVER_READY = 222;

    @BindView(R.id.rv_dev)
    RecyclerView rvDev;
    @BindView(R.id.ly_sliding)
    SlidingLayout lySliding;
    @BindView(R.id.snackbar_container)
    CoordinatorLayout snackBarContainer;
    /**
     * empty vied
     */
    View emptyView;
    TextView tvWelcome;
    RippleView lyAdd;
    RippleView lyScan;
    RippleView lyAddLan;

    private EdwinDevice mCurOpEdwinDevice;//当前操作设备
    private DeviceAdapter mAdapter;
    private RefreshTask mRefreshTask; //刷新线程任务
    private Handler PPPPMsgHandler;


    private OnDeviceListener mListener;
    /**
     * wait server thread
     */
    private ServiceWaitThread mThread;
    /**
     * empty view click
     */
    private RippleView.OnRippleCompleteListener mRippleCompleteListener = new RippleView.OnRippleCompleteListener() {
        @Override
        public void onComplete(RippleView rippleView) {
            if (Constants.KEY_TAG_CLICK.equals(rippleView.getTag())) {
                Bundle bundle = new Bundle();
                DevTypeGroupBean typeGroup = new DevTypeGroupBean(Constants.DeviceTypeGroup.GROUP_4)
                        .addType(P2PConstants.DeviceType.IPC)
                        .addType(P2PConstants.DeviceType.IPCC)
                        .addType(P2PConstants.DeviceType.IPFC)
                        .addType(P2PConstants.DeviceType.BELL_LTK6112C)
                        .addType(P2PConstants.DeviceType.BELL_BI_DIRECTIONAL)
                        .addType(P2PConstants.DeviceType.BELL_UNIDIRECTIONAL)
                        .addType(P2PConstants.DeviceType.CAT_SING_EYE)
                        .addType(P2PConstants.DeviceType.CAT_DOUBLE_EYE);
                bundle.putParcelable(Constants.BundleKey.KEY_DEV_TYPE_GROUP, typeGroup);

                switch (rippleView.getId()) {

                    case R.id.ly_add:
//                        bundle.putBoolean(Constants.BundleKey.KEY_ADD_LAN, false);
                        gotoActivity(DevAddAty.class, bundle);
                        break;

                    case R.id.ly_add_lan:
                        bundle.putInt(Constants.BundleKey.KEY_ADD_TYPE, Constants.AddType.LAN);
                        gotoActivity(DevAddAty.class, bundle);
                        break;

                    case R.id.ly_scan:
                        checkPermission(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA,
                                new PermissionResultCallback() {
                                    @Override
                                    public void onPermissionGranted() {

                                        if (Build.VERSION.SDK_INT >= Version.API23_M) {
                                            gotoActivity(CaptureActivity.class);
                                        } else {
                                            if (CameraUtil.isCameraUseable()) {
                                                gotoActivity(CaptureActivity.class);
                                            } else {
                                                new MaterialDialog.Builder(getActivity())
                                                        .title(R.string.permission_title_rationale)
                                                        .content(R.string.permission_camera_rationale)
                                                        .positiveText(R.string.str_ok)
                                                        .negativeText(R.string.str_cancel)
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                                                startActivity(intent);
                                                            }
                                                        }).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onPermissionDenied() {

                                    }
                                });
                        break;

                }
                rippleView.setTag(null);
            }

        }
    };

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof OnDeviceListener) {
            mListener = (OnDeviceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeviceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (PPPPMsgHandler != null) {
            PPPPMsgHandler.removeMessages(MSG_REFRESH_STATUS);
            PPPPMsgHandler.removeMessages(MSG_SERVER_READY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home_dev;
    }

    public MainAty getParentAty() {
        return (MainAty) (super.getActivity());
    }

    @Override
    protected void initToolBar() {
    }

    @Override
    protected void initViewsAndEvents() {

        //在配置变化的时候将这个fragment保存下来
        setRetainInstance(true);//TODO

        initEmptyView();
        initPPPPHandler();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvDev.setLayoutManager(layoutManager);
        rvDev.setItemAnimator(new DefaultItemAnimator());


        if (BridgeService.isReady()) {
            onServiceReady();

        } else {
            Intent intent = new Intent();
            intent.setClass(getActivity(), BridgeService.class);
            getActivity().startService(intent);

            mThread = new ServiceWaitThread();
            mThread.start();
        }


    }

    @Override
    protected void lazyLoad() {
//        initPushParam();
    }

    private void initEmptyView() {
        emptyView = getActivity().getLayoutInflater().inflate(R.layout.empty_dev, (ViewGroup) rvDev.getParent(), false);

        tvWelcome = (TextView) emptyView.findViewById(R.id.tv_welcome);
        lyAdd = (RippleView) emptyView.findViewById(R.id.ly_add);
        lyAddLan = (RippleView) emptyView.findViewById(R.id.ly_add_lan);

        lyScan = (RippleView) emptyView.findViewById(R.id.ly_scan);

        tvWelcome.setText(getString(R.string.tips_welcome_to_use, getString(R.string.app_name)));
        lyAdd.setOnClickListener(this);
        lyAddLan.setOnClickListener(this);
        lyScan.setOnClickListener(this);
        lyAdd.setOnRippleCompleteListener(mRippleCompleteListener);
        lyAddLan.setOnRippleCompleteListener(mRippleCompleteListener);
        lyScan.setOnRippleCompleteListener(mRippleCompleteListener);

    }

    private void onServiceReady() {
        List<EdwinDevice> deviceList = MyApp.getDaoSession().getEdwinDeviceDao().loadAll();
        BridgeService.addP2PAppCallBack(mP2pCallBack);

        mAdapter = new DeviceAdapter(deviceList);
        rvDev.setAdapter(mAdapter);
        mAdapter.setEmptyView(emptyView);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnMenuClickListener(this);

        mListener.onDevListSizeChanged(mAdapter.getData().isEmpty());
        mAdapter.notifyDataSetChanged();

        BridgeService.getInstance().connectDevices(mAdapter.getData());

        for (EdwinDevice device : deviceList) {
            TaskWorkServer.startService(getContext(),
                    TaskWorkServer.ACTION_ADD_FCM_TOPIC,
                    device.getDevId().replace("-", ""));
        }

    }

    @SuppressLint("HandlerLeak")
    private void initPPPPHandler() {
        PPPPMsgHandler = new Handler() {
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case MSG_REFRESH_STATUS:
                        int index = msg.arg1;
                        if (mAdapter != null)
                            mAdapter.notifyItemChanged(index);
                        break;
                    case MSG_SERVER_READY:
                        onServiceReady();
                        break;
                }
            }
        };
    }

    @Override
    protected SimpleP2PAppCallBack getP2PCallBack() {
        return new SimpleP2PAppCallBack() {

//            @Override
//            public void onPushSetOk(String did, int msgType) {
//                super.onPushSetOk(did, msgType);
//                List<PushSet> pushSets = MyApp.getDaoSession().getPushSetDao()
//                        .queryBuilder().where(PushSetDao.Properties.DevId.eq(did)).list();
//                for (PushSet push: pushSets ){
//                    push.setDevPushOk(true);
//                }
//                MyApp.getDaoSession().getPushSetDao().updateInTx( pushSets );
//            }


            @Override
            public void onSetTimeZone(String did, int msgType, DevTimeZone timeZone) {
                super.onSetTimeZone(did, msgType, timeZone);

                int index = getDeviceIndex(did);
                if (index != -1) {
                    mAdapter.getData().get(index).setTimeZoneOk(true);
                    MyApp.getDaoSession().getEdwinDeviceDao().update(mAdapter.getData().get(index));

                    Message message = Message.obtain();
                    message.what = MSG_REFRESH_STATUS;
                    message.arg1 = index;
                    PPPPMsgHandler.sendMessage(message);
                }
            }

            @Override
            public void onGetSubList(String did, int msgType, String dataStr) {
                if (mAdapter == null || mAdapter.getData() == null)
                    return;
                List<SubDevice> list = JSON.parseObject(dataStr, new TypeReference<List<SubDevice>>() {
                });
                if (list != null) {
                    MyApp.getDaoSession().getSubDeviceDao().deleteAll();
                    if (!list.isEmpty()) {
                        for (SubDevice dev : list) {
                            dev.setPid(did);
                        }
                        Logger.i("-------------- list : " + list);
                        try {
                            MyApp.getDaoSession().getSubDeviceDao().insertInTx(list);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_UPDATE);//
                }
            }

            @Override
            public void onSetResult(String did, int msgType, SetResult setResult) {
                if (mAdapter == null || mAdapter.getData() == null)
                    return;
                if (msgType == CmdConstant.CmdType.USER_IPCAM_CFG_433_RESP
                        || msgType == CmdConstant.CmdType.USER_IPCAM_SET_433_RESP
                        || msgType == CmdConstant.CmdType.USER_IPCAM_DEL_433_RESP) {
                    if (setResult != null && setResult.getResult() == P2PConstants.SetResVal.RES_OK) {
                        ApiMgrV2.getSubDevList(did);
                    }
                }
            }

            @Override
            public void onStatusChanged(String did, int type, int status) {
                if (mAdapter == null || mAdapter.getData() == null)
                    return;
                int index = getDeviceIndex(did);
                Logger.t(TAG).i("-------------------> did: " + did + ", status: " + status + " , index: " + index);
                if (index != -1) {
                    mAdapter.getData().get(index).setStatus(status);

                    EdwinDevice dev = mAdapter.getData().get(index);
                    if (status == P2PConstants.P2PStatus.ON_LINE) {
                        dev.setUserPwdOK(true);
                        MyApp.getDaoSession().getEdwinDeviceDao().update(dev);

                        if (dev != null && dev.isIpc()) {
                            ApiMgrV2.getSubDevList(did);
                        }
                    } else if (status == P2PConstants.P2PStatus.ERR_USER_PWD) {
                        dev.setUserPwdOK(false);
                        MyApp.getDaoSession().getEdwinDeviceDao().update(dev);
                    }

                    //TODO----NICKY
                    if (status == P2PConstants.P2PStatus.ON_LINE) {
                        if (!SystemValue.isCallRunning) {
                            ApiMgrV2.setPush(did);
                        }

//                        ApiMgrV2.setSysTime(did, DateUtil.getCommTimeStr2(System.currentTimeMillis()) );

                        if (!dev.isTimeZoneOk()) {
                            TimeZone tz = TimeZone.getDefault();
                            ApiMgrV2.setTimeZone(did, tz.getRawOffset() / 60000);
                        }
//                      TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_SET_DEV_PUSH);
                    }

                    Message message = Message.obtain();
                    message.what = MSG_REFRESH_STATUS;
                    message.arg1 = index;
                    PPPPMsgHandler.sendMessage(message);
                }
            }

            @Override
            public void onGetBattery(String did, int msgType, Battery battery) {
                super.onGetBattery(did, msgType, battery);
            }
        };
    }

    private void cancelRefreshTask() {
        if (mRefreshTask != null) {
            mRefreshTask.cancel(true);
            mRefreshTask = null;
        }
    }

    public void executeRefreshTask() {
        ConnectivityManager cManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            cancelRefreshTask();
            mRefreshTask = new RefreshTask();
            mRefreshTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
        } else {
            SnackbarUtil.ShortSnackbar(snackBarContainer, getStringForFrag(R.string.Disconnected)).show();
        }
    }

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
        if (mAdapter == null)
            return;
//        Logger.t(TAG).e("receive event code : " + event.getEventCode() +" , desc: "+ Constants.getEventDesc(event.getEventCode()) );
        switch (event.getEventCode()) {

            case Constants.EdwinEventType.EVENT_DEV_ADD:
                Logger.t(TAG).w("-------------> EVENT_DEV_ADD");
                List<EdwinDevice> devList = (List<EdwinDevice>) event.getData();
                List<PushSet> pushSets = new ArrayList<>();
                if (devList != null && !devList.isEmpty()) {
                    for (final EdwinDevice device : devList) {
                        if (device != null && !mAdapter.getData().contains(device)) {
                            mAdapter.addData(device);

                            PushSet pushSet = new PushSet();
                            pushSet.setDevId(device.getDevId());
//                            pushSet.setJPushTagOK( false );
                            pushSet.setFcmTopicOK(false);
                            pushSet.setDevPushOk(false);
                            pushSet.setDeleted(false);
                            pushSets.add(pushSet);

                            TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_ADD_FCM_TOPIC, device.getDevId().replace("-", ""));

                        }
                    }

                    DataLogic.saveJpushTagOk(false);
                    try {
                        MyApp.getDaoSession().getPushSetDao().insertOrReplaceInTx(pushSets);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_SET_PUSH_TAG);
//                    TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_SET_DEV_PUSH);

                    if (BridgeService.isReady()) {
                        BridgeService.getInstance().connectDevices(devList);
                    }

                    mListener.onDevListSizeChanged(mAdapter.getData().isEmpty());
                }
                break;

            case Constants.EdwinEventType.EVENT_DEV_DELETE:
                EdwinDevice delDev = (EdwinDevice) event.getData();
                executeDelete(delDev);
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_NAME:
                EdwinDevice nameDev = (EdwinDevice) event.getData();
                if (nameDev != null) {
                    int index = mAdapter.getData().indexOf(nameDev);
                    if (index != -1) {
                        mAdapter.getData().get(index).setName(nameDev.getName());
                        mAdapter.notifyItemChanged(index);
                    }
                }
                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_BG:
                EdwinDevice bgDev = (EdwinDevice) event.getData();
                if (bgDev != null) {
                    int index = mAdapter.getData().indexOf(bgDev);
                    if (index != -1) {
                        mAdapter.getData().get(index).setBgPath(bgDev.getBgPath());
                        mAdapter.notifyItemChanged(index);
                    }
                }
                break;

//            case P2PConstants.P2PEventType.EVENT_DEV_UPDATE_PWD:
//                EdwinDevice pwdDev = (EdwinDevice) event.getData();
//                if (pwdDev != null) {
//                    int index = mAdapter.getData().indexOf(pwdDev);
//                    if (index != -1) {
//                        mAdapter.getData().get(index).setPwd(pwdDev.getPwd());
//                        mAdapter.notifyItemChanged(index);
//                        if (BridgeService.isReady()) {
//                            BridgeService.getInstance().refreshDevice(pwdDev);
//                        }
//                    }
//                }
//                break;

            case Constants.EdwinEventType.EVENT_DEV_UPDATE_USER_PWD:
                EdwinDevice editDev = (EdwinDevice) event.getData();
                if (editDev != null) {
                    int index = mAdapter.getData().indexOf(editDev);
                    if (index != -1) {
                        mAdapter.getData().get(index).setPwd(editDev.getPwd());
                        mAdapter.getData().get(index).setUser(editDev.getUser());
                        mAdapter.notifyItemChanged(index);
                        if (BridgeService.isReady()) {
                            BridgeService.getInstance().refreshDevice(editDev);
                        }
                    }
                }
                break;

            case Constants.EdwinEventType.EVENT_GOTO_BACKGROUND:
                if (BridgeService.isReady() && mAdapter != null) {
                    BridgeService.getInstance().stopDevices(mAdapter.getData());
                }
                break;

            case Constants.EdwinEventType.EVENT_GOTO_FOREGROUND:
                if (BridgeService.isReady() && mAdapter != null) {
                    BridgeService.getInstance().connectDevices(mAdapter.getData());
                }
                break;

            default:
                break;
        }
    }

    private void executeDelete(EdwinDevice data) {
        postEdwinEvent(Constants.EdwinEventType.EVENT_RECORD_UPDATE);//
        postEdwinEvent(Constants.EdwinEventType.EVENT_PHOTO_VIDEO_UPDATE);//

        if (data != null && mAdapter.getData() != null && !mAdapter.getData().isEmpty()) {
            int index = mAdapter.getData().indexOf(data);
            if (index >= 0) {
                mAdapter.remove(index);
            }

            CallAlarmUtil.getInstance().onDeviceDeleted(data.getDevId());

            List<PushSet> pushSets = MyApp.getDaoSession().getPushSetDao()
                    .queryBuilder().where(PushSetDao.Properties.DevId.eq(data.getDevId())).list();
            for (PushSet push : pushSets) {
                push.setDeleted(true);
            }
            MyApp.getDaoSession().getPushSetDao().updateInTx(pushSets);

            DataLogic.saveJpushTagOk(false);
            TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_SET_PUSH_TAG);
            TaskWorkServer.startService(getContext(), TaskWorkServer.ACTION_DEL_FCM_TOPIC, data.getDevId().replace("-", ""));

            if (BridgeService.isReady()) {
                BridgeService.getInstance().stopDevice(data.getDevId());
            }
            mListener.onDevListSizeChanged(mAdapter.getData().isEmpty());
        }
    }

    /**
     * 获取设备在列表中的下标
     */
    private int getDeviceIndex(String did) {
        int index = -1;
        int i = 0;
        for (EdwinDevice dev : mAdapter.getData()) {
            if (IdUtil.isSameId(did, dev.getDevId())) {
                index = i;
                break;
            }
            i++;
        }
        return index;
    }

    /**
     * 是否正在连接
     */
    private boolean isConnecting(EdwinDevice data) {
        ConnectivityManager cManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(getActivity().getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            int status = data.getStatus();
            if (P2PConstants.P2PStatus.CONNECTING == status) {
                SnackbarUtil.ShortSnackbar(snackBarContainer, getStringForFrag(R.string.hint_dev_connecting)).show();
                return true;
            }
        } else {
            SnackbarUtil.ShortSnackbar(snackBarContainer, getStringForFrag(R.string.Disconnected)).show();
        }

        return false;
    }


//    /** 获取设备 */
//    private EdwinDevice getDevice( String did ){
//        EdwinDevice device = null;
//        for (EdwinDevice dev : mDevList) {
//            if (dev.getDevId().equals(did)) {
//                device = dev;
//                break;
//            }
//        }
//        return device;
//    }

    private boolean checkLogin(EdwinDevice data) {

        mCurOpEdwinDevice = data;
        int status = data.getStatus();

        if (status == P2PConstants.P2PStatus.NOT_LOGIN || status == P2PConstants.P2PStatus.ERR_USER_PWD || StringUtils.isEmpty(data.getUser())) {
            //|| !data.isUserPwdOK()
//            final String devId = data.getDevId();
            LoginDialog dialog = new LoginDialog();
            dialog.setOkStr(getStringForFrag(R.string.str_ok)).setCancelStr(getStringForFrag(R.string.str_cancel));
            dialog.setMaxLength(20);
            dialog.setOnLoginClick(new LoginDialog.OnLoginClickListener() {

                @Override
                public void onLogin(String user, String pwd) {
                    if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(pwd)) {
                        int index = mAdapter.getData().indexOf(mCurOpEdwinDevice);
                        mCurOpEdwinDevice.setUser(user);
                        mCurOpEdwinDevice.setPwd(pwd);
                        if (pwd.startsWith("admin1") || pwd.startsWith("admin")) {
                            BaseApp.showToastLong("该密码为初始密码，请及时修改");
                        }
                        if (index != -1) {
                            mAdapter.notifyItemChanged(index);
                        }
                        MyApp.getDaoSession().getEdwinDeviceDao().update(mCurOpEdwinDevice);
                        if (BridgeService.isReady()) {
                            BridgeService.getInstance().refreshDevice(mCurOpEdwinDevice);
                        }
                    }
                }

                @Override
                public void onCancel() {
                }
            });
            dialog.show(getSupportFragmentManager(), "__login_dlg__");
            return false;
        }

        return true;
    }

    /**
     * empty view click
     */
    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastClick(getActivity(), view))
            return;
        switch (view.getId()) {
            case R.id.ly_add:
            case R.id.ly_add_lan:
            case R.id.ly_scan:
                view.setTag(Constants.KEY_TAG_CLICK);
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        if (ClickUtil.isFastClick(getActivity(), view))
            return;
        final EdwinDevice item = mAdapter.getItem(position);
        if (item == null) {
            return;
        }
        if (isConnecting(item)) {
            return;
        }
        if (!checkLogin(item)) {
            return;
        }
        if (!item.isOnline() && !item.isSleep()) {
            if (BridgeService.isReady()) {
                BridgeService.getInstance().refreshDevice(item);
            }
            return;
        }

        checkPermission(new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_CODE_FOR_AUDIO_RECORD,
                new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted() {
                        if (item.isSleep()) {
                            ThreadPoolMgr.getCustomThreadPool2().submit(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Avapi.Wake(item.getDevId());
                                        }
                                    }
                            );
                        }

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, item);
                        bundle.putBoolean(Constants.BundleKey.KEY_IS_MONITOR, true);
//                bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                        if (item.isIpc()) {
                            gotoActivity(IpcVideoAty.class, bundle);
                        } else {
                            gotoActivity(BellVideoAty.class, bundle);
                        }
//                gotoActivity(BellVideoAty.class, bundle);
//                gotoActivity(IpcVideoAty.class, bundle);

                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });
    }

    @Override
    public void onShareClick(EdwinDevice data, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, data);
        gotoActivity(DevQRCodeAty.class, bundle);
    }

    @Override
    public void onEditClick(EdwinDevice data, int position) {
        mCurOpEdwinDevice = data;
        LoginDialog dialog = new LoginDialog();
        dialog.setOkStr(getStringForFrag(R.string.str_ok)).setCancelStr(getStringForFrag(R.string.str_cancel));
        dialog.setMaxLength(20);
        dialog.setOnLoginClick(new LoginDialog.OnLoginClickListener() {
            @Override
            public void onLogin(String user, String pwd) {

                if (!StringUtils.isEmpty(user) && !StringUtils.isEmpty(pwd)) {
                    int index = mAdapter.getData().indexOf(mCurOpEdwinDevice);
                    mCurOpEdwinDevice.setUser(user);
                    mCurOpEdwinDevice.setPwd(pwd);
                    if (index != -1) {
                        mAdapter.notifyItemChanged(index);
                    }
                    MyApp.getDaoSession().getEdwinDeviceDao().update(mCurOpEdwinDevice);
                    if (BridgeService.isReady()) {
                        BridgeService.getInstance().refreshDevice(mCurOpEdwinDevice);
                    }
                }
            }

            @Override
            public void onCancel() {
            }
        });
        dialog.show(getSupportFragmentManager(), "__login_dlg__");
    }

    @Override
    public void onDeleteClick(final EdwinDevice data, int position) {
        if (data != null) {

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.str_delete)
                    .content(R.string.tips_del_dev)
                    .positiveText(R.string.str_ok)
                    .negativeText(R.string.str_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            MyApp.getDaoSession().getEdwinDeviceDao().delete(data);

                            PhotoVideoDao photoVideoDao = MyApp.getDaoSession().getPhotoVideoDao();
                            EdwinRecordDao recordDao = MyApp.getDaoSession().getEdwinRecordDao();
                            DeviceBgDao bgDao = MyApp.getDaoSession().getDeviceBgDao();
                            SubDeviceDao subDao = MyApp.getDaoSession().getSubDeviceDao();

                            photoVideoDao.queryBuilder().
                                    where(PhotoVideoDao.Properties.Did.eq(data.getId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();

                            recordDao.queryBuilder().
                                    where(EdwinRecordDao.Properties.Did.eq(data.getId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();

                            bgDao.queryBuilder().
                                    where(DeviceBgDao.Properties.Did.eq(data.getId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();

                            subDao.queryBuilder().
                                    where(SubDeviceDao.Properties.Pid.eq(data.getDevId()))
                                    .buildDelete().executeDeleteWithoutDetachingEntities();


                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();

                                    File devPhotoDir = new File(PhotoVideoUtil.getPhotoDirPath(false), data.getDevId());
                                    File devVideoDir = new File(PhotoVideoUtil.getVideoDirPath(), data.getDevId());
                                    if (devPhotoDir.exists()) {
                                        FileUtil.deleteFilesByDirectory(devPhotoDir);
                                        devPhotoDir.delete();
                                    }
                                    if (devVideoDir.exists()) {
                                        FileUtil.deleteFilesByDirectory(devVideoDir);
                                        devVideoDir.delete();
                                    }
                                }
                            }.start();

                            showLoadDialog(R.string.deleting, new EdwinTimeoutCallback(2000) {

                                @Override
                                public void onTimeOut() {

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideLoadDialog();
                                            executeDelete(data);
                                        }
                                    });
                                }
                            }, null);
                        }
                    }).show();

        }
    }

    @Override
    public void onSetClick(EdwinDevice data, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, data);
        bundle.putBoolean(Constants.BundleKey.KEY_DEV_ONLINE, data.isOnline());
        gotoActivity(SetDevAty.class, bundle);
    }

    @Override
    public void onFileClick(EdwinDevice data, int position) {
        if (isConnecting(data)) {
            return;
        }
        if (!checkLogin(data)) {
            return;
        }
        if (!data.isOnline()) {
            SnackbarUtil.ShortSnackbar(snackBarContainer, getStringForFrag(R.string.hint_dev_offline)).show();
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, data);
        bundle.putBoolean(Constants.BundleKey.KEY_DEV_ONLINE, data.isOnline());
        gotoActivity(ChooseDateAty.class, bundle);
    }

    /**
     * 设备列表个数发生改变 或者 设备刷新状态变化
     */
    public interface OnDeviceListener {

        void onDevListSizeChanged(boolean isEmpty);

        void onRefreshChanged(boolean isRefreshing);
    }

    private class ServiceWaitThread extends Thread {
        public void run() {
            while (!BridgeService.isReady()) {
                try {
                    sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException("waiting thread sleep() has been interrupted");
                }
            }
            PPPPMsgHandler.sendEmptyMessage(MSG_SERVER_READY);
            mThread = null;
        }
    }

    /**
     * 刷新任务
     */
    private class RefreshTask extends AsyncTask<Void, Void, Void> {

        boolean isSerReady = false;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mListener.onRefreshChanged(true);

            isSerReady = BridgeService.isReady();

            if (!isSerReady) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BridgeService.class);
                getActivity().startService(intent);
                sleep(500);
                BridgeService.addP2PAppCallBack(mP2pCallBack);
            }
            startTimeoutThread(new EdwinTimeoutCallback(10 * 1000) {
                @Override
                public void onTimeOut() {
                    cancelRefreshTask();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mListener.onRefreshChanged(false);

                        }
                    });
                }
            });
        }


        @Override
        protected Void doInBackground(Void... params) {
            if (isSerReady) {
                BridgeService.getInstance().restartP2P(false);
            }
            while (!BridgeService.isReady()) {//|| !BridgeService.getInstance().isP2pLibStartOK()
                sleep(30);
            }
            BridgeService.getInstance().refreshDevices(mAdapter.getData());
            sleep(200);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.notifyDataSetChanged();
            mListener.onRefreshChanged(false);
            super.onPostExecute(result);
        }
    }

//    @Override
//    public void onEditClick(EdwinDevice data) {
//        if( isConnecting(data) ){
//            return;
//        } if( !checkLogin(data) ){
//            return;
//        }
//        int status = data.getStatus();
//        if (status != Constants.DeviceStatus.PPPP_STATUS_ON_LINE) {
//            showToast(R.string.hint_dev_offline);
//            return;
//        }
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, data );
//        gotoActivity(ModifyPwdAty.class,bundle);
//
//    }


}
