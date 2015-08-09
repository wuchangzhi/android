package com.ckt.francis.navigationmap.receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ckt.francis.navigationmap.activitys.LogUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wuchangzhi on 15-8-8.
 */
public class PushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if(intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

            LogUtil.d("title = " + title + ": Message = " + message);
        } else if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_ALERT);

            LogUtil.d("title = " + title + ": Message = " + message);
        }
    }
}
