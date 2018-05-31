package com.nicky.framework.base;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.widget.Checkable;

/**
 * 
 * @ClassName: BaseEditAdapter
 * @Description: 可以切换到编辑模式的适配器
 * @author NickyHuang
 * @date 2016-3-10
 * @param <T>
 */
public abstract class BaseEditAdapter< T extends Checkable> extends BaseXAdapter< T > {
	
	protected boolean isEditMode;
	
	public BaseEditAdapter(Context context) {
		super(context);
	}
	
	public BaseEditAdapter(Context context, List<T> datas) {
		super(context, datas);
	}
	
	public boolean isEditMode() {
		return isEditMode;
	}

	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}
	
	public void toggleEdit(){
		isEditMode = !isEditMode;
		if( !isEditMode )
			deselectAll();
		notifyDataSetChanged();
	}
	
	public void toggleSelectAll(){
		if(isEditMode)
		{
			if(isSelectAll())
				deselectAll();
			else
				selectAll();
			notifyDataSetChanged();
		}
	}
	
	public boolean isSelectAll(){
		for (T data : getDatas()) {
			if( !data.isChecked() )
				return false;
		}
		return true;
	}
	
	
	public List<T> getSelectedDatas(){
		List<T> selectedList = new ArrayList<T>();
		for (T item : getDatas()) {
			if(item.isChecked())
			{
				selectedList.add(item);
			}
		}
		return selectedList;
	}
	
//	public void removeDeltedDatas( List<T> deleteDatas ){
//		for (T item : deleteDatas ) {
//			if( getDatas().contains(item) )
//			{
//				getDatas().remove(item);
//			}
//		}
//	}
	
	
	private void selectAll(){
		for (T data : getDatas()) {
			data.setChecked(true);
		}
	}
	
	private void deselectAll(){
		for (T data : getDatas()) {
			data.setChecked(false);
		}
	}

}
