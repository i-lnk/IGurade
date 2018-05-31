package com.rl.geye.ui.dlg;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.aigestudio.wheelpicker.WheelPicker.OnItemSelectedListener;
import com.nicky.framework.base.BaseDialog;
import com.rl.commons.bean.EdwinItem;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 数据选择框<EdwinItem 类型数据>
 *
 * @author NickyHuang
 */
public class ChooseDataDialog extends BaseDialog {

    private static final EdwinItem INVALID_VAL = null;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wheel_data)
    WheelPicker wheelData;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;
    private EdwinItem curData = INVALID_VAL;


    private String okStr;
    private String cancelStr;
    private String title;
    private String unit;


    private List<EdwinItem> dataList = new ArrayList<>();


    private OnDataChooseListener mOnDataChooseListener;
    private OnItemSelectedListener mItemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(WheelPicker picker, Object data, int position) {

            switch (picker.getId()) {
                case R.id.wheel_data:
                    curData = (EdwinItem) data;
                    break;
            }

        }
    };

    public ChooseDataDialog setOnDataChooseListener(OnDataChooseListener listener) {
        this.mOnDataChooseListener = listener;
        return this;
    }

    @Override
    protected int getAnimStyle() {
        return com.nicky.framework.R.style.BottomDialogStyle;
    }

    @Override
    protected void initDialog(Dialog dialog) {
        setDialogGravity(Gravity.BOTTOM);
        View view = inflateContentView(R.layout.dialog_choose_data);
        ButterKnife.bind(this, view);
        if (okStr != null) {
            btnOk.setText(okStr);
        }

        if (cancelStr != null) {
            btnCancel.setText(cancelStr);
        }

        if (!StringUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }
        if (!StringUtils.isEmpty(unit)) {
            tvUnit.setText(unit);
            tvUnit.setVisibility(View.VISIBLE);
        }

        if (dataList.isEmpty()) {
            for (int i = 0; i < 30; i++) {
                dataList.add(new EdwinItem(String.valueOf(i), i));
            }
        }
        int index = dataList.indexOf(curData);
        if (index == -1) {
            index = 0;
            curData = dataList.get(0);
        }
        wheelData.setData(dataList);
        wheelData.setSelectedItemPosition(index);
        wheelData.setOnItemSelectedListener(mItemSelectedListener);

        dialog.setContentView(view);
    }


    public ChooseDataDialog setOkStr(String okStr) {
        this.okStr = okStr;
        return this;
    }

    public ChooseDataDialog setCancelStr(String cancelStr) {
        this.cancelStr = cancelStr;
        return this;
    }


    public ChooseDataDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChooseDataDialog setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public ChooseDataDialog setCurData(EdwinItem data) {
        curData = data;
        return this;
    }

    public ChooseDataDialog setDatas(List<EdwinItem> datas) {
        if (datas != null && !datas.isEmpty())
            dataList = datas;
        return this;
    }

    public ChooseDataDialog setDatas(EdwinItem[] datas) {
        if (datas != null && datas.length > 0) {
            dataList.clear();
            for (EdwinItem data : dataList) {
                dataList.add(data);
            }
        }
        return this;
    }


    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (mOnDataChooseListener != null) {
                    mOnDataChooseListener.onDataChoose(curData);
                }
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }


    public interface OnDataChooseListener {
        void onDataChoose(EdwinItem data);
    }


}
