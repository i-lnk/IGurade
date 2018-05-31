package com.rl.geye.ui.aty;


import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edwintech.vdp.jni.ApiMgrV2;
import com.nicky.framework.widget.XEditText;
import com.rl.commons.interf.EdwinTimeoutCallback;
import com.rl.commons.utils.StringUtils;
import com.rl.geye.MyApp;
import com.rl.geye.R;
import com.rl.geye.base.BaseP2PAty;
import com.rl.geye.constants.Constants;
import com.rl.geye.db.bean.EdwinDevice;
import com.rl.p2plib.bean.SetResult;
import com.rl.p2plib.constants.P2PConstants;

import butterknife.BindView;

import static com.rl.commons.BaseApp.showToast;


/**
 * Created by Nicky on 2016/9/21.
 * 修改设备密码
 */
public class ModifyPwdAty extends BaseP2PAty implements TextWatcher {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_pwd_orig)
    XEditText etPwdOrig;
    @BindView(R.id.et_pwd_new)
    XEditText etPwdNew;
    @BindView(R.id.et_pwd_new2)
    XEditText etPwdNew2;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.ly_all)
    LinearLayout lyAll;


    @Override
    protected int getLayoutId() {
        return R.layout.aty_modify_pwd;
    }

    @Override
    public View getVaryTargetView() {
        return lyAll;
    }

    @Override
    protected void initToolBar() {
        initCommonToolBar(toolbar);
    }

    @Override
    protected void onP2PStatusChanged() {

    }


    @Override
    protected void initViewsAndEvents() {
//        btnOk.getBackground().setLevel(2);

        btnOk.setOnClickListener(this);
        btnOk.setEnabled(false);
        etPwdOrig.requestFocus();
        etPwdOrig.addTextChangedListener(this);
        etPwdNew.addTextChangedListener(this);
        etPwdNew2.addTextChangedListener(this);
    }


    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {

            case R.id.btn_ok:
                String origP = etPwdOrig.getText().toString().trim();
                String newP = etPwdNew.getText().toString().trim();
                String newP2 = etPwdNew2.getText().toString().trim();

                if (!(origP.equals(mDevice.getPwd()))) {
                    showToast(R.string.tips_err_pwd_orig);
                } else if (!(newP.equals(newP2))) {
                    showToast(R.string.tips_err_pwd_new_2);
                } else if (newP.equals(mDevice.getPwd())) {
                    showToast(R.string.tips_err_pwd_same);
                } else {
                    showLoadDialog(new EdwinTimeoutCallback(5000) {

                        @Override
                        public void onTimeOut() {
                            hideLoadDialog();
                        }
                    }, null);
                    updatePwd(origP, newP);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (StringUtils.isEmpty(etPwdOrig.getText().toString().trim())
                || StringUtils.isEmpty(etPwdNew.getText().toString().trim())
                || StringUtils.isEmpty(etPwdNew2.getText().toString().trim())) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }
    }

    @Override
    protected MyP2PCallBack getP2PCallBack() {
        return new MyP2PCallBack() {
            @Override
            public void onSetResult(String did, int msgType, SetResult setRes) {
                super.onSetResult(did, msgType, setRes);
                if (setRes != null && isSameDevice(did)) {
                    if (setRes.getResult() == P2PConstants.SetResVal.RES_OK) {
                        String newP = etPwdNew.getText().toString().trim();

                        EdwinDevice dev = MyApp.getDaoSession().getEdwinDeviceDao().load(mDevice.getId());
                        dev.setPwd(newP);
                        mDevice.setPwd(newP);
                        MyApp.getDaoSession().getEdwinDeviceDao().update(dev);

                        fromIntent.putExtra(Constants.BundleKey.KEY_DEV_INFO, mDevice);
                        setResult(RESULT_OK, fromIntent);
                        postEdwinEvent(Constants.EdwinEventType.EVENT_DEV_UPDATE_PWD, mDevice);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                hideLoadDialog();
                                showToast(R.string.tips_err_set);
                            }
                        });
                    }
                    //				if( setRes.getResult() == SetResVal.RES_ERR_PWD ){
                    //					runOnUiThread(new Runnable() {
                    //
                    //						@Override
                    //						public void run() {
                    //							showToast(R.string.tips_err_pwd);
                    //						}
                    //					});
                    //				}else if( setRes.getResult() == SetResVal.RES_ERR_USER ){
                    //					runOnUiThread(new Runnable() {
                    //
                    //						@Override
                    //						public void run() {
                    //							showToast(R.string.tips_err_user);
                    //						}
                    //					});
                    //				}else{
                    //					//TODO
                    //				}
                }
            }
        };
    }


    /**
     * 修改密码
     */
    private void updatePwd(String oldPwd, String newPwd) {
        ApiMgrV2.updatePwd(mDevice.getDevId(), mDevice.getUser(), oldPwd, newPwd);
    }


}
