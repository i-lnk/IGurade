package com.rl.geye.ui.frag;

import android.Manifest;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rl.commons.interf.PermissionResultCallback;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 1
 */
public class AddStepInitFrag extends BaseDevAddFrag {

    private static final int REQUEST_CODE_FOR_LOCATION = 2222;
    @BindView(R.id.iv_dev_step)
    ImageView ivStep;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.btn_next)
    Button btnNext;
    private OnEvents mListener;

    @Override
    protected void onAttachToContext(Context context) {
        if (context instanceof OnEvents) {
            mListener = (OnEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEvents");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_step_init;
    }

    @Override
    public View getVaryTargetView() {
        return null;
    }

    @Override
    protected void initToolBar() {

    }

    @Override
    protected void initViewsAndEvents() {
//        btnNext.getBackground().setLevel(2);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastClick(getActivity(), view))
                    return;
                checkPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_CODE_FOR_LOCATION, new PermissionResultCallback() {
                    @Override
                    public void onPermissionGranted() {
                        mListener.gotoNextForInit();
                    }

                    @Override
                    public void onPermissionDenied() {

                    }
                });

            }
        });


    }

    @Override
    protected void lazyLoad() {

    }

    public interface OnEvents {
        void gotoNextForInit();
    }


}
