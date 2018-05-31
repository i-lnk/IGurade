package com.rl.geye.ui.dlg;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.nicky.framework.base.BaseDialog;
import com.nicky.framework.widget.XEditText;
import com.rl.commons.filter.EditChsLenInputFilter;
import com.rl.commons.utils.ClickUtil;
import com.rl.geye.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@SuppressLint("NewApi")
/**
 *
 * @ClassName: LoginDialog
 * @Description: 设备登录框
 * @author NickyHuang
 * @date 2016-8-18 上午11:41:40 
 *
 */
public class LoginDialog extends BaseDialog {

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.et_user)
    XEditText etUser;

    @BindView(R.id.et_pwd)
    XEditText etPwd;

    @BindView(R.id.ly_user)
    TextInputLayout textInputUser;
    @BindView(R.id.ly_pwd)
    TextInputLayout textInputPwd;

    private InputMethodManager imm;
    private int maxLength = 0;
    private String cancelStr;
    private String okStr;
    private OnLoginClickListener onClick;

    @Override
    protected void initDialog(Dialog dialog) {

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        setDialogGravity(Gravity.CENTER);
        View view = inflateContentView(R.layout.dialog_login);
        ButterKnife.bind(this, view);
        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }
        if (okStr != null) {
            btnOk.setText(okStr);
        }

        if (maxLength != 0) {
            etUser.setFilters(new InputFilter[]{new EditChsLenInputFilter(maxLength)});
            etPwd.setFilters(new InputFilter[]{new EditChsLenInputFilter(maxLength)});
//			InputFilter[] filter = {new LengthFilter(maxLength)};
//			etContent.setFilters(filter);
        }
//		if( !showEtcontent ){
////			etContent.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//			etContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
//		}
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.setContentView(view);
    }

    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    void widgetClick(View v) {
        if (ClickUtil.isFastClick(getActivity(), v))
            return;
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (onClick != null) {
                    onClick.onCancel();
                    dismiss();
                }
                break;
            case R.id.btn_ok:
                String user = etUser.getText().toString();
                String pwd = etPwd.getText().toString();

                if (TextUtils.isEmpty(user)) {
                    Toast.makeText(getActivity(), R.string.hint_user, Toast.LENGTH_SHORT).show();
//				AppContext.getInstance().BaseApp.showToast("Enter the content can not be empty");
                } else if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getActivity(), R.string.hint_pwd, Toast.LENGTH_SHORT).show();
                } else if (onClick != null) {
                    dismiss();
                    onClick.onLogin(user, pwd);
                }
                break;
        }
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public LoginDialog setCancelStr(String cancelStr) {
        this.cancelStr = cancelStr;
        return this;
    }

    public LoginDialog setOkStr(String okStr) {
        this.okStr = okStr;
        return this;
    }

    public void setOnLoginClick(OnLoginClickListener onClick) {
        this.onClick = onClick;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null && !getActivity().isFinishing()) {
            View v = getActivity().getCurrentFocus();
            if (etUser.isFocused()) {
                v = etUser;
            } else if (etPwd.isFocused()) {
                v = etPwd;
            }
            if (v != null)
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        }
    }


    public interface OnLoginClickListener {
        void onLogin(String user, String pwd);

        void onCancel();
    }


}
