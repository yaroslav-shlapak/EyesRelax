package com.voidgreen.eyesrelax.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voidgreen.eyesrelax.MainActivity;
import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.service.TimeService;

/**
 * Created by Void on 28-Jun-15.
 */
public class PauseStopButtonsFragment extends Fragment {
    OnStopButtonClickListener stopButtonCallBack;
    Button pauseButton;
    String state = "Pause";

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

        final MainActivity activity = (MainActivity) getActivity();
        Button stopButton = (Button) getActivity().findViewById(R.id.stopButton);
/*        float density = getResources().getDisplayMetrics().density;
        stopButton.setTextSize(Constants.TEXT_SIZE * density);*/

        pauseButton = (Button) getActivity().findViewById(R.id.pauseButton);
        pauseButton.setText(state);
/*        pauseButton.setTextSize(Constants.TEXT_SIZE * density);*/

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resources resources = getResources();
                Intent intent = new Intent(activity, TimeService.class);
                intent.putExtra(resources.getString(R.string.serviceTask),
                        resources.getString(R.string.pauseTask));
                intent.addCategory(TimeService.TAG);
                activity.stopService(intent);
                activity.unbindTimeService();
                Log.d("PauseStop", "stopService");

                stopButtonCallBack.onStopButtonClick();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pauseBattonText = pauseButton.getText().toString();
                updatePauseResumeButton(pauseBattonText);


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        state = activity.state;

        switch(state) {
            case "pause":
                pauseButton.setText("Pause");
                break;
            case "resume":
                pauseButton.setText("Resume");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setState(String state) {
        this.state = state;
    }

    private void updatePauseResumeButton(String pauseBattonText) {
        Resources resources = getResources();
        Activity activity = getActivity();
        Intent intent = new Intent(activity, TimeService.class);
        intent.addCategory(TimeService.TAG);

        switch(pauseBattonText) {
            case "Pause":
                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.pauseTask));
                activity.startService(intent);
                pauseButton.setText("Resume");
                pauseButton.setBackgroundResource(R.drawable.button_resume);
                break;
            case "Resume":
                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.resumeTask));
                activity.startService(intent);
                pauseButton.setText("Pause");
                pauseButton.setBackgroundResource(R.drawable.button_pause);
                break;
        }
    }


    private boolean isTimeServiceRunning() {
        return TimeService.serviceRunning;
    }
}
