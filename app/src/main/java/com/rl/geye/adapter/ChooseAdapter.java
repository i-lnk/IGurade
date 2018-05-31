package com.rl.geye.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicky.framework.base.BaseXAdapter;
import com.rl.commons.interf.Chooseable;
import com.rl.geye.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @param <TC>
 * @author NickyHuang
 * @ClassName: ChooseAdapter
 * @Description: 选择列表适配器
 * @date 2016-3-9
 */
public class ChooseAdapter<TC extends Chooseable> extends BaseXAdapter<TC> {

    private TC choosedItem;

    public ChooseAdapter(Context context) {
        super(context);
    }

    public ChooseAdapter(Context context, List<TC> datas) {
        super(context, datas);
    }

    public void chooseItem(TC choosedItem) {
        this.choosedItem = choosedItem;
        notifyDataSetChanged();
    }

    public void chooseItem(int position) {
        this.choosedItem = getItem(position);
        notifyDataSetChanged();
    }


    @Override
    protected ViewHolder<TC> newItemView() {
        return new ChooseViewHolder();
    }


    class ChooseViewHolder extends ViewHolder<TC> {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.icon_item)
        ImageView icon;

//		@BindView(R.id.icon_choose)
//		ImageView iconChoose;

        @BindView(R.id.icon_choose)
        AppCompatRadioButton iconChoose;

        @Override
        public int inflateViewId() {
            return R.layout.item_list_choose;
        }

        @Override
        public void initBind(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @Override
        public void bindingData(View convertView, TC data) {
            if (data.hasIcon()) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(data.getIconResid());
            } else {
                icon.setVisibility(View.GONE);
            }
            icon.setVisibility(data.hasIcon() ? View.VISIBLE : View.GONE);
            tvName.setText(data.getName());

            if (choosedItem != null && data.equals(choosedItem)) {
                iconChoose.setChecked(true);
            } else
                iconChoose.setChecked(false);

        }
    }
}
