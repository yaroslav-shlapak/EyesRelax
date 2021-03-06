package com.voidgreen.eyesrelax;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.voidgreen.eyesrelax.service.TimeService;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.SharedPrefUtility;
import com.voidgreen.eyesrelax.utilities.Utility;

/**
 * Created by Void on 17-Jul-15.
 */
public class OnBootCompletedBroadcastREceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("OnBootCompletedBroadcastReceiver", "onReceive before if");

        Utility.saveTimeString(context, Constants.ZERO_PROGRESS);
        Utility.saveStageString(context, Constants.WORK_STAGE);

        if(SharedPrefUtility.isStartOnBootEnabled(context)) {
            Log.d("OnBootCompletedBroadcastReceiver", "onReceive in if");

            Resources resources = context.getResources();
            Intent serviceIntent = new Intent(context, TimeService.class);
            serviceIntent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.startTask));
            serviceIntent.addCategory(TimeService.TAG);
            context.startService(serviceIntent);


        }

    }
}
