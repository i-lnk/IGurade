package com.nicky.framework.tableview;

import android.view.View;

public class ViewItem implements IListItem {
	
	private boolean mClickable = true;
	private View mView;
	private int viewId = -1;

	public ViewItem(View view, boolean clickable,int viewId) {
		this.mView = view;
		this.viewId = viewId;
		this.mClickable = clickable;
	}

	public ViewItem(View view, boolean clickable) {
		this.mView = view;
		this.mClickable = clickable;
	}

	public ViewItem(View view,int viewId) {
		this.mView = view;
		this.viewId = viewId;
	}

	public ViewItem(View view) {
		this.mView = view;
	}
	
	public View getView() {
		return this.mView;
	}

	public int getViewId() {
		return viewId;
	}

	public void setViewId(int viewId) {
		this.viewId = viewId;
	}

	@Override
	public boolean isClickable() {
		return mClickable;
	}

	@Override
	public void setClickable(boolean clickable) {
		mClickable = clickable;		
	}
	
}
