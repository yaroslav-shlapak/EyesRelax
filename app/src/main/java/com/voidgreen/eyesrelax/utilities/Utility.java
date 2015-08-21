package com.voidgreen.eyesrelax.utilities;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.Display;
import android.widget.Toast;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.service.TimeService;

import java.util.concurrent.TimeUnit;

/**
 * Created by y.shlapak on Jun 30, 2015.
 */
public class Utility {
    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public static String combinationFormatter(final long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long hours = TimeUnit.MILLISECONDS.toHours(millis);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "00" : hours < 10 ? String.valueOf("0" + hours) :
                String.valueOf(hours));
        b.append(":");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) :
                String.valueOf(minutes));
        b.append(":");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) :
                String.valueOf(seconds));
        return b.toString();
    }

    public static String getSavedTimeString(Context context) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        return batteryInfoSharedPref.getString
                (context.getString(R.string.timeString), Constants.ZERO_PROGRESS);
    }

    public static void saveTimeString(Context context, String value) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = batteryInfoSharedPref.edit();
        editor.putString(context.getString(R.string.timeString), value);
        editor.apply();
    }

    public static String getStageString(Context context) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        return batteryInfoSharedPref.getString
                (context.getString(R.string.stageSting), "");
    }

    public static void saveStageString(Context context, String value) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = batteryInfoSharedPref.edit();
        editor.putString(context.getString(R.string.stageSting), value);
        editor.apply();
    }

    public static int getTimeValue(Context context) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        return batteryInfoSharedPref.getInt
                (Constants.TIME_VALUE_SHARED_PREFERENCE, 0);
    }

    public static void saveTimeValue(Context context, int value) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = batteryInfoSharedPref.edit();
        editor.putInt(Constants.TIME_VALUE_SHARED_PREFERENCE, value);
        editor.apply();
    }

    public static String getState(Context context) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        return batteryInfoSharedPref.getString
                (Constants.PAUSE_STOP_BUTTONS_STATE, "Pause");
    }

    public static void saveState(Context context, String value) {
        SharedPreferences batteryInfoSharedPref = context.getSharedPreferences(context.getString(R.string.eyesRelaxSharedPref),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = batteryInfoSharedPref.edit();
        editor.putString(Constants.PAUSE_STOP_BUTTONS_STATE, value);
        editor.apply();
    }


    /**
     * Is the screen of the device on.
     *
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public static boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager powerManager = (PowerManager) context.getSystemService(Service.POWER_SERVICE);
            return powerManager.isScreenOn();
        }
    }

    public static boolean isTimeServiceRunning() {
        return TimeService.serviceRunning;
    }

    public static String getState() {
        if (isTimeServiceRunning()) {
            return TimeService.getState();
        } else {
            return Constants.DEFAULT_STATE;
        }
    }

    public static String getStage() {
        if (isTimeServiceRunning()) {
            return TimeService.getStage();
        } else {
            return Constants.DEFAULT_STAGE;
        }
    }

    public static String getNotificationString() {
        if (isTimeServiceRunning()) {
            return TimeService.getNotificationString();
        } else {
            return Constants.DEFAULT_TIME_STRING;
        }
    }


    public static int getStageTime() {
        if (isTimeServiceRunning()) {
            return (int) TimeService.getStageTime();
        } else {
            return Constants.DEFAULT_STAGE_TIME;
        }
    }


}
