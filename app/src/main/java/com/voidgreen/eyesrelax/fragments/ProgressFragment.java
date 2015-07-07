package com.voidgreen.eyesrelax.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.utilities.Constants;

/**
 * Created by Void on 28-Jun-15.
 */
public class ProgressFragment extends Fragment {
    TextView textView;

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
        textView = (TextView) activity.findViewById(R.id.textView);
        //AnimationProgressBarUtility.initAnimation(progressBar, activity.getApplicationContext());

    }

    @Override
    public void onResume() {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter(Constants.BROADCAST_NAME));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            Log.d("BroadcastReceiver", "onReceive");
            long message = intent.getLongExtra(Constants.BROADCAST_DATA, -1);

            textView.setText(Long.toString(message));
        }
    };
}
