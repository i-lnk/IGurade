package com.rl.geye.ui.frag;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 Ap配置提示
 */
public class AddStepApTipsFrag extends BaseDevAddFrag {

    private final static int REQUEST_FOR_SET_WIFI = 99;
    @BindView(R.id.btn_next)
    Button btnNext;
//    private EdwinWifiInfo mWifiInfo;
    private OnEvents mListener;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof OnEvents) {
            mListener = (OnEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEvents");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_step_ap_tips;
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

//        mWifiInfo = getArguments().getParcelable(AddStepConfigFrag.ARG_WIFI_INFO);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                mListener.gotoNextForApTips();

            }
        });

        checkApWifi();


    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_FOR_SET_WIFI == requestCode) {
            //TODO
            checkApWifi();
        }
    }

    private void checkApWifi() {
        if (!isApWifi()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.wifi_set_dev_dlg_title)
                    .content(R.string.wifi_set_dev_dlg_msg)
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .positiveText(R.string.str_ok)
                    .negativeText(R.string.str_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (!getActivity().isFinishing()) {
                                // 转到手机设置界面，用户设置WIFI
                                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivityForResult(intent, REQUEST_FOR_SET_WIFI);
                            }
                        }
                    }).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (!getActivity().isFinishing()) {
                        getActivity().onBackPressed();
                    }
                }
            }).show();
        }
    }

    private boolean isApWifi() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {

            ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED
                    || mWifi.isConnected()) {
                String _connectedSsid = wifiInfo.getSSID();
                int iLen = _connectedSsid.length();
                if (iLen == 0) {

                    return false;
                }
                if (_connectedSsid.startsWith("\"") && _connectedSsid.endsWith("\"")) {
                    _connectedSsid = _connectedSsid.substring(1, iLen - 1);
                }
                return _connectedSsid.startsWith("AP_");

            }
        }
        return false;
    }


    public interface OnEvents {

        void gotoNextForApTips();
    }
}
