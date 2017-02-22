package com.xiong.howstheweather.util;

/**
 * Created by xiong on 2017/2/22.
 * 定义一个接口
 */

public interface HttpCallbackListener {
    void onFinish(String response);//当服务器成功响应的时候调用此方法
    void onError(Exception e);//当进行网络操作错误的时候调用此方法
}
