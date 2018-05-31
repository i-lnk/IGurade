package com.rl.geye.ui.frag;


import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;
import com.nicky.framework.tableview.CustomTableItem;
import com.nicky.framework.tableview.UITableView;
import com.nicky.framework.tableview.ViewItem;
import com.rl.geye.R;
import com.rl.geye.base.BaseDevAddFrag;
import com.rl.geye.constants.Constants;
import com.rl.geye.ui.aty.HelpAddAty;

import butterknife.BindView;


/**
 * Created by Nicky on 2016/9/18.
 * 添加设备步骤 选择方式
 */
public class AddStepChooseFrag extends BaseDevAddFrag {

    private static final int ID_AP = 12;
    private static final int ID_QR = 13;
    private static final int ID_WIRED = 14;

    @BindView(R.id.tb_01)
    UITableView tb01;

    @BindView(R.id.tv_help)
    TextView tvHelp;

    private CustomTableItem itemAp;   // Ap方式
    private CustomTableItem itemWired;   // 有线方式
    private CustomTableItem itemQR;   // 二维码方式
    private OnEvents mListener;

    public interface OnEvents  {
        void gotoNextForChoose(int chooseType);
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        //do something
        if (context instanceof OnEvents) {
            mListener = (OnEvents) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStepChooseEvents");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_add_step_choose;
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

        itemWired = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_DESC);
        itemAp = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_DESC);
        itemQR = new CustomTableItem(getActivity(), CustomTableItem.ITEM_TYPE_DESC);


        itemAp.setName(getStringForFrag(R.string.ap_config));
        itemAp.setDesc(getStringForFrag(R.string.ap_desc));
        itemAp.setIconImageResource(R.mipmap.a1_ic_choose_1);
        itemAp.setMinHeight(getResources().getDimensionPixelSize(com.nicky.framework.R.dimen.table_min_height_v2));

        itemWired.setName(getStringForFrag(R.string.wired_config));
        itemWired.setDesc(getStringForFrag(R.string.wired_desc));
        itemWired.setIconImageResource(R.mipmap.a1_ic_choose_2);
        itemWired.setMinHeight(getResources().getDimensionPixelSize(com.nicky.framework.R.dimen.table_min_height_v2));

        itemQR.setName(getStringForFrag(R.string.qrcode_config));
        itemQR.setDesc(getStringForFrag(R.string.qrcode_desc));
        itemQR.setIconImageResource(R.mipmap.a1_ic_choose_3);
        itemQR.setMinHeight(getResources().getDimensionPixelSize(com.nicky.framework.R.dimen.table_min_height_v2));

        tb01.clear();
        tb01.addViewItem(new ViewItem(itemQR, ID_QR));
        tb01.addViewItem(new ViewItem(itemAp, ID_AP));
        tb01.addViewItem(new ViewItem(itemWired, ID_WIRED));
        tb01.commit();

        tb01.setTableClickListener(new UITableView.TableClickListener() {
            @Override
            public void onTableClick(ViewItem view) {
                switch (view.getViewId()) {
                    case ID_WIRED:
                        mListener.gotoNextForChoose(Constants.AddType.WIRED);
                        break;
                    case ID_AP:
                        mListener.gotoNextForChoose(Constants.AddType.AP);
                        break;
                    case ID_QR:
                        mListener.gotoNextForChoose(Constants.AddType.QR);
                        break;
                }
            }
        });

        tvHelp.getPaint().setFlags(tvHelp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvHelp.getPaint().setAntiAlias(true);//抗锯齿


        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(HelpAddAty.class);
            }
        });

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
