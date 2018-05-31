package com.nicky.framework.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 适配器基类
 * @author NickyHuang
 *
 */
public abstract class BaseXAdapter<T> extends BaseAdapter {

	 protected Context mContext;
	 private List<T> mDatas;
	
	 public BaseXAdapter(Context context) {
	        this(context,null);
	 }
	 
	
	public BaseXAdapter(Context context, List<T> datas) {
        if (datas == null)
            datas = new ArrayList<>();
        this.mDatas = datas;
        this.mContext = context;
    }
	
	@Override
	public int getCount() {
		return mDatas.size();
	}
	
	public int getDataSize()
	{
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		try {
			return mDatas.get(position);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder<T> holder;
		
        if (convertView == null ||convertView.getTag()==null ) {
        	holder = newItemView();
            convertView = View.inflate(mContext, holder.inflateViewId(), null);
			holder.initBind(convertView);
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder<T>) convertView.getTag();
        }
        T item = getItem(position);
        if(item!=null) {
			holder.bindingData(convertView, item);
			holder.updateConvertView( convertView, item,  position);
		}
        return convertView;
	}

	public void setDatas(List<T> list)
	{
		if(list!=null)
			mDatas = list;
	}
	
	public List<T> getDatas() {
        return mDatas == null ? (mDatas = new ArrayList<T>()) : mDatas;
    }

	public void addData(T t) {
		mDatas.add(t);
		notifyDataSetChanged();
	}

	public void addData(T[] tArray) {
		mDatas.addAll(Arrays.asList(tArray));
		notifyDataSetChanged();
	}

	public void addDatas(List<T> tList) {
		 if (mDatas != null && tList != null && !tList.isEmpty()) {
			 mDatas.addAll(tList);
		 }
		notifyDataSetChanged();
	}
	
	public void appendDatas(List<T> tList,int position) {
		 if (mDatas != null && tList != null && !tList.isEmpty() &&  position<=mDatas.size() ) {
			 mDatas.addAll(position,tList);
		 }
		notifyDataSetChanged();
	}
	
	public void setData(int position,T t) {
		mDatas.set(position, t);
		notifyDataSetChanged();
	}
	public void removeData(int position) {
		mDatas.remove(position);
		notifyDataSetChanged();
	}
	
	public void removeData(T t) {
		mDatas.remove(t);
		notifyDataSetChanged();
	}

	public void clear() {
		mDatas.clear();
		notifyDataSetChanged();
	}
	
	abstract protected ViewHolder<T> newItemView();
	
	public abstract static class ViewHolder<T> {
		
        abstract public int inflateViewId();

		/** 初始化绑定View 工具(如:ButterKnife) */
		abstract public void initBind(View convertView);

        /** 绑定ViewHolder数据 */
        abstract public void bindingData(View convertView, T data);

        /** 刷新当前ItemView视图 */
        public void updateConvertView( View convertView, T data,  int position) {}

	}

}
