package com.voidgreen.eyesrelax;

import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * Created by Void on 28-Jun-15.
 */
public class AnimationProgressBarUtility {


    private static ObjectAnimator objectAnimator;

    public static void initAnimation(ProgressBar progressBar) {
        objectAnimator = ObjectAnimator.ofInt(progressBar, "progress", 1, 500);
        objectAnimator.setDuration(120000); //in milliseconds
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


}
