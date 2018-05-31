package com.rl.geye.ui.dlg;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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
public class InputDialog extends BaseDialog {

    @BindView(R.id.btn_cancel)
    Button btnCancel;

    @BindView(R.id.btn_ok)
    Button btnOk;

    @BindView(R.id.et_content)
    XEditText etContent;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.top_line)
    View topLine;

    @BindView(R.id.ly_et)
    TextInputLayout textInputName;
    private int maxLength = 0;
    private String title;
    private String hint;
    private String content;
    private String cancelStr;
    private String okStr;
    private boolean showEtcontent = true;
    private boolean showTitle = true;
    private OnEditClickListener onClick;

    @Override
    protected void initDialog(Dialog dialog) {
        setDialogGravity(Gravity.CENTER);
        View view = inflateContentView(R.layout.dialog_input);
        ButterKnife.bind(this, view);
        if (showTitle) {
            tvTitle.setVisibility(View.VISIBLE);
            topLine.setVisibility(View.VISIBLE);
            if (title != null) {
                tvTitle.setText(title);
            }
        } else {
            tvTitle.setVisibility(View.GONE);
            topLine.setVisibility(View.GONE);
        }

        if (hint != null) {
            etContent.setHint(hint);
            textInputName.setHint(hint);
        }
        if (content != null) {
            etContent.setText(content);
            etContent.requestFocus();
        }
        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }
        if (okStr != null) {
            btnOk.setText(okStr);
        }

        if (maxLength != 0) {
            etContent.setFilters(new InputFilter[]{new EditChsLenInputFilter(maxLength)});
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
                String content = etContent.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(getActivity(), "Please input some content.", Toast.LENGTH_SHORT).show();
//				AppContext.getInstance().showToast("Enter the content can not be empty");
                } else if (onClick != null) {
                    dismiss();
                    onClick.onResult(content);
                }
                break;
        }
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public InputDialog setTitle(@NonNull String title) {
        this.title = title;
        showTitle = true;
        return this;
    }

    public InputDialog setNoTitle() {
        showTitle = false;
        title = null;
        return this;
    }

    public InputDialog setHint(String hint) {
        this.hint = hint;
        return this;
    }

    public InputDialog setShowEt(boolean showEtcontent) {
        this.showEtcontent = showEtcontent;
        return this;
    }

    public InputDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public InputDialog setCancelStr(String cancelStr) {
        this.cancelStr = cancelStr;
        return this;
    }

    public InputDialog setOkStr(String okStr) {
        this.okStr = okStr;
        return this;
    }

    public void setOnEditClick(OnEditClickListener onClick) {
        this.onClick = onClick;
    }

    public interface OnEditClickListener {
        void onResult(String result);

        void onCancel();
    }


}
