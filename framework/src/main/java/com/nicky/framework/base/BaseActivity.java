package com.nicky.framework.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;

import com.nicky.framework.R;
import com.nicky.framework.component.AtyGotoComponent;
import com.nicky.framework.component.GotoComponent;
import com.nicky.framework.component.LoadingDlgComponent;
import com.nicky.framework.component.MyLoadingDlgComponent;
import com.nicky.framework.component.MyTimeOutComponent;
import com.nicky.framework.component.MyVaryViewComponent;
import com.nicky.framework.component.TimeOutComponent;
import com.nicky.framework.component.VaryViewComponent;
import com.nicky.framework.util.StatusBarUtil;
import com.nicky.framework.varyview.VaryViewHelperController;
import com.rl.commons.BaseApp;
import com.rl.commons.compatibility.Version;
import com.rl.commons.interf.DlgCancelCallback;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.log.XLog;
import com.rl.commons.utils.AppManager;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.FileUtil;
import com.rl.commons.utils.OSUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/9/8.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        View.OnClickListener, GotoComponent, VaryViewComponent, TimeOutComponent, LoadingDlgComponent { //,SnackbarEnable

    protected String TAG = getClass().getSimpleName();

    protected Bundle mSavedInstanceState;

//    private AtyDelegate mContextDelegate;


    private AtyGotoComponent mGotoComponent;
    private MyVaryViewComponent mVaryViewComponent;
    private MyTimeOutComponent mTimeOutComponent;
    private MyLoadingDlgComponent mLoadingDlgComponent;


    /**
     * Screen information
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    private boolean isPaused = false;

    /**
     * 传递过来的intent
     */
    protected Intent fromIntent;

    /**
     * (传递来的)准备数据是否正常
     */
    protected boolean prepareOk = true;

    /**
     * 手机 Rom 类型
     */
    private OSUtil.ROM_TYPE mRomType = OSUtil.ROM_TYPE.OTHER;


    /************************************************************************/

    /**
     * 界面layout ID
     */
    protected abstract int getLayoutId();

    /**
     * 是否启用淡色状态栏(电池文字为黑色 背景为淡色底)
     */
    protected boolean useLightBarStatus() {
        return false;
    }

    /**
     * 4.4以上 6.0以下设置状态栏颜色
     */
    protected int getBarStatusColor(){ return R.color.colorPrimaryDark;   }

    /**
     * 初始化绑定View 工具(如:ButterKnife)
     */
    protected abstract void initBind();

    /**
     * 解绑 工具(如:ButterKnife)
     */
    protected abstract void unBind();


    /**
     * 是否在该页面注册 EventBus事件
     */
    protected boolean isBindEventBusHere() {
        return false;
    }

    /**
     * 是否忽略该页面自身发出来的 EventBus事件
     */
    protected boolean ignoreEventSelf() {
        return true;
    }

    /**
     * register EventBus
     */
    protected abstract void registerEventBus();

    /**
     * unregister EventBus
     */
    protected abstract void unregisterEventBus();


    /**
     * get loading target view(加载中,空数据,错误数据显示处---被替换的View)
     */
    protected View getVaryTargetView() {
        return null;
    }

    /**
     * 初始化准备数据,返回准备结果(是否数据正常)
     */
    protected boolean initPrepareData() {
        return true;
    }

    /**
     * 初始化标题栏
     */
    protected abstract void initToolBar();


    /**
     * initViewsAndEvents之前的初始化工作
     */
    protected void prepareInitView(Bundle savedInstanceState){}

    /**
     * 初始化View和事件
     */
    protected abstract void initViewsAndEvents();

    /**
     * 点击事件
     */
    protected abstract void onClickView(View v);


    /************************************************************************/

    private static BaseActivity runningActivity;

    public static BaseActivity getRunningActivity() {
        return runningActivity;
    }

    public static void setRunningActivity(BaseActivity activity) {
        runningActivity = activity;
    }

    protected Class<?> getXClass() {
        return getClass();
    }

    protected BaseActivity getActivity() {
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT  && Build.VERSION.SDK_INT<Build.VERSION_CODES.M  ) {
//            StatusBarUtil.transparencyBar(this);
//        }

        mGotoComponent = new AtyGotoComponent(this);
        mLoadingDlgComponent = new MyLoadingDlgComponent(this);
        mTimeOutComponent = new MyTimeOutComponent();

        mSavedInstanceState = savedInstanceState;
        AppManager.getInstance().addActivity(this);
        if (isBindEventBusHere()) {
            registerEventBus();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;

        mRomType = OSUtil.getRomType();
        int layoutId = getLayoutId();
        if (layoutId != 0) {

            fromIntent = getIntent();
            prepareOk = initPrepareData();

            /** 6.0以下 除小米魅族外 其它手机淡色状态栏效果不好,故而将主题更改  */
            if ( useLightBarStatus() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {

                switch (mRomType) {
                    case EMUI:
                    case OTHER:
                        setTheme(R.style.AppTheme_NoActionBar_NoTrans_Light);
                        break;
                }
            }

            setContentView(layoutId);

            initBind();
            if (getVaryTargetView() != null) {
                mVaryViewComponent = new MyVaryViewComponent(getVaryTargetView());
            }



            /** 设置成浅色状态栏(6.0以下 只有小米魅族可以设置) */
            setStatusBar( useLightBarStatus(),getBarStatusColor()  );

            if (prepareOk) {
                prepareInitView(savedInstanceState);
                initToolBar();
                initViewsAndEvents();
            } else if (getVaryTargetView() != null) {
                showError(getString(R.string.err_data), null);
            } else {
                XLog.e(this, getString(R.string.err_data) + " , VaryTargetView 未设置");
            }
        } else {
            XLog.e(this, getClass().getSimpleName() + " 页面 layout id 未设置");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        fromIntent = getIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadDialog();
        AppManager.getInstance().removeActivity(this);
        if (isBindEventBusHere()) {
            unregisterEventBus();
        }
        unBind();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRunningActivity(this);
        ClickUtil.init(this); //快速点击置0
        BaseApp myApp = (BaseApp) this.getApplication();
        if (myApp.wasInBackground) {
            //Do specific came-here-from-background code
        }
        myApp.stopActivityTransitionTimer();
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((BaseApp) this.getApplication()).startActivityTransitionTimer();
        isPaused = true;
    }

    protected boolean isPaused() {
        return isPaused;
    }


    protected void setStatusBar(  boolean useLightBarStatus, int colorId ){
        Window window = getWindow();
        /** 设置成浅色状态栏(6.0以下 只有小米魅族可以设置, 因此其它的改为黑色 ) */
        if ( useLightBarStatus ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                switch (mRomType) {
                    case MIUI:
                        XLog.v(TAG, "MIUI");
                        StatusBarUtil.MIUISetStatusBarLightMode(this, true);
//                        if (  Build.VERSION.SDK_INT<Build.VERSION_CODES.M  ) {
//                            StatusBarUtil.setStatusBarColor(this,colorId);
//                        }
                        break;
                    case FLYME:
                        XLog.v(TAG, "FLYME");
                        StatusBarUtil.FlymeSetStatusBarLightMode(window, true);
//                        if (  Build.VERSION.SDK_INT<Build.VERSION_CODES.M  ) {
//                            StatusBarUtil.setStatusBarColor(this,colorId);
//                        }
                        break;
                    case EMUI:
                    case OTHER:
                        // 上面已设置成light主题--此处无需处理
                        XLog.v(TAG, "OTHER");
                        break;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

        }else{
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
//                    && Build.VERSION.SDK_INT<Build.VERSION_CODES.M  ) {
//                StatusBarUtil.setStatusBarColor(this,colorId);
//            }
        }
    }


    protected void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /************************************ 点击事件处理 ***********************************/

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastClick(getActivity(), v))
            return;
        onClickView(v);
    }

    /************************************ 权限处理 ***********************************/

    private SparseArray<PermissionResultCallback> requestCodePermissionMap = new SparseArray<>();

    private static final int REQUEST_CODE_WRITE_SETTINGS = 5858;

    /**
     * 检查权限，并处理
     */
    public void checkPermission(@NonNull String[] permissions, int requestCode, @NonNull PermissionResultCallback callback) {
        if (permissions == null)
            return;
//        boolean needTipsDlg = false;
//        for ( String permission:permissions ){
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
//                needTipsDlg = true;
//                break;
//            }
//        }
        requestCodePermissionMap.put(requestCode, callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getNotGrantedPermissions(permissions) != null) {
                requestPermissions(permissions, requestCode);
//                ActivityCompat.requestPermissions(this, permissions, requestCode);
            } else {
                callback.onPermissionGranted();
            }
        } else {
            callback.onPermissionGranted();
        }
    }


    /**
     * 获取当中未授权的权限
     *
     * @param permissions
     * @return
     */
    private String[] getNotGrantedPermissions(@NonNull String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String permission : permissions) {
            boolean permissionGranted;
            if (Version.getTargetSdkVer(getActivity()) >= Version.API23_M) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                permissionGranted = ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                permissionGranted = PermissionChecker.checkSelfPermission(getActivity(), permission) == PermissionChecker.PERMISSION_GRANTED;
            }
            if (!permissionGranted) {
                list.add(permission);
            }
        }
        if (list != null && !list.isEmpty()) {
            String[] strArr = new String[list.size()];
            list.toArray(strArr);
            return strArr;
        } else
            return null;
    }

    private boolean isAllPermissionsGranted(int[] grantResults) {
        if (grantResults == null || grantResults.length == 0)
            return false;
        for (int res : grantResults) {
            if (res != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionResultCallback callback = requestCodePermissionMap.get(requestCode);
        if (callback != null) {
            if (isAllPermissionsGranted(grantResults)) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    /**
     * 系统设置权限需要特殊处理
     */
    public void requestWriteSettings(@NonNull PermissionResultCallback callback) {

        requestCodePermissionMap.put(REQUEST_CODE_WRITE_SETTINGS, callback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
        } else {
            callback.onPermissionGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            PermissionResultCallback callback = requestCodePermissionMap.get(requestCode);
            if (Settings.System.canWrite(this)) {
                if (callback != null) {
                    callback.onPermissionGranted();
                }
            } else {
                callback.onPermissionDenied();
            }
        }
    }


    /*********************************************************网络处理*********************************************************/
    /**
     * 判断网络是否连接
     */
    public boolean isNetAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }


    /*********************************************************文件操作*********************************************************/
    /**
     * 删除文件夹
     */
    protected void deleteDirFiles(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            return;
        }
        FileUtil.deleteFilesByDirectory(dirFile);
    }

    /**
     * 删除文件
     */
    protected boolean deleteNorFile(String filePath) {
        File delFile = new File(filePath);
        if (delFile.exists()) {
            return delFile.delete();
        }
        return true;
    }

    /*********************************************************其它操作*********************************************************/

    protected View.OnLongClickListener mMenuItemLongClickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View view) {
            return true;
        }
    };

    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


    /******************************************************************************************************************/
    @Override
    public void startTimeoutThread(EdwinTimeoutCallback callback) {
        mTimeOutComponent.startTimeoutThread(callback);
    }

    @Override
    public void clearTimeoutThread() {
        mTimeOutComponent.clearTimeoutThread();
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz) {
        mGotoComponent.gotoActivity(clz);
    }

    @Override
    public void gotoActivity(Class<? extends Activity> clz, Bundle data) {
        mGotoComponent.gotoActivity(clz, data);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode) {
        mGotoComponent.gotoActivityForResult(clz, requestCode);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode, Bundle data) {
        mGotoComponent.gotoActivityForResult(clz, requestCode, data);
    }

    @Override
    public void showLoading() {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showLoading();
    }

    @Override
    public void showLoading(int resId) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showLoading(resId);
    }

    @Override
    public void showLoading(String msg) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showLoading(msg);
    }

    @Override
    public void showEmpty(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showEmpty(msg, onClickListener);
    }

    @Override
    public void showEmpty(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showEmpty(msg, iconId, onClickListener);
    }

    @Override
    public void showError(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showError(msg, onClickListener);
    }

    @Override
    public void showError(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if (mVaryViewComponent != null)
            mVaryViewComponent.showError(msg, iconId, onClickListener);
    }

    @Override
    public void hideMsgView() {
        if (mVaryViewComponent != null)
            mVaryViewComponent.hideMsgView();
    }

    @Override
    public LoadingDialog showLoadDialog(EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return mLoadingDlgComponent.showLoadDialog(timeoutCallback, cancelCallback);
    }

    @Override
    public LoadingDialog showLoadDialog(int resId, EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return mLoadingDlgComponent.showLoadDialog(resId, timeoutCallback, cancelCallback);
    }

    @Override
    public LoadingDialog showLoadDialog(String text, EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return mLoadingDlgComponent.showLoadDialog(text, timeoutCallback, cancelCallback);
    }

    @Override
    public void hideLoadDialog() {
        mLoadingDlgComponent.hideLoadDialog();
    }

    @Override
    public void setLoadingTips(String text) {
        mLoadingDlgComponent.setLoadingTips(text);
    }

}
