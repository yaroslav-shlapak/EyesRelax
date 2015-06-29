package com.voidgreen.eyesrelax.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.voidgreen.eyesrelax.R;
import com.voidgreen.eyesrelax.service.TimeService;

/**
 * Created by Void on 28-Jun-15.
 */
public class StartButtonFragment extends Fragment {
    OnStartButtonClickListener startButtonCallBack;

    public interface OnStartButtonClickListener {
        public void onStartButtonClick();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            startButtonCallBack = (OnStartButtonClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.start_button_layout, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Button startButton = (Button) getActivity().findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AnimationProgressBarUtility.start();

                Resources resources = getResources();
                Intent intent = new Intent(getActivity(), TimeService.class);
                intent.putExtra(resources.getString(R.string.serviceTask), resources.getString(R.string.startTask));
                getActivity().startService(intent);

                startButtonCallBack.onStartButtonClick();
            }
        });
    }
}
