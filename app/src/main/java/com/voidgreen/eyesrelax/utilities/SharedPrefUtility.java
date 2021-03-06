package com.voidgreen.eyesrelax.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.preferences.NumberPickerRelaxPreference;
import com.voidgreen.eyesrelax.preferences.NumberPickerWorkPreference;

/**
 * Created by Void on 28-Jun-15.
 */
public class SharedPrefUtility {
    private final static int DEFAULT_WORK_INDEX = 2;
    private final static int DEFAULT_RELAX_INDEX = 5;

    public static int getRelaxTime(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return NumberPickerRelaxPreference.getNumValue(sharedPreferences.getInt(resources.getString(R.string.pref_key_relax_period), DEFAULT_RELAX_INDEX));
    }

    public static int getWorkTime(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return NumberPickerWorkPreference.getNumValue(sharedPreferences.getInt(resources.getString(R.string.pref_key_work_period), DEFAULT_WORK_INDEX));
    }

    public static boolean isSoundEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_sound), false);
    }

    public static boolean isStartOnBootEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_start_on_boot), false);
    }

    public static boolean isVibrationEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_vibration), false);
    }

    public static boolean is30sEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_30s_notification), false);
    }

    public static boolean isPCmodeEnabled(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(resources.getString(R.string.pref_key_pc_mode), false);
    }

}
