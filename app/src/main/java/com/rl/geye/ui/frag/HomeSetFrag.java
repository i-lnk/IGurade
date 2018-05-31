package com.rl.geye.ui.frag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.SwitchTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.commons.utils.AppManager;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyFrag;
import com.rl.geye.bean.RingBean;
import com.rl.geye.constants.Constants;
import com.rl.geye.logic.DataLogic;
import com.rl.geye.ui.aty.AboutAty;
import com.rl.geye.ui.aty.HelpCenterAty;
import com.rl.geye.ui.aty.PermissionAty;
import com.rl.geye.ui.aty.RingSetAty;
import com.rl.p2plib.BridgeService;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/13.
 * 首页 -- 设置
 */
public class HomeSetFrag extends BaseMyFrag implements UITableView.TableClickListener {


    private static final int REQUEST_CODE_RING_CALL = 41;
    private static final int REQUEST_CODE_RING_ALARM = 42;


    @BindView(R.id.table_01)
    UITableView table01;
    @BindView(R.id.table_02)
    UITableView table02;
    private CustomTableItem itemRingCall;
    private CustomTableItem itemRingAlarm;
    private SwitchTableItem itemVibrate;
    private SwitchTableItem itemNoDisturb;

    private CustomTableItem itemPermission;
    private CustomTableItem itemAbout;
    private CustomTableItem itemHelp;
    private CustomTableItem itemExit;
    private RingBean curCallRing;
    private RingBean curAlarmRing;
    private boolean vibrateEnable;
    private boolean noDisturbEnable;


    /**
     * switch item click and check changed callback
     */
    private SwitchTableItem.OnSwitchListener mOnSwitchListener = new SwitchTableItem.OnSwitchListener() {

        @Override
        public void onSwitchClick(int switchId) {
        }

        @Override
        public void onSwitchChanged(int switchId, boolean isChecked) {

            switch (switchId) {
                case R.id.tb_set_id_vibrate:
                    vibrateEnable = isChecked;
                    DataLogic.saveVibrateEnable(isChecked);
                    break;

                case R.id.tb_set_id_no_disturb:
                    noDisturbEnable = isChecked;
                    DataLogic.saveNoDisturb(isChecked);
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.frag_home_setting;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
        curCallRing = DataLogic.getRingBell();
        curAlarmRing = DataLogic.getRingAlarm();
        vibrateEnable = DataLogic.isVibrateEnable();
        noDisturbEnable = DataLogic.isNoDisturb();

        itemRingCall = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemRingAlarm = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemVibrate = new SwitchTableItem(getActivity());
        itemNoDisturb = new SwitchTableItem(getActivity());
        itemHelp = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemAbout = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemPermission = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemExit = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemRingCall.setName(getStringForFrag(R.string.ring_call));
        itemRingCall.setIconImageResource(R.mipmap.ic_set_ring_alarm);//TODO
        itemRingCall.setValue(curCallRing.getName());

        itemRingAlarm.setName(getStringForFrag(R.string.ring_alarm));
        itemRingAlarm.setIconImageResource(R.mipmap.ic_set_ring_alarm);
        itemRingAlarm.setValue(curAlarmRing.getName());

        itemVibrate.setName(getStringForFrag(R.string.str_vibrate));
        itemVibrate.setIconImageResource(R.mipmap.ic_set_vibrate);
        itemVibrate.setOnSwitchListener(R.id.tb_set_id_vibrate, mOnSwitchListener);
        itemVibrate.setChecked(vibrateEnable);


        itemNoDisturb.setName(getStringForFrag(R.string.status_block));
        itemNoDisturb.setIconImageResource(R.mipmap.ic_set_no_disturb);
        itemNoDisturb.setOnSwitchListener(R.id.tb_set_id_no_disturb, mOnSwitchListener);
        itemNoDisturb.setChecked(noDisturbEnable);

        itemAbout.setName(getStringForFrag(R.string.str_about));
        itemAbout.setIconImageResource(R.mipmap.ic_set_about);

        itemHelp.setName(getStringForFrag(R.string.help_center));
        itemHelp.setIconImageResource(R.mipmap.a2_ic_help);

        itemPermission.setName(getStringForFrag(R.string.permission_center));
        itemPermission.setIconImageResource(R.mipmap.a2_ic_permission);

        itemExit.setName(getStringForFrag(R.string.str_exit));
        itemExit.setIconImageResource(R.mipmap.ic_set_exit);

        table01.clear();
        table01.addViewItem(new ViewItem(itemRingCall, R.id.tb_set_ring_call));
        table01.addViewItem(new ViewItem(itemRingAlarm, R.id.tb_set_ring_alarm));
        table01.addViewItem(new ViewItem(itemVibrate, R.id.tb_set_id_vibrate));
        table01.addViewItem(new ViewItem(itemNoDisturb, R.id.tb_set_id_no_disturb));
        table01.commit();

        table02.clear();
        table02.addViewItem(new ViewItem(itemAbout, R.id.tb_set_about));
        table02.addViewItem(new ViewItem(itemHelp, R.id.tb_set_help));
        table02.addViewItem(new ViewItem(itemPermission, R.id.tb_set_permission));
        table02.addViewItem(new ViewItem(itemExit, R.id.tb_set_exit));
        table02.commit();

        table01.setTableClickListener(this);
        table02.setTableClickListener(this);

    }

    @Override
    public void onTableClick(ViewItem view) {
        Bundle bundle = new Bundle();
        switch (view.getViewId()) {
            case R.id.tb_set_ring_call:
                bundle.putInt(Constants.BundleKey.KEY_RING_TYPE, RingBean.RingType.CALL);
                gotoActivityForResult(RingSetAty.class, REQUEST_CODE_RING_CALL, bundle);
                break;
            case R.id.tb_set_ring_alarm:
                bundle.putInt(Constants.BundleKey.KEY_RING_TYPE, RingBean.RingType.ALARM);
                gotoActivityForResult(RingSetAty.class, REQUEST_CODE_RING_ALARM, bundle);
                break;

            case R.id.tb_set_id_vibrate:
                itemVibrate.toggleSwitch();
                break;

            case R.id.tb_set_id_no_disturb:
                itemNoDisturb.toggleSwitch();
                break;

            case R.id.tb_set_about:
                gotoActivity(AboutAty.class);
                break;
            case R.id.tb_set_help:
                gotoActivity(HelpCenterAty.class);
                break;
            case R.id.tb_set_permission:
                gotoActivity(PermissionAty.class);
                break;
            case R.id.tb_set_exit:
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.str_exit)
                        .content(R.string.exit_app)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                Intent intent = new Intent();
                                intent.setClass(getActivity(), BridgeService.class);
                                getActivity().stopService(intent);

                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try {
                                            sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        AppManager.getInstance().AppExit();
                                    }
                                }.start();
                            }
                        }).show();
                break;
        }
    }

    @Override
    protected void lazyLoad() {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_RING_CALL) {
                curCallRing = DataLogic.getRingBell();
                itemRingCall.setValue(curCallRing.getName());
            } else if (requestCode == REQUEST_CODE_RING_ALARM) {
                curAlarmRing = DataLogic.getRingAlarm();
                itemRingAlarm.setValue(curAlarmRing.getName());
            }
        }

    }


    @Override
    protected void onAttachToContext(Context context) {

    }
}
