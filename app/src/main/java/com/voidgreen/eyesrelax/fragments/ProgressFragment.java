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
import android.widget.TextView;

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
    int maxValue = 100;
    String stage = "";
    String timeLeft = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        updateProgressFromBundle(savedInstanceState);

        Log.d(Constants.LOG_ID, "onCreate " + stage);

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

        mCircleView = (CircleProgressView) view.findViewById(R.id.circleView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        setProgressValue(0);
        return view;
    }

    public void setProgressValue(int currentValue) {
        mCircleView.setMaxValue(maxValue);
        Log.d(Constants.LOG_ID, "setProgressValue " + maxValue);
        Log.d(Constants.LOG_ID, "setProgressValue " + currentValue);
        mCircleView.setValue(currentValue);
        //mCircleView.setValueAnimated(currentValue, 1500);
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

    private void updateProgressFromBundle(Bundle bundle) {
        if(bundle != null) {
            String s1 = bundle.getString(Constants.PROGRESS_KEY);
            if(s1 != null) {
                progress = s1;
            }
            String s2 = bundle.getString(Constants.STAGE_KEY);
            if(s2 != null) {

                setStage(s2);
                Log.d(Constants.LOG_ID, "updateProgressFromBundle " + stage);
            }
            int v = bundle.getInt(Constants.VALUE_KEY, -1);
            if(v != -1) {
                value = v;
            }

        } else {
            Context context = activity.getApplicationContext();
            progress = Utility.getSavedTimeString(context);
            stage = Utility.getStageString(context);
            value = Utility.getTimeValue(context);
        }
        setStageImage();
    }

    private void setStage(String s) {
        Log.d(Constants.LOG_ID, "setStage " + s);
        Context context = activity.getApplicationContext();

        switch(s) {
            case Constants.WORK_STAGE:
            case "work":
                stage = Constants.WORK_STAGE;
                //imageView.setImageResource(android.R.color.transparent);
                maxValue = SharedPrefUtility.getWorkTime(context) * Constants.MIN_TO_MILLIS_MULT;
                break;
            case Constants.RELAX_STAGE:
            case "relax":
                stage = Constants.RELAX_STAGE;
                //imageView.setImageResource(android.R.color.transparent);
                maxValue = SharedPrefUtility.getRelaxTime(context) * Constants.SEC_TO_MILLIS_MULT;
                break;
            default:
                maxValue = 100;
                stage = "";
        }
    }

    private void setStageImage() {
        if(imageView != null) {
            switch (stage) {
                case Constants.WORK_STAGE:
                    imageView.setImageResource(R.drawable.eye_white_open_notification_large);
                    mCircleView.setBarColor(getResources().getColor(R.color.red));
                    mCircleView.setTextColor(getResources().getColor(R.color.white));
                    break;
                case Constants.RELAX_STAGE:
                    imageView.setImageResource(R.drawable.eye_white_closed_notification_large);
                    mCircleView.setBarColor(getResources().getColor(R.color.green));
                    mCircleView.setTextColor(getResources().getColor(R.color.white));
                    break;
                default:
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
        if(s != null) {
            progress = s;
        }
        mCircleView.setText(progress);

    }

    private void updateValueCircleView(Intent intent) {
        int v = intent.getIntExtra(Constants.BROADCAST_TIME_INT_DATA, -1);

        if(v != -1) {
            value = v;
        }
        setProgressValue(value);
        Utility.saveTimeValue(activity.getApplicationContext(), v);
    }


    private void updateStageTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_STAGE_DATA);
        if(s != null) {

            setStage(s);
            Log.d(Constants.LOG_ID, "updateStageTextView " + stage);
        }

        setStageImage();
        Utility.saveStageString(activity.getApplicationContext(), stage);

    }
    @Override
    public void onPause() {

        activity.unregisterReceiver(timeStringReceiver);
        activity.unregisterReceiver(stageStringReceiver);
        activity.unregisterReceiver(timeIntReceiver);

        Context context = activity.getApplicationContext();
        Utility.saveTimeString(context, progress);
        Utility.saveStageString(context, stage);
        Utility.saveTimeValue(context, value);

        putStringToBundle();
        super.onPause();
    }

    private void putStringToBundle() {
        Intent mIntent = new Intent(activity, ProgressFragment.class);
        Bundle extras = mIntent.getExtras();
        updateProgressFromBundle(extras);
    }


}
