package com.rl.geye.ui.widget;


import android.app.Activity;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.rl.geye.R;


/**
 * @author NickyHuang
 * @ClassName: CameraMenuWin
 * @Description: 预置位操作菜单
 * @date 2015-10-14
 */
public class CameraMenuWin extends PopupWindow implements OnClickListener {


    int popupWidth = 0;
    int popupHeight = 0;
    private Activity mContext;
    private RelativeLayout mLayout;
    private ImageView iv01;
    private ImageView iv02;
    private ImageView iv03;
    private ImageView iv04;
    private ImageView ivSet;

    private Handler mHandler = new Handler();

    private boolean isEditMode;

    private String devId;

    public CameraMenuWin(Activity context, String devId) {
        mContext = context;
        this.devId = devId;
        init();
    }

    public void init() {

        mLayout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.view_set_menu, null);
        setContentView(mLayout);


        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        mLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupWidth = mLayout.getMeasuredWidth();
        popupHeight = mLayout.getMeasuredHeight();


        iv01 = (ImageView) mLayout.findViewById(R.id.iv_menu_1);
        iv02 = (ImageView) mLayout.findViewById(R.id.iv_menu_2);
        iv03 = (ImageView) mLayout.findViewById(R.id.iv_menu_3);
        iv04 = (ImageView) mLayout.findViewById(R.id.iv_menu_4);
        ivSet = (ImageView) mLayout.findViewById(R.id.iv_menu_set);


        isEditMode = false;
        onEditChanged();


        this.setOutsideTouchable(true);
        this.setFocusable(true);
        mLayout.setFocusableInTouchMode(true);
        mLayout.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    hideMenu();
                }
                return false;
            }
        });

        mLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        hideMenu();

                        break;
                    case MotionEvent.ACTION_UP:
                        v.performClick();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


        iv01.setOnClickListener(this);
        iv02.setOnClickListener(this);
        iv03.setOnClickListener(this);
        iv04.setOnClickListener(this);
        ivSet.setOnClickListener(this);

    }

    public void hideMenu() {
        if (isShowing()) {
            dismiss();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_menu_set:
                isEditMode = !isEditMode;
                onEditChanged();
                break;
            case R.id.iv_menu_1:
                sendCmd(0);
                break;
            case R.id.iv_menu_2:
                sendCmd(1);
                break;
            case R.id.iv_menu_3:
                sendCmd(2);
                break;
            case R.id.iv_menu_4:
                sendCmd(3);
                break;
            default:
                return;
        }
//		if (isShowing()) {
//			mHandler.postDelayed(new Runnable() {
//
//				@Override
//				public void run() {
//					dismiss();
//				}
//			}, 500);
//			closeAnimation(mLayout);
//		}
    }

    public void destroy() {
        isEditMode = false;
    }


    private void onEditChanged() {
        if (isEditMode) {
            iv01.setImageLevel(2);
            iv02.setImageLevel(2);
            iv03.setImageLevel(2);
            iv04.setImageLevel(2);
            ivSet.setImageResource(R.mipmap.bt_b2_01);
        } else {
            iv01.setImageLevel(1);
            iv02.setImageLevel(1);
            iv03.setImageLevel(1);
            iv04.setImageLevel(1);
            ivSet.setImageResource(R.mipmap.bt_b1_01);
        }
    }


    private void sendCmd(int index) {
        if (isEditMode) {
            ApiMgrV2.setPresetPos(devId, index);
        } else {
            ApiMgrV2.gotoPresetPos(devId, index);
        }
    }


    public void showWindow(View anchor) {
//		showAnimation(mLayout);
//		setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
        isEditMode = false;
        onEditChanged();
    }

}


