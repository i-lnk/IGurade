package com.rl.geye.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rl.commons.utils.ClickUtil;
import com.rl.commons.utils.FileUtil;
import com.rl.geye.R;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.geye.util.PhotoVideoUtil;
import com.rl.p2plib.constants.P2PConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Nicky on 2016/10/31.
 * 设备列表适配
 */
public class DeviceAdapter extends BaseQuickAdapter<EdwinDevice, DeviceAdapter.MyViewHolder> {
    private OnMenuClickListener mListener;

    public DeviceAdapter(List<EdwinDevice> data) {
        super(R.layout.item_rv_dev, data);
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mListener = listener;
    }

    @Override
    protected void convert(final MyViewHolder helper, final EdwinDevice data) {
        if (data != null) {
            if (FileUtil.isFileExist(data.getBgPath())) {
                Glide.with(mContext)
                        .load(data.getBgPath())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
//                  .placeholder(R.drawable.bg_photo_loading)
                        .error(R.mipmap.a0_bg_dev)
                        .crossFade()
                        .into(helper.bgItem);
//                tvName.setTextColor( ContextCompat.getColor(mContext,R.color.white));
//                tvStatus.setTextColor( ContextCompat.getColor(mContext,R.color.white));
                helper.bgItem2.setVisibility(View.VISIBLE);
            } else {
                String filePath = PhotoVideoUtil.getPhotoDirPath(false) + data.getDevId() + "/" + Constants.DEVICE_BG_NAME;
                if (FileUtil.isFileExist(filePath)) {
                    Glide.with(mContext)
                            .load(filePath)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
//                          .placeholder(R.drawable.bg_photo_loading)
                            .error(R.mipmap.a0_bg_dev)
                            .crossFade()
                            .into(helper.bgItem);
                    helper.bgItem2.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(mContext)
                            .load(data.getBgPath())
//                          .skipMemoryCache(true)
//                          .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
//                          .placeholder(R.drawable.bg_photo_loading)
                            .error(R.mipmap.a0_bg_dev)
                            .crossFade()
                            .into(helper.bgItem);
                    helper.bgItem2.setVisibility(View.GONE);
                }
//                tvName.setTextColor( ContextCompat.getColor(mContext,R.color.text_black));
//                tvStatus.setTextColor( ContextCompat.getColor(mContext,R.color.text_q2));
            }

            helper.tvStatus.getCompoundDrawables()[0].setLevel(1);
            helper.ivPlay.setVisibility(View.GONE);
            helper.tvName.setText(data.getName());
//            Logger.i("dev status------------------->"+data.getStatus() );
            int resid;
            switch (data.getStatus()) {
                case P2PConstants.P2PStatus.NOT_LOGIN:
                    resid = R.string.pppp_status_user_not_login;
                    helper.tvStatus.getCompoundDrawables()[0].setLevel(2);
                    helper.ivPlay.setVisibility(View.VISIBLE);
                    break;
                case P2PConstants.P2PStatus.ON_LINE:
                    resid = R.string.pppp_status_online;
                    helper.tvStatus.getCompoundDrawables()[0].setLevel(2);
                    helper.ivPlay.setVisibility(View.VISIBLE);
//				    Log.i("DEV_LIST","resid=======++"+"在线");
                    break;
                case P2PConstants.P2PStatus.CONNECTING:
                    resid = R.string.pppp_status_connecting;
                    break;
                case P2PConstants.P2PStatus.CONNECT_FAILED:
                    resid = R.string.pppp_status_connect_failed;
                    break;
                case P2PConstants.P2PStatus.DISCONNECT:
                    resid = R.string.pppp_status_disconnect;
                    break;
                case P2PConstants.P2PStatus.INITIALING:
                    resid = R.string.pppp_status_initialing;
                    break;
                case P2PConstants.P2PStatus.INVALID_ID:
                    resid = R.string.pppp_status_invalid_id;
                    break;
                case P2PConstants.P2PStatus.DEVICE_NOT_ON_LINE:
                    resid = R.string.pppp_status_disconnect;
//				    Log.i("DEV_LIST","resid====="+"不在线" );
                    break;
                case P2PConstants.P2PStatus.CONNECT_TIMEOUT:
                    resid = R.string.pppp_status_connect_timeout;
                    break;
                case P2PConstants.P2PStatus.ERR_USER_PWD:
                    resid = R.string.pppp_status_err_user_pwd;
                    break;
//                case P2PConstants.P2PStatus.USER_LOGIN:
//                    resid = R.string.pppp_status_connect_user_login;
//                    break;
                case P2PConstants.P2PStatus.SLEEP:
                    resid = R.string.pppp_status_sleep;
                    break;
                case P2PConstants.P2PStatus.EXCEED_SESSION:
                    resid = R.string.pppp_status_connect_not_allow;
                    break;
                default:
                    resid = R.string.pppp_status_unknown;
            }
            helper.tvStatus.setText(resid);


            final int realPosition = helper.getLayoutPosition() - getHeaderLayoutCount();

            helper.lyShare.setOnClickListener(new MenuClickListener(data, realPosition));
            helper.lyEdit.setOnClickListener(new MenuClickListener(data, realPosition));
            helper.lyDelete.setOnClickListener(new MenuClickListener(data, realPosition));
            helper.lyFile.setOnClickListener(new MenuClickListener(data, realPosition));
            helper.lyConfig.setOnClickListener(new MenuClickListener(data, realPosition));

        }

    }

    @Override
    protected View getItemView(int layoutResId, ViewGroup parent) {
        View view = super.getItemView(layoutResId, parent);
        view.setTag(R.id.BaseQuickAdapter_databinding_support, 1);
        return view;
    }


    public interface OnMenuClickListener {
        void onShareClick(EdwinDevice data, int position);

        void onEditClick(EdwinDevice data, int position);

        void onDeleteClick(EdwinDevice data, int position);

        void onSetClick(EdwinDevice data, int position);

        void onFileClick(EdwinDevice data, int position);
    }

    public static class MyViewHolder extends BaseViewHolder {


        @BindView(R.id.ly_item)
        RelativeLayout lyItem;
        @BindView(R.id.iv_bg)
        ImageView bgItem;

        @BindView(R.id.bg_item)
        FrameLayout bgItem2;

        @BindView(R.id.iv_play)
        ImageView ivPlay;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_status)
        TextView tvStatus;

        @BindView(R.id.iv_share)
        ImageView ivShare;
        @BindView(R.id.ly_share)
        View lyShare;

        @BindView(R.id.iv_del)
        ImageView ivDelete;
        @BindView(R.id.ly_del)
        View lyDelete;

        @BindView(R.id.iv_edit)
        ImageView ivEdit;
        @BindView(R.id.ly_edit)
        View lyEdit;

        @BindView(R.id.iv_file)
        ImageView ivFile;
        @BindView(R.id.ly_file)
        View lyFile;

        @BindView(R.id.iv_config)
        ImageView ivConfig;
        @BindView(R.id.ly_config)
        View lyConfig;


        public MyViewHolder(View itemView) {
            super(itemView);
//            Logger.e("itemView: "+itemView);
            if (itemView.getTag(R.id.BaseQuickAdapter_databinding_support) != null) {
                ButterKnife.bind(this, itemView);
            }
        }

    }

    public class MenuClickListener implements View.OnClickListener {
        private EdwinDevice data;
        private int position;

        public MenuClickListener(EdwinDevice data, int position) {
            this.data = data;
            this.position = position;
        }


        @Override
        public void onClick(View v) {
            if (mListener == null) {
                return;
            }
            if (ClickUtil.isFastClick(mContext, v))
                return;
            switch (v.getId()) {
                case R.id.ly_share:
                    mListener.onShareClick(data, position);
                    break;
                case R.id.ly_edit:
                    mListener.onEditClick(data, position);
                    break;
                case R.id.ly_del:
                    mListener.onDeleteClick(data, position);
                    break;
                case R.id.ly_file:
                    mListener.onFileClick(data, position);
                    break;
                case R.id.ly_config:
                    mListener.onSetClick(data, position);
                    break;
            }
        }
    }


}
