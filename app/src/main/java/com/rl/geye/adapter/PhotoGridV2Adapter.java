package com.rl.geye.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.rl.geye.R;
import com.rl.geye.image.PhotoBucket;
import com.rl.geye.image.PhotoItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nicky on 2016/10/31.
 */
public class PhotoGridV2Adapter extends RecyclerView.Adapter<PhotoGridV2Adapter.PhotoViewHolder> {


    public int currentBucketIndex = 0;
    private LayoutInflater inflater;
    private RequestManager mGlideRequestManager;
    private List<PhotoBucket> photoBuckets = new ArrayList<>();
    private int imageSize;
    private int columnNumber = 3;

    private OnPhotoClickListener onPhotoClickListener = null;

    public PhotoGridV2Adapter(Context context, RequestManager requestManager, List<PhotoBucket> photoBuckets) {
        this.photoBuckets = photoBuckets;
        this.mGlideRequestManager = requestManager;
        inflater = LayoutInflater.from(context);
        setColumnNumber(context, columnNumber);
    }

    private void setColumnNumber(Context context, int columnNumber) {
        this.columnNumber = columnNumber;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNumber;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setCurrentBucketIndex(int currentBucketIndex) {
        this.currentBucketIndex = currentBucketIndex;
    }

    public List<PhotoItem> getCurrentPhotos() {
        List<PhotoItem> list = null;
        try {
            list = photoBuckets.get(currentBucketIndex).getPhotos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_grid_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {

        final PhotoItem photo = getItem(position);
        mGlideRequestManager.load(photo.getPath())
                .centerCrop()
                .dontAnimate()
                .thumbnail(0.5f)
                .override(imageSize, imageSize)
                .placeholder(R.drawable.bg_photo_loading_2)
                .error(R.mipmap.ic_empty_photo2)
                .into(holder.ivPhoto);

        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPhotoClickListener != null) {
                    int pos = holder.getAdapterPosition();
                    onPhotoClickListener.onPhotoClick(view, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int photosCount = (photoBuckets == null || photoBuckets.isEmpty()) ? 0 : getCurrentPhotos().size();
        return photosCount;
    }

    public PhotoItem getItem(int position) {
        List<PhotoItem> photos = getCurrentPhotos();
        if (photos != null && !photos.isEmpty() && photos.size() > position) {
            return photos.get(position);
        }
        return null;
    }

    @Override
    public void onViewRecycled(PhotoViewHolder holder) {
        Glide.clear(holder.ivPhoto);
        super.onViewRecycled(holder);
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(View v, int position);
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_photo)
        ImageView ivPhoto;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
