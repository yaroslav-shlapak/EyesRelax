package com.voidgreen.eyesrelax.service;


import android.app.KeyguardManager;
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
import android.os.Vibrator;
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
    private EyesRelaxCountDownTimer timer;
    private NotificationCompat.Builder notificationBuilder;
    private Notification notification;
    final public static String TAG = "TimeService";
    private LocalBroadcastManager broadcaster;
    private final IBinder mBinder = new TimeBinder();
    private String state = "start";
    private NotificationManager mNotificationManager;

    public void setStage(String stage) {
        this.stage = stage;
        Log.d("TimeService", "setStage : " + stage );
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
        Log.d("TimeService", "setState : " + state);
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
        timeSequence(task, stage);
        return super.onStartCommand(intent, flags, startId);
    }

    private void timeSequence(String task, String stage) {
        Context context = getApplicationContext();

        switch (task) {
            case "start":
                //Utility.showToast(context, "onHandleIntent:start");
                setState("stop");
                Log.d("onStartCommand", "start");
                if(timer == null) {
                    Log.d("onStartCommand", "" + (SettingsDataUtility.getWorkTime(context)));
                    Log.d("onStartCommand", "" + (SettingsDataUtility.getRelaxTime(context)));
                    long  stageTime;
                    switch (stage) {
                        case "work":
                            startCountdownNotification(R.string.workStageTitle, R.string.workStageMessage,
                                    R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);
                            stageTime = SettingsDataUtility.getWorkTime(context) * Constants.SEC_TO_MILLIS_MULT + Constants.SEC_TO_MILLIS_MULT;
                            break;
                        case "relax":
                            startCountdownNotification(R.string.relaxStageTitle, R.string.relaxStageMessage,
                                    R.drawable.ic_eye_closed, R.drawable.eye_white_closed_notification_large);
                            stageTime = SettingsDataUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT + Constants.SEC_TO_MILLIS_MULT;
                            break;
                        default:
                            stageTime = 0;
                            break;
                    }

                    timer = new EyesRelaxCountDownTimer(stageTime, Constants.TICK_PERIOD, true);
                    timer.create();
                    Log.d("timeSequence", "start");
                }
                timer.resume();

                break;

            case "pause":
                setState("resume");
                if(timer != null) {
                    Log.d("timeSequence", "pause");
                    timer.pause();
                }

                break;

            case "resume":
                setState("pause");
                if(timer != null) {
                    Log.d("timeSequence", "resume");
                    timer.resume();
                }

                break;

            case "stop":
                Log.d("timeSequence", "stop");
                stopTimer();
                stopSelf();

                break;

            default:
                //Utility.showToast(context, "onHandleIntent:default");
                Log.d("onStartCommand", "default");
                break;
        }
    }

    private void stopTimer() {
        setStage("work");
        if(timer != null) {
            timer.cancel();
            Log.d("onStartCommand", "cancel");
        }
        timer = null;
        setState("start");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        Utility.saveTimeString(getApplicationContext(), Constants.ZERO_PROGRESS);
        sendTimeString(Constants.ZERO_PROGRESS);
        mNotificationManager.cancel(Constants.NOTIFICATION_COUNTDOWN_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    private class EyesRelaxCountDownTimer extends CountDownTimerWithPause {
        private boolean preRelaxNotification = true;

        public EyesRelaxCountDownTimer(long millisOnTimer, long countDownInterval, boolean runAtStart) {
            super(millisOnTimer, countDownInterval, runAtStart);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            // Puts the status into the Intent

            //Log.d("onHandleIntent", "onTick: " + millisUntilFinished);

            // Broadcasts the Intent to receivers in this app.
            //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);
            String notificationString = Utility.combinationFormatter(millisUntilFinished);
            updateNotification(notificationString);
            sendTimeString(notificationString);
            if(millisUntilFinished > 30 * 1000) {
                if(preRelaxNotification) {
                    preRelaxNotification = false;
                    switch (stage) {
                        case "work":
                            startTimerFinishedNotification(R.string.prerelaxStageTitle, R.string.prerelaxStageTitle,
                                    R.drawable.ic_eye_open, R.drawable.eye_white_open_notification_large);
                            vibrateShort();
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
            vibrateLong();
            Log.d("TimerFinished", stage);
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
        setNotification(titleText, tickerText, smallIcon, largeIcon);

        notificationBuilder.setContentText(Constants.ZERO_PROGRESS);
        notificationBuilder.setOngoing(true);

        buildNotification(Constants.NOTIFICATION_COUNTDOWN_ID);

    }

    private void startTimerFinishedNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        setNotification(titleText, tickerText, smallIcon, largeIcon);

        //notificationBuilder.setContentText(Constants.ZERO_PROGRESS);
        notificationBuilder.setOngoing(false);

        buildNotification(Constants.NOTIFICATION_FINISHED_ID);

    }

    private void buildNotification(int notificationId) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(Constants.NOTIFICATION_COUNTDOWN_ID, notification);

        startForeground(notificationId, notification);
    }

    private void setNotification(int titleText, int tickerText, int smallIcon, int largeIcon) {
        Resources resources = getResources();
        notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(smallIcon)
                        .setTicker(resources.getString(tickerText))
                        .setContentTitle(resources.getString(titleText));
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


    }

    public void sendTimeString(String message) {
        Intent intent = new Intent(Constants.BROADCAST_TIME_STRING_NAME);
        if(message != null) {
            intent.putExtra(Constants.BROADCAST_TIME_STRING_DATA, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    private void vibrateLong() {
        // Get instance of Vibrator from current Context
        long vt = 100;
        long dt = 500;
        long delay = 0;
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {delay, vt, dt, vt, dt * 4 / 5, vt, dt * 2 / 3, vt, dt / 3, vt, dt / 3, vt, dt / 3};

        vibrate(pattern);

    }

    private void vibrateShort() {
        // Get instance of Vibrator from current Context
        long vt = 200;
        long dt = 200;
        long delay = 0;
        // Each element then alternates between vibrate, sleep, vibrate, sleep...
        long[] pattern = {delay, vt, dt, vt, dt};

        vibrate(pattern);

    }

    private void vibrate(long[] pattern) {
        Log.d("vibrate", pattern.toString());
        // Get instance of Vibrator from current Context
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Output yes if can vibrate, no otherwise
        if (v.hasVibrator()) {
            // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
            v.vibrate(pattern, -1);
        } else {

        }
    }


    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** System Defined Broadcast */
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                CountDownTimer countDownTimer = null;

                if(stage.contentEquals("work")) {
                    if (strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON)) {
                        if (myKM.inKeyguardRestrictedInputMode()) {
                            timeSequence("pause", "work");
                            Log.d("screenOnOffReceiver", "pause");
                            countDownTimer = new CountDownTimer(
                                    SettingsDataUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT, 1 * Constants.SEC_TO_MILLIS_MULT) {
                                long millis;
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    millis = millisUntilFinished;
                                }

                                @Override
                                public void onFinish() {
                                    if(millis < (2 * Constants.SEC_TO_MILLIS_MULT)) {
                                        stopTimer();
                                    }
                                    Log.d("screenOnOffReceiver", "onFinish");
                                }
                            };
                            countDownTimer.start();
                            //System.out.println("Screen off " + "LOCKED");
                        } else {
                            if (timer != null) {
                                timeSequence("resume", "work");
                                Log.d("screenOnOffReceiver", "resume");
                            } else {
                                timeSequence("start", "work");
                                Log.d("screenOnOffReceiver", "start");
                            }

                            if(countDownTimer != null) {
                                countDownTimer.cancel();

                            }


                            //System.out.println("Screen off " + "UNLOCKED");
                        }
                    }
                }
            }
        };

        getApplicationContext().registerReceiver(screenOnOffReceiver, theFilter);
    }

}
