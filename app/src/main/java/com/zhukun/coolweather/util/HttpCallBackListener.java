package com.zhukun.coolweather.util;

/**
 * Created by Administrator on 2015/9/8.
 */
public interface HttpCallBackListener {
    void onFinish(String response);
    void onError(Exception e);
}
