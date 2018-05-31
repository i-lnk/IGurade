package com.rl.geye.adapter;


import android.support.v4.content.ContextCompat;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.rl.geye.R;
import com.rl.geye.bean.RecordGroup;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinRecord;
import com.rl.p2plib.constants.P2PConstants;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 2016/10/31.
 * 消息记录适配
 */
public class RecordMsgAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    private boolean isEditMode = false;
    private OnCheckListener mOnCheckListener;

    private int mSelectedCount = 0;

    public RecordMsgAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(Constants.LevelType.GROUP, R.layout.item_group_record);
        addItemType(Constants.LevelType.CHILD, R.layout.item_child_record_msg);
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

    public ArrayList<RecordGroup> getGroupList() {
        ArrayList<RecordGroup> groupList = new ArrayList<>();
        for (MultiItemEntity item : getData()) {
            if (item instanceof RecordGroup) {
                RecordGroup group = (RecordGroup) item;
                groupList.add(group);
            }
        }
        return groupList;
    }

    public List<EdwinRecord> getSelectedDatas() {
        List<EdwinRecord> selectedList = new ArrayList<>();
        for (MultiItemEntity item : getData()) {
            if (item instanceof RecordGroup) {
                RecordGroup group = (RecordGroup) item;
                selectedList.addAll(group.getSelectedDatas());
            }
        }
        return selectedList;
    }

    public void selectAll() {
        mSelectedCount = 0;
        for (MultiItemEntity item : getData()) {
            if (item instanceof RecordGroup) {
                ((RecordGroup) item).selectAll();
                mSelectedCount += ((RecordGroup) item).getChildrenCount();
            } else if (item instanceof EdwinRecord) {
                ((EdwinRecord) item).setChecked(true);
            }
        }
    }

//    public List<EdwinRecord> getUnSelectedDatas(){
//        List<EdwinRecord> unSelectedList = new ArrayList<>();
//        for ( MultiItemEntity item : getData() ){
//            if(item instanceof RecordGroup){
//                RecordGroup group = (RecordGroup) item;
//                unSelectedList.addAll( group.getUSelectedDatas() );
//            }
//        }
//        return unSelectedList;
//    }

    public void deselectAll() {
        for (MultiItemEntity item : getData()) {
            if (item instanceof RecordGroup) {
                ((RecordGroup) item).deselectAll();
            } else if (item instanceof EdwinRecord) {
                ((EdwinRecord) item).setChecked(false);
            }
        }
        mSelectedCount = 0;
    }

    public boolean isSelectAll() {

        for (MultiItemEntity item : getData()) {
            if (item instanceof RecordGroup) {
                if (!((RecordGroup) item).isSelectAll())
                    return false;
            } else if (item instanceof EdwinRecord) {

                if (!((EdwinRecord) item).isChecked())
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
                final RecordGroup group = (RecordGroup) data;
                if (group != null) {
                    String desc = mContext.getString(R.string.str_message);
                    int count = group.getChildrenCount();

                    holder.setText(R.id.tv_date, group.getDate())
                            .setText(R.id.tv_desc, desc)
                            .setText(R.id.tv_count, count < 100 ? String.format("(%d)", count) : "99+")
                            .setVisible(R.id.iv_check, isEditMode);
//                              .setChecked( R.id.iv_expand, group.isExpanded() )
//                              .setChecked(R.id.iv_check,group.isSelectAll() );

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
                final EdwinRecord child = (EdwinRecord) data;
                if (child != null) {
                    String name = child.getDevice().getName();
                    int descResId = -1;
                    int typeResId = -1;
                    int descColor = ContextCompat.getColor(mContext, R.color.text_hint);
                    switch (child.getType()) {
                        case P2PConstants.PushType.CALL:
                            descResId = child.isAnswered() ? R.string.record_call_ok : R.string.record_call_no;
                            typeResId = child.isAnswered() ? R.mipmap.a3_ic_call_ok : R.mipmap.a3_ic_call_no;
                            descColor = ContextCompat.getColor(mContext, child.isAnswered() ? R.color.text_hint : R.color.text_red);
                            break;
                        case P2PConstants.PushType.PIR:
                            descResId = R.string.record_pir;
                            typeResId = R.mipmap.a3_ic_alarm;
                            break;
                        case P2PConstants.PushType.DETECTION:
                            descResId = R.string.record_detect;
                            typeResId = R.mipmap.a3_ic_alarm;
                            break;
                        case P2PConstants.PushType.ALARM_DISMANTLE:
                            descResId = R.string.record_dismantle;
                            typeResId = R.mipmap.a3_ic_alarm;
                            break;
                        case P2PConstants.PushType.ALARM_433:
                            descResId = R.string.record_433;
                            typeResId = R.mipmap.a3_ic_alarm;
                            break;
                        case P2PConstants.PushType.LOW_CHARGE:
                            descResId = R.string.record_low_charge;
                            typeResId = R.mipmap.a3_ic_alarm;
                            break;
                        default:
                            return;
                    }
                    holder.setText(R.id.tv_date, child.getFormatTime())
                            .setText(R.id.tv_dev_name, name)
                            .setText(R.id.tv_desc, descResId)
                            .setTextColor(R.id.tv_desc, descColor)
                            .setImageResource(R.id.iv_type, typeResId)
                            .setVisible(R.id.iv_check_temp, isEditMode)
                            .setVisible(R.id.iv_check, isEditMode);
//                            .setChecked(R.id.iv_check,child.isChecked() );
                    if (child.getType() == P2PConstants.PushType.ALARM_433 && child.getSubDev() != null) {
                        holder.setText(R.id.tv_desc,
                                mContext.getString(R.string.record_433, child.getSubDev().getName()));
                    }
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
                }
                break;

        }


//        if (data != null) {
//            final int realPosition = helper.getLayoutPosition() - getHeaderLayoutCount();
//        }

    }


    public interface OnCheckListener {
        void OnCheckChange(boolean isSelectedAll, int selectedCount);
    }


}
