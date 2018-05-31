package com.nicky.framework.base;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.nicky.framework.R;

/**
 * 
 * @ClassName: BaseDialog
 * @Description: dialog的基类
 * @author NickyHuang
 * @date 2016-3-9
 */
public abstract class BaseDialog extends DialogFragment {
	
	protected Dialog dialog;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = createDialog();
		setDialogGravity(Gravity.BOTTOM);
		initDialog(dialog);
		dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
		dialog.setCancelable(cancelable);
		return dialog;
	}
	
	/**
	 * 设置dialog的重心 
	 */
	protected void setDialogGravity(int gravity) {
		dialog.getWindow().setGravity(gravity);
	}
	
	/**
	 * 初始化dialog
	 */
	protected abstract void initDialog(Dialog dialog);

//	/** 初始化绑定View 工具(如:ButterKnife) */
//	protected abstract void initBind(View view);
	
	/**
	 * dialog的样式，主要是动画，返回0的话，将默认没有动画效果
	 */
	protected int getAnimStyle() {
		return R.style.BaseDialogStyle;
	}

	private Dialog createDialog() {
		if (dialog == null) {
			int style = getAnimStyle();
			if (style != 0) {
				dialog = new Dialog(getActivity(), style);
			} else {
				dialog = new Dialog(getActivity());
			}
		}
		return dialog;
	}

	/**
	 * 根据布局文件加载dialog的contentView
	 */
	protected View inflateContentView(int id) {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View view = layoutInflater.inflate(id, null);
		// 设置Dialog最小宽度为屏幕宽度
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
		view.setMinimumWidth((int) displaymetrics.widthPixels);
//		initBind(view);
		return view;
	}
	
	private OnDismissListener onDismissListener;
	
	public static class OnDismissListener {
		public void onDismiss(){};
		public void onCancel(){};
	}
	
	public void setOnDismissListener(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (onDismissListener != null) {
			onDismissListener.onDismiss();
		}
	}
	
	private boolean cancelable = true;
	
	public void setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		super.setCancelable(cancelable);
	}
	
	private boolean canceledOnTouchOutside = true;
	
	public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
		this.canceledOnTouchOutside = canceledOnTouchOutside;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		if (onDismissListener != null) {
			onDismissListener.onCancel();
		}
	}
	
}
