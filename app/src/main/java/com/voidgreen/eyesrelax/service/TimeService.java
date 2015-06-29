package com.voidgreen.eyesrelax.service;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.SettingsDataUtility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends IntentService {


    public TimeService() {
        super("TimeService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Resources resources = getResources();
        String task = intent.getStringExtra(resources.getString(R.string.serviceTask));
        Context context = getApplicationContext();
        EyesRelaxCountDownTimer timer = null;

        switch (task) {
            case "start":
                timer = new EyesRelaxCountDownTimer(SettingsDataUtility.getWorkTime(context), 1000);
                break;

            case "stop":
                if (timer != null) {
                    timer.cancel();
                }
                break;

            default:
                break;
        }
    }

    private class EyesRelaxCountDownTimer extends CountDownTimer {
        Intent localIntent;

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public EyesRelaxCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            localIntent = new Intent(Constants.BROADCAST_ACTION);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent
            localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, millisUntilFinished);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
        }

        @Override
        public void onFinish() {

        }
    }
}
