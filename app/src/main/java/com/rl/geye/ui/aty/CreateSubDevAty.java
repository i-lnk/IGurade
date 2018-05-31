package com.rl.geye.ui.aty;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nicky.framework.widget.XEditText;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.R;
import com.rl.geye.adapter.ChooseAdapter;
import com.rl.geye.base.BaseMyAty;
import com.rl.geye.bean.SubType;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/11/7.
 * 新增子设备
 */
public class CreateSubDevAty extends BaseMyAty implements AdapterView.OnItemClickListener {

    private static final int[] typeIconResId = {
            R.mipmap.ic_433_remote_2, R.mipmap.ic_433_alarm_2, R.mipmap.ic_433_other_2
    };
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_name)
    XEditText etName;
    @BindView(R.id.iv_name)
    ImageView ivName;
    @BindView(R.id.lv_type)
    ListView listView;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;
    private ChooseAdapter<SubType> mAdapter;
    private List<SubType> mDataList = new ArrayList<>();
    private EdwinDevice mDevice;
    private SubType mSubDevType;

    @Override
    protected int getLayoutId() {
        return R.layout.aty_create_sub_dev;
    }

    @Override
    protected boolean initPrepareData() {
        if (fromIntent != null) {
            mDevice = fromIntent.getParcelableExtra(Constants.BundleKey.KEY_DEV_INFO);
        }
        return mDevice != null;
    }

    @Override
    protected View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
        tvTitle.setText(R.string.str_device);
    }

    @Override
    protected void initViewsAndEvents() {
        mDataList.clear();
        mDataList.add(new SubType(P2PConstants.SubDevType.REMOTE_CONTROL));
        mDataList.add(new SubType(P2PConstants.SubDevType.ALARM));
        mDataList.add(new SubType(P2PConstants.SubDevType.OTHER));

        btnNext.setOnClickListener(this);
        mAdapter = new ChooseAdapter<>(this, mDataList);
        mAdapter.chooseItem(0);
        mSubDevType = mDataList.get(0);
        listView.setAdapter(mAdapter);
        btnNext.setEnabled(false);

        listView.setOnItemClickListener(this);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onDataChanged();
            }
        });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                etName.requestFocus();
////				Window window = getActivity().getWindow();
////				window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//				imm.showSoftInput(etId, InputMethodManager.SHOW_FORCED); //强制显示键盘
            }
        }, 100);
    }

    private void onDataChanged() {
        if (!StringUtils.isEmpty(etName.getText().toString().trim()) && mSubDevType != null) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {

            case R.id.btn_next:
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                bundle.putString(Constants.BundleKey.KEY_SUB_NAME, etName.getText().toString().trim());
                bundle.putInt(Constants.BundleKey.KEY_SUB_TYPE, mSubDevType.getType());
                gotoActivity(CheckCodeAty.class, bundle);
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mAdapter.chooseItem(position);
        mSubDevType = mDataList.get(position);
        ivName.setImageResource(typeIconResId[position]);
        onDataChanged();
    }

}
