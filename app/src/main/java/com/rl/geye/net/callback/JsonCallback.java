package com.rl.geye.net.callback;

import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;
import com.rl.geye.MyApp;
import com.rl.geye.net.FastJsonConvert;
import com.rl.geye.net.model.LzyResponse;
import com.rl.geye.net.model.SimpleResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by cc on 2017/3/20.
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        // request.headers("header1", "HeaderValue1")
        //        .params("params1", "ParamsValue1")
        //       .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }

    @Override
    public T convertSuccess(Response response) throws Exception {

        Type genType = getClass().getGenericSuperclass();
        //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数值
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];

        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");

        Type rawType = ((ParameterizedType) type).getRawType();

        Type typeArgument = ((ParameterizedType) type).getActualTypeArguments()[0];

        JsonReader jsonReader = new JsonReader(response.body().charStream());
        String jsonStr = response.body().string();
        if (typeArgument == Void.class) {

            SimpleResponse simpleResponse = FastJsonConvert.fromJson(jsonStr, SimpleResponse.class);
            // SimpleResponse simpleResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
            response.close();
            return (T) simpleResponse.toLzyResponse();
        } else if (rawType == LzyResponse.class) {
            //有数据类型，表示有data
            LzyResponse lzyResponse = FastJsonConvert.fromJson(jsonStr, type);
//            LzyResponse lzyResponse = Convert.fromJson(jsonReader, type);
            response.close();
            return (T) lzyResponse;
//            int code = lzyResponse.errorCode;
//            if (code == 0) { // 成功
//                return (T) lzyResponse;
//            } else if (code == 1000 ) {
//                throw new IllegalStateException("access token 验证失败");
//            } else if (code == 1001) {
//                throw new IllegalStateException("手机号格式不正确");
//            } else if (code == 1002) {
//                throw new IllegalStateException("手机验证码格式不正确");
//            } else if (code == 1003) {
//                throw new IllegalStateException("未获取过手机验证码");
//            } else if (code == 1004) {
//                throw new IllegalStateException("手机验证码已经过期");
//            }else if (code == 1005) {
//                throw new IllegalStateException("手机验证码不正确");
//            } else {
//                throw new IllegalStateException(lzyResponse.errorMessage);
//            }

        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        if (null != response) {
            int code = response.code();
            switch (code) {
                case 404:
                    onErrorHandled("找不到服务器地址");
                    break;
                case 408:
                    onErrorHandled("请求链接超时");
                    break;
                default:
                    onErrorHandled("服务器异常");
                    break;
            }
        } else {
            onErrorHandled("网络连接不可用，请稍后重试");
        }
    }

    /**
     * 错误消息处理，只有错误消息提示
     *
     * @param errorMsg
     */
    protected void onErrorHandled(String errorMsg) {
        MyApp.showToast(errorMsg);
        Logger.e("http error:" + errorMsg);
    }

}