package com.voidgreen.eyesrelax.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.animation.CircleProgressView;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.SharedPrefUtility;
import com.voidgreen.eyesrelax.utilities.Utility;

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {
    CircleProgressView mCircleView;
    ImageView imageView;
    BroadcastReceiver timeStringReceiver;
    BroadcastReceiver stageStringReceiver;
    BroadcastReceiver timeIntReceiver;
    Activity activity;
    String progress = Constants.ZERO_PROGRESS;
    int value = 0;
    int maxValue = 0;
    String stage = "";
    String timeLeft = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        updateValues();


        timeStringReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateProgressTextView(intent);

            }
        };
        stageStringReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateStageTextView(intent);
            }
        };

        timeIntReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateValueCircleView(intent);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.progress_layout, container, false);
        updateValues();
        mCircleView = (CircleProgressView) view.findViewById(R.id.circleView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        setProgressValue(value);
        return view;
    }

    public void setProgressValue(int currentValue) {
        mCircleView.setMaxValue(maxValue);
        Log.d(Constants.LOG_ID, "setProgressValue maxValue = " + maxValue);
        Log.d(Constants.LOG_ID, "setProgressValue currentValue = " + currentValue);
        //mCircleView.setValue(currentValue);
        mCircleView.setValueAnimated(currentValue, 300);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        updateValues();

        activity.registerReceiver((timeStringReceiver),
                new IntentFilter(Constants.BROADCAST_TIME_STRING_NAME));
        activity.registerReceiver((stageStringReceiver),
                new IntentFilter(Constants.BROADCAST_STAGE_NAME));
        activity.registerReceiver((timeIntReceiver),
                new IntentFilter(Constants.BROADCAST_TIME_INT_NAME));
        Intent intent = activity.getIntent();
        updateProgressTextView(intent);
        updateStageTextView(intent);
        updateValueCircleView(intent);
        setStageImage();


        super.onResume();
    }

    private void updateValues() {
        progress = Utility.getNotificationString();
        stage = Utility.getStage();
        value = Utility.getStageTime();
        setStageImage();
        setStage(stage);
    }

    private void setStage(String s) {
        Log.d(Constants.LOG_ID, "setStage " + s);
        Context context = activity.getApplicationContext();

        stage = s;
        switch (s) {
            case "work":
                //imageView.setImageResource(android.R.color.transparent);
                maxValue = SharedPrefUtility.getWorkTime(context) * Constants.MIN_TO_MILLIS_MULT;
                break;
            case "relax":
                //imageView.setImageResource(android.R.color.transparent);
                maxValue = SharedPrefUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT;
                break;
            default:

        }
    }

    private void setStageImage() {
        if (imageView != null) {
            Context context = activity.getApplicationContext();
            switch (stage) {
                case "work":
                    imageView.setImageResource(R.drawable.eye_white_open_notification_large);
                    mCircleView.setBarColor(getResources().getColor(R.color.red));
                    mCircleView.setTextColor(getResources().getColor(R.color.red));
                    maxValue = SharedPrefUtility.getWorkTime(context) * Constants.MIN_TO_MILLIS_MULT;
                    break;
                case "relax":
                    imageView.setImageResource(R.drawable.eye_white_closed_notification_large);
                    mCircleView.setBarColor(getResources().getColor(R.color.green));
                    mCircleView.setTextColor(getResources().getColor(R.color.green));
                    maxValue = SharedPrefUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT;
                    break;
                default:
                    mCircleView.setTextColor(getResources().getColor(R.color.white));
                    maxValue = 0;
                    imageView.setImageDrawable(null);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PROGRESS_KEY, progress);
        outState.putString(Constants.STAGE_KEY, stage);
        outState.putInt(Constants.VALUE_KEY, value);
        super.onSaveInstanceState(outState);
    }

    private void updateProgressTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_TIME_STRING_DATA);
        if (s != null) {
            progress = s;
        }
        mCircleView.setText(progress);

    }

    private void updateValueCircleView(Intent intent) {
        int v = intent.getIntExtra(Constants.BROADCAST_TIME_INT_DATA, -1);

        if (v != -1) {
            value = v;
        }

        Log.d(Constants.LOG_ID, "updateValueCircleView value = " + value);
        setProgressValue(value);
    }


    private void updateStageTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_STAGE_DATA);
        if (s != null) {

            setStage(s);
            Log.d(Constants.LOG_ID, "updateStageTextView " + stage);
        }

        setStageImage();
    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(timeStringReceiver);
        activity.unregisterReceiver(stageStringReceiver);
        activity.unregisterReceiver(timeIntReceiver);

        super.onPause();
    }
}
