package com.voidgreen.eyesrelax.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;
import com.voidgreen.eyesrelax.utilities.Utility;

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {
    TextView progressTextView;
    TextView stageTextView;
    TextView timeLeftTextView;
    BroadcastReceiver timeStringReceiver;
    BroadcastReceiver stageStringReceiver;
    Activity activity;
    String progress = Constants.ZERO_PROGRESS;
    String stage = "";
    String timeLeft = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        updateProgressFromBundle(savedInstanceState);



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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.progress_layout, container, false);
        progressTextView = (TextView) view.findViewById(R.id.textViewTime);
        stageTextView = (TextView) view.findViewById(R.id.textViewStage);
        timeLeftTextView = (TextView) view.findViewById(R.id.textViewTimeLeft);
        return view;
    }

    @Override
    public void onStart() {        super.onStart();


        //AnimationProgressBarUtility.initAnimation(progressBar, activity.getApplicationContext());

    }

    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(activity).registerReceiver((timeStringReceiver),
                new IntentFilter(Constants.BROADCAST_TIME_STRING_NAME));
        Intent intent = activity.getIntent();
        updateProgressTextView(intent);
        updateStageTextView(intent);


        super.onResume();
    }

    private void updateProgressFromBundle(Bundle bundle) {
        if(bundle != null) {
            String s = bundle.getString(Constants.PROGRESS_KEY);
            if(s != null) {
                progress = s;
            }

        } else {
            progress = Utility.getSavedTimeString(activity.getApplicationContext());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.PROGRESS_KEY, progress);
        super.onSaveInstanceState(outState);
    }

    private void updateProgressTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_TIME_STRING_DATA);
        if(s != null) {
            progress = s;
        }
        progressTextView.setText(progress);
    }

    private void updateStageTextView(Intent intent) {
        String s = intent.getStringExtra(Constants.BROADCAST_STAGE_DATA);
        if(s != null) {
            stage = s;
            switch(stage) {
                case "work":
                case "relax":
                    timeLeft = Constants.TIME_LEFT;
                    timeLeftTextView.setText(Constants.TIME_LEFT);
                    break;
                default:
                    timeLeft = "";
            }
        }
        stageTextView.setText(stage);

    }
    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(activity).unregisterReceiver(timeStringReceiver);
        Utility.saveTimeString(activity.getApplicationContext(), progress);

        putStringToBundle();
        super.onPause();
    }

    private void putStringToBundle() {
        Intent mIntent = new Intent(activity, ProgressFragment.class);
        Bundle extras = mIntent.getExtras();
        updateProgressFromBundle(extras);
    }


}
