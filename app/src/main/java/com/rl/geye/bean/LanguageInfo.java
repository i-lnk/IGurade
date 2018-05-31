package com.rl.geye.bean;

/**
 * Created by Nicky on 2017/11/17.
 */
public class LanguageInfo {
    /**
     * lang选项表示语言，0为中文，1为英文。
     * Update_string: 表示升级原因，以UTF-8格式存储内容。
     */
    private int lang;
    private String update_string;

    public int getLang() {
        return lang;
    }

    public void setLang(int lang) {
        this.lang = lang;
    }

    public String getUpdate_string() {
        return update_string;
    }

    public void setUpdate_string(String update_string) {
        this.update_string = update_string;
    }
}
