package com.rl.geye.ui.aty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicky.framework.widget.PagerSlidingTabStrip;
import com.nicky.framework.widget.XViewPager;
import com.rl.geye.R;
import com.rl.geye.adapter.MyFragPageAdapter;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.ui.frag.SetDevNormalFrag;
import com.rl.geye.ui.frag.SetDevSubListFrag;

import butterknife.BindView;

/**
 * Created by Nicky on 2017/8/12.
 * 设备设置
 */

public class SetDevAty extends BaseP2PAty {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.viewpager_set)
    XViewPager viewpagerSet;
    @BindView(R.id.btn_check_code)
    Button btnCheckCode;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;

    private SetDevNormalFrag fragNormal;
    private SetDevSubListFrag fragSublist;
    private MyFragPageAdapter mAdapter;

    @Override
    protected void onP2PStatusChanged() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.aty_set_dev;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.dev_set);
    }

    @Override
    protected void initViewsAndEvents() {
        int tabTextSize = getResources().getDimensionPixelSize(R.dimen.text_15);
        if (fragNormal == null) {
            fragNormal = new SetDevNormalFrag();
        }
        fragNormal.setArguments(fromIntent.getExtras());
        if (fragSublist == null) {
            fragSublist = new SetDevSubListFrag();
        }
        fragSublist.setArguments(fromIntent.getExtras());
        Fragment[] frags = new Fragment[]{fragNormal, fragSublist};
        CharSequence[] titles = new CharSequence[]{getString(R.string.tab_dev_set_2),
                getString(R.string.tab_dev_set_3)};
        mAdapter = new MyFragPageAdapter(getSupportFragmentManager(), frags, titles);
        viewpagerSet.setAdapter(mAdapter);
        viewpagerSet.setOffscreenPageLimit(3);

        tabs.setTextSize(tabTextSize);
        // Bind the tabs to the ViewPager
        tabs.setViewPager(viewpagerSet);


        btnCheckCode.setVisibility(mDevice.isIpc() ? View.VISIBLE : View.GONE);
        btnCheckCode.setOnClickListener(this);
        btnCheckCode.setEnabled(isOnline);
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {

            case R.id.btn_check_code:
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                gotoActivity(CreateSubDevAty.class, bundle);
                break;
        }
    }

}
