package com.zhukun.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhukun.coolweather.service.AutoUpdateService;

/**
 * Created by Administrator on 2015/9/16.
 */
public class AutoUpdateReciver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
