package com.voidgreen.eyesrelax;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.voidgreen.eyesrelax.fragments.PauseStopButtonsFragment;
import com.voidgreen.eyesrelax.fragments.StartButtonFragment;
import com.voidgreen.eyesrelax.service.TimeService;
import com.voidgreen.eyesrelax.utilities.Utility;


public class MainActivity extends Activity
        implements StartButtonFragment.OnStartButtonClickListener,
        PauseStopButtonsFragment.OnStopButtonClickListener {
    public String state = "start";
    TimeService mService;
    boolean mBound = false;
    PauseStopButtonsFragment pauseStopButtonsFragment;
    StartButtonFragment startButtonFragment;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            if (Utility.isScreenOn(getApplicationContext())) {
                Log.d("mConnection", "onServiceConnected");
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                TimeService.TimeBinder binder = (TimeService.TimeBinder) service;
                mService = binder.getService();
                mBound = true;
                setActivityUI();
            } else {
                mBound = false;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        Log.d("MainActivity", "onCreate");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
        unbindTimeService();

    }

    public void unbindTimeService() {
        if (mBound) {
            Log.d("MainActivity", "unbindTimeService");
            unbindService(mConnection);
            mBound = false;
        }
    }

    private void bindTimeService() {
        if (Utility.isScreenOn(getApplicationContext())) {
            Intent intent = new Intent(this, TimeService.class);
            intent.addCategory(TimeService.TAG);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            setActivityUI();
            Log.d("MainActivity", "bindTimeService");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unbind from the service
        Log.d("MainActivity", "onPause");
        unbindTimeService();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
        state = Utility.getState();
        bindTimeService();
    }

    private void setActivityUI() {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            String state = mService.getState();
            Log.d("setActivityUI", state);
            this.state = state;
            switch (state) {
                case "start":
                    setStartButtonFragment();
                    break;

                case "pause":
                    setPauseStopButtonFragment("pause");
                    break;

                case "resume":
                    setPauseStopButtonFragment("resume");
                    break;

                case "stop":
                    setPauseStopButtonFragment("pause");
                    this.state = "pause";
                    unbindTimeService();
                    break;

                default:
                    setStartButtonFragment();

                    break;
            }
        } else {
            Log.d("ActivityOnCreate", "else");
            if (!Utility.isTimeServiceRunning()) {
                setStartButtonFragment();
            } else {
                this.state = Utility.getState();
                switch (this.state) {
                    case "stop":
                        this.state = "pause";
                        break;
                }
            }
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
        FragmentManager fragmentManager = getFragmentManager();
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                Context context = getApplicationContext();
                intent = new Intent(this, EyesRelaxSettingsActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return true;
            case R.id.action_play:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.voidgreen.eyesrelax"));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStartButtonClick() {
        setPauseStopButtonFragment("pause");
    }

    @Override
    public void onStopButtonClick() {
        setStartButtonFragment();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
