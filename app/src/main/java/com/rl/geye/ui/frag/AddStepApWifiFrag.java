package com.rl.geye.ui.frag;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
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
import com.nicky.framework.widget.XEditText;
import com.rl.commons.filter.EditChsLenInputFilter;
import com.rl.commons.filter.EditNoChsInputFilter;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;
import com.rl.geye.bean.EdwinWifiInfo;
import com.rl.geye.ui.dlg.ChooseDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.rl.commons.BaseApp.showToast;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 wifi选择(AP)
 */
public class AddStepApWifiFrag extends BaseDevAddFrag implements TextView.OnEditorActionListener, TextWatcher {

//    private final static int REQUEST_FOR_SET_WIFI = 66;

    private final static int REQUEST_FOR_SET_GPS = 10;

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


    private EdwinWifiInfo mEdwinWifi;
    private OnEvents mListener;
    private InputMethodManager imm;


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
        return R.layout.frag_add_step_wifi_ap;
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
                if (mEdwinWifi != null) {
                    mEdwinWifi.setWifiPwd(etPwd.getText().toString().trim());
                    mListener.gotoNextForApWifi(mEdwinWifi);
                }
            }
        });

        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastClick(getActivity(), v))
                    return;
                scanWifiList();
            }
        });


        btnNext.setEnabled(true);
        etPwd.setOnEditorActionListener(this);
        etPwd.addTextChangedListener(this);

        etPwd.setFilters(new InputFilter[]{new EditNoChsInputFilter(), new EditChsLenInputFilter(20)});

        if (mEdwinWifi == null) {

            scanWifiList();
        } else {
            tvWifi.setText(mEdwinWifi.getName());
            etPwd.setText(mEdwinWifi.getWifiPwd());
        }

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
        mEdwinWifi = null;
        super.onDestroyView();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                String pwd = etPwd.getText().toString().trim();
                String ssid = tvWifi.getText().toString().trim();
                if (pwd.length() < 8) {
                    showToast(R.string.tips_pwd_lengh_not_enough);
                } else if (StringUtils.isEmpty(ssid)) {
                    showToast(R.string.tips_get_wifi_info);
                } else if (mEdwinWifi != null) {
                    mEdwinWifi.setWifiPwd(pwd);
                    mListener.gotoNextForApWifi(mEdwinWifi);
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
        String ssid = tvWifi.getText().toString().trim();
        if ((StringUtils.isEmpty(pwd) || pwd.length() >= 8) && !StringUtils.isEmpty(ssid)) {
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


    private void scanWifiList() {

        if (!isLocationEnabled()) {
            showGpsSetDlg();
        } else {
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
//        Logger.i("========= wifi list: "+list );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_FOR_SET_GPS == requestCode) {
            scanWifiList();
        }

    }

    private void showGpsSetDlg() {


        new MaterialDialog.Builder(getActivity())
                .title(R.string.gps_set_dlg_title)
                .content(R.string.gps_set_dlg_msg)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.str_ok)
                .negativeText(R.string.str_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!getActivity().isFinishing()) {
                            // 转到手机设置界面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_FOR_SET_GPS);
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

    /**
     * 检测定位服务
     */
    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getActivity().getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public interface OnEvents {
        void gotoNextForApWifi(EdwinWifiInfo wifiInfo);
    }

}
