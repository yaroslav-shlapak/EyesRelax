package com.voidgreen.eyesrelax.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.voidgreen.eyesrelax.utilities.AnimationProgressBarUtility;
import com.voidgreen.eyesrelax.R;

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.progress_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();

        ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        AnimationProgressBarUtility.initAnimation(progressBar, activity.getApplicationContext());

    }
}
