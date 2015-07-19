package com.voidgreen.eyesrelax.utilities;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by y.shlapak on Jul 14, 2015.
 */
public class VibratorUtility {
    public static void vibrateLong(Context context) {
        if(SharedPrefUtility.isVibrationEnabled(context)) {
            // Get instance of Vibrator from current Context
            long vt = 300;
            long dt = 800;
            long delay = 0;
            // Each element then alternates between vibrate, sleep, vibrate, sleep...
            long[] pattern = {delay, vt, dt, vt, dt * 4 / 5, vt, dt * 2 / 3, vt, dt / 3, vt, dt / 3, vt, dt / 3};

            vibrate(pattern, context);
        }

    }

    public static void vibrateShort(Context context) {
        if(SharedPrefUtility.isVibrationEnabled(context)) {
            // Get instance of Vibrator from current Context
            long vt = 300;
            long dt = 300;
            long delay = 0;
            // Each element then alternates between vibrate, sleep, vibrate, sleep...
            long[] pattern = {delay, vt, dt, vt, dt};

            vibrate(pattern, context);
        }
    }

    public static void vibrate(long[] pattern, Context context) {
        //Log.d("vibrate", pattern.toString());
        // Get instance of Vibrator from current Context

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            // Output yes if can vibrate, no otherwise
            if (v.hasVibrator()) {
                // The '-1' here means to vibrate once, as '-1' is out of bounds in the pattern array
                v.vibrate(pattern, -1);
            } else {

            }
    }
}
