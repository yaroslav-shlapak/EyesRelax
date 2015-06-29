package com.voidgreen.eyesrelax.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.voidgreen.eyesrelax.R;

/**
 * Created by Void on 28-Jun-15.
 */
public class SettingsDataUtility {
    private final static int DEFAULT = 30;

    public static int getRelaxTime(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getInt(resources.getString(R.string.pref_key_relax_period), DEFAULT);
    }

    public static int getWorkTime(Context context) {
        Resources resources = context.getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getInt(resources.getString(R.string.pref_key_start_on_boot), DEFAULT);
    }

}
