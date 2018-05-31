package com.rl.geye.ui.frag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.edwintech.vdp.jni.ApiMgrV2;
import com.lzy.okgo.OkGo;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.commons.bean.EdwinItem;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PFrag;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.image.ImageUtil;
import com.rl.geye.net.NetUrl;
import com.rl.geye.net.callback.MyStringCallback;
import com.rl.geye.ui.aty.DetectSetAty;
import com.rl.geye.ui.aty.ModifyPwdAty;
import com.rl.geye.ui.aty.SDCardAty;
import com.rl.geye.ui.aty.TimeZoneAty;
import com.rl.geye.ui.dlg.ChooseDataDialog;
import com.rl.geye.ui.dlg.PhotoChooseDialog;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.geye.util.SnackbarUtil;
import com.rl.p2plib.bean.DetectInfo;
import com.rl.p2plib.bean.DevSysSet;
import com.rl.p2plib.bean.DevTimeZone;
import com.rl.p2plib.bean.OsdInfo;
import com.rl.p2plib.bean.PowerData;
import com.rl.p2plib.bean.RecordTime;
import com.rl.p2plib.bean.SysInfo;
import com.rl.p2plib.bean.SysVersion;
import com.rl.p2plib.bean.UpdateProgress;
import com.rl.p2plib.bean.VoiceData;
import com.rl.p2plib.constants.P2PConstants;
import com.rl.p2plib.utils.JSONUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Nicky on 2017/8/12.
 * 设备设置(常规设置)
 */
public class SetDevNormalFrag extends BaseP2PFrag implements UITableView.TableClickListener {

    private static final int REQUEST_CODE_DETECT = 11;
    private static final int REQUEST_CODE_RECORD = 12;
    private static final int REQUEST_CODE_PWD = 15;
    private static final int REQUEST_CODE_TIMEZONE = 23;
    private static final int REQUEST_CODE_SD = 24;
    @BindView(R.id.tb_01)
    UITableView tb01;
    @BindView(R.id.tb_02)
    UITableView tb02;
    @BindView(R.id.tb_03)
    UITableView tb03;
    @BindView(R.id.tv_dev_id)
    TextView tvId;
    @BindView(R.id.snackbar_container)
    CoordinatorLayout snackBarContainer;
    private CustomTableItem itemName;   // 设备名称
    private CustomTableItem itemBg;   // 设备背景
    private CustomTableItem itemPwd;   // 修改密码
    private CustomTableItem itemDetect; // 移动侦测
    private CustomTableItem itemRecord; // 自动录像
    private CustomTableItem itemPower; // 电源频率
    private CustomTableItem itemVoice;
    private CustomTableItem itemTimeZone;
    private CustomTableItem itemLanguage;
    private CustomTableItem itemSDCard;
    private CustomTableItem itemRestore;
    private CustomTableItem itemRestart;
    private CustomTableItem itemSystem;
    private PowerData mPowerData;
    private VoiceData mVoiceData;
    private DetectInfo mDetectInfo;
    private RecordTime mRecordTime;
//    private DevLanguage devLanguage = new DevLanguage(P2PConstants.LanguageType.TYPE_EN);
//    private List<DevLanguage> mLanguageList = new ArrayList<>();
    private String bgFilePathKj = "";
    private DevSysSet mSysSet;
    private List<EdwinItem> mLanguageList = new ArrayList<>();
    private List<EdwinItem> powerList = new ArrayList<>(); //音量列表
    private List<EdwinItem> voiceList = new ArrayList<>(); //音量列表
    private DevTimeZone curTimeZone;// = new DevTimeZone(TimeZoneVal.DMS_GMT_08);
//    private boolean disconnectInUpdate = false; //更新中设备断开连接
    private SysInfo mSysInfo;
    private SysVersion mSysVersion;
    private UpdateProgress mUpdateProgress;
    MaterialDialog mUpgradeDialog = null;
    private Handler mHandler;
    private boolean isUpdating = false; //正在更新
    private Thread refreshThread = null;
    private volatile boolean refreshRunFlag = true;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_dev_set_normal;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        bgFilePathKj = PhotoVideoUtil.getPhotoDirPath(false) + mDevice.getDevId() + "/" + Constants.DEVICE_BG_NAME;

        powerList.clear();
        for (int i = 50; i <= 60; i += 10) {
            powerList.add(new EdwinItem(String.valueOf(i), i));
        }

        voiceList.clear();
        for (int i = 1; i <= 5; i++) {
            voiceList.add(new EdwinItem(String.valueOf(i), i));
        }

        mLanguageList.clear();
        mLanguageList.add(new EdwinItem(getString(R.string.language_en), P2PConstants.LanguageType.TYPE_EN));
        mLanguageList.add(new EdwinItem(getString(R.string.language_ch), P2PConstants.LanguageType.TYPE_CN));

        tvId.setText("ID: " + mDevice.getDevId());


        itemName = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemBg = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemPwd = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemDetect = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemRecord = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemName.setName(getStringForFrag(R.string.dev_name));
        itemName.setIconImageResource(R.mipmap.a2_ic_set_name);
        itemName.setValue(mDevice.getName());

        itemBg.setName(getStringForFrag(R.string.dev_set_bg));
        itemBg.setIconImageResource(R.mipmap.a2_ic_set_bg);

        itemPwd.setName(getStringForFrag(R.string.dev_set_pwd));
        itemPwd.setIconImageResource(R.mipmap.a0_ic_edit);


        itemDetect.setName(getStringForFrag(R.string.deployment));
        itemDetect.setIconImageResource(R.mipmap.a2_ic_set_defence);
        itemDetect.setValue("");

        itemRecord.setName(getStringForFrag(R.string.auto_record));
        itemRecord.setIconImageResource(R.mipmap.a2_ic_set_record);
        itemRecord.setValue("");
        itemPower = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemVoice = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemTimeZone = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemLanguage = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemSDCard = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemRestore = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemRestart = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemSystem = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemPower.setName(getStringForFrag(R.string.power_frequency));
        itemPower.setIconImageResource(R.mipmap.a2_ic_set_power);
        itemVoice.setValue("");

        itemVoice.setName(getStringForFrag(R.string.adjust_voice));
        itemVoice.setIconImageResource(R.mipmap.ic_q1_2);
        itemVoice.setValue("");
        itemTimeZone.setName(getStringForFrag(R.string.time_zone));
        itemTimeZone.setIconImageResource(R.mipmap.a2_ic_set_timezone);
        itemLanguage.setName(getStringForFrag(R.string.dev_language));
        itemLanguage.setIconImageResource(R.mipmap.a2_ic_set_language);

        itemSDCard.setName(getStringForFrag(R.string.sdcard));
        itemSDCard.setIconImageResource(R.mipmap.a2_ic_set_sd);
        itemRestore.setName(getStringForFrag(R.string.restore_factory));
        itemRestore.setIconImageResource(R.mipmap.a2_ic_set_restore);
        itemRestart.setName(getStringForFrag(R.string.restart_dev));
        itemRestart.setIconImageResource(R.mipmap.a2_ic_set_restart);

        itemSystem.setName(getStringForFrag(R.string.dev_sys));
        itemSystem.setIconImageResource(R.mipmap.a2_ic_set_sys_version);

        tb01.clear();
        tb01.addViewItem(new ViewItem(itemBg, R.id.tb_set_bg));
        tb01.addViewItem(new ViewItem(itemName, R.id.tb_set_name));
        tb01.addViewItem(new ViewItem(itemPwd, R.id.tb_set_pwd));
        tb01.commit();

        tb02.clear();
        tb02.addViewItem(new ViewItem(itemDetect, R.id.tb_set_detect));
        tb02.addViewItem(new ViewItem(itemRecord, R.id.tb_set_auto_record));
        tb02.commit();

        tb03.clear();
        tb03.addViewItem(new ViewItem(itemPower, R.id.tb_set_power_frequency));
        tb03.addViewItem(new ViewItem(itemVoice, R.id.tb_set_adjust_voice));
        tb03.addViewItem(new ViewItem(itemTimeZone, R.id.tb_set_time_zone));
        tb03.addViewItem(new ViewItem(itemLanguage, R.id.tb_set_language));
        tb03.addViewItem(new ViewItem(itemSDCard, R.id.tb_set_sd));
        tb03.addViewItem(new ViewItem(itemRestore, R.id.tb_set_reset));
        tb03.addViewItem(new ViewItem(itemRestart, R.id.tb_set_reboot));
        tb03.addViewItem(new ViewItem(itemSystem, R.id.tb_set_system));
        tb03.commit();

        tb01.setTableClickListener(this);
        tb02.setTableClickListener(this);
        tb03.setTableClickListener(this);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case R.id.msg_update_poser:
                        onPowerChanged();
                        break;
                    case R.id.msg_update_voice:
                        onVoiceChanged();
                        break;
                    case R.id.msg_update_name:
                        onDevNameChanged();
                        break;
                    case R.id.msg_update_auto_record:
                        onAutoRecordChanged();
                        break;
                    case R.id.msg_update_detect:
                        onDetectChanged();
                        break;
                    case R.id.msg_update_time_zone:
                        if (curTimeZone != null) {
                            itemTimeZone.setValue(curTimeZone.getCity());
                        }
                        break;
                    case R.id.msg_update_language:
                        if (mSysSet != null) {
                            itemLanguage.setValue(mSysSet.getLanguageStr());
                        }
                        break;
                    case R.id.msg_update_version:
                        itemSystem.setValue(getStringForFrag(R.string.version_info) + mSysInfo.getVersion());
                        checkVersion();

                        boolean isSdEnabled = (mSysInfo != null && mSysInfo.getSupportStorage() == 1);
                        boolean isAutoRecordEnabled = (mSysInfo != null && mSysInfo.getSupportStorage() == 1);
                        tb02.setClickable(itemRecord, isOnline && isAutoRecordEnabled);
                        itemRecord.setBgEnabled(isOnline && isAutoRecordEnabled);
                        tb03.setClickable(itemSDCard, isOnline && isSdEnabled);
                        itemSDCard.setBgEnabled(isOnline && isSdEnabled);
                        if (isOnline) {
                            if (isAutoRecordEnabled) {
                                getAutoRecord();
                            }
                        }
                        break;
                    case R.id.msg_update_sysinfo:
                        itemSystem.setImgValue(R.mipmap.ic_status_err);
                        MaterialDialog.Builder mUpgradeDialogBuilder = new MaterialDialog.Builder(getActivity())
                            .content(R.string.tips_version)
                            .positiveText(R.string.str_ok)
                            .negativeText(R.string.str_cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    executeUpdate();
                                }
                            });
                        mUpgradeDialog = mUpgradeDialogBuilder.build();
                        mUpgradeDialog.show();
                        break;
                    case R.id.msg_update_disconnect:
                        try {
                            getActivity().finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case R.id.msg_update_progress:
                        if (mUpdateProgress != null && mUpdateProgress.getStatus() == 0) {
                            setLoadingTips(getString(R.string.downloading_with_progress,
                                    mUpdateProgress.getResult()) + "%");
                            if (mUpdateProgress.getResult() == 100) {
                                isUpdating = false;
                                stopRefresh();
                                hideLoadDialog();
                                new MaterialDialog.Builder(getActivity())
                                    .content(R.string.update_success)
                                    .cancelable(false)
                                    .canceledOnTouchOutside(false)
                                    .positiveText(R.string.str_ok)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            try {
                                                postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_REBOOT);
                                                getActivity().finish();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).show();
                            }
                        } else {
                            hideLoadDialog();
                            SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.update_failed)).show();
                        }
                        break;
                }
            }
        };
        onP2PStatusChanged();
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onTableClick(ViewItem view) {
        Bundle bundle = new Bundle();
        switch (view.getViewId()) {
            case R.id.tb_set_name:
                onClickDevName();
                break;
            case R.id.tb_set_pwd:
                bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                gotoActivityForResult(ModifyPwdAty.class, REQUEST_CODE_PWD, bundle);
                break;
            case R.id.tb_set_bg:
                PhotoChooseDialog photoDlg = new PhotoChooseDialog();
                photoDlg.withAspectRatio(720.f, 308.f)
                        .setOnPhotoListener(new PhotoChooseDialog.OnPhotoListener() {
                            @Override
                            public void onTakePhoto(Uri uri) {
                                if (uri != null) {
                                    refreshBg(ImageUtil.uriToFilePath(uri, getActivity()));
                                }
                            }

                            @Override
                            public void onChoosePhoto(Uri uri) {
                                if (uri != null) {
                                    refreshBg(ImageUtil.uriToFilePath(uri, getActivity()));
                                }
                            }

                            @Override
                            public void onRestore() {
                                File bgFile = new File(bgFilePathKj);
                                if (bgFile.exists()) {
                                    bgFile.delete();
                                }
                                refreshBg("");
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                photoDlg.show(getSupportFragmentManager(), "__photo_dlg__");
                break;

            case R.id.tb_set_detect:
                if (mDetectInfo != null &&
                        mSysInfo != null &&
                        mDevice != null) {
                    bundle.putBoolean(Constants.BundleKey.KEY_IS_DETECT, true);
                    bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                    bundle.putParcelable(Constants.BundleKey.KEY_DETECT_INFO, mDetectInfo);
                    bundle.putParcelable(Constants.BundleKey.KEY_SYS_INFO, mSysInfo);
                    gotoActivityForResult(DetectSetAty.class, REQUEST_CODE_DETECT, bundle);
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.tips_data_get_failed, getString(R.string.deployment))).show();
                }
                break;

            case R.id.tb_set_auto_record:
                if (mRecordTime != null && mSysInfo != null && mDevice != null) {
                    bundle.putBoolean(Constants.BundleKey.KEY_IS_DETECT, false);
                    bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                    bundle.putParcelable(Constants.BundleKey.KEY_DETECT_INFO, mRecordTime);
                    bundle.putParcelable(Constants.BundleKey.KEY_SYS_INFO, mSysInfo);
                    gotoActivityForResult(DetectSetAty.class, REQUEST_CODE_RECORD, bundle);
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.tips_data_get_failed, getString(R.string.auto_record))).show();
                }
                break;
            case R.id.tb_set_power_frequency:
                if (mPowerData != null) {
                    int position = mPowerData.getMode() == P2PConstants.PowerType.TYPE_60 ? 60 : 50;
                    ChooseDataDialog dlg = new ChooseDataDialog();
                    dlg.setTitle(getString(R.string.power_frequency))
                            .setCurData(new EdwinItem(String.valueOf(position), position))
                            .setDatas(powerList)
                            .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                            .setOnDataChooseListener(new ChooseDataDialog.OnDataChooseListener() {
                                @Override
                                public void onDataChoose(EdwinItem data) {
                                    mPowerData.setMode(data.getVal() == 60 ? P2PConstants.PowerType.TYPE_60 : P2PConstants.PowerType.TYPE_50);
                                    itemPower.setValue(data.getVal() + "");
                                    ApiMgrV2.savePower(mDevice.getDevId(), mPowerData);
                                }
                            })
                            .show(getSupportFragmentManager(), "__set__power__");
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.tips_data_get_failed,
                            getString(R.string.power_frequency))).show();
                }
                break;
            case R.id.tb_set_adjust_voice:
                if (mVoiceData != null) {
                    ChooseDataDialog dlg = new ChooseDataDialog();
                    dlg.setTitle(getString(R.string.adjust_voice))
                            .setCurData(new EdwinItem(String.valueOf(mVoiceData.getAudioVolume()),
                                    mVoiceData.getAudioVolume()))
                            .setDatas(voiceList)
                            .setOkStr(getString(R.string.str_ok)).setCancelStr(getString(R.string.str_cancel))
                            .setOnDataChooseListener(new ChooseDataDialog.OnDataChooseListener() {
                                @Override
                                public void onDataChoose(EdwinItem data) {
                                    mVoiceData.setAudioVolume(data.getVal());
                                    itemVoice.setValue(mVoiceData.getAudioVolume() + "");
                                    ApiMgrV2.saveVoice(mDevice.getDevId(), mVoiceData);
                                }
                            })
                            .show(getSupportFragmentManager(), "__set__voice__");
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer,
                            getString(R.string.tips_data_get_failed,
                                    getString(R.string.adjust_voice))).show();
                }
                break;
            case R.id.tb_set_time_zone:
                if (curTimeZone != null) {
                    bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                    bundle.putParcelable(Constants.BundleKey.KEY_TIME_ZONE, curTimeZone);
                    gotoActivityForResult(TimeZoneAty.class, REQUEST_CODE_TIMEZONE, bundle);
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer,
                            getString(R.string.tips_data_get_failed,
                                    getString(R.string.time_zone))).show();
                }
                break;

            case R.id.tb_set_language:
                if (mSysSet != null) {
                    EdwinItem curDevLanguage = new EdwinItem(mSysSet.getLanguageStr(), mSysSet.getLanguage());
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.choose_language)
                            .items(mLanguageList)
                            .dividerColor(ContextCompat.getColor(getActivity(), R.color.divider))
                            .itemsCallbackSingleChoice(mLanguageList.indexOf(curDevLanguage), new MaterialDialog.ListCallbackSingleChoice() {

                                @Override
                                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                    if (which > -1 && which < mLanguageList.size()) {
                                        mSysSet.setLanguage(mLanguageList.get(which).getVal());
                                        itemLanguage.setValue(mLanguageList.get(which).getName());
                                        //TODO
                                        setSysSet();
                                    }
                                    return true;
                                }
                            })
                            .positiveText(R.string.str_ok).show();
                } else {
                    SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.tips_data_get_failed,
                            getString(R.string.dev_language))).show();
                }

//                new MaterialDialog.Builder(getActivity())
//                        .title(R.string.choose_language)
//                        .items(mLanguageList)
//                        .dividerColor(ContextCompat.getColor(getActivity(),R.color.divider))
//                        .itemsCallbackSingleChoice(  mLanguageList.indexOf(devLanguage), new MaterialDialog.ListCallbackSingleChoice(){
//
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                if (which > -1 && which < mLanguageList.size()) {
//                                    devLanguage = mLanguageList.get(which);
//                                    setDevLanguage();
//                                    itemLanguage.setValue(devLanguage.getName());
//                                }
//                                return true;
//                            }
//                        }  )
//                        .positiveText(R.string.str_ok).show();

                break;

            case R.id.tb_set_sd:
                bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                gotoActivity(SDCardAty.class, bundle);
                break;

            case R.id.tb_set_reset:
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.need_restore_factory)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                executeRebootReset(false);
                            }
                        }).show();
                break;

            case R.id.tb_set_reboot:
                new MaterialDialog.Builder(getActivity())
                        .content(R.string.need_restart_dev)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                executeRebootReset(true);
                            }
                        }).show();
                break;

            case R.id.tb_set_system:
                if (mSysVersion != null && mSysInfo != null) {
                    if (!StringUtils.isEmpty(mSysVersion.getUrl())
                            && mSysVersion.getVer().compareTo(mSysInfo.getVersion()) > 0) {
                        mHandler.sendEmptyMessage(R.id.msg_update_sysinfo);
                    } else {
                        SnackbarUtil.ShortSnackbar(snackBarContainer, getString(R.string.version_latest)).show();
                    }
                }
                break;

        }
    }

    @Override
    public void onDestroyView() {
        if(mUpgradeDialog != null && mUpgradeDialog.isShowing()){
            mUpgradeDialog.dismiss();
            mUpgradeDialog = null;
        }
        super.onDestroyView();
        mHandler.removeMessages(R.id.msg_update_name);
        mHandler.removeMessages(R.id.msg_update_auto_record);
        mHandler.removeMessages(R.id.msg_update_detect);
        mHandler.removeMessages(R.id.msg_update_time_zone);
        mHandler.removeMessages(R.id.msg_update_language);
        mHandler.removeMessages(R.id.msg_update_sysinfo);
        mHandler.removeMessages(R.id.msg_update_version);
        mHandler.removeMessages(R.id.msg_update_disconnect);
        mHandler.removeMessages(R.id.msg_update_progress);
        mHandler.removeMessages(R.id.msg_update_voice);
        mHandler.removeMessages(R.id.msg_update_poser);
        stopRefresh();
    }

    @Override
    protected void onP2PStatusChanged() {

        boolean isSdEnabled = (mSysInfo != null && mSysInfo.getSupportStorage() == 1);
        boolean isAutoRecordEnabled = (mSysInfo != null && mSysInfo.getSupportStorage() == 1);

//        tb01.setClickable(itemName,isOnline);
        tb01.setClickable(itemPwd, isOnline);
//        itemName.setBgEnabled(isOnline);
        itemPwd.setBgEnabled(isOnline);

        tb02.setClickable(itemDetect, isOnline);
        tb02.setClickable(itemRecord, isOnline && isAutoRecordEnabled);
        itemDetect.setBgEnabled(isOnline);
        itemRecord.setBgEnabled(isOnline && isAutoRecordEnabled);

        tb03.setClickable(itemVoice, isOnline);
        tb03.setClickable(itemTimeZone, isOnline);
        tb03.setClickable(itemLanguage, isOnline);
        tb03.setClickable(itemSDCard, isOnline && isSdEnabled);
        tb03.setClickable(itemRestore, isOnline);
        tb03.setClickable(itemRestart, isOnline);
        tb03.setClickable(itemSystem, isOnline);
        itemSDCard.setBgEnabled(isOnline && isSdEnabled);
        itemRestore.setBgEnabled(isOnline);
        itemRestart.setBgEnabled(isOnline);
        itemPower.setBgEnabled(isOnline);
        itemVoice.setBgEnabled(isOnline);
        itemTimeZone.setBgEnabled(isOnline);
        itemLanguage.setBgEnabled(isOnline);
        itemSystem.setBgEnabled(isOnline);

        if (isOnline) {
            getDevSys();
            getDetect();
            if (isAutoRecordEnabled) {
                getAutoRecord();
            }

            getSysSet();
            getDevTimeZone();
            getPower();
            getVoice();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_DETECT) {
//                DetectInfo detect = data.getParcelableExtra(Constants.BundleKey.KEY_DETECT_INFO);
//                if (detect != null) {
//                    mDetectInfo = detect;
//                    onDetectChanged();
//                }
                if (isOnline)
                    getDetect();
            } else if (requestCode == REQUEST_CODE_RECORD) {
//                RecordTime record = data.getParcelableExtra(Constants.BundleKey.KEY_DETECT_INFO);
//                if (record != null) {
//                    recordTime = record;
//                    onAutoRecordChanged();
//                }
                if (isOnline)
                    getAutoRecord();
            } else if (REQUEST_CODE_PWD == requestCode) {
                EdwinDevice dev = data.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO);
                if (dev != null) {
                    mDevice.setPwd(dev.getPwd());
                }
            }
        }
        if (REQUEST_CODE_TIMEZONE == requestCode && resultCode == Activity.RESULT_OK) {
            getDevTimeZone();
        }
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {

        return new MyP2PCallBack() {

            @Override
            public void onGetPower(String did, int msgType, PowerData powerData) {
                super.onGetPower(did, msgType, powerData);
                if (powerData != null && isSameDevice(did)) {
                    mPowerData = powerData;
                    mHandler.sendEmptyMessage(R.id.msg_update_poser);
                }
            }

            @Override
            public void onGetVoice(String did, int msgType, VoiceData voiceData) {
                super.onGetVoice(did, msgType, voiceData);
                if (voiceData != null && isSameDevice(did)) {
                    mVoiceData = voiceData;
                    mHandler.sendEmptyMessage(R.id.msg_update_voice);
                }
            }

            @Override
            public void onStatusChanged(String did, int type, int param) {
                super.onStatusChanged(did, type, param);
                if (isSameDevice(did)) {
                    //TODO
                    switch (param) {
                        case P2PConstants.P2PStatus.CONNECT_FAILED:
                        case P2PConstants.P2PStatus.DISCONNECT:
                        case P2PConstants.P2PStatus.DEVICE_NOT_ON_LINE:
                            if (isUpdating) {
//                            disconnectInUpdate = true;
                                isUpdating = false;
                                stopRefresh();
                                mHandler.sendEmptyMessage(R.id.msg_update_disconnect);
                            }
                    }
                }
            }

            @Override
            public void onGetOsdInfo(String did, int msgType, OsdInfo osdInfo) {
                super.onGetOsdInfo(did, msgType, osdInfo);
//                if( isSameDevice(did) && osdInfo!=null && !mDevice.getName().equals(osdInfo.getChannel_name_text()) ){
//                    mDevice.setName(osdInfo.getChannel_name_text());
//                    mHandler.sendEmptyMessage(R.id.msg_update_name);
//                }
            }

            @Override
            public void onGetDetectInfo(String did, int msgType, DetectInfo detectInfo) {
                super.onGetDetectInfo(did, msgType, detectInfo);
                if (detectInfo != null && isSameDevice(did)) {
                    mDetectInfo = detectInfo;
                    mHandler.sendEmptyMessage(R.id.msg_update_detect);
                }
            }

            @Override
            public void onGetRecordTime(String did, int msgType, RecordTime recordTime) {
                super.onGetRecordTime(did, msgType, recordTime);
                if (recordTime != null && isSameDevice(did)) {
                    mRecordTime = recordTime;
                    mHandler.sendEmptyMessage(R.id.msg_update_auto_record);
                }
            }

            @Override
            public void onGetDevSysSet(String did, int msgType, DevSysSet sysSet) {
                super.onGetDevSysSet(did, msgType, sysSet);
                if (sysSet != null) {
                    mSysSet = sysSet;
//                    mOrigSysSet = (DevSysSet) mSysSet.clone();
                    mHandler.sendEmptyMessage(R.id.msg_update_language);
                }
            }

//            @Override
//            public void onGetLanguage(String did, int msgType, DevLanguage language) {
//                super.onGetLanguage(did, msgType, language);
//                if( language!=null && isSameDevice(did) ){
//                    devLanguage = language;
//                    mHandler.sendEmptyMessage(R.id.msg_update_language);
//                }
//            }

            @Override
            public void onGetTimeZone(String did, int msgType, DevTimeZone timeZone) {
                super.onGetTimeZone(did, msgType, timeZone);
                if (timeZone != null && isSameDevice(did)) {
                    curTimeZone = timeZone;
                    mHandler.sendEmptyMessage(R.id.msg_update_time_zone);
                }
            }

            @Override
            public void onGetSysInfo(String did, int msgType, SysInfo sysInfo) {
                super.onGetSysInfo(did, msgType, sysInfo);
                if (sysInfo != null && isSameDevice(did)) {
                    mSysInfo = sysInfo;
                    mHandler.sendEmptyMessage(R.id.msg_update_version);
                }
            }

            @Override
            public void onUpdateProgress(String did, int msgType, UpdateProgress updateProgress) {
                super.onUpdateProgress(did, msgType, updateProgress);
                if (updateProgress != null && isSameDevice(did)) {
                    mUpdateProgress = updateProgress;
                    mHandler.sendEmptyMessage(R.id.msg_update_progress);
                }
            }
        };

    }

    @Override
    protected void onAttachToContext(Context context) {

    }

    /**
     * 修改背景
     */
    private void refreshBg(String path) {
        File origBg = new File(mDevice.getBgPath());
        if (origBg != null && origBg.exists()) {
            origBg.delete();
        }
        mDevice.setBgPath(path);
        MyApp.getDaoSession().getEdwinDeviceDao().update(mDevice);
        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_UPDATE_BG, mDevice);
    }

    /**
     * 修改名称
     */
    private void onClickDevName() {
        new MaterialDialog.Builder(getActivity())
//                        .title(R.string.dlg_name_input_title)
                .content(R.string.dev_name)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(R.string.str_ok)
                .negativeText(R.string.str_cancel)
                .input(getString(R.string.dev_name), itemName.getValueStr(), false,
                        new MaterialDialog.InputCallback() {

                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                String result = input.toString();
                                if (!mDevice.getName().equals(result)) {
//                                    setDevName(result);
                                    mDevice.setName(result);
                                    onDevNameChanged();
                                }
                            }
                        }).show();
    }

    /**
     * 设备名称变更
     */
    private void onDevNameChanged() {
        itemName.setValue(mDevice.getName());
        MyApp.getDaoSession().getEdwinDeviceDao().update(mDevice);
        //TODO
//        TbDevice.getInstance().updateDevName(mDevice.getDevId(),mDevice.getName());
//        TbPicVideo.getInstance().updateDevName(mDevice.getDevId(),mDevice.getName());
//        TbRecord.getInstance().updateDevName(mDevice.getDevId(),mDevice.getName());
        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_UPDATE_NAME, mDevice);
    }


    /**
     * 移动侦测数据变更
     */
    private void onDetectChanged() {
        if (mDevice.isIpc()) {
            if (mDetectInfo != null && mDetectInfo.isOn()) {
                int startH = mDetectInfo.getStart_hour();
                int endH = mDetectInfo.getClose_hour();
                int startM = mDetectInfo.getStart_mins();
                int endM = mDetectInfo.getClose_mins();
                if (startH == 22 && endH == 6 && startM == 0 && endM == 0) {
                    itemDetect.setValue(getStringForFrag(R.string.has_been_open_one));
                } else if (startH == 0 && endH == 23 && startM == 0 && endM == 0) {
                    itemDetect.setValue(getStringForFrag(R.string.has_been_open_one_two));
                } else {
                    itemDetect.setValue(mDetectInfo.getFormatTimeStr());
                }
            } else {
                itemDetect.setValue(getStringForFrag(R.string.not_been_open));
            }
        } else {
            if (mDetectInfo != null) {
                int tempPir = mDetectInfo.getEnablePir() > 0 ? 1 : 0;
                int val = tempPir + mDetectInfo.getEnableRemoveAlaram() * 2;
                switch (val) {
                    case 1:
                        itemDetect.setValue(getStringForFrag(R.string.pir_open));
                        break;
                    case 2:
                        itemDetect.setValue(getStringForFrag(R.string.alarm_remove_open));
                        break;
                    case 3:
                        itemDetect.setValue(getStringForFrag(R.string.pir_alarm_remove_open));
                        break;
                    default:
                        itemDetect.setValue(getStringForFrag(R.string.not_been_open));
                        break;
                }
            } else {
                itemDetect.setValue(getStringForFrag(R.string.not_been_open));
            }
        }

    }

    /**
     * 更新电源频率
     */
    private void onPowerChanged() {
        if (mPowerData == null) {
            itemPower.setValue("50");
        } else {
            itemPower.setValue((mPowerData.getMode() == P2PConstants.PowerType.TYPE_60 ? 60 : 50) + "");
        }
    }

    /**
     * 更新音量
     */
    private void onVoiceChanged() {
        if (mVoiceData == null) {
            itemVoice.setValue("0");
        } else {
            itemVoice.setValue(mVoiceData.getAudioVolume() + "");
        }
    }

    /**
     * 自动录像数据变更
     */
    private void onAutoRecordChanged() {
        itemRecord.setValue(getString(R.string.record_time_desc, mRecordTime.getVideo_lens()));
//        if ( mRecordTime!=null && mRecordTime.isOn()) {
//            int startH  = mRecordTime.getStart_hour();
//            int endH = mRecordTime.getClose_hour();
//            int startM = mRecordTime.getStart_mins();
//            int endM =  mRecordTime.getClose_mins();
//            if (startH == 22 && endH == 6 && startM == 0 && endM  == 0   ) {
//                itemRecord.setValue(getStringForFrag(R.string.has_been_open_one));
//            } else if (startH == 0 && endH == 23 && startM == 0 && endM  == 0  ) {
//                itemRecord.setValue(getStringForFrag(R.string.has_been_open_one_two));
//            } else {
//                itemRecord.setValue(mRecordTime.getFormatTimeStr());
//            }
//        } else {
//            itemRecord.setValue(getStringForFrag(R.string.not_been_open));
//        }
    }

    /**
     * 检查版本
     */
    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        params.put("devid", mDevice.getDevId());
        params.put("cloudid", mDevice.getDevId());
        params.put("curver", mSysInfo.getVersion());
        params.put("model", mSysInfo.getModel());
        params.put("odm", String.valueOf(mSysInfo.getOdm()));
        params.put("lang", String.valueOf(mSysInfo.getLanguage()));

        OkGo.<String>get(NetUrl.checkVersion()).tag(this)
                .params(params)
                .execute(new MyStringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
//                        Logger.w("----------->responseData:"+s);
                        if (!StringUtils.isEmpty(s)) {
                            mSysVersion = JSONUtil.fromJson(s, SysVersion.class);
                            if (mSysVersion != null && !StringUtils.isEmpty(mSysVersion.getUrl()) && mSysVersion.getVer().compareTo(mSysInfo.getVersion()) > 0) {
                                mHandler.sendEmptyMessage(R.id.msg_update_sysinfo);
                            }
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                    }
                });

    }


/************************************** 执行指令**************************************/

//    /** 获取设备名  */
//    private void getDevName(){
//        ApiMgrV2.getDevName(mDevice.getDevId());
//    }
//
//    /** 设置设备名  */
//    private void setDevName(String name){
//        ApiMgrV2.setDevName( mDevice.getDevId(),name);
//    }

    /**
     * 获取录像时间
     */
    private void getAutoRecord() {
        ApiMgrV2.getAutoRecord(mDevice.getDevId());
    }

    /**
     * 获取电源
     */
    private void getPower() {
        ApiMgrV2.getPower(mDevice.getDevId());
    }

    /**
     * 获取音量
     */
    private void getVoice() {
        ApiMgrV2.getVoice(mDevice.getDevId());
    }

    /**
     * 获取设备移动侦测
     */
    private void getDetect() {
        ApiMgrV2.getDetect(mDevice.getDevId());
    }


    /**
     * 获取设备时区
     */
    private void getDevTimeZone() {
        ApiMgrV2.getDevTimeZone(mDevice.getDevId());
    }

    /**
     * 获取设备系统信息
     */
    private void getDevSys() {
        ApiMgrV2.getDevSys(mDevice.getDevId());
    }


    /**
     * 获取设备系统设置
     */
    private void getSysSet() {
        ApiMgrV2.getSysSet(mDevice.getDevId());
    }

    /**
     * 设置设备系统设置
     */
    private void setSysSet() {
//        ApiMgrV2.setDevLanguage( mDevice.getDevId(),devLanguage);
        ApiMgrV2.setSysSet(mDevice.getDevId(), mSysSet);
    }

    /**
     * 重启控制
     */
    private void executeRebootReset(boolean isReboot) {
        ApiMgrV2.rebootReset(mDevice.getDevId(), isReboot, mSysSet);
        int tipsId = isReboot ? R.string.rebooting : R.string.restoring;
        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_REBOOT);
        showLoadDialog(tipsId, new EdwinTimeoutCallback(5 * 1000) {

            @Override
            public void onTimeOut() {
                try {
                    hideLoadDialog();
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }


    private void executeUpdate() {

        ApiMgrV2.updateDevSys(mDevice.getDevId(), mSysVersion);
        isUpdating = true;
        showLoadDialog(R.string.downloading, new EdwinTimeoutCallback(3 * 60 * 1000) {

            @Override
            public void onTimeOut() {
                try {
                    isUpdating = false;
                    stopRefresh();
                    hideLoadDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, null);
        executeRefresh();

    }

    private void getUpdateProgress() {
        ApiMgrV2.getUpdateProgress(mDevice.getDevId());
    }

    //2S刷新一次SD卡状态
    private void executeRefresh() {

        if (refreshThread == null) {
            refreshRunFlag = true;
            refreshThread = new Thread(new Runnable() {

                @Override
                public void run() {

                    while (refreshRunFlag) {
                        try {
                            /** 多次sleep 以便快速中断 */
                            for (int i = 0; i < 20; i++) {
                                Thread.sleep(100);
                                if (!refreshRunFlag) {
                                    break;
                                }
                            }
                            if (!refreshRunFlag) {
                                break;
                            }
                            getUpdateProgress();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            refreshThread.start();
        }
    }

    private void stopRefresh() {
        if (refreshThread == null) {
            return;
        }
        if (refreshThread.isAlive()) {
            try {
                refreshRunFlag = false;
                refreshThread.join(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        refreshThread = null;
    }


}
