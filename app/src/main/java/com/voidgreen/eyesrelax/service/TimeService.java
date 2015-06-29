package com.voidgreen.eyesrelax.service;


import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends IntentService {


    public TimeService() {
        super("TimeService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Uri receivedData = intent.getData();

    }


}
