package com.rl.geye.ui.aty;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.SwitchTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.util.PermissionUtil;

import butterknife.BindView;

/**
 * Author: SXF
 * E-mail: xue.com.fei@outlook.com
 * CreatedTime: 2018/4/22 16:01
 * <p>
 * PermissionAty
 */
public class PermissionAty extends BaseMyAty implements UITableView.TableClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.table_01)
    UITableView table01;

    @BindView(R.id.table_02)
    UITableView table02;

    private SwitchTableItem itemNotification;
    private SwitchTableItem itemIgnoreBattery;

    private CustomTableItem itemLockScreen;
    private CustomTableItem itemAutoStart;
    private CustomTableItem itemBackground;

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
                case R.id.tb_set_notification:
                    if (isChecked) {
                        PermissionUtil.mayRequestNotificationEnabled(PermissionAty.this);
                    }
                    break;
                case R.id.tb_set_ignore_battery:
                    if (isChecked) {
                        PermissionUtil.mayIgnoringBatteryOptimizations(PermissionAty.this, PermissionUtil.IGNOR_BATTERY);
                    }
                    break;
            }
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.aty_permission;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.permission_center);
    }

    @Override
    protected void initViewsAndEvents() {
        itemNotification = new SwitchTableItem(this);
        itemIgnoreBattery = new SwitchTableItem(this);

        itemLockScreen = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemAutoStart = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
        itemBackground = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);

        itemNotification.setName(getString(R.string.permission_notification_bar));
        itemNotification.setIconImageResource(R.mipmap.ic_message);
        itemNotification.setChecked(PermissionUtil.checkAreNotificationEnabled(this));
        itemNotification.setOnSwitchListener(R.id.tb_set_notification, mOnSwitchListener);
        itemNotification.setBgEnabled(!itemNotification.isChecked());

        itemIgnoreBattery.setName(getString(R.string.permission_ignore_battery));
        itemIgnoreBattery.setIconImageResource(R.mipmap.ic_protect);
        itemIgnoreBattery.setChecked(PermissionUtil.checkIgnoringBatteryOptimizations(this));
        itemIgnoreBattery.setOnSwitchListener(R.id.tb_set_ignore_battery, mOnSwitchListener);
        itemIgnoreBattery.setBgEnabled(!itemIgnoreBattery.isChecked());

        itemLockScreen.setName(getString(R.string.permission_lock_screen));
        itemLockScreen.setIconImageResource(R.mipmap.ic_protect);

        itemAutoStart.setName(getString(R.string.permission_autorun));
        itemAutoStart.setIconImageResource(R.mipmap.ic_protect);

        itemBackground.setName(getString(R.string.permission_background));
        itemBackground.setIconImageResource(R.mipmap.ic_protect);

        table01.clear();
        table01.addViewItem(new ViewItem(itemNotification, R.id.tb_set_notification));
        table01.addViewItem(new ViewItem(itemIgnoreBattery, R.id.tb_set_ignore_battery));
        table01.commit();

        table02.clear();
        if (Build.BRAND.equals("xiaomi") || Build.BRAND.equals("Xiaomi")) {
            table02.addViewItem(new ViewItem(itemLockScreen, R.id.tb_set_lock_screen));
        }
        table02.addViewItem(new ViewItem(itemAutoStart, R.id.tb_set_autostart));
        table02.addViewItem(new ViewItem(itemBackground, R.id.tb_set_background));
        table02.commit();

        table01.setTableClickListener(this);
        table02.setTableClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionUtil.checkAreNotificationEnabled(this)) {
            itemNotification.setChecked(false);
        } else {
            itemNotification.setChecked(true);
            itemNotification.setBgEnabled(false);
        }

        if (!PermissionUtil.checkIgnoringBatteryOptimizations(this)) {
            itemIgnoreBattery.setChecked(false);
        } else {
            itemIgnoreBattery.setChecked(true);
            itemIgnoreBattery.setBgEnabled(false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected void onClickView(View v) {

    }

    @Override
    public void onTableClick(ViewItem view) {
        switch (view.getViewId()) {
            case R.id.tb_set_lock_screen:
                PermissionUtil.mayRequestAllPermissionSetting(this);
                break;
            case R.id.tb_set_autostart:
                PermissionUtil.mayRequestSelfLaunching(Build.BRAND, this);
                break;
            case R.id.tb_set_background:
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.permission_background)
                        .content(R.string.permission_background_msg)
                        .positiveText(R.string.str_ok)
                        .negativeText(R.string.str_cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        }).show();
                break;
        }
    }
}
