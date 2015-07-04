package com.voidgreen.eyesrelax.service;


import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.CountDownTimerWithPause;
import com.voidgreen.eyesrelax.utilities.SettingsDataUtility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends Service {
    EyesRelaxCountDownTimer timer;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();
        Context context = getApplicationContext();
        String task = "";
        if (intent !=null && intent.getExtras()!=null) {
            task = intent.getStringExtra(resources.getString(R.string.serviceTask));z
        }
        Log.d("onHandleIntent", "onHandleIntent");
        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                Log.d("onHandleIntent", "onHandleIntent:start");
                timer = new EyesRelaxCountDownTimer(SettingsDataUtility.getWorkTime(context) * 60 * 1000, 5000);
                timer.start();

                break;

            case "pause":
                try {
                    timer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            default:
                //Utility.showToast(context, "onHandleIntent:default");
                Log.d("onHandleIntent", "onHandleIntent:default");
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            Log.d("onHandleIntent", "onDestroy:stopTimer");
            timer.stop();
        }
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {
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

            Log.d("onHandleIntent", "onTick: " + millisUntilFinished);

            localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, millisUntilFinished);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

        }

        @Override
        public void onFinish() {

        }
    }

    private class MyTimer {
        public int getTimerLength() {
            return timerLength;
        }

        public void setTimerLength(int timerLength) {
            this.timerLength = timerLength;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public MyTimer(int timerLength, int interval) {
            this.timerLength = timerLength;
            this.interval = interval;
        }

        private int timerLength;
        private int interval;

        public void start() {

        }

        public void pause() {

        }

        public void stop() {

        }

        /**
         * Callback fired on regular interval.
         * @param millisUntilFinished The amount of time until finished.
         */
        public void onTick(int millisUntilFinished) {

        }

        /**
         * Callback fired when the time is up.
         */
        public  void onFinish() {

        }


    }
}
