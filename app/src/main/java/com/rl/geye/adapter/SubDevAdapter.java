package com.rl.geye.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicky.framework.base.BaseXAdapter;
import com.rl.geye.R;
import com.rl.geye.db.bean.SubDevice;
import com.rl.p2plib.constants.P2PConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @author NickyHuang
 * @ClassName: SubDevAdapter
 * @Description: 433子设备列表适配器
 * @date 2016-5-5 上午10:43:06
 */
public class SubDevAdapter extends BaseXAdapter<SubDevice> {


    public SubDevAdapter(Context context) {
        super(context);
    }

    public SubDevAdapter(Context context, List<SubDevice> datas) {
        super(context, datas);
    }


    @Override
    protected ViewHolder<SubDevice> newItemView() {
        return new SubDevViewHolder();
    }

    class SubDevViewHolder extends ViewHolder<SubDevice> {

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_desc)
        TextView tvDesc;

        @BindView(R.id.ic_dev)
        ImageView icDev;

        @BindView(R.id.bottom_line)
        View bottomLine;

        @BindView(R.id.middle_line)
        View middleLine;

        @Override
        public int inflateViewId() {
            return R.layout.item_list_subdev;
        }

        @Override
        public void initBind(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @Override
        public void bindingData(View convertView, SubDevice data) {
            tvName.setText(data.getName());
//				tvDesc.setText( data.isBlock()?mContext.getString(R.string.status_block):
//						mContext.getString(R.string.status_ok) );
            tvDesc.setText(R.string.status_ok);

            switch (data.getType()) {
                case P2PConstants.SubDevType.REMOTE_CONTROL:
                    icDev.setImageResource(R.mipmap.ic_433_remote);
                    break;
                case P2PConstants.SubDevType.ALARM:
                    icDev.setImageResource(R.mipmap.ic_433_alarm);
                    break;
                case P2PConstants.SubDevType.OTHER:
                    icDev.setImageResource(R.mipmap.ic_433_other);
                    break;
            }
        }

        @Override
        public void updateConvertView(View convertView, SubDevice data, int position) {
            super.updateConvertView(convertView, data, position);
            if (position < getCount() - 1) {
                bottomLine.setVisibility(View.GONE);
                middleLine.setVisibility(View.VISIBLE);
            } else {
                bottomLine.setVisibility(View.VISIBLE);
                middleLine.setVisibility(View.GONE);
            }
        }
    }


}
