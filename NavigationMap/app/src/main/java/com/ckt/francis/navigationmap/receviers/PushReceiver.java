package com.ckt.francis.navigationmap.receviers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.ckt.francis.navigationmap.util.LogUtil;
import com.ckt.francis.navigationmap.activitys.MainActivity;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by wuchangzhi on 15-8-8.
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String MESSAGE = "_message" ;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Intent _intent = new Intent(context,MainActivity.class);
        _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            _intent.putExtra(MESSAGE,message);
            LogUtil.d("title = " + title + ": Message = " + message);

            context.startActivity(_intent);
        } else if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)){
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String message = bundle.getString(JPushInterface.EXTRA_ALERT);

            LogUtil.d("title = " + title + ": Message = " + message);
            _intent.putExtra(MESSAGE, message);
            context.startActivity(_intent);
        }
    }
}
