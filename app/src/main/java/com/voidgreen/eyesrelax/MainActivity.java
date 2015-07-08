package com.voidgreen.eyesrelax;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.voidgreen.eyesrelax.fragments.PauseStopButtonsFragment;
import com.voidgreen.eyesrelax.fragments.StartButtonFragment;
import com.voidgreen.eyesrelax.service.TimeService;


public class MainActivity extends ActionBarActivity
        implements StartButtonFragment.OnStartButtonClickListener,
        PauseStopButtonsFragment.OnStopButtonClickListener {
    TimeService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.buttonsFrame) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            Intent intent = new Intent(this, TimeService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            if (mBound) {
                // Call a method from the LocalService.
                // However, if this call were something that might hang, then this request should
                // occur in a separate thread to avoid slowing down the activity performance.
                String state = mService.getState();
                switch (state) {
                    case "start":
                        setStartButtonFragment();
                        break;

                    case "pause":
                        setPauseStopButtonFragment();
                        PauseStopButtonsFragment fragment_obj = (PauseStopButtonsFragment)getSupportFragmentManager().
                                findFragmentById(R.id.pauseButton);

                        break;

                    case "resume":
                        setPauseStopButtonFragment();
                        break;

                    case "stop":
                        setPauseStopButtonFragment();
                        break;

                    default:

                        break;
                }
            }


        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, TimeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    public void setStartButtonFragment() {
        // Create a new Fragment to be placed in the activity layout
        Fragment fragment = new StartButtonFragment();
        int fragmentId = R.id.buttonsFrame;

        replaceFragment(fragment, fragmentId);
    }

    public void setPauseStopButtonFragment() {
        // Create a new Fragment to be placed in the activity layout
        Fragment fragment = new PauseStopButtonsFragment();
        int fragmentId = R.id.buttonsFrame;

        replaceFragment(fragment, fragmentId);
    }

    public void replaceFragment(Fragment fragment, int fragmentId) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(fragmentId, fragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Context context = getApplicationContext();
                Intent intent = new Intent(this, EyesRelaxSettingsActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    public void onStartButtonClick() {
        setPauseStopButtonFragment();
    }

    @Override
    public void onStopButtonClick() {
        setStartButtonFragment();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TimeService.TimeBinder binder = (TimeService.TimeBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
