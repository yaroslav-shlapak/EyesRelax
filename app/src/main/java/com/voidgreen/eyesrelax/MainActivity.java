package com.voidgreen.eyesrelax;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.voidgreen.eyesrelax.fragments.PauseStopButtonsFragment;
import com.voidgreen.eyesrelax.fragments.StartButtonFragment;
import com.voidgreen.eyesrelax.service.TimeService;
import com.voidgreen.eyesrelax.utilities.Constants;


public class MainActivity extends ActionBarActivity
        implements StartButtonFragment.OnStartButtonClickListener,
        PauseStopButtonsFragment.OnStopButtonClickListener {


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

        createNotification();
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

    public void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.eye_white_open_hole)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        Bitmap bm = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.eye_white_open_hole),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width),
                getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height),
                true);
        mBuilder.setLargeIcon(bm);
        mBuilder.setOngoing(true);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(Constants.NOTIFICATION_ID, mBuilder.build());
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
