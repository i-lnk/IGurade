package com.nicky.framework.tableview;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 
 * @ClassName: SwitchTableItem
 * @Description: 自定义table item(带checkbox)
 * @author NickyHuang
 * @date 2016-3-9
 */
public class SwitchTableItem extends CustomTableItem {
	
	public interface OnSwitchListener{
		
		/** 点击事件回调 */
		void onSwitchClick(int switchId);
		
		/** check changed 事件回调 */
		void onSwitchChanged(int switchId, boolean isChecked);
	}
	
	
	private OnSwitchListener mOnSwitchListener;
	private int switchId = -1;
	
	public SwitchTableItem(Context context) {
		super(context,ITEM_TYPE_SWITCH);
		initSwitch();
	}
	
	
	public void toggleSwitch(){
		if(switchItem!=null)
		{
			switchItem.toggle();
		}
	}
	public void setChecked(boolean checked)
	{
		if (switchItem != null && switchItem.isChecked() != checked) {
			switchItem.setChecked(checked);
		}
	}
	public boolean isChecked(){
		return switchItem==null?false:switchItem.isChecked();
	}
	
	
	private void initSwitch(){
		if( switchItem!=null )
		{
			switchItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (iconLeft != null)
						iconLeft.setSelected(isChecked);
					if(mOnSwitchListener!=null)
						mOnSwitchListener.onSwitchChanged(switchId,isChecked);
				}
			});
			
			switchItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mOnSwitchListener!=null)
						mOnSwitchListener.onSwitchClick(switchId);
				}
			});
		}
	}


	public void setOnSwitchListener( int switchId,OnSwitchListener listener) {
		this.switchId = switchId;
		this.mOnSwitchListener = listener;
	}


}
