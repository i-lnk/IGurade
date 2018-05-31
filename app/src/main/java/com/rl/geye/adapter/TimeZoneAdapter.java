package com.rl.geye.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rl.geye.R;
import com.rl.p2plib.bean.DevTimeZone;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Nicky on 2016/10/31.
 * 时区列表适配
 */
public class TimeZoneAdapter extends BaseQuickAdapter<DevTimeZone, TimeZoneAdapter.MyViewHolder> {

    private DevTimeZone choosedItem;
    private boolean isOnline = true;

    public TimeZoneAdapter(List<DevTimeZone> data) {
        super(R.layout.item_rv_timezone, data);
    }

    public void chooseItem(DevTimeZone choosedItem) {
        this.choosedItem = choosedItem;
        notifyDataSetChanged();
    }

    public void chooseItem(int position) {
        this.choosedItem = (DevTimeZone) getItem(position);
        notifyDataSetChanged();
    }


    public void setOnline(boolean online) {
        isOnline = online;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(final MyViewHolder helper, final DevTimeZone data) {
        if (data != null) {
            final int realPosition = helper.getLayoutPosition() - getHeaderLayoutCount();
            helper.tvCity.setText(data.getCity());
            helper.tvDesc.setText(data.getDesc());

            if (choosedItem != null && data.equals(choosedItem)) {
                helper.iconChoose.setVisibility(View.VISIBLE);
            } else {
                helper.iconChoose.setVisibility(View.GONE);
            }
            if (realPosition < mData.size() - 1) {
                helper.bottomLine.setVisibility(View.GONE);
                helper.middleLine.setVisibility(View.VISIBLE);
            } else {
                helper.bottomLine.setVisibility(View.VISIBLE);
                helper.middleLine.setVisibility(View.GONE);
            }
            if (isOnline) {
                helper.bgEnable.setVisibility(View.GONE);
            } else {
                helper.bgEnable.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = super.getItemView(layoutResId, parent);
        view.setTag(R.id.BaseQuickAdapter_databinding_support, 1);
        return view;
    }


    public static class MyViewHolder extends BaseViewHolder {


        @BindView(R.id.tv_city)
        TextView tvCity;
        @BindView(R.id.tv_desc)
        TextView tvDesc;
        @BindView(R.id.ic_check)
        ImageView iconChoose;
        @BindView(R.id.middle_line)
        View middleLine;
        @BindView(R.id.bottom_line)
        View bottomLine;
        @BindView(R.id.ly_item)
        View lyItem;
        @BindView(R.id.bg_enable)
        View bgEnable;


        public MyViewHolder(View itemView) {
            super(itemView);
//            Logger.e("itemView: "+itemView);
            if (itemView.getTag(R.id.BaseQuickAdapter_databinding_support) != null) {
                ButterKnife.bind(this, itemView);
            }
        }

    }


}
