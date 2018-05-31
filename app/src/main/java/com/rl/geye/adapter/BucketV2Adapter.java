package com.rl.geye.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.nicky.framework.base.BaseXAdapter;
import com.rl.geye.R;
import com.rl.geye.image.PhotoBucket;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nicky on 2016/9/18.
 * 相册适配(目录)
 */
public class BucketV2Adapter extends BaseXAdapter<PhotoBucket> {

    private RequestManager mGlideRequestManager;


    public BucketV2Adapter(Context context, RequestManager requestManager, List<PhotoBucket> datas) {
        super(context, datas);
        mGlideRequestManager = requestManager;
    }

    @Override
    protected ViewHolder<PhotoBucket> newItemView() {
        return new BucketViewHolder();
    }

    class BucketViewHolder extends ViewHolder<PhotoBucket> {
        @BindView(R.id.iv_bucket)
        ImageView ivBucket;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.ly_bucket)
        LinearLayout lyBucket;

        @Override
        public int inflateViewId() {
            return R.layout.item_bucket2;
        }

        @Override
        public void initBind(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        @Override
        public void bindingData(View convertView, PhotoBucket data) {

            mGlideRequestManager
                    .load(data.getCoverPath())
                    .centerCrop()
                    .placeholder(R.drawable.bg_photo_loading)
                    .error(R.mipmap.ic_empty_photo2)
                    .crossFade()
                    .into(ivBucket);

            tvName.setText(data.getName());
            tvCount.setText(mContext.getString(R.string.image_count, data.getPhotos().size()));
        }
    }

}
