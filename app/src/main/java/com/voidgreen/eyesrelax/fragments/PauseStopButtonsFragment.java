package com.voidgreen.eyesrelax.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voidgreen.eyesrelax.utilities.AnimationProgressBarUtility;
import com.voidgreen.eyesrelax.R;

/**
 * Created by Void on 28-Jun-15.
 */
public class PauseStopButtonsFragment extends Fragment {
    OnStopButtonClickListener stopButtonCallBack;

    public interface OnStopButtonClickListener {
        public void onStopButtonClick();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            stopButtonCallBack = (OnStopButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.pause_stop_button_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button stopButton = (Button) getActivity().findViewById(R.id.stopButton);
        Button pauseButton = (Button) getActivity().findViewById(R.id.pauseButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationProgressBarUtility.stop();
                stopButtonCallBack.onStopButtonClick();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationProgressBarUtility.pause();
            }
        });
    }

}
