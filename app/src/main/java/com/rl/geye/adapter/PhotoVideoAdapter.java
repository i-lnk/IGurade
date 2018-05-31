package com.rl.geye.adapter;


import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;
import com.rl.geye.bean.PhotoVideoGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.PhotoVideo;
import com.rl.geye.image.GlideCircleTransform;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/10/31.
 * 图片、视频记录适配
 */
public class PhotoVideoAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private int type = P2PConstants.PhotoVideoType.PICTURE;


    private RequestManager mGlideRequestManager;

    private OnPhotoClickListener mPhotoListener;
    private OnVideoClickListener mVideoListener;

    private boolean isEditMode = false;
    private OnCheckListener mOnCheckListener;


    private int mSelectedCount = 0;

    public PhotoVideoAdapter(List<MultiItemEntity> data, int type, RequestManager requestManager) {

        super(data);
        addItemType(Constants.LevelType.GROUP, R.layout.item_group_record);
        addItemType(Constants.LevelType.CHILD, type == P2PConstants.PhotoVideoType.PICTURE ?
                R.layout.item_child_photo : R.layout.item_child_video);
        this.type = type;
        this.mGlideRequestManager = requestManager;

    }

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public void setOnCheckListener(OnCheckListener listener) {
        mOnCheckListener = listener;
    }


    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
    }

    public void setPhotoListener(OnPhotoClickListener listener) {
        mPhotoListener = listener;
    }

    public void setVideoListener(OnVideoClickListener listener) {
        mVideoListener = listener;
    }

    public ArrayList<PhotoVideoGroup> getGroupList() {

        ArrayList<PhotoVideoGroup> groupList = new ArrayList<>();
        for (MultiItemEntity item : getData()) {
            if (item instanceof PhotoVideoGroup) {
                PhotoVideoGroup group = (PhotoVideoGroup) item;
                groupList.add(group);
            }
        }
        return groupList;
    }

    public List<PhotoVideo> getSelectedDatas() {
        List<PhotoVideo> selectedList = new ArrayList<>();
        for (MultiItemEntity item : getData()) {
            if (item instanceof PhotoVideoGroup) {
                PhotoVideoGroup group = (PhotoVideoGroup) item;
                selectedList.addAll(group.getSelectedDatas());
            }
        }
        return selectedList;
    }

    public void selectAll() {
        mSelectedCount = 0;
        for (MultiItemEntity item : getData()) {
            if (item instanceof PhotoVideoGroup) {
                ((PhotoVideoGroup) item).selectAll();
                mSelectedCount += ((PhotoVideoGroup) item).getChildrenCount();
            } else if (item instanceof PhotoVideo) {
                ((PhotoVideo) item).setChecked(true);
            }
        }
    }

    public void deselectAll() {
        for (MultiItemEntity item : getData()) {
            if (item instanceof PhotoVideoGroup) {
                ((PhotoVideoGroup) item).deselectAll();
            } else if (item instanceof PhotoVideo) {
                ((PhotoVideo) item).setChecked(false);
            }
        }
        mSelectedCount = 0;
    }

    public boolean isSelectAll() {

        for (MultiItemEntity item : getData()) {
            if (item instanceof PhotoVideoGroup) {
                if (!((PhotoVideoGroup) item).isSelectAll())
                    return false;
            } else if (item instanceof PhotoVideo) {

                if (!((PhotoVideo) item).isChecked())
                    return false;
            }
        }
        return true;
    }

    public void toggleSelectAll() {
        if (isSelectAll())
            deselectAll();
        else
            selectAll();
        notifyDataSetChanged();
    }

    @Override
    protected void convert(final BaseViewHolder holder, final MultiItemEntity data) {

        switch (holder.getItemViewType()) {

            case Constants.LevelType.GROUP:
                final PhotoVideoGroup group = (PhotoVideoGroup) data;
                if (group != null) {
                    String desc = type == P2PConstants.PhotoVideoType.PICTURE ? mContext.getString(R.string.str_photo) : mContext.getString(R.string.str_video);
                    int count = group.getChildrenCount();

                    holder.setText(R.id.tv_date, group.getDate())
                            .setText(R.id.tv_desc, desc)
                            .setText(R.id.tv_count, count < 100 ? String.format("(%d)", count) : "99+")
                            .setVisible(R.id.iv_check, isEditMode);
//                            .setChecked( R.id.iv_expand, group.isExpanded() )
//                            .setChecked(R.id.iv_check,group.isSelectAll() );

                    holder.getView(R.id.iv_check).setSelected(group.isSelectAll());
                    holder.getView(R.id.iv_expand).setSelected(group.isExpanded());

                    holder.getView(R.id.iv_check).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int origGroupCheck = group.getSelectedCount();
                            group.toggleSelectAll();
                            mSelectedCount += (group.isSelectAll() ? group.getChildrenCount() - origGroupCheck : 0 - origGroupCheck);
                            notifyDataSetChanged();
                            if (mOnCheckListener != null) {
                                mOnCheckListener.OnCheckChange(isSelectAll(), mSelectedCount);
                            }
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = holder.getAdapterPosition();
                            if (group.isExpanded()) {
                                collapse(pos);
                            } else {
                                expand(pos);
                            }
                        }
                    });
                }
                break;

            case Constants.LevelType.CHILD:
                final PhotoVideo child = (PhotoVideo) data;

                if (child != null) {

                    String name = child.getDevice().getName();

                    holder.setText(R.id.tv_date, child.getFormatTime())
                            .setText(R.id.tv_dev_name, name)
                            .setVisible(R.id.iv_check_temp, isEditMode)
                            .setVisible(R.id.iv_check, isEditMode);
//                            .setChecked(R.id.iv_check,child.isChecked() );
                    holder.getView(R.id.iv_check).setSelected(child.isChecked());

                    if (isEditMode) {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                child.toggle();
                                mSelectedCount += (child.isChecked() ? 1 : -1);
                                notifyDataSetChanged();
                                if (mOnCheckListener != null) {
                                    mOnCheckListener.OnCheckChange(isSelectAll(), mSelectedCount);
                                }
                            }
                        });
                    }


                    if (type == P2PConstants.PhotoVideoType.PICTURE) {
                        mGlideRequestManager
                                .load(child.getPath())
                                .centerCrop()
                                .placeholder(R.drawable.bg_photo_circle_loading)
                                .error(R.mipmap.ic_empty_photo)
                                .crossFade()
                                .transform(new GlideCircleTransform(mContext))
                                .into((ImageView) holder.getView(R.id.iv_photo));

                        if (!isEditMode) {
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int cp = getParentPosition(child);
                                    PhotoVideoGroup group = ((PhotoVideoGroup) getData().get(cp));
                                    if (mPhotoListener != null) {
                                        mPhotoListener.OnPhotoClick(group, cp, child, group.getSubItems().indexOf(child));
                                    }
                                }
                            });
                        }


                    } else {
//                        ImageUtil.getVideoThumbnail( child.getPath() );
//                        ((ImageView) holder.getView(R.id.iv_video)).setImageBitmap( ImageUtil.getVideoThumbnail( child.getPath() ) );
                        mGlideRequestManager
                                .load(child.getPathThumb())
                                .centerCrop()
                                .placeholder(R.drawable.bg_photo_circle_loading)
                                .error(R.mipmap.ic_empty_video)
                                .crossFade()
                                .transform(new GlideCircleTransform(mContext))
                                .into((ImageView) holder.getView(R.id.iv_video));

                        holder.addOnClickListener(R.id.iv_video);

                        if (!isEditMode) {
                            holder.getView(R.id.iv_video).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ClickUtil.isFastClick(mContext, v))
                                        return;
                                    if (mVideoListener != null) {
                                        int groupPosition = getParentPosition(child);
                                        PhotoVideoGroup group = ((PhotoVideoGroup) getData().get(groupPosition));
                                        mVideoListener.OnVideoClick(group, groupPosition, child, group.getSubItems().indexOf(child));
                                    }
                                }
                            });
                        }

                    }
                }
                break;

        }


//        if (data != null) {
//            final int realPosition = helper.getLayoutPosition() - getHeaderLayoutCount();
//
//
//        }

    }

    public interface OnCheckListener {
        void OnCheckChange(boolean isSelectedAll, int selectedCount);
    }

    public interface OnPhotoClickListener {
        void OnPhotoClick(PhotoVideoGroup group, int groupPosition, PhotoVideo child, int childPosition);
    }


    public interface OnVideoClickListener {
        void OnVideoClick(PhotoVideoGroup group, int groupPosition, PhotoVideo child, int childPosition);
    }


}
