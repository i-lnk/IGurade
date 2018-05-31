package com.rl.geye.ui.aty;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nicky.framework.slidinglayout.SlidingLayout;
import com.rl.geye.R;
import com.rl.geye.base.BaseMyAty;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nicky on 2017/9/20.
 */

public class HelpAddAty extends BaseMyAty {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.ly_all)
    SlidingLayout lyAll;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_help_add;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.add_help_title);
    }

    @Override
    protected void initViewsAndEvents() {

    }

    @Override
    protected void onClickView(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
