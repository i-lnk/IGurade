package com.rl.geye.ui.aty;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.widget.RippleView;
import com.rl.commons.bean.EdwinEvent;
import com.rl.commons.compatibility.Version;
import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.CameraUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.DevTypeGroupBean;
import com.rl.geye.constants.Constants;
import com.rl.p2plib.constants.P2PConstants;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 选择添加设备方式 (手动添加 、扫描二维码 )
 */
public class ChooseAddModeAty extends BaseMyAty {

    private static final int REQUEST_CODE_FOR_CAMERA = 5005;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    //    @BindView(R.id.tv_add_manual)
//    TextView tvAddManual;
//    @BindView(R.id.tv_add_scan)
//    TextView tvAddScan;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_welcome)
    TextView tvWelcome;
    @BindView(R.id.ly_add)
    RippleView lyAdd;
    @BindView(R.id.ly_add_lan)
    RippleView lyAddLan;
    @BindView(R.id.ly_scan)
    RippleView lyScan;


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

    @Override
    protected int getLayoutId() {
        return R.layout.aty_choose_add_mode;
    }

    @Override
    public View getVaryTargetView() {
        return null;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.add_dev);
    }

    @Override
    protected void initViewsAndEvents() {

//        tvWelcome.setVisibility(View.INVISIBLE);
        tvWelcome.setText(getString(R.string.tips_welcome_to_use, getString(R.string.app_name)));

        lyAdd.setOnClickListener(this);
        lyAddLan.setOnClickListener(this);
        lyScan.setOnClickListener(this);
        lyAdd.setOnRippleCompleteListener(mRippleCompleteListener);
        lyAddLan.setOnRippleCompleteListener(mRippleCompleteListener);
        lyScan.setOnRippleCompleteListener(mRippleCompleteListener);
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.ly_add:
            case R.id.ly_add_lan:
            case R.id.ly_scan:
                v.setTag(Constants.KEY_TAG_CLICK);
                break;
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
        switch (event.getEventCode()) {
            case Constants.EdwinEventType.EVENT_DEV_ADD_COMPLETE:
            case Constants.EdwinEventType.EVENT_DEV_ADD_CANCEL:
                if (getActivity() != null && !isFinishing())
                    finish();
                break;
            default:
                break;
        }
    }

}
