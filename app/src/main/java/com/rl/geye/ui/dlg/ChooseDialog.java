package com.rl.geye.ui.dlg;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nicky.framework.base.BaseDialog;
import com.rl.commons.interf.Chooseable;
import com.rl.geye.R;
import com.rl.geye.adapter.ChooseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 选择对话框
 *
 * @author NickyHuang
 */
public class ChooseDialog<T extends Chooseable> extends BaseDialog implements OnItemClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.layout_max)
    FrameLayout layoutMax;

    @BindView(R.id.layout_min)
    FrameLayout layoutMin;

    @BindView(R.id.listview_max)
    ListView listViewMax;

    @BindView(R.id.listview_min)
    ListView listViewMin;

    private ChooseAdapter<T> mAdapter;

    private List<T> mList = new ArrayList<>();
    private T choosedItem;

    private String title;

    private OnItemChooseListener<T> mOnItemChooseListener;

    @Override
    protected void initDialog(Dialog dialog) {
        setDialogGravity(Gravity.CENTER);
        View view = inflateContentView(R.layout.dialog_choose);
        ButterKnife.bind(this, view);

        if (mAdapter == null) {
            mAdapter = new ChooseAdapter<T>(getActivity(), mList);
        }
        if (mList != null) {
            mAdapter.setDatas(mList);
            if (choosedItem != null)
                mAdapter.chooseItem(choosedItem);
            notifyDataSetChanged();
        }

        if (title != null) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        }

        dialog.setContentView(view);
    }

//	@Override
//	protected void initBind(View view) {
//		ButterKnife.bind(this,view);
//	}

    public ChooseDialog<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public ChooseDialog<T> setListDatas(List<T> list) {
        mList = list;
        return this;
    }

    public ChooseDialog<T> setChoosedItem(T obj) {
        choosedItem = obj;
        return this;
    }

    public ChooseDialog<T> setOnItemChooseListener(OnItemChooseListener<T> listener) {
        this.mOnItemChooseListener = listener;
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        choosedItem = mList.get(position);
        mAdapter.chooseItem(choosedItem);
        if (mOnItemChooseListener != null) {
            mOnItemChooseListener.onItemChoose(position, choosedItem);
        }
        dismiss();
    }

    public void notifyDataSetChanged() {
        if (mList == null || layoutMin == null || layoutMax == null
                || listViewMax == null || listViewMax == null) {
            return;
        }
        if (mList.size() > 5) {
            layoutMin.setVisibility(View.GONE);
            layoutMax.setVisibility(View.VISIBLE);
            listViewMax.setAdapter(mAdapter);
            listViewMax.setOnItemClickListener(this);
        } else {
            layoutMax.setVisibility(View.GONE);
            layoutMin.setVisibility(View.VISIBLE);
            listViewMin.setAdapter(mAdapter);
            listViewMin.setOnItemClickListener(this);
        }
        mAdapter.notifyDataSetChanged();
    }


    public interface OnItemChooseListener<T2 extends Chooseable> {
        public void onItemChoose(int position, T2 data);
    }


}
