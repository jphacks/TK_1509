package com.gaasii.eye_et_glass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gaasii.eye_et_glass.Constants;


public final class CustomExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(Constants.LOG_TAG, "onReceive: " + intent.getAction());
        intent.setClass(context, CustomExtensionService.class);
        context.startService(intent);
    }
}
