package com.voidgreen.eyesrelax.utilities;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.widget.Toast;

import com.voidgreen.eyesrelax.R;

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

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Service.POWER_SERVICE);
        return powerManager.isScreenOn();
    }
}
