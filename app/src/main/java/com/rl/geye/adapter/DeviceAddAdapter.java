package com.rl.geye.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rl.geye.R;
import com.rl.geye.db.bean.EdwinDevice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Nicky on 2016/10/31.
 * 设备列表适配
 */
public class DeviceAddAdapter extends BaseMultiItemQuickAdapter<EdwinDevice, DeviceAddAdapter.MyViewHolder> {


    public DeviceAddAdapter(List<EdwinDevice> data) {
        super(data);
        addItemType(EdwinDevice.ITEM_VIEW_ADDED, R.layout.item_rv_dev_added);
        addItemType(EdwinDevice.ITEM_VIEW_NEW, R.layout.item_rv_dev_new);
    }


    @Override
    protected void convert(final MyViewHolder helper, final EdwinDevice data) {
        helper.tvId.setText(data.getDevId());
        helper.tvName.setText(data.getName());
        helper.ivItem.setImageResource(R.mipmap.ic_devset_name);//TODO
        switch (data.getItemType()) {
            case EdwinDevice.ITEM_VIEW_ADDED:
                break;
            case EdwinDevice.ITEM_VIEW_NEW:
                helper.setChecked(R.id.checkbox, data.isChecked());
                break;
        }
    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = super.getItemView(layoutResId, parent);
        view.setTag(R.id.BaseQuickAdapter_databinding_support, 1);
        return view;
    }

    public List<EdwinDevice> getAllChecked() {
        List<EdwinDevice> checkDatas = new ArrayList<>();
        for (EdwinDevice item : getData()) {
            if (item.isChecked() && !item.isAdded()) {
                checkDatas.add(item);
            }
        }
        return checkDatas;
    }

    public boolean hasNewDevice() {
        for (EdwinDevice item : getData()) {
            if (!item.isAdded()) {
                return true;
            }
        }
        return false;
    }


    public static class MyViewHolder extends BaseViewHolder {

        @BindView(R.id.iv_item)
        ImageView ivItem;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_id)
        TextView tvId;

        public MyViewHolder(View itemView) {
            super(itemView);
//            Logger.e("itemView: "+itemView);
            if (itemView.getTag(R.id.BaseQuickAdapter_databinding_support) != null) {
                ButterKnife.bind(this, itemView);
            }
        }

    }


}
