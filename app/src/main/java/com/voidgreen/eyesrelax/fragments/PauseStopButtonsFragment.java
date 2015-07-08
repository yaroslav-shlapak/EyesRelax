package com.voidgreen.eyesrelax.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.service.TimeService;

/**
 * Created by Void on 28-Jun-15.
 */
public class PauseStopButtonsFragment extends Fragment {
    OnStopButtonClickListener stopButtonCallBack;
    Button pauseButton;

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

        final Activity activity = getActivity();
        Button stopButton = (Button) getActivity().findViewById(R.id.stopButton);
        pauseButton = (Button) getActivity().findViewById(R.id.pauseButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnimationProgressBarUtility.stop();
                Resources resources = getResources();
                Intent intent = new Intent(activity, TimeService.class);
                intent.putExtra(resources.getString(R.string.serviceTask),
                        resources.getString(R.string.pauseTask));
                intent.addCategory(TimeService.TAG);
                activity.stopService(intent);
                Log.d("PauseStop", "stopService");

                stopButtonCallBack.onStopButtonClick();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnimationProgressBarUtility.pause();
                String pauseBattonText = pauseButton.getText().toString();
                updatePauseResumeButton(pauseBattonText);


            }
        });
    }

    public void updatePauseResumeButton(String pauseBattonText) {
        Resources resources = getResources();
        Activity activity = getActivity();
        Intent intent = new Intent(activity, TimeService.class);
        intent.addCategory(TimeService.TAG);

        switch(pauseBattonText) {
            case "Pause":

                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.pauseTask));
                activity.startService(intent);
                pauseButton.setText("Resume");
                break;
            case "Resume":

                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.resumeTask));
                activity.startService(intent);
                pauseButton.setText("Pause");
                break;
        }
    }

}
