package com.rl.geye.ui.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.rl.geye.R;
import com.rl.geye.adapter.PopupAdapter;


/**
 * @author NickyHuang
 * @ClassName: XPopupWindow
 * @Description: 弹出框
 * @date 2016-4-5 上午11:05:02
 */
@SuppressLint("NewApi")
public class XPopupWindow extends PopupWindow {

    int popupWidth = 0;
    int popupHeight = 0;
    private Context mContext;
    private PopupAdapter mAdapter;
    private ListView listViewDown;
    private ListView listViewUp;

    //	private View rootView;
//	private boolean isPopup = false;
    private RelativeLayout layoutUp;
    private RelativeLayout layoutDown;

    public XPopupWindow(Context context) {
        super(context);
        mContext = context;
        initView();
    }

//	public void setTitle(String title){
//		if( tvTitle!=null ){
//			tvTitle.setVisibility(View.VISIBLE);
//			tvTitle.setText(title);
//		}
//	}

    public XPopupWindow(Context context, PopupAdapter adapter) {
        super(context);
        mContext = context;
        this.mAdapter = adapter;
        initView();
    }

    public void setAdapter(PopupAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (listViewDown != null && listViewDown.getVisibility() == View.VISIBLE) {
            listViewDown.setOnItemClickListener(listener);
        }
        if (listViewUp != null && listViewUp.getVisibility() == View.VISIBLE) {
            listViewUp.setOnItemClickListener(listener);
        }
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.view_popup, null);
        setContentView(popupView);

        // 设置宽度,若没有设置宽度为LayoutParams.WRAP_CONTENT
//		setWidth(dp2px(130));
        setHeight(LayoutParams.WRAP_CONTENT);
        setWidth(LayoutParams.WRAP_CONTENT);

        // 设置动画，也可以不设置。不设置则是显示默认的
//		setAnimationStyle(R.style.AnimTop);

        // 这里很重要，不设置这个ListView得不到相应
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);

//		rootView = popupView.findViewById(R.id.root_view);

        layoutUp = (RelativeLayout) popupView.findViewById(R.id.layout_up);
        layoutDown = (RelativeLayout) popupView.findViewById(R.id.layout_down);
        listViewUp = (ListView) popupView.findViewById(R.id.list_menus_up);
        listViewDown = (ListView) popupView.findViewById(R.id.list_menus_down);

        listViewUp.setAdapter(mAdapter);
        listViewDown.setAdapter(mAdapter);

        popupView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        popupWidth = popupView.getMeasuredWidth();
        popupHeight = popupView.getMeasuredHeight();

//		tvTitle = (TextView) popupView.findViewById(R.id.tv_title);

    }

    @Override
    public void showAsDropDown(View anchor) {

        layoutUp.setVisibility(View.GONE);
        layoutDown.setVisibility(View.VISIBLE);

        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {

        layoutUp.setVisibility(View.GONE);
        layoutDown.setVisibility(View.VISIBLE);
        super.showAsDropDown(anchor, xoff, yoff);
    }


    public void showAsPullUp(View anchor) {
        showAsPullUp(anchor, 0, 0);
    }

    /**
     * 在指定控件上方显示，默认x座标与指定控件的中点x座标相同
     *
     * @param anchor
     * @param xoff
     * @param yoff
     */
    public void showAsPullUp(View anchor, int xoff, int yoff) {
        layoutDown.setVisibility(View.GONE);
        layoutUp.setVisibility(View.VISIBLE);

//		int height = mContext.getResources().getDimensionPixelSize(R.dimen.popup_height);
//		layoutUp.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		int popupWidth = layoutUp.getMeasuredWidth();
//		int popupHeight = layoutUp.getMeasuredHeight();

//		XLog.i("NickyTag", "*******---------------->popupHeight: "+popupHeight);

        //保存anchor在屏幕中的位置
        int[] location = new int[2];
        //保存anchor上部中点
        int[] anchorCenter = new int[2];
        //读取位置anchor座标
        anchor.getLocationOnScreen(location);
        //计算anchor中点
        anchorCenter[0] = location[0];//+anchor.getWidth()/2;
        anchorCenter[1] = location[1] - anchor.getHeight();

//    	XLog.i("NickyTag", "*******---------------->anchorCenter y: "+anchorCenter[1]);


        //Gravity.TOP|Gravity.LEFT
        super.showAtLocation(anchor, Gravity.NO_GRAVITY,
                anchorCenter[0] + xoff,
                anchorCenter[1] - popupHeight + yoff);
    }


}
