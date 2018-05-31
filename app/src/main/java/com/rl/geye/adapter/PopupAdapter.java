package com.rl.geye.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicky.framework.base.BaseXAdapter;
import com.rl.geye.R;
import com.rl.geye.bean.StreamItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author NickyHuang
 * @ClassName: PopupAdapter
 * @Description: popupWindow 内容适配器
 * @date 2016-4-5 上午10:06:09
 */
public class PopupAdapter extends BaseXAdapter<StreamItem> {

    private StreamItem choosedItem;

    public PopupAdapter(Context context) {
        super(context);
    }

    public PopupAdapter(Context context, List<StreamItem> datas) {
        super(context, datas);
    }

    public void chooseItem(StreamItem choosedItem) {
        this.choosedItem = choosedItem;
        notifyDataSetChanged();
    }

    public void chooseItem(int position) {
        this.choosedItem = getItem(position);
        notifyDataSetChanged();
    }


    @Override
    protected ViewHolder<StreamItem> newItemView() {
        return new PopupViewHolder();
    }

    class PopupViewHolder extends ViewHolder<StreamItem> {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.icon_item)
        ImageView icon;

        @Override
        public int inflateViewId() {
            return R.layout.item_list_popup;
        }

        @Override
        public void initBind(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @Override
        public void bindingData(View convertView, StreamItem data) {
            if (data.hasIcon()) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(data.getIconResid());
                if (choosedItem != null && data.equals(choosedItem)) {
                    icon.setSelected(true);
                } else {
                    icon.setSelected(false);
                }
            } else {
                icon.setVisibility(View.GONE);
            }
            tvName.setText(data.getName());
        }


    }


}
