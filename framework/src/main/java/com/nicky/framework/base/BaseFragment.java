package com.nicky.framework.base;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nicky.framework.component.FragGotoComponent;
import com.nicky.framework.component.GotoComponent;
import com.nicky.framework.component.LoadingDlgComponent;
import com.nicky.framework.component.MyLoadingDlgComponent;
import com.nicky.framework.component.MyTimeOutComponent;
import com.nicky.framework.component.MyVaryViewComponent;
import com.nicky.framework.component.TimeOutComponent;
import com.nicky.framework.component.VaryViewComponent;
import com.nicky.framework.varyview.VaryViewHelperController;
import com.rl.commons.BaseApp;
import com.rl.commons.compatibility.Version;
import com.rl.commons.interf.DlgCancelCallback;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.log.XLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/9/8.
 */
public abstract class BaseFragment extends Fragment implements GotoComponent,VaryViewComponent,TimeOutComponent,LoadingDlgComponent {
    //implements SnackbarEnable

    protected  String TAG = getClass().getSimpleName();

    protected View mView = null;

    private boolean isVisible = true;
    private boolean isPrepared;
    private boolean needRefresh = true;
    private boolean isFirstLoad = true;

    protected boolean isPaused = false;

//    private FragDelegate mContextDelegate;


    private FragGotoComponent mGotoComponent;
    private MyVaryViewComponent mVaryViewComponent;
    private MyTimeOutComponent mTimeOutComponent;
    private MyLoadingDlgComponent mLoadingDlgComponent;


    /************************************************************************/



    /** 界面layout ID */
    protected abstract int getLayoutId();

    /** 初始化绑定View 工具(如:ButterKnife) */
    protected abstract void initBind();

    /** 解绑 工具(如:ButterKnife) */
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



    /**   get loading target view(加载中,空数据,错误数据显示处---被替换的View)  */
    protected  View getVaryTargetView(){ return null; }

    /** 初始化准备数据,返回准备结果(是否数据正常) */
    protected boolean initPrepareData(){ return true; }

    /** 初始化标题栏 */
    protected abstract void initToolBar();

    /** 初始化View和事件 */
    protected abstract void initViewsAndEvents();

    /** 延迟加载  */
    protected abstract void lazyLoad();



    /************************************************************************/

    protected Class<?> getXClass(){
        return getClass();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGotoComponent = new FragGotoComponent(this);
        if(getVaryTargetView()!=null){
            mVaryViewComponent = new MyVaryViewComponent(getVaryTargetView());
        }
        mLoadingDlgComponent = new MyLoadingDlgComponent(this);
        mTimeOutComponent = new MyTimeOutComponent();


        TAG = this.getClass().getSimpleName();
        if (isBindEventBusHere()) {
            registerEventBus();
        }
        XLog.i(TAG,"onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        XLog.i(TAG,"onCreateView");
        if (getLayoutId() != 0) {
            mView  = inflater.inflate(getLayoutId(), null);
            initBind();
            return mView;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        XLog.i(TAG,"onViewCreated");
        mView = view;

        initPrepareData();
        initToolBar();
        initViewsAndEvents();
        isPrepared = true;
        if(isFirstLoad)
        {
            onVisible();
            isFirstLoad = false;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        XLog.e(TAG,"onDestroyView");
        isPrepared = false;
        needRefresh = true;
        unBind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XLog.e(TAG,"onDestroy");
        if (isBindEventBusHere()) {
            unregisterEventBus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPaused = true;

    }

    @Override
    public void onResume() {
        super.onResume();
        isPaused = false;

    }

    protected boolean isPaused(){
        return isPaused;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        XLog.i(TAG,"setUserVisibleHint-------"+isVisibleToUser);
        //getUserVisibleHint()
        if (isVisibleToUser) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * fragment visible
     */
    protected synchronized void onVisible()
    {
        if (!isPrepared || !isVisible || !needRefresh ) {
            return;
        }
        needRefresh = false;
        lazyLoad();
    }

    /**
     * fragment invisible
     */
    protected  void onInvisible(){ }


    protected boolean isFragVisible(){
        return isVisible;
    }
    /**
     * refresh data
     */
    public void refresh() {
        this.needRefresh = true;
    }

    /**
     * get the support fragment manager
     *
     * @return
     */
    protected FragmentManager getSupportFragmentManager() {
        if( getActivity() instanceof BaseActivity )
        {
            return ((BaseActivity)getActivity()).getSupportFragmentManager();
        }
        return null;
    }

    public String getStringForFrag(int resId){
        String str ;
        try{
            str = super.getString(resId);
        }catch (Exception e){
            e.printStackTrace();
            str = null;
        }
        if( str == null){
            try{
                str = BaseApp.context().getString(resId);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return str;
    }



    protected void sleep(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /************************************** 权限 处理 ***********************************************/

    private SparseArray<PermissionResultCallback> requestCodePermissionMap = new SparseArray<>();
    /**
     * 检查权限，并处理
     */
    protected void checkPermission(@NonNull String[] permissions, int requestCode, @NonNull PermissionResultCallback callback) {

//        BaseMyAty aty = (BaseMyAty) getActivity();
//        aty.checkPermission( permissions,requestCode,callback);
        requestCodePermissionMap.put(requestCode, callback);
        if (Version.sdkAboveOrEqual(Version.API23_M)) {
            if (getNotGrantedPermissions(permissions) != null) {
                requestPermissions(permissions, requestCode);
            } else {
                callback.onPermissionGranted();
            }
        } else {
            callback.onPermissionGranted();
        }
    }


    /**
     * 获取当中未授权的权限
     */

//    @TargetApi(Build.VERSION_CODES.KITKAT)
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
//            if( permissionGranted && Version.getTargetSdkVer(getActivity()) >= Version.API19_KITKAT_44 ) {
//                //小米等手机需要特殊处理
//                AppOpsManager appOpsMgr = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
//                appOpsMgr.startOp()
//                int check = appOpsMgr.checkOp( AppOpsManager.OPSTR_CAMERA, AppDevice.getAppUid(),getActivity().getPackageName());
//                permissionGranted = appOpsMgr.checkOp( AppOpsManager.OPSTR_CAMERA, android.os.Process.myUid(),getActivity().getPackageName())==AppOpsManager.MODE_ALLOWED;
//            }
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



    /*********************************************************其它操作*********************************************************/

    protected View.OnLongClickListener mMenuItemLongClickListener = new View.OnLongClickListener(){

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
        mGotoComponent.gotoActivity(clz,data);
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode) {
        mGotoComponent.gotoActivityForResult( clz,requestCode );
    }

    @Override
    public void gotoActivityForResult(Class<? extends Activity> clz, int requestCode, Bundle data) {
        mGotoComponent.gotoActivityForResult( clz,requestCode,data );
    }

    @Override
    public void showLoading() {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showLoading();
    }

    @Override
    public void showLoading(int resId) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showLoading(resId);
    }

    @Override
    public void showLoading(String msg) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showLoading(msg);
    }

    @Override
    public void showEmpty(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showEmpty(msg,onClickListener);
    }

    @Override
    public void showEmpty(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showEmpty(msg,iconId,onClickListener);
    }

    @Override
    public void showError(String msg, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showError(msg,onClickListener);
    }

    @Override
    public void showError(String msg, int iconId, VaryViewHelperController.OnMsgViewClickListener onClickListener) {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.showError(msg,iconId,onClickListener);
    }

    @Override
    public void hideMsgView() {
        if(mVaryViewComponent!=null)
            mVaryViewComponent.hideMsgView();
    }

    @Override
    public LoadingDialog showLoadDialog(EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return  mLoadingDlgComponent.showLoadDialog( timeoutCallback,cancelCallback);
    }

    @Override
    public LoadingDialog showLoadDialog(int resId, EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return  mLoadingDlgComponent.showLoadDialog(resId, timeoutCallback,cancelCallback);
    }

    @Override
    public LoadingDialog showLoadDialog(String text, EdwinTimeoutCallback timeoutCallback, DlgCancelCallback cancelCallback) {
        return  mLoadingDlgComponent.showLoadDialog(text, timeoutCallback,cancelCallback);
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
