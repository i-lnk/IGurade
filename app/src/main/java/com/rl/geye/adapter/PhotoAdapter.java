package com.rl.geye.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nicky.framework.base.BaseActivity;
import com.rl.geye.R;
import com.rl.geye.bean.ImageItem;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Nicky on 2016/10/10.
 * 图片 适配器(全屏查看图片时用到)
 */
public class PhotoAdapter extends PagerAdapter {
    private List<ImageItem> mList;
    private Context mContext;

    public PhotoAdapter(Context context) {
//		inflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
        mContext = context;
    }

    public PhotoAdapter(Context context, List<ImageItem> list) {
//		inflater = LayoutInflater.from(context);
        this.mList = list;
        mContext = context;
    }

    public List<ImageItem> getDatas() {
        return mList;
    }

    public void setDatas(List<ImageItem> list) {
        this.mList = list;
    }

    /**
     * 添加
     */
    public void addDatas(List<ImageItem> datas) {
        if (datas != null && !datas.isEmpty()) {
            mList.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        if (getCount() <= position) {
            return;
        }
        mList.remove(position);
        notifyDataSetChanged();
    }

    public void removeItem(ImageItem item) {
        if (item != null) {
            mList.remove(item);
            notifyDataSetChanged();
        }
    }


    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public ImageItem getItem(int position) {
        if (getCount() <= position) {
            return null;
        }
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        ImageItem item = getItem(position);
        if (item != null) {
            Glide.with(mContext)
                    .load(item.getPath())
                    .fitCenter()
//                    .placeholder(R.drawable.bg_photo_loading)
                    .error(R.mipmap.ic_empty_photo2)
                    .crossFade()
//                    .transform(new GlideRoundTransform(mContext))
                    .into(photoView);
        }
        // Now just add PhotoView to ViewPager and return it
        container.addView(photoView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (mContext instanceof BaseActivity) {
                    BaseActivity aty = (BaseActivity) mContext;
                    if (aty != null && !aty.isFinishing()) {
                        aty.finish();
                    }
                }
            }

            @Override
            public void onOutsidePhotoTap() {
                if (mContext instanceof BaseActivity) {
                    BaseActivity aty = (BaseActivity) mContext;
                    if (aty != null && !aty.isFinishing()) {
                        aty.finish();
                    }
                }
            }
        });

        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

}
