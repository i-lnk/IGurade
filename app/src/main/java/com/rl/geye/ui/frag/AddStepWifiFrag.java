package com.rl.geye.ui.frag;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.progressbar.CircularProgressBar;
import com.nicky.framework.widget.XEditText;
import com.rl.commons.ThreadPoolMgr;
import com.rl.commons.filter.EditChsLenInputFilter;
import com.rl.commons.filter.EditNoChsInputFilter;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;
import com.rl.geye.bean.EdwinWifiInfo;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.ui.dlg.ChooseDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.rl.commons.BaseApp.showToast;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 wifi选择
 */
public class AddStepWifiFrag extends BaseDevAddFrag implements TextView.OnEditorActionListener, TextWatcher {

    private final static int REQUEST_FOR_SET_WIFI = 66;
    @BindView(R.id.iv_dev_step)
    ImageView ivStep;
    @BindView(R.id.tv_wifi)
    TextView tvWifi;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.et_pwd)
    XEditText etPwd;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.scroll)
    ScrollView scroll;
    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.loading_progress)
    CircularProgressBar progressBar;
    private EdwinWifiInfo mEdwinWifi;
    private OnEvents mListener;
    private InputMethodManager imm;
    private AsyncTask<Void, Boolean, Boolean> mInitWifiTask;
    private boolean isWifiInitOk = false;

//    private VoicePlayer player;//声波通讯播放器


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
        return R.layout.frag_add_step_wifi;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
//        btnNext.getBackground().setLevel(2);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                mEdwinWifi.setWifiPwd(etPwd.getText().toString().trim());
//                if ( devTypeGroup.getTypeGroup() == Constants.DeviceTypeGroup.GROUP_2 ){
//                    if( player!=null )
//                        player.play(DataEncoder.encodeSSIDWiFi(mEdwinWifi.getWifiName(), mEdwinWifi.getWifiPwd()), 1, 1000);
//                }
                DataLogic.saveWifiPwd(mEdwinWifi.getWifiName(), mEdwinWifi.getWifiPwd());
                mListener.gotoNextForWifi(mEdwinWifi);
            }
        });


        btnNext.setEnabled(true);
        etPwd.setOnEditorActionListener(this);
        etPwd.addTextChangedListener(this);

        etPwd.setFilters(new InputFilter[]{new EditNoChsInputFilter(), new EditChsLenInputFilter(20)});


        mEdwinWifi = new EdwinWifiInfo();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                etPwd.setText("");
            }
        });

//        if (!ElianNative.LoadLib()) {
//            showToast("can't load elianjni  lib");
//            getActivity().finish();
//        } else
        {
            initWifi();
//            if ( devTypeGroup.getTypeGroup() == Constants.DeviceTypeGroup.GROUP_2 ){
//                initVoicePlayer();
//            }
        }

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick(getActivity(), v))
                    return;
                scanWifiList();
            }
        });


    }

    private void scanWifiList() {

        if (mEdwinWifi == null) {
            mEdwinWifi = new EdwinWifiInfo();
        }
        ArrayList<ScanResult> list; //存放周围wifi热点对象的列表
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        list = (ArrayList<ScanResult>) wifiManager.getScanResults();

        List<EdwinWifiInfo> wifiList = new ArrayList<>();
        for (ScanResult item : list) {
            EdwinWifiInfo wifiInfo = new EdwinWifiInfo();
            wifiInfo.setWifiName(item.SSID);
            wifiInfo.setWifiPwd("");
            wifiInfo.setBssid(item.BSSID);
            wifiInfo.setLevel(item.level);
            wifiList.add(wifiInfo);
        }

        ChooseDialog<EdwinWifiInfo> dialog2 = new ChooseDialog<>();
        dialog2.setTitle(getString(R.string.choose_wifi)).
                setListDatas(wifiList)
                .setChoosedItem(mEdwinWifi)
                .setOnItemChooseListener(new ChooseDialog.OnItemChooseListener<EdwinWifiInfo>() {

                    @Override
                    public void onItemChoose(int position, EdwinWifiInfo data) {
                        if (!data.equals(mEdwinWifi)) {
                            mEdwinWifi = data;
                            tvWifi.setText(mEdwinWifi.getName());
                            etPwd.setText("");
                        }
                    }
                }).show(getSupportFragmentManager(), "__DEV_WIFI_DLG__");

    }

    @Override
    protected void lazyLoad() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
//                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                scroll.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                if (rootView != null) {
                    int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                    if (heightDiff > 100) {
                        scrollToBottom();
                    }
                }
            }
        });
    }

    private void scrollToBottom() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (scroll != null)
                    scroll.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
            }
        });
    }

    @Override
    public void onDestroyView() {
        hideSoftInput();
        super.onDestroyView();
    }

//    private void initVoicePlayer(){
//        //autoSetAudioVolumn();
//        int freqs[] = new int[19];
//        int baseFreq = 4000;
//        for (int i = 0; i < freqs.length; i++) {
//            freqs[i] = baseFreq + i * 150;
//        }
//        // 创建声波通讯播放器
//        player = new VoicePlayer(44100);
//        player.setFreqs(freqs);
////            // 创建声波通讯识别器
////            recognizer = new VoiceRecognizer(44100);
////            recognizer.setFreqs(freqs);
////            recognizer.start();
//    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                String pwd = etPwd.getText().toString().trim();
                if (pwd.length() < 8) {
                    showToast(R.string.tips_pwd_lengh_not_enough);
                } else if (!isWifiInitOk) {
                    showToast(R.string.tips_get_wifi_info);
                } else {
                    mEdwinWifi.setWifiPwd(pwd);
//                    if ( devTypeGroup.getTypeGroup() == Constants.DeviceTypeGroup.GROUP_2 ){
//                        if( player!=null )
//                            player.play(DataEncoder.encodeSSIDWiFi(mEdwinWifi.getWifiName(), mEdwinWifi.getWifiPwd()), 1, 1000);
//                    }
                    DataLogic.saveWifiPwd(mEdwinWifi.getWifiName(), mEdwinWifi.getWifiPwd());
                    mListener.gotoNextForWifi(mEdwinWifi);
                }
                break;
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String pwd = etPwd.getText().toString().trim();
        if ((StringUtils.isEmpty(pwd) || pwd.length() >= 8) && isWifiInitOk) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    public void hideSoftInput() {
        if (etPwd != null) {
            imm.hideSoftInputFromWindow(etPwd.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    /**
     * 初始化wifi
     */
    private void initWifi() {
        cancelWifiInitTask();

        mInitWifiTask = new AsyncTask<Void, Boolean, Boolean>() {

            private String errMsg = "";
            private String mWifiName = "";
            private byte mAuthMode;
            private String mCapabilities = "";
            private int mLocalIp;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                etPwd.setText("");
                tvWifi.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                isWifiInitOk = false;
            }


            @Override
            protected Boolean doInBackground(Void... params) {
                errMsg = getStringForFrag(R.string.tips_get_wifi_info);

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
                            errMsg = getStringForFrag(R.string.tips_wifi_not_connect);
                            return false;
                        }
                        if (_connectedSsid.startsWith("\"") && _connectedSsid.endsWith("\"")) {
                            _connectedSsid = _connectedSsid.substring(1, iLen - 1);
                        }

                        mWifiName = _connectedSsid;
                        mLocalIp = wifiInfo.getIpAddress();
//                        XLog.i(TAG, "wifiName = " + mWifiName);
                        List<ScanResult> scanResultList = wifiManager.getScanResults();
                        for (int i = 0, len = scanResultList.size(); i < len; i++) {
                            ScanResult AccessPoint = scanResultList.get(i);
                            mCapabilities = AccessPoint.capabilities;
                            if (mCapabilities == null) {
                                errMsg = "请开启手机权限";
                                return false;
                            }

                            if (AccessPoint.SSID.equals(_connectedSsid)) {
                                boolean WpaPsk = AccessPoint.capabilities.contains("WPA-PSK");
                                boolean Wpa2Psk = AccessPoint.capabilities.contains("WPA2-PSK");
                                boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
                                boolean Wpa2 = AccessPoint.capabilities.contains("WPA2-EAP");
                                if (AccessPoint.capabilities.contains("WEP")) {
                                    mAuthMode = AuthMode.Open;
                                    break;
                                }
                                if (WpaPsk && Wpa2Psk) {
                                    mAuthMode = AuthMode.WPA1PSKWPA2PSK;
                                    break;
                                } else if (Wpa2Psk) {
                                    mAuthMode = AuthMode.WPA2PSK;
                                    break;
                                } else if (WpaPsk) {
                                    mAuthMode = AuthMode.WPAPSK;
                                    break;
                                }
                                if (Wpa && Wpa2) {
                                    mAuthMode = AuthMode.WPA1WPA2;
                                    break;
                                } else if (Wpa2) {
                                    mAuthMode = AuthMode.WPA2;
                                    break;
                                } else if (Wpa) {
                                    mAuthMode = AuthMode.WPA;
                                    break;
                                }
                                mAuthMode = AuthMode.Open;
                            }
                        }
                        sleep(1000);
                        return true;

                    } else {
                        errMsg = getStringForFrag(R.string.tips_wifi_not_connect);
                        return false;
                    }
                }
                errMsg = getStringForFrag(R.string.tips_wifi_not_connect);
                return false;
            }


            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (!result) {
                    showWifiSetDlg();
//                    showToast(errMsg);
                } else {
                    isWifiInitOk = true;
                    String pwd = DataLogic.getWifiPwd(mWifiName);
                    if (etPwd == null)
                        return;
                    etPwd.setText(pwd);
                    mEdwinWifi.setWifiName(mWifiName);
                    mEdwinWifi.setAuthMode(mAuthMode);
                    mEdwinWifi.setCapabilities(mCapabilities);
                    mEdwinWifi.setLocalIp(mLocalIp);
                    tvWifi.setText(mWifiName);
                    tvWifi.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    etPwd.requestFocus();
                    etPwd.setSelection(pwd.length());//将光标移至文字末尾
                    imm.showSoftInput(etPwd, InputMethodManager.SHOW_FORCED); //强制显示键
//                    etPwd.setText("");
                }
            }
        };
        mInitWifiTask.executeOnExecutor(ThreadPoolMgr.getCustomThreadPool());
    }

    private void cancelWifiInitTask() {
        if (mInitWifiTask != null) {
            mInitWifiTask.cancel(true);
            mInitWifiTask = null;
        }
    }

    private void showWifiSetDlg() {
        tvWifi.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        new MaterialDialog.Builder(getActivity())
                .title(R.string.wifi_set_dlg_title)
                .content(R.string.wifi_set_dlg_msg)
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_FOR_SET_WIFI == requestCode) {
            initWifi();
        }

    }

    public interface OnEvents {
        void gotoNextForWifi(EdwinWifiInfo wifiInfo);
    }


    public class AuthMode {
        public final static byte Open = 0x00;
        public final static byte Shared = 0x01;
        public final static byte AutoSwitch = 0x02;
        public final static byte WPA = 0x03;
        public final static byte WPAPSK = 0x04;
        public final static byte WPANone = 0x05;
        public final static byte WPA2 = 0x06;
        public final static byte WPA2PSK = 0x07;
        public final static byte WPA1WPA2 = 0x08;
        public final static byte WPA1PSKWPA2PSK = 0x09;
    }
}
