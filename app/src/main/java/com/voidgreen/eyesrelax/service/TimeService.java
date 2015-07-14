package com.voidgreen.eyesrelax.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Binder;
import android.os.CountDownTimer;
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
import com.voidgreen.eyesrelax.utilities.VibratorUtility;

/**
 * Created by Void on 29-Jun-15.
 */
public class TimeService extends Service {
    private EyesRelaxCountDownTimer timer;
    private NotificationCompat.Builder notificationBuilder;
    private Notification notification;
    final public static String TAG = "TimeService";
    private LocalBroadcastManager broadcaster;
    private final IBinder mBinder = new TimeBinder();
    private String state = "start";
    private NotificationManager mNotificationManager;
    BroadcastReceiver screenOnOffReceiver;
    private boolean uiForbid;

    public void setStage(String stage) {
        this.stage = stage;
        //Log.d("TimeService", "setStage : " + stage );
    }

    String stage = "work";

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
        //Log.d("TimeService", "setState : " + state);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        registerBroadcastReceiver();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Resources resources = getResources();

        String task = "";
        if (intent !=null && intent.getExtras()!=null) {
            task = intent.getStringExtra(resources.getString(R.string.serviceTask));
        }
        uiForbid = true;
        timeSequence(task, stage);
        return super.onStartCommand(intent, flags, startId);
    }

    private void timeSequence(String task, String stage) {
        Context context = getApplicationContext();

        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                setState("stop");
                uiForbid = false;
                //Log.d("onStartCommand", "start");
                if(timer == null) {
                    //Log.d("onStartCommand", "" + (SettingsDataUtility.getWorkTime(context)));
                    //Log.d("onStartCommand", "" + (SettingsDataUtility.getRelaxTime(context)));
                    long  stageTime;
                    switch (stage) {
                        case "work":
                            if(Utility.isScreenOn(context)) {
                                startCountdownNotification(R.string.workStageTitle, R.string.workStageMessage,
                                        R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);
                                stageTime = SettingsDataUtility.getWorkTime(context) * Constants.SEC_TO_MILLIS_MULT
                                        + Constants.MIN_TO_MILLIS_MULT;
                                timer = new EyesRelaxCountDownTimer(stageTime, Constants.TICK_PERIOD, true);
                                timer.create();
                            }
                            break;
                        case "relax":
                            startCountdownNotification(R.string.relaxStageTitle, R.string.relaxStageMessage,
                                    R.drawable.ic_eye_closed, R.drawable.eye_white_closed_notification_large);
                            stageTime = SettingsDataUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT
                                    + Constants.SEC_TO_MILLIS_MULT;
                            timer = new EyesRelaxCountDownTimer(stageTime, Constants.TICK_PERIOD, true);
                            timer.create();
                            break;
                        default:
                            stageTime = 0;
                            break;
                    }
                    Log.d("timeSequence", "start");
                }
                break;

            case "pause":
                pauseTimer();
                break;

            case "resume":
                uiForbid = false;
                resumeTimer();
                break;

            case "stop":
                stopTimer();
                stopSelf();
                break;

            default:
                //Utility.showToast(context, "onHandleIntent:default");
                //Log.d("onStartCommand", "default");
                break;
        }
    }

    private void stopTimer() {
        setStage("work");
        if(timer != null) {
            timer.cancel();
            Log.d("timeSequence", "stop");
        }
        timer = null;
        setState("start");
    }

    private void pauseTimer() {
        setState("resume");
        if(timer != null) {
            Log.d("timeSequence", "pause");
            timer.pause();
        }
    }

    private void resumeTimer() {
        setState("pause");
        if(timer != null) {
            Log.d("timeSequence", "resume");
            timer.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        sendTimeString(Constants.ZERO_PROGRESS);
        mNotificationManager.cancel(Constants.NOTIFICATION_COUNTDOWN_ID);
        getApplicationContext().unregisterReceiver(screenOnOffReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TimeService", "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //getApplicationContext().unregisterReceiver(screenOnOffReceiver);
        return super.onUnbind(intent);
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {
        private boolean preRelaxNotification = true;

        public EyesRelaxCountDownTimer(long millisOnTimer, long countDownInterval, boolean runAtStart) {
            super(millisOnTimer, countDownInterval, runAtStart);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent

            // Broadcasts the Intent to receivers in this app.
            //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            String notificationString = Utility.combinationFormatter(millisUntilFinished);
            Log.d("onTick", notificationString);
            updateNotification(notificationString);
            sendTimeString(notificationString);
            if(millisUntilFinished < 30 * Constants.SEC_TO_MILLIS_MULT) {
                if(preRelaxNotification) {
                    preRelaxNotification = false;
                    switch (stage) {
                        case "work":
                            startTimerFinishedNotification(R.string.prerelaxStageTitle, R.string.prerelaxStageTitle,
                                    R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);
                            VibratorUtility.vibrateShort(getApplicationContext());
                            break;
                        case "relax":

                            break;
                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void onFinish() {
            finishAll();
            VibratorUtility.vibrateLong(getApplicationContext());
            //Log.d("TimerFinished", stage);
            //stopForeground(true);
            switch (stage) {
                case "work":
                    setStage("relax");
                    //setPopUpMessage();
                    break;
                case "relax":
                    setStage("work");

                    break;
                default:
                    break;
            }
            timeSequence("start", stage);

        }
    }

    private void updateNotification(String notificationString) {
        if (notificationBuilder != null) {
            notificationBuilder.setContentText(notificationString);
            //startForeground(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
            mNotificationManager.notify(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder.build());
        }
    }

    private void finishAll() {
        sendTimeString(Constants.ZERO_PROGRESS);
        updateNotification(Constants.ZERO_PROGRESS);

        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        setState("start");
        timer = null;

    }

    private void startCountdownNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        notificationBuilder = setNotification(titleText, tickerText, smallIcon, largeIcon, true);

        notificationBuilder.setContentText(Constants.ZERO_PROGRESS);

        buildNotification(Constants.NOTIFICATION_COUNTDOWN_ID, notificationBuilder, true);

    }

    private void startTimerFinishedNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        NotificationCompat.Builder notificationBuilder = setNotification(titleText, tickerText, smallIcon, largeIcon, false);
        buildNotification(Constants.NOTIFICATION_FINISHED_ID, notificationBuilder, false);

    }

    private void buildNotification(int notificationId, NotificationCompat.Builder notificationBuilder, boolean startForegroundEnable) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(notificationId, notification);
        if(startForegroundEnable) {
            startForeground(notificationId, notification);
        }
    }

    private NotificationCompat.Builder setNotification(int titleText, int tickerText, int smallIcon, int largeIcon, boolean onGoingEnable) {
        Resources resources = getResources();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(smallIcon)
                        .setTicker(resources.getString(tickerText))
                        .setContentTitle(resources.getString(titleText))
                        .setOngoing(onGoingEnable);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

/*        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), largeIcon),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                true);
        notificationBuilder.setLargeIcon(bm);*/

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
        return notificationBuilder;
    }

    public void sendTimeString(String message) {
        Intent intent = new Intent(Constants.BROADCAST_TIME_STRING_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_TIME_STRING_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }


    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        screenOnOffReceiver = new BroadcastReceiver() {
            CountDownTimer countDownTimer = null;
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();


                if(!uiForbid && stage.contentEquals("work")) {
                    if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                            if(countDownTimer == null) {
                                pauseTimer();
                                Log.d("screenOnOffReceiver", "pause");
                                countDownTimer = new CountDownTimer(
                                        SettingsDataUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT,
                                        Constants.SEC_TO_MILLIS_MULT) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        stopTimer();
                                        Log.d("screenOnOffReceiver", "onFinish");
                                    }
                                };
                                countDownTimer.start();
                            }
                            //System.out.println("Screen off " + "LOCKED");
                        } else if(strAction.equals(Intent.ACTION_SCREEN_ON)) {
                            if (timer != null) {
                                resumeTimer();
                                Log.d("screenOnOffReceiver", "resume");
                            } else {
                                timeSequence("start", "work");
                                Log.d("screenOnOffReceiver", "start");
                            }

                            if(countDownTimer != null) {
                                countDownTimer.cancel();
                                countDownTimer = null;
                                Log.d("screenOnOffReceiver", "cancelTimer");
                            }

                            //System.out.println("Screen off " + "UNLOCKED");
                        }
                    }
                }

        };

        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);
        Log.d("TimeService", "registerBroadcastReceiver");
    }




}
