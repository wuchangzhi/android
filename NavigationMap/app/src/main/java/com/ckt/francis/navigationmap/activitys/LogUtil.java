package com.ckt.francis.navigationmap.activitys;

import android.util.Log;

/**
 * Created by wuchangzhi on 15-8-8.
 */
public class LogUtil {
    private static final String TAG = "JPush";
    private static final boolean DEBUG = true;

    public static void v(String msg){
        if(DEBUG) {
            Log.v(TAG,msg);
        }
    }

    public static void d(String msg){
        if(DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg){
        if(DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg){
        if(DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg){
        if(DEBUG) {
            Log.e(TAG, msg);
        }
    }
}
