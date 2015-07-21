package com.voidgreen.eyesrelax;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.voidgreen.eyesrelax.service.TimeService;
import com.voidgreen.eyesrelax.utilities.SharedPrefUtility;

/**
 * Created by Void on 17-Jul-15.
 */
public class OnBootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnBootCompletedBroadcastReceiver", "onReceive before if");

        if(SharedPrefUtility.isStartOnBootEnabled(context)) {
            Log.d("OnBootCompletedBroadcastReceiver", "onReceive in if");
            Resources resources = context.getResources();
            Intent serviceIntent = new Intent(context, TimeService.class);
            intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.startTask));
            intent.addCategory(TimeService.TAG);
            context.startService(serviceIntent);
        }

    }
}
