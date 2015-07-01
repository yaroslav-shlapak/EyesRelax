package com.voidgreen.eyesrelax;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.voidgreen.eyesrelax.fragments.PauseStopButtonsFragment;
import com.voidgreen.eyesrelax.fragments.StartButtonFragment;
import com.voidgreen.eyesrelax.service.TimeService;


public class MainActivity extends ActionBarActivity
        implements StartButtonFragment.OnStartButtonClickListener,
                    PauseStopButtonsFragment.OnStopButtonClickListener{


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

            setStartButtonFragment();

        }

/*        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
        mStatusIntentFilter.addDataScheme("http");*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, TimeService.class);
        this.stopService(intent);
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

    public void setFragment(Fragment fragment, int fragmentId) {
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        //firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragmentId, fragment).commit();
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


}
