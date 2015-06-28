package com.voidgreen.eyesrelax;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;

/**
 * Created by Void on 28-Jun-15.
 */
public class SettingsDartaUtility {
    public static String getRelaxTime(Context context) {
        Resources resources = context.getResources();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        this.textSize = sharedPreferences.getInt(resources.getString(R.string.pref_text_size_key), this.textSize);
        this.textColor = sharedPreferences.getInt(resources.getString(R.string.pref_text_color_key), this.textColor);
    }

}
