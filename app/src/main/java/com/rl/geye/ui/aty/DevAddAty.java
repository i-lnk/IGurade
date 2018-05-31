package com.rl.geye.ui.aty;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.DevTypeGroupBean;
import com.rl.geye.bean.EdwinWifiInfo;
import com.rl.geye.constants.Constants;
import com.rl.geye.ui.frag.AddStepApTipsFrag;
import com.rl.geye.ui.frag.AddStepApWifiFrag;
import com.rl.geye.ui.frag.AddStepChooseFrag;
import com.rl.geye.ui.frag.AddStepInitFrag;
import com.rl.geye.ui.frag.AddStepQRFrag;
import com.rl.geye.ui.frag.AddStepSearchFrag;
import com.rl.geye.ui.frag.AddStepWifiFrag;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备
 */
public class DevAddAty extends BaseMyAty implements
        AddStepInitFrag.OnEvents,
        AddStepWifiFrag.OnEvents,
        AddStepApWifiFrag.OnEvents,
        AddStepChooseFrag.OnEvents,
        AddStepQRFrag.OnEvents,
        AddStepApTipsFrag.OnEvents {


    private final static int STEP_INIT = 0; // 初始页
    private final static int STEP_WIFI = 1; // 输入WIFI
    private final static int STEP_CHOOSE = 2; // 添加方式
    private final static int STEP_QR = 3; // 二维码

    private final static int STEP_AP_TIPS = 4; // ap配置提示
    private final static int STEP_AP_WIFI = 5; // WIFI配置(AP)

    private final static int STEP_CONFIG_AP = 6; // AP配置
    private final static int STEP_CONFIG_QR = 7; // 二维码配置
    private final static int STEP_CONFIG_WIRED = 8; // 有线配置

    private final static int STEP_CONFIG_LAN = 9; // 局域网搜索

    private final static List<Integer> stepList = new ArrayList<>();

    static {
        stepList.clear();
        stepList.add(STEP_INIT);
        stepList.add(STEP_WIFI);
        stepList.add(STEP_CHOOSE);
        stepList.add(STEP_QR);
        stepList.add(STEP_CONFIG_WIRED);
        stepList.add(STEP_AP_TIPS);
        stepList.add(STEP_AP_WIFI);
        stepList.add(STEP_CONFIG_AP);
        stepList.add(STEP_CONFIG_QR);
        stepList.add(STEP_CONFIG_LAN);
    }

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.frag_container)
    FrameLayout fragContainer;
    @BindView(R.id.ly_all)
    View lyAll;

    private AddStepInitFrag fragInit;
    private AddStepWifiFrag fragWifi;


    private AddStepSearchFrag fragConfigWired;
    private AddStepSearchFrag fragConfigAp;
    private AddStepSearchFrag fragConfigQr;
    private AddStepSearchFrag fragConfigLan;

    private AddStepApTipsFrag fragApTips;
    private AddStepApWifiFrag fragApWifi;

    private AddStepChooseFrag fragChoose;
    private AddStepQRFrag fragQR;
    private Fragment curFrag;
    private int curStep = STEP_INIT;
    private EdwinWifiInfo mEdwinWifi;

    private int chooseType = Constants.AddType.WIRED;

    private DevTypeGroupBean devTypeGroup;
//    private int devType = Constants.DeviceType.IP02;
//    private boolean isLanSearch = false;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_dev_add;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            devTypeGroup = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DEV_TYPE_GROUP);
            chooseType = fromIntent.getIntExtra(Constants.BundleKey.KEY_ADD_TYPE, Constants.AddType.WIRED);
        }
        return devTypeGroup != null;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.add_dev);
    }

    @Override
    protected void initViewsAndEvents() {

        if (chooseType == Constants.AddType.LAN) {
            replaceFragment(STEP_CONFIG_LAN);
            showStepView(STEP_CONFIG_LAN);
        } else {
            replaceFragment(STEP_INIT);
            showStepView(STEP_INIT);
        }
//        executeInit();
        mEdwinWifi = new EdwinWifiInfo();

    }

    @Override
    protected void onClickView(View v) {

    }


    private void showStepView(int step) {
        if (!stepList.contains(step))
            return;
        curStep = step;
    }

    private void replaceFragment(int step) {
        if (!stepList.contains(step))
            return;
        Fragment frag = null;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BundleKey.KEY_DEV_TYPE_GROUP, devTypeGroup);
        switch (step) {
            case STEP_INIT:
                if (fragInit == null) {
                    fragInit = new AddStepInitFrag();
                }
                fragInit.setArguments(bundle);
                frag = fragInit;
                break;
            case STEP_WIFI:
                if (fragWifi == null) {
                    fragWifi = new AddStepWifiFrag();
                }
                fragWifi.setArguments(bundle);
                frag = fragWifi;
                break;
            case STEP_CHOOSE:
                if (fragChoose == null) {
                    fragChoose = new AddStepChooseFrag();
                }
                fragChoose.setArguments(bundle);
                frag = fragChoose;
                break;

            case STEP_CONFIG_WIRED:
                if (fragConfigWired == null) {
                    fragConfigWired = new AddStepSearchFrag();
                }
                bundle.putParcelable(AddStepSearchFrag.ARG_WIFI_INFO, mEdwinWifi);
//               bundle.putBoolean(AddStepConfigFrag.ARG_NEED_CONFIG, STEP_CONFIG_AUTO == step);
                fragConfigWired.setArguments(bundle);
                frag = fragConfigWired;
                break;

            case STEP_AP_TIPS:
                if (fragApTips == null) {
                    fragApTips = new AddStepApTipsFrag();
                }
//                    bundle.putParcelable(AddStepConfigFrag.ARG_WIFI_INFO, mEdwinWifi);
//                    fragConfigAp.setArguments(bundle);
                frag = fragApTips;
                break;

            case STEP_AP_WIFI:
                if (fragApWifi == null) {
                    fragApWifi = new AddStepApWifiFrag();
                }
                fragApWifi.setArguments(bundle);
                frag = fragApWifi;
                break;


            case STEP_CONFIG_AP:
                if (fragConfigAp == null) {
                    fragConfigAp = new AddStepSearchFrag();
                }
                bundle.putParcelable(AddStepSearchFrag.ARG_WIFI_INFO, mEdwinWifi);
                bundle.putBoolean(AddStepSearchFrag.ARG_IS_AP, false);
                fragConfigAp.setArguments(bundle);
                frag = fragConfigAp;
                break;

            case STEP_QR:
                if (fragQR == null) {
                    fragQR = new AddStepQRFrag();
                }
                bundle.putParcelable(AddStepSearchFrag.ARG_WIFI_INFO, mEdwinWifi);
                fragQR.setArguments(bundle);
                frag = fragQR;
                break;

            case STEP_CONFIG_QR:
                if (fragConfigQr == null) {
                    fragConfigQr = new AddStepSearchFrag();
                }
                bundle.putParcelable(AddStepSearchFrag.ARG_WIFI_INFO, mEdwinWifi);
//                    bundle.putBoolean(AddStepConfigFrag.ARG_NEED_CONFIG, false);
                fragConfigQr.setArguments(bundle);
                frag = fragConfigQr;
                break;

            case STEP_CONFIG_LAN:
                if (fragConfigLan == null) {
                    fragConfigLan = new AddStepSearchFrag();
                    mEdwinWifi = new EdwinWifiInfo();
                    mEdwinWifi.setWifiName("");
                    mEdwinWifi.setWifiPwd("");
                }
                bundle.putParcelable(AddStepSearchFrag.ARG_WIFI_INFO, mEdwinWifi);
//                    bundle.putBoolean(AddStepConfigFrag.ARG_NEED_CONFIG, false);
                fragConfigLan.setArguments(bundle);
                frag = fragConfigLan;
                break;

//            case STEP_CONFIG_AUTO:
//            case STEP_CONFIG_LAN:
//            case STEP_CONFIG_QR:
//                if (fragConfig == null) {
//                    fragConfig = new AddStepConfigFrag();
//                    bundle.putParcelable(AddStepConfigFrag.ARG_WIFI_INFO, mEdwinWifi);
//                    bundle.putBoolean(AddStepConfigFrag.ARG_NEED_CONFIG, STEP_CONFIG_AUTO==step);
//                    fragConfig.setArguments(bundle);
//                }
//                frag = fragConfig;
//                break;
//            case STEP_OK:
//                if (fragOk == null) {
//                    fragOk = new AddStepOKFrag();
//                }
//                fragOk.setArguments(bundle);
//                frag = fragOk;
//                break;
            default:
                break;
        }
        if (frag != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.setCustomAnimations(R.anim.slide_in_right_to_left,
                    R.anim.slide_out_right_to_left,
                    R.anim.slide_in_left_to_right,
                    R.anim.slide_out_left_to_right);
            String tagFrag = String.format("dev_add_frag_tag_%d", step + 1);

            transaction.replace(R.id.frag_container, frag, tagFrag);

            if (step != STEP_INIT && step != STEP_CONFIG_LAN)
                transaction.addToBackStack(tagFrag);
            try {
                transaction.commitAllowingStateLoss();
//                transaction.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
            curFrag = frag;
            //getSupportFragmentManager().executePendingTransactions();
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
//        XLog.e(TAG,"------------count : "+count );
//        for (int i=0;i<count;i++)
//        {
//            XLog.e(TAG,"------------Entry : "+getSupportFragmentManager().getBackStackEntryAt(i) );
//        }
        switch (curStep) {
            case STEP_INIT:
            case STEP_CONFIG_LAN:
                super.onBackPressed();
                break;

            case STEP_CHOOSE:
                super.onBackPressed();
                showStepView(STEP_INIT);
                break;

            case STEP_AP_TIPS:
            case STEP_WIFI:
                super.onBackPressed();
                showStepView(STEP_CHOOSE);
                break;

            case STEP_CONFIG_WIRED:
            case STEP_QR:
                super.onBackPressed();
                showStepView(STEP_WIFI);
                break;

            case STEP_AP_WIFI:
                super.onBackPressed();
                showStepView(STEP_AP_TIPS);
                break;

            case STEP_CONFIG_AP:
                super.onBackPressed();
                showStepView(STEP_AP_WIFI);
                break;
            case STEP_CONFIG_QR:
                super.onBackPressed();
                showStepView(STEP_QR);
                break;


        }
    }

    @Override
    public void finish() {
//        super.onBackPressed();
//        getSupportFragmentManager().popBackStack();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.addToBackStack(null);
//        try {
//            transaction.commitAllowingStateLoss();
////                transaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (!isFinishing())
            super.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_close, menu);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.item_close);
                if (v != null) {
                    v.setOnLongClickListener(mMenuItemLongClickListener);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.item_close:
                if (ClickUtil.isFastClick(getActivity(), toolbar))
                    return super.onOptionsItemSelected(item);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.item_close).setVisible(curStep != STEP_CONFIG_LAN);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void gotoNextForInit() {
        replaceFragment(STEP_CHOOSE);
        showStepView(STEP_CHOOSE);
    }

    @Override
    public void gotoNextForChoose(int chooseType) {
        this.chooseType = chooseType;
        if (chooseType == Constants.AddType.AP) {
            replaceFragment(STEP_AP_TIPS);
            showStepView(STEP_AP_TIPS);
        } else {
            replaceFragment(STEP_WIFI);
            showStepView(STEP_WIFI);
        }

    }

    @Override
    public void gotoNextForWifi(EdwinWifiInfo wifiInfo) {
        mEdwinWifi = wifiInfo;
        switch (chooseType) {
            case Constants.AddType.WIRED:
                replaceFragment(STEP_CONFIG_WIRED);
                showStepView(STEP_CONFIG_WIRED);
                break;
            case Constants.AddType.AP:
                break;
            case Constants.AddType.QR:
                replaceFragment(STEP_QR);
                showStepView(STEP_QR);
                break;
        }
    }


    @Override
    public void gotoNextForQR() {
        replaceFragment(STEP_CONFIG_QR);
        showStepView(STEP_CONFIG_QR);
    }

    @Override
    public void gotoNextForApTips() {
        replaceFragment(STEP_AP_WIFI);
        showStepView(STEP_AP_WIFI);
    }


    @Override
    public void gotoNextForApWifi(EdwinWifiInfo wifiInfo) {
        mEdwinWifi = wifiInfo;
        replaceFragment(STEP_CONFIG_AP);
        showStepView(STEP_CONFIG_AP);
    }
}
