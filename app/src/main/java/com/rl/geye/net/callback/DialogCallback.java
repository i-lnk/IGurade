package com.rl.geye.net.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Window;

import com.lzy.okgo.request.BaseRequest;

/**
 * Created by cc on 2017/3/20.
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {
    private ProgressDialog dialog;
    private Context context;

    public DialogCallback(Activity activity) {
        super();
        context = activity;
        initDialog(activity, "");
    }

    public DialogCallback(Activity activity, String msg) {
        super();
        context = activity;
        initDialog(activity, msg);
    }

    public DialogCallback() {
        super();
    }

    private void initDialog(Activity activity, String message) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(TextUtils.isEmpty(message) ? "No network" : message);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onAfter(@Nullable T t, @Nullable Exception e) {
        super.onAfter(t, e);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
