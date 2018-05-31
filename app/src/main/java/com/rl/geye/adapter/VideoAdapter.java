package com.rl.geye.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicky.framework.base.BaseXAdapter;
import com.rl.geye.R;
import com.rl.p2plib.bean.EdwinVideo;
import com.rl.p2plib.constants.CmdConstant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 日志适配器
 */
public class VideoAdapter extends BaseXAdapter<EdwinVideo> {


    public VideoAdapter(Context context) {
        super(context);
    }

    public VideoAdapter(Context context, List<EdwinVideo> datas) {
        super(context, datas);
    }


    @Override
    protected ViewHolder<EdwinVideo> newItemView() {
        return new MyViewHolder();
    }

    class MyViewHolder extends ViewHolder<EdwinVideo> {

        @BindView(R.id.iv_item)
        ImageView ivItem;

        @BindView(R.id.tv_name)
        TextView tvName;
//        @BindView(R.id.tv_size)
//        TextView tvSize;

        @BindView(R.id.tv_category)
        TextView tvCategory;


        @Override
        public int inflateViewId() {
            return R.layout.item_list_video;
        }

        @Override
        public void initBind(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @Override
        public void bindingData(View convertView, EdwinVideo data) {
            tvName.setText(data.getName());
            if (CmdConstant.CmdType.AVIOCTRL_EVENT_RINGBELL == data.getEvent()) {
                if (1 == data.getReply()) {
                    tvCategory.setText(R.string.Filevideo_event_ringbell_no_answer);
                    tvCategory.setTextColor(mContext.getResources().getColor(R.color.red));
                } else if (2 == data.getReply()) {
                    tvCategory.setText(R.string.Filevideo_event_ringbell_answer);
                    tvCategory.setTextColor(mContext.getResources().getColor(R.color.btn_blue));
                } else {
                    tvCategory.setVisibility(View.GONE);
                }
            } else if (CmdConstant.CmdType.AVIOCTRL_EVENT_PIR == data.getEvent()) {
                tvCategory.setText(R.string.Filevideo_event_pir);
                tvCategory.setTextColor(mContext.getResources().getColor(R.color.text_video_green));
            } else if (CmdConstant.CmdType.AVIOCTRL_EVENT_MOTIONDECT == data.getEvent()) {
                tvCategory.setText(R.string.motion_detect);
                tvCategory.setTextColor(mContext.getResources().getColor(R.color.text_video_green));
            } else {
                tvCategory.setVisibility(View.GONE);
            }
        }

        @Override
        public void updateConvertView(View convertView, EdwinVideo data, int position) {
            super.updateConvertView(convertView, data, position);
        }
    }


}
