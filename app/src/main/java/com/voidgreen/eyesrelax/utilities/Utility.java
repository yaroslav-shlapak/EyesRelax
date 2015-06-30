package com.voidgreen.eyesrelax.utilities;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by y.shlapak on Jun 30, 2015.
 */
public class Utility {
    public static void showToast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}
