package com.rl.p2plib.bean;

/**
 * Created by Nicky on 2017/8/19.
 */

public class UpdateProgress {

    private int status;//0 成功

    private int result;//0~100

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
