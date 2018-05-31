package com.nicky.framework.base;





import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.nicky.framework.R;

/**
 * 
 * @ClassName: LoadingDialog
 * @Description: 加载框
 * @author NickyHuang
 * @date 2016-3-9
 */
public class LoadingDialog extends Dialog {

	private TextView tvLoading;
	
	public LoadingDialog(Context context) {
		super(context);
		init(context);
	}

	public LoadingDialog(Context context, int defStyle) {
		super(context, defStyle);
		init(context);
	}

	protected LoadingDialog(Context context, boolean cancelable,
			OnCancelListener listener) {
		super(context, cancelable, listener);
		init(context);
	}
	
	private void init(Context context) {
		setCanceledOnTouchOutside(true);
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_loading, null);
		tvLoading = (TextView) view.findViewById(R.id.loading_msg);
		setContentView(view);
	}
	

	public void setLoadMsg(String content){
		tvLoading.setText(content);
	}

}
