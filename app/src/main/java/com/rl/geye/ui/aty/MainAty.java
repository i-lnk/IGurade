package com.rl.geye.ui.aty;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.nicky.framework.widget.XViewPager;
import com.orhanobut.logger.Logger;
import com.rl.commons.BaseApp;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.utils.AppDevice;
import com.rl.commons.utils.FileUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.commons.utils.SysCategory;
import com.rl.geye.BuildConfig;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.adapter.MyFragPageAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.AppVersionInfo;
import com.rl.geye.constants.SystemValue;
import com.rl.geye.db.bean.DeviceBg;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.net.NetUrl;
import com.rl.geye.receiver.INetChangeListener;
import com.rl.geye.receiver.NetworkChangedReceiver;
import com.rl.geye.service.TaskWorkServer;
import com.rl.geye.ui.frag.HomeDevFrag;
import com.rl.geye.ui.frag.HomeMsgFrag;
import com.rl.geye.ui.frag.HomeRecordFrag;
import com.rl.geye.ui.frag.HomeSetFrag;
import com.rl.geye.ui.frag.TabPhotoFrag;
import com.rl.geye.ui.frag.TabVideoFrag;
import com.rl.geye.util.PermissionUtil;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.p2plib.utils.JSONUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainAty extends BaseMyAty implements
        HomeDevFrag.OnDeviceListener,
        HomeRecordFrag.OnRecordListener,
        TabVideoFrag.OnVideoListener,
        TabPhotoFrag.OnPhotoListener,
        HomeMsgFrag.OnMsgListener,
        INetChangeListener {

    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;
    private final static int TAB_DEV = 0;
    private final static int TAB_RECORD = 1;
    private final static int TAB_MSG = 2;
    private final static int TAB_SET = 3;
    public static boolean isSetNotice = true;
    public static MainAty instance;
    @BindView(R.id.vp_main)
    XViewPager vpMain;
    @BindView(R.id.tv_dev)
    TextView tvDev;
    @BindView(R.id.tv_record)
    TextView tvRecord;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_set)
    TextView tvSet;
    @BindView(R.id.tab_dev)
    View tabDev;
    @BindView(R.id.tab_record)
    View tabRecord;
    @BindView(R.id.tab_msg)
    View tabMsg;
    @BindView(R.id.tab_set)
    View tabSet;
    @BindView(R.id.ly_top_dev)
    FrameLayout lyTopDev;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ic_refresh)
    ImageView icRefresh;
    @BindView(R.id.loading_progress)
    CircularProgressBar loadingProgress;
    @BindView(R.id.ic_add)
    ImageView icAdd;
    @BindView(R.id.ly_top_record)
    FrameLayout lyTopRecord;
    @BindView(R.id.tv_photo)
    TextView tvPhoto;
    @BindView(R.id.tv_video)
    TextView tvVideo;
    @BindView(R.id.ly_photo_del)
    View lyDelPhoto;
    @BindView(R.id.ly_video_del)
    View lyDelVideo;
    @BindView(R.id.ic_del_photo)
    ImageView icDelPhoto;
    @BindView(R.id.ic_del_video)
    ImageView icDelVideo;
    @BindView(R.id.ly_top_msg)
    FrameLayout lyTopMsg;
    @BindView(R.id.ic_del_msg)
    ImageView icDelMsg;
    @BindView(R.id.ly_top_set)
    FrameLayout lyTopSet;
    private Context mContext;
    private HomeDevFrag fragDev;
    private HomeRecordFrag fragRecord;
    private HomeMsgFrag fragMsg;
    private HomeSetFrag fragSetting;
    private MyFragPageAdapter mAdapter;
    private List<View> mTabs = new ArrayList<>();
    private List<View> mTopTabs = new ArrayList<>();
    private List<PhotoVideo> mPhotoVideoList = new ArrayList<>();
    private List<DeviceBg> mBGList = new ArrayList<>();
    private AsyncTask<Void, Void, Void> syncTask;
    //    private JPluginPlatformInterface pHuaweiPushInterface;
    private SharedPreferences sp;
    private boolean isFisrtt = false;

    /**
     * 是否已启动
     */
    public static boolean isReady() {
        return instance != null;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.aty_main;
    }

    @Override
    protected void initToolBar() {

        toolbar.setNavigationIcon(null);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

    }

    @Override
    protected void initViewsAndEvents() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }

        vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position);
                selectTopTab(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mContext = this;
        tabDev.setOnClickListener(this);
        tabRecord.setOnClickListener(this);
        tabMsg.setOnClickListener(this);
        tabSet.setOnClickListener(this);

        icRefresh.setOnClickListener(this);
        icAdd.setOnClickListener(this);

        icDelPhoto.setOnClickListener(this);
        icDelVideo.setOnClickListener(this);
        icDelMsg.setOnClickListener(this);

        tvPhoto.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        //忽略电池优化
        if (SysCategory.SYS_EMUI.equals(SysCategory.getSystem()) ||
                SysCategory.SYS_MIUI.equals(SysCategory.getSystem()) ||
                SysCategory.SYS_FLYME.equals(SysCategory.getSystem())) {
            ignoreBatteryOptimization(MainAty.this);
        }
        if (!PermissionUtil.checkAreNotificationEnabled(getActivity())){
            notificationEnable();
        }
//        pHuaweiPushInterface = new JPluginPlatformInterface(this.getApplicationContext());
        initPhotoVideoDir();
        syncPhotoVideo();
        onDataReady();
        sp = getSharedPreferences("firstStart", MODE_PRIVATE);
        isFisrtt = sp.getBoolean("showDialog", true);
        if (isFisrtt) {
            sp.edit().putBoolean("showDialog", false).commit();
            final MaterialDialog dialog = new MaterialDialog.Builder(MainAty.this)
                    .customView(R.layout.custome_view, false)
                    .show();
            View customeView = dialog.getCustomView();
            Button button = (Button) customeView.findViewById(R.id.positiveButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        Map<String, String> params = new HashMap<>();
        params.put("appname", "Iguarder");
        OkGo.<String>get(NetUrl.checkAppUpdate()).tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
//                        Logger.w("----------->responseData:"+s);
                        if (!StringUtils.isEmpty(s)) {
                            try {
                                String json = s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1);
//                                JSONObject jsonObj = new JSONObject(json);
//                                if(jsonObj!=null){
//                                    JSONObject androidObj = jsonObj.getJSONObject("android");
//                                    if(androidObj!=null){
//                                        int update =  androidObj.getInt("update");
//                                        String version = androidObj.getString("version");
//                                    }
//                                }
//                                AppVersionInfo mSysVersion = new Gson().fromJson(s,AppVersionInfo.class);
                                final AppVersionInfo mSysVersion = JSONUtil.fromJson(json, AppVersionInfo.class);
                                if (mSysVersion != null && (mSysVersion.getAndroid().getUpdate() > 0
                                        || mSysVersion.getAndroid().getVersion().compareTo(AppDevice.getVersionName()) > 0)) {
                                    MyApp.IS_NEW_VERSION = false;
                                    MyApp.url = mSysVersion.getAndroid().getUrl();
                                    new MaterialDialog.Builder(getActivity())
                                            .title(R.string.version_check)
                                            .content(R.string.version_find)
                                            .positiveText(R.string.update)
                                            .negativeText(R.string.str_cancel)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//
//                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                                    intent.setData( Uri.parse( mSysVersion.getAndroid().getUrl() ) );
//                                                    startActivity(intent);

                                                    downloadApk(MyApp.url);
                                                    //TODO
                                                }
                                            }).show();


//                                    mHandler.sendEmptyMessage( R.id.msg_update_sysinfo  );
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });


    }

    @Override
    protected void onClickView(View v) {

        switch (v.getId()) {
            case R.id.tab_dev:
                vpMain.setCurrentItem(TAB_DEV);
                break;
            case R.id.tab_record:
                vpMain.setCurrentItem(TAB_RECORD);
                break;
            case R.id.tab_msg:
                vpMain.setCurrentItem(TAB_MSG);
                break;
            case R.id.tab_set:
                vpMain.setCurrentItem(TAB_SET);
                break;

            case R.id.ic_refresh:
                if (fragDev != null)
                    fragDev.executeRefreshTask();
                break;
            case R.id.ic_add:
                gotoActivity(ChooseAddModeAty.class);
                break;

            case R.id.tv_photo:
                if (fragRecord != null) {
                    fragRecord.selectTabPhoto();
                }
                break;

            case R.id.tv_video:
                if (fragRecord != null) {
                    fragRecord.selectTabVideo();
                }
                break;

            case R.id.ic_del_photo:
                if (fragRecord != null) {
                    fragRecord.deletePhoto();
                }
                break;

            case R.id.ic_del_video:
                if (fragRecord != null) {
                    fragRecord.deleteVideo();
                }
                break;

            case R.id.ic_del_msg:
                if (fragMsg != null) {
                    fragMsg.gotoDelete();
                }
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Log.e("ON BACK DOWN","USER CLICK THE BACK KEY");
//        moveTaskToBack(true);
        AlertDialog.Builder exitDlg = new AlertDialog.Builder(this);
        exitDlg.setTitle(R.string.str_exit);
        exitDlg.setMessage(R.string.tips_exit_app);
        exitDlg.setPositiveButton(R.string.str_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       MainAty.super.onBackPressed();
                    }
                });
        exitDlg.setNegativeButton(R.string.str_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        exitDlg.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        //引用当前Activity,并且如果HMS SDK没有初始化则会初始化HMS SDK.
//        pHuaweiPushInterface.onStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //清空对当前Activity的引用.
//        pHuaweiPushInterface.onStop(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //经过用户操作解决完错误之后,返回让HMS重新初始化..JPush中调用HMS SDK
        //解决错误的接口传入的requestCode为10001,开发者调用时请注意不要同样
        //使用10001
//        if (requestCode == JPluginPlatformInterface.JPLUGIN_REQUEST_CODE) {
//            pHuaweiPushInterface.onActivityResult(this, requestCode, resultCode, data);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkChangedReceiver.removeRegisterObserver(this);
        instance = null;
//        try {
//            unbindService(mServiceConnection);
////            Intent intent = new Intent();
////            intent.setClass(getActivity(), BridgeService.class);
////            getActivity().stopService(intent);
////            AppManager.getInstance().AppExit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            XLog.e( TAG, e.toString() );
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNetObserver();
        SystemValue.appIsStarting = false;
        instance = this;
        isSetNotice = true;
//        String rid = MiPushClient.getRegId(getApplicationContext());
        String register = JPushInterface.getRegistrationID(getApplicationContext());
        Logger.w("register=" + register);
//        Logger.w("验证小米推送是否成功:rid=" + rid);
//        Toast.makeText(MainAty.this,"rid="+rid,Toast.LENGTH_SHORT).show();
    }

    private void initNetObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            connectivityManager.requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    if (!JPushInterface.getConnectionState(MainAty.this) || JPushInterface.isPushStopped(MainAty.this)) {
                        if (!JPushInterface.isPushStopped(MainAty.this))
                            JPushInterface.stopPush(MainAty.this);
                        JPushInterface.resumePush(MainAty.this);
                    } else {
                        TaskWorkServer.startService(MainAty.this, TaskWorkServer.ACTION_SET_PUSH_TAG);
                    }
                }
            });
        } else {
            NetworkChangedReceiver.registerObserver(this);
        }
    }

    private void onDataReady() {
        String name1 = makeFragmentName(vpMain.getId(), TAB_DEV);
        String name2 = makeFragmentName(vpMain.getId(), TAB_RECORD);
        String name3 = makeFragmentName(vpMain.getId(), TAB_MSG);
        String name4 = makeFragmentName(vpMain.getId(), TAB_SET);

        FragmentManager fm = getSupportFragmentManager();
        fragDev = (HomeDevFrag) fm.findFragmentByTag(name1);
        fragRecord = (HomeRecordFrag) fm.findFragmentByTag(name2);
        fragMsg = (HomeMsgFrag) fm.findFragmentByTag(name3);
        fragSetting = (HomeSetFrag) fm.findFragmentByTag(name4);

        if (fragDev == null) {
            fragDev = new HomeDevFrag();
        }
        if (fragRecord == null) {
            fragRecord = new HomeRecordFrag();
        }
        if (fragMsg == null) {
            fragMsg = new HomeMsgFrag();
        }
        if (fragSetting == null)
            fragSetting = new HomeSetFrag();

        Fragment[] frags = new Fragment[]{fragDev, fragRecord, fragMsg, fragSetting};

        CharSequence[] titles = new CharSequence[]{getString(R.string.title_device),
                getString(R.string.str_record),
                getString(R.string.str_message),
                getString(R.string.str_settings)};

        mAdapter = new MyFragPageAdapter(getSupportFragmentManager(), frags, titles);

        mTabs.clear();
        mTabs.add(tabDev);
        mTabs.add(tabRecord);
        mTabs.add(tabMsg);
        mTabs.add(tabSet);

        mTopTabs.clear();
        mTopTabs.add(lyTopDev);
        mTopTabs.add(lyTopRecord);
        mTopTabs.add(lyTopMsg);
        mTopTabs.add(lyTopSet);


        vpMain.setAdapter(mAdapter);
        vpMain.setOffscreenPageLimit(4);
        vpMain.setCurrentItem(0);
        vpMain.setEnableScroll(false);
        selectTab(TAB_DEV);
        selectTopTab(TAB_DEV);
    }


    /**
     * 首页底部标签选择
     */
    private void selectTab(int position) {
        if (mTabs == null || mTabs.size() <= position)
            return;
        for (View tab : mTabs) {
            tab.setSelected(false);
        }
        mTabs.get(position).setSelected(true);
    }

    /**
     * 首页toolbar切换
     */
    private void selectTopTab(int position) {
        if (mTopTabs == null || mTopTabs.size() <= position)
            return;
        for (View tab : mTopTabs) {
            tab.setVisibility(View.GONE);
        }
        mTopTabs.get(position).setVisibility(View.VISIBLE);
    }


//    /**
//     * 用于Fragment 控制控件显示隐藏
//     */
//    public void setViewVisibility(@IdRes int id, int visibility) {
//        try {
//            findViewById(id).setVisibility(visibility);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 初始化图片视频文件夹路径
     */
    private void initPhotoVideoDir() {
        File picDir = PhotoVideoUtil.getPhotoDir(false);
        if (!picDir.exists()) {
            picDir.mkdirs();
        }
        File croppedDir = PhotoVideoUtil.getPhotoDir(true);
        if (!croppedDir.exists()) {
            croppedDir.mkdirs();
        }
        File videoDir = PhotoVideoUtil.getVideoDir();
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }

    }

    /**
     * 设备列表toolbar变化
     */
    @Override
    public void onDevListSizeChanged(boolean isEmpty) {
        if (isEmpty) {
            icAdd.setVisibility(View.GONE);
            icRefresh.setVisibility(View.GONE);
        } else {
            icAdd.setVisibility(View.VISIBLE);
            icRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefreshChanged(boolean isRefreshing) {
        if (isRefreshing) {
            loadingProgress.setVisibility(View.VISIBLE);
            icRefresh.setVisibility(View.GONE);
        } else {
            loadingProgress.setVisibility(View.GONE);
            icRefresh.setVisibility(View.VISIBLE);
        }
    }


    /**  设备列表toolbar变化 end */

    /**
     * 图片视频列表toolbar变化
     */
    @Override
    public void onRecordTabChanged(boolean isPhotoSelected) {
        if (isPhotoSelected) {
            tvPhoto.setSelected(true);
            tvVideo.setSelected(false);
            lyDelPhoto.setVisibility(View.VISIBLE);
            lyDelVideo.setVisibility(View.GONE);
        } else {
            tvPhoto.setSelected(false);
            tvVideo.setSelected(true);
            lyDelPhoto.setVisibility(View.GONE);
            lyDelVideo.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 消息列表toolbar变化
     */
    @Override
    public void onMsgListSizeChanged(boolean isEmpty) {
        if (isEmpty) {
            icDelMsg.setVisibility(View.GONE);
        } else {
            icDelMsg.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 列表toolbar变化
     */
    @Override
    public void onVideoListSizeChanged(boolean isEmpty) {
        if (isEmpty) {
            icDelVideo.setVisibility(View.GONE);
        } else {
            icDelVideo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPhotoListSizeChanged(boolean isEmpty) {
        if (isEmpty) {
            icDelPhoto.setVisibility(View.GONE);
        } else {
            icDelPhoto.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 本地图片视频 与 数据库比对并同步
     */
    private void cancelSync() {
        if (syncTask != null) {
            syncTask.cancel(true);
            syncTask = null;
        }
    }

    /**
     * 本地图片视频 与 数据库比对并同步
     */
    private synchronized void syncPhotoVideo() {
        cancelSync();
        syncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoadDialog(R.string.synchronizing_data, null, null);
            }

            @Override
            protected Void doInBackground(Void... params) {

                boolean isAppInstalled = DataLogic.isAppInstalled();

                if (isAppInstalled) {
                    mPhotoVideoList.clear();
                    mPhotoVideoList.addAll(MyApp.getDaoSession().getPhotoVideoDao().loadAll());
                    mBGList.clear();
                    mBGList.addAll(MyApp.getDaoSession().getDeviceBgDao().loadAll());
                    syncAllPhotos();
                    syncAllVideos();
                } else {
                    DataLogic.saveAppInstalled(true);
                    File picDir = PhotoVideoUtil.getPhotoDir(false);
                    File croppedDir = PhotoVideoUtil.getPhotoDir(true);
                    File videoDir = PhotoVideoUtil.getVideoDir();
                    FileUtil.deleteFilesByDirectory(picDir);
                    FileUtil.deleteFilesByDirectory(croppedDir);
                    FileUtil.deleteFilesByDirectory(videoDir);
                }


                MediaScannerConnection.scanFile(getActivity(), new String[]{
                                PhotoVideoUtil.getPhotoDirPath(false), PhotoVideoUtil.getVideoDirPath()}, null
                        , new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
//                                onDataReady();
                            }
                        }
                );

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hideLoadDialog();
//                ImageUtil.scanDirAsync( getActivity(),PhotoVideoUtil.getPhotoDirPath(false) );
//                ImageUtil.scanDirAsync( getActivity(),PhotoVideoUtil.getVideoDirPath() );
                //TODO
//                postEdwinEvent( );
            }
        };
        syncTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());

    }

    private void syncAllPhotos() {
        File picDir = PhotoVideoUtil.getPhotoDir(false);
        if (!picDir.exists()) {
            picDir.mkdirs();
        } else {
            syncFile(picDir, true);
        }
    }

    private void syncAllVideos() {
        File videoDir = PhotoVideoUtil.getVideoDir();
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        } else {
            syncFile(videoDir, false);
        }
    }


    private void syncFile(File file, final boolean isPhoto) {
        file.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    syncFile(file, isPhoto);
                } else {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    int i = name.lastIndexOf(".");
                    if (i != -1) {
                        name = name.substring(i);
                        boolean isValidFile;
                        if (isPhoto) {
                            isValidFile = name.equalsIgnoreCase(".jpg") || name.equalsIgnoreCase(".jpeg");
                        } else {
                            isValidFile = name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".avi");
                        }

                        if (isValidFile) {
                            int index = mPhotoVideoList.indexOf(new PhotoVideo(path));
                            if (index != -1) {
                                mPhotoVideoList.remove(index);
                            } else {
                                int index2 = mBGList.indexOf(new DeviceBg(path));
                                if (index2 != -1) {
                                    mBGList.remove(index2);
                                } else {
                                    file.delete();
                                }
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }


    /**
     * 从服务器端下载最新apk
     */
    private void downloadApk(String url) {
        //显示下载进度
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        //访问网络下载apk
        new Thread(new DownloadApk(url, dialog)).start();
    }


    @Override
    public void onNetChanged(boolean isConnected) {
        if (isConnected) {
            Logger.t(TAG).w("JPUSH onNetChanged ");
            if (!JPushInterface.getConnectionState(MainAty.this) || JPushInterface.isPushStopped(MainAty.this)) {
                if (!JPushInterface.isPushStopped(MainAty.this))
                    JPushInterface.stopPush(MainAty.this);
                JPushInterface.resumePush(MainAty.this);
            } else {
                TaskWorkServer.startService(MainAty.this, TaskWorkServer.ACTION_SET_PUSH_TAG);
            }
        }
    }

    /**
     * 下载完成,提示用户安装
     */
    private void installApk(File file) {
        //调用系统安装程序
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        } else {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

        }

        mContext.startActivity(intent);
    }

    //    http://blog.csdn.net/wxz1179503422/article/details/56671609
    public void ignoreBatteryOptimization(Activity activity) {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        boolean hasIgnored = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(activity.getPackageName());
        } else {
            hasIgnored = true;
        }
        if (!hasIgnored) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            if (intent != null) {
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                startActivity(intent);
            }
        }
    }

    /**
     * 通知栏激活
     */
    protected void notificationEnable() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.permission_notification_bar)
                .content(R.string.permission_notification_msg)
                .positiveText(R.string.permission_title_rationale)
                .negativeText(R.string.str_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PermissionUtil.mayRequestAllPermissionSetting(MainAty.this);
                    }
                }).show();
    }

    /**
     * 访问网络下载apk
     */
    private class DownloadApk implements Runnable {

        InputStream is;
        FileOutputStream fos;
        private ProgressDialog dialog;
        private String url;

        public DownloadApk(String url, ProgressDialog dialog) {
            this.dialog = dialog;
            this.url = url.replaceAll(" ", "");
        }

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder().get().url(url).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    Log.d("开始下载apk的url=", url);
                    //获取内容总长度
                    long contentLength = response.body().contentLength();
                    //设置最大值
                    dialog.setMax((int) contentLength);
                    //保存到sd卡
                    File apkFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".apk");
                    fos = new FileOutputStream(apkFile);
                    //获得输入流
                    is = response.body().byteStream();
                    //定义缓冲区大小
                    byte[] bys = new byte[1024];
                    int progress = 0;
                    int len = -1;
                    while ((len = is.read(bys)) != -1) {
                        try {
                            Thread.sleep(1);
                            fos.write(bys, 0, len);
                            fos.flush();
                            progress += len;
                            //设置进度
                            dialog.setProgress(progress);
                        } catch (InterruptedException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BaseApp.showToast("Error:10002");
                                }
                            });
                        }
                    }
                    //下载完成,提示用户安装
                    installApk(apkFile);
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseApp.showToast("Error:10003");
                    }
                });
            } finally {
                //关闭io流
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is = null;
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos = null;
                }
            }
            dialog.dismiss();
        }
    }

}
