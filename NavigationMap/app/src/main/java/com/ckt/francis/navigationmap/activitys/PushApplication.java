package com.ckt.francis.navigationmap.activitys;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cn.jpush.android.api.JPushInterface;

public class PushApplication extends Application {
    private static final String TAG = "JPush";

    @Override
    public void onCreate() {
         super.onCreate();
         
         JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);     		// 初始化 JPush

        SDKInitializer.initialize(getApplicationContext());
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=55d9caa5");
    }
}
