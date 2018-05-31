package com.rl.geye.net.model;

/**
 * Created by cc on 2017/3/20.
 */
public class SimpleResponse extends Base {

    public int code;
    public String message;

    public LzyResponse toLzyResponse() {
        LzyResponse lzyResponse = new LzyResponse();
        lzyResponse.code = code;
        lzyResponse.message = message;
        return lzyResponse;
    }
}