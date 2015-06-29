package com.voidgreen.eyesrelax.utilities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Void on 28-Jun-15.
 */
public class AnimationProgressBarUtility {


    private static ObjectAnimator objectAnimator;

    public static void initAnimation(ProgressBar progressBar, Context context) {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 1, 100);
        objectAnimator.setDuration(SettingsDataUtility.getRelaxTime(context)); //in milliseconds
        objectAnimator.setupStartValues();
        objectAnimator.setInterpolator(new DecelerateInterpolator());
    }

    public static void start() {
        if(objectAnimator != null) {
            objectAnimator.start();
        }
    }

    public static void stop() {
        if(objectAnimator != null) {
            objectAnimator.end();
        }
    }

    public static void pause() {
        if(objectAnimator != null) {
            objectAnimator.pause();
        }
    }

    public static void setProgress() {
        if(objectAnimator != null) {
            //objectAnimator.setIntValues(100, 500);
            start();
        }
    }


}
