package com.rl.geye.net.callback;

import com.lzy.okgo.callback.StringCallback;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Nicky on 2017/8/19.
 */

public abstract class MyStringCallback extends StringCallback {

    @Override
    public String convertSuccess(Response response) throws Exception {

        ResponseBody body = response.body();
        if (body == null)
            return null;
        String str = new String(body.bytes());
//        String s = StringConvert.create().convertSuccess(response);
        response.close();
        return str;
    }

}
