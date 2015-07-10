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
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.voidgreen.eyesrelax.MainActivity;
import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.CountDownTimerWithPause;
import com.voidgreen.eyesrelax.utilities.SettingsDataUtility;
import com.voidgreen.eyesrelax.utilities.Utility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends Service {
    EyesRelaxCountDownTimer timer;
    NotificationCompat.Builder notificationBuilder;
    final public static String TAG = "TimeService";
    LocalBroadcastManager broadcaster;
    private final IBinder mBinder = new TimeBinder();
    String state = "start";

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TimeBinder extends Binder {
        public TimeService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TimeService.this;
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();
        Context context = getApplicationContext();
        String task = "";
        if (intent !=null && intent.getExtras()!=null) {
            task = intent.getStringExtra(resources.getString(R.string.serviceTask));
        }

        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                Log.d("onStartCommand", "start");
                if(timer == null) {
                    Log.d("onStartCommand", "" + (SettingsDataUtility.getWorkTime(context)));
                    Log.d("onStartCommand", "" + (SettingsDataUtility.getRelaxTime(context)));
                    createNotification();
                    timer = new EyesRelaxCountDownTimer(SettingsDataUtility.getWorkTime(context) * 60 * 1000, 1000, true);
                    timer.create();
                }
                timer.resume();
                setState("stop");

                break;

            case "pause":

                if(timer != null) {
                    Log.d("onStartCommand", "pause");
                    timer.pause();
                }
                setState("resume");
                break;

            case "resume":
                if(timer != null) {
                    timer.resume();
                }
                setState("pause");
                break;

            case "stop":
                if(timer != null) {
                    timer.cancel();
                    Log.d("onStartCommand", "cancel");
                }
                setState("start");
                break;

            default:
                //Utility.showToast(context, "onHandleIntent:default");
                Log.d("onStartCommand", "default");
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
        finishAll();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {

        public EyesRelaxCountDownTimer(long millisOnTimer, long countDownInterval, boolean runAtStart) {
            super(millisOnTimer, countDownInterval, runAtStart);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent

            //Log.d("onHandleIntent", "onTick: " + millisUntilFinished);

            // Broadcasts the Intent to receivers in this app.
            //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            String notificationString  = Utility.combinationFormatter(millisUntilFinished);
            notificationBuilder.setContentText(notificationString);
            startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build());

            sendTimeString(notificationString);



        }

        @Override
        public void onFinish() {
            finishAll();
            stopSelf();
        }
    }

    private void finishAll() {
        sendTimeString(Constants.ZERO_PROGRESS);
        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        if (notificationBuilder != null) {
            notificationBuilder.setContentText(Constants.ZERO_PROGRESS);
            notificationBuilder.setOngoing(false);
            startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build());
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(Constants.NOTIFICATION_ID);
        }
        setState("start");
    }

    public void createNotification() {
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.eye_white_open)
                        .setContentTitle("Time to relax")
                        .setContentText(Constants.ZERO_PROGRESS);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eye_white_open_notification_large),
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

    public void sendTimeString(String message) {
        Intent intent = new Intent(Constants.BROADCAST_TIME_STRING_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_TIME_STRING_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    public void sendState(String message) {
        Intent intent = new Intent(Constants.BROADCAST_STATE_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_STATE_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }

}
