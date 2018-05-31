package com.rl.p2plib.bean;

/**
 * Created by Nicky on 2017/3/30.
 * 设备版本
 */
public class SysVersion {

    private String url;
    private String ver;
    private int size;
    private String md5;
    private int result;
    private String msg;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SysVersion{" +
                "url='" + url + '\'' +
                ", ver='" + ver + '\'' +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                ", result=" + result +
                ", msg='" + msg + '\'' +
                '}';
    }
}
