package com.rl.geye.ui.aty;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nicky.framework.slidinglayout.SlidingLayout;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/9/25.
 */

public class HelpCenterAty extends BaseMyAty {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.ly_all)
    SlidingLayout lyAll;
//    @BindView(R.id.table_01)
//    UITableView table01;
//    private CustomTableItem itemHelpPush;
//    private CustomTableItem itemHelpPermission;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_help_center;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.help_center);
    }

    @Override
    protected void initViewsAndEvents() {
//        itemHelpPush = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
//        itemHelpPermission = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_COMMON);
//
//        itemHelpPush.setName(getString(R.string.help_push));
//        itemHelpPush.setIconVisibility(View.GONE);
//
//        itemHelpPermission.setName(getString(R.string.help_permission));
//        itemHelpPermission.setIconVisibility(View.GONE);
//
//
//        table01.clear();
//        table01.addViewItem(new ViewItem(itemHelpPush, R.id.tb_help_push));
//        table01.addViewItem(new ViewItem(itemHelpPermission, R.id.tb_help_permission));
//        table01.commit();


    }

    @Override
    protected void onClickView(View v) {

    }


}
