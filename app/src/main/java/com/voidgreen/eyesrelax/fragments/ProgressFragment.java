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

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {
    TextView textView;
    BroadcastReceiver receiver;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(Constants.BROADCAST_TIME_STRING_DATA);
                textView.setText(s);
                //Log.d("ProgressFragment", "receiver.onReceive");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.progress_layout, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
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
        LocalBroadcastManager.getInstance(activity).registerReceiver((receiver),
                new IntentFilter(Constants.BROADCAST_TIME_STRING_NAME)
        );
        super.onResume();
    }

    @Override
    public void onPause() {

        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
        super.onPause();
    }

}
