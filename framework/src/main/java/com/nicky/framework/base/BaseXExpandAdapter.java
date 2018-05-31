package com.nicky.framework.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.rl.commons.bean.BaseGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 2级列表适配器基类
 * @author NickyHuang
 *
 */
public abstract class BaseXExpandAdapter< CHILD , GROUP extends BaseGroup<CHILD>> extends BaseExpandableListAdapter {


	 protected Context mContext;
	 private List<GROUP> mDatas;

	 public BaseXExpandAdapter(Context context) {
	        this(context,null);
	 }


	public BaseXExpandAdapter(Context context, List<GROUP> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        this.mDatas = datas;
        this.mContext = context;
    }


	public List<GROUP> getDatas(){
		return mDatas;
	}

	/*---------------------------------group---------------------------------------*/
	@Override
	public int getGroupCount() {
		return (mDatas == null) ? 0 : mDatas.size();
	}

	@Override
	public GROUP getGroup(int groupPosition) {
		try {
			return (mDatas == null) ? null : mDatas.get(groupPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public void setGroup( int groupPosition,GROUP group){
		if( mDatas!=null ){
			try{
				mDatas.set(groupPosition,group);
				notifyDataSetChanged();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removeGroup( int groupPosition ){
		if( mDatas!=null ){
			try{
				mDatas.remove(groupPosition);
				notifyDataSetChanged();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removeGroup( GROUP group ){
		if( mDatas!=null && group!=null ){
			try{
				mDatas.remove(group);
				notifyDataSetChanged();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*---------------------------------child---------------------------------------*/
	@Override
	public int getChildrenCount(int groupPosition) {
		try{
			return (mDatas == null) ? 0 : mDatas.get(groupPosition).getChildrenCount();
		}catch (Exception e){
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public CHILD getChild(int groupPosition, int childPosition) {
		try {
			return (mDatas == null) ? null : mDatas.get(groupPosition).getChild(childPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}


	public void removeChild( int groupPosition, int childPosition ){
		if( mDatas!=null ){
			try{
				mDatas.get(groupPosition).removeMember(childPosition);
				notifyDataSetChanged();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*---------------------------------other---------------------------------------*/
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}


	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
							 View convertView, ViewGroup parent){

		GroupViewHolder<GROUP> holder;
        if (convertView == null ||convertView.getTag()==null ) {
        	holder = newGroupItemView();
            convertView = View.inflate(mContext, holder.inflateViewId(), null);
			holder.initBind(convertView);
            convertView.setTag(holder);
        } else {
        	holder = (GroupViewHolder<GROUP>) convertView.getTag();
        }

		GROUP group = getGroup(groupPosition);
		if( group!=null ) {
			holder.bindingData(convertView, group,isExpanded);
			holder.updateConvertView(convertView, group, groupPosition,isExpanded);
		}
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
							 boolean isLastChild, View convertView, ViewGroup parent){

		ChildViewHolder<CHILD,GROUP> holder;// = newChildItemView()

		if (convertView == null ||convertView.getTag()==null ) {
			holder = newChildItemView();
			convertView = View.inflate(mContext, holder.inflateViewId(), null);
			holder.initBind(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder<CHILD,GROUP>) convertView.getTag();
		}
		GROUP group = getGroup(groupPosition);
		CHILD child = getChild(groupPosition,childPosition);
		if( group!=null && child!=null ){
			holder.bindingData(convertView, group ,child );
			holder.updateConvertView(convertView, group, groupPosition,child,childPosition);
		}
		return convertView;
	}


	abstract protected GroupViewHolder<GROUP> newGroupItemView();

	public abstract static class GroupViewHolder<T> {

        abstract public int inflateViewId();

		/** 初始化绑定View 工具(如:ButterKnife) */
		abstract public void initBind(View convertView);

        /** 绑定ViewHolder数据 */
        abstract public void bindingData(View convertView, T group,boolean isExpanded);

        /** 刷新当前ItemView视图 */
        public void updateConvertView(View convertView,T group, int groupPosition,boolean isExpanded) {}

	}

	abstract protected ChildViewHolder<CHILD,GROUP> newChildItemView();

	public abstract static class ChildViewHolder<TC,TG> {

		protected Object getHolderObj(){ return this; }

		abstract public int inflateViewId();

		/** 初始化绑定View 工具(如:ButterKnife) */
		abstract public void initBind(View convertView);

		/** 绑定ViewHolder数据 */
		abstract public void bindingData(View convertView, TG group,TC child);

		/** 刷新当前ItemView视图 */
		public void updateConvertView(View convertView, TG data,
									  int groupPosition,
									  TC child, int childPosition) {
		}

	}

}
