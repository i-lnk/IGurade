package com.nicky.framework.varyview;


import com.nicky.framework.R;
import com.rl.commons.BaseApp;
import com.rl.commons.utils.ClickUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class VaryViewHelperController {

	public enum MsgType {
		ERROR_NORMAL, ERROR_NET, MSG_EMPTY
	}
	
	/** 自定义 ViewClickListerer */
	public interface OnMsgViewClickListener {
		void onClick(View v);
	}
	

	private IVaryViewHelper helper;

	public VaryViewHelperController(View view) {
		this(new VaryViewHelper(view));
	}

	public VaryViewHelperController(IVaryViewHelper helper) {
		super();
		this.helper = helper;
	}

	public void showMsgView(MsgType msgType, String msg, int iconId,
			final OnMsgViewClickListener onClickListener) {

		View layout = helper.inflate(R.layout.comm_message);
		TextView textView = (TextView) layout.findViewById(R.id.message_info);
		String defaultMsg = "";
		int defaultId = -1;
		switch (msgType) {
		case ERROR_NORMAL:
			defaultMsg = BaseApp.context().getResources()
					.getString(R.string.common_error_msg);
			defaultId = R.mipmap.ic_error;
			break;
		case ERROR_NET:
			defaultMsg = BaseApp.context().getResources()
					.getString(R.string.common_no_network_msg);
			defaultId = R.mipmap.ic_exception;
			break;
		case MSG_EMPTY:
			defaultMsg = BaseApp.context().getResources()
					.getString(R.string.common_empty_msg);
			defaultId = R.mipmap.ic_exception;
			break;
		default:
			break;
		}
		textView.setText(!TextUtils.isEmpty(msg) ? msg : defaultMsg);
		ImageView imageView = (ImageView) layout
				.findViewById(R.id.message_icon);
		imageView.setImageResource(iconId != -1 ? iconId : defaultId);

		if (null != onClickListener) {
			layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if ( ClickUtil.isFastClick(helper.getContext(),v) )
						return;
					onClickListener.onClick(v);
				}
			});
		}
		helper.showLayout(layout);
	}

	public void showLoading(String msg) {
		View layout = helper.inflate(R.layout.comm_loading);
		if (!TextUtils.isEmpty(msg)) {
			TextView textView = (TextView) layout
					.findViewById(R.id.loading_msg);
			textView.setText(msg);
		}
		helper.showLayout(layout);
	}

	public void showLoading(int resId) {
		if( getContext()!=null ){
			String msg = getContext().getString(resId);
			View layout = helper.inflate(R.layout.comm_loading);
			if (!TextUtils.isEmpty(msg)) {
				TextView textView = (TextView) layout
						.findViewById(R.id.loading_msg);
				textView.setText(msg);
			}
			helper.showLayout(layout);
		}
	}

	public void restore() {
		helper.restoreView();
	}

	public Context getContext(){
		if(helper!=null)
			return  helper.getContext();
		return null;
	}
}
