package com.rl.geye.bean;

import java.util.List;

/**
 * Created by Nicky on 2017/11/17.
 */

public class AndroidInfo {

    /**
     * Version: 为服务器中app版本
     * Update: 为表示app是否强制升级，1为强制升级，0为用户本次可不升级。
     * Url: 为升级下载地址。
     */
    private String version;
    private int update;
    private String url;

    private List<LanguageInfo> content;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<LanguageInfo> getContent() {
        return content;
    }

    public void setContent(List<LanguageInfo> content) {
        this.content = content;
    }
}
