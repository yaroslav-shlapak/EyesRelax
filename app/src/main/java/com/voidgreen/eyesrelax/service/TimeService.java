package com.voidgreen.eyesrelax.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.voidgreen.eyesrelax.MainActivity;
import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.CountDownTimerWithPause;
import com.voidgreen.eyesrelax.utilities.SettingsDataUtility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends Service {
    EyesRelaxCountDownTimer timer;
    NotificationCompat.Builder notificationBuilder;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();
        Context context = getApplicationContext();
        String task = "";
        if (intent !=null && intent.getExtras()!=null) {
            task = intent.getStringExtra(resources.getString(R.string.serviceTask));
        }
        Log.d("onStartCommand", "onHandleIntent");
        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                Log.d("onStartCommand", "start");
                if(timer == null) {
                    timer = new EyesRelaxCountDownTimer(SettingsDataUtility.getWorkTime(context) * 60 * 1000, 1000  , true);
                    timer.create();
                }
                timer.resume();

                break;

            case "pause":

                Log.d("onStartCommand", "pause");
                timer.pause();
                break;

            case "stop":
                timer.cancel();
                Log.d("onStartCommand", "cancel");
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
            Log.d("onDestroy", "cancelTimer");
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {
        Intent localIntent;

        public EyesRelaxCountDownTimer(long millisOnTimer, long countDownInterval, boolean runAtStart) {
            super(millisOnTimer, countDownInterval, runAtStart);
            this.localIntent = new Intent(Constants.BROADCAST_ACTION);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent

            Log.d("onHandleIntent", "onTick: " + millisUntilFinished);

            localIntent.putExtra(Constants.EXTENDED_DATA_STATUS, millisUntilFinished);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            notificationBuilder.setContentText(Long.toString(millisUntilFinished));
            startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build());

        }

        @Override
        public void onFinish() {

        }
    }

    public void createNotification() {
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.eye_white_open)
                        .setContentTitle("Time to relax")
                        .setContentText("00:00:00");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eye_white_open),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                true);
        notificationBuilder.setLargeIcon(bm);
        notificationBuilder.setOngoing(true);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(Constants.NOTIFICATION_ID, notification);

        startForeground(Constants.NOTIFICATION_ID, notification);
    }
}
