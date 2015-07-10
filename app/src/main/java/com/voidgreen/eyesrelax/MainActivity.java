package com.voidgreen.eyesrelax;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
    PauseStopButtonsFragment pauseStopButtonsFragment;
    StartButtonFragment startButtonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        Intent intent = new Intent(this, TimeService.class);
        intent.addCategory(TimeService.TAG);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setActivityUI();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        unbindTimeService();

    }
    public void unbindTimeService() {
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
    protected void onResume() {
        super.onResume();
    }

    private void setActivityUI() {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            String state = mService.getState();
            switch (state) {
                case "start":
                    setStartButtonFragment();
                    Log.d("ActivityOnCreate", "start");
                    break;

                case "pause":
                    setPauseStopButtonFragment("Pause");
                    Log.d("ActivityOnCreate", "pause");

                    break;

                case "resume":
                    setPauseStopButtonFragment("Resume");
                    Log.d("ActivityOnCreate", "resume");
                    break;

                case "stop":
                    setPauseStopButtonFragment("Pause");
                    unbindTimeService();
                    Log.d("ActivityOnCreate", "stop");
                    break;

                default:
                    setStartButtonFragment();
                    Log.d("ActivityOnCreate", "default");
                    break;
            }
        } else {
            Log.d("ActivityOnCreate", "else");
            setStartButtonFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setStartButtonFragment() {
        // Create a new Fragment to be placed in the activity layout
        startButtonFragment = new StartButtonFragment();
        int fragmentId = R.id.buttonsFrame;

        replaceFragment(startButtonFragment, fragmentId);
    }

    public void setPauseStopButtonFragment(String state) {
        // Create a new Fragment to be placed in the activity layout
        pauseStopButtonsFragment = new PauseStopButtonsFragment();

        int fragmentId = R.id.buttonsFrame;

        replaceFragment(pauseStopButtonsFragment, fragmentId);
        pauseStopButtonsFragment.setState(state);
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
        setPauseStopButtonFragment("Pause");
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
            Log.d("mConnection", "onServiceConnected");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TimeService.TimeBinder binder = (TimeService.TimeBinder) service;
            mService = binder.getService();
            mBound = true;
            setActivityUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
