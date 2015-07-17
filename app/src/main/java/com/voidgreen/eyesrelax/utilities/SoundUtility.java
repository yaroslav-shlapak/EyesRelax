package com.voidgreen.eyesrelax.utilities;

import android.content.Context;
import android.media.MediaPlayer;

import com.voidgreen.eyesrelax.R;

/**
 * Created by y.shlapak on Jul 17, 2015.
 */
public class SoundUtility {

    public static void playWorkEnd(Context context) {
        playTrack(context, R.raw.work_end);
    }

    public static void playRelaxEnd(Context context) {
        playTrack(context, R.raw.relax_end);
    }

    public static void playNotify(Context context) {
        playTrack(context, R.raw.notify);
    }

    private static void playTrack(Context context, int track) {
        if(SharedPrefUtility.isSoundEnabled(context)) {
            MediaPlayer mp = MediaPlayer.create(context, track);
            mp.start();
        }
    }
}
