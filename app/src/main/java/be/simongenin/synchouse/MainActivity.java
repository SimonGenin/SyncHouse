package be.simongenin.synchouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import be.simongenin.synchouse.fragments.AlarmFragment;
import be.simongenin.synchouse.fragments.DishWasherFragment;
import be.simongenin.synchouse.fragments.DryerFragment;
import be.simongenin.synchouse.fragments.MowerFragment;
import be.simongenin.synchouse.fragments.WashingMachineFragment;
import be.simongenin.synchouse.fragments.WindowsFragment;
import be.simongenin.synchouse.gcm.GCMPreferences;
import be.simongenin.synchouse.gcm.RegistrationIntentService;


/***
 * @author Simon Genin
 *
 * This class is the heart of the application.
 * It handles all the different fragments, and the global layout such
 * as the drawer.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // TAG
    private static final String TAG = MainActivity.class.getSimpleName();

    // TODO move into prefs
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // Application instance
    private SyncHouseApplication application;

    // Queue of the requests for the server
    private RequestQueue requestQueue;

    // The broadcast receiver
    private BroadcastReceiver registrationBroadcastReceiver;

    // The drawer
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Get the instance of the application (it contains the request queue)
         */
        application = (SyncHouseApplication) getApplication();

        // TODO remove (testing)
        application.isUserConnected = true;


        /**
         * If no one is connected, launhc the login activity.
         */
        if (!application.isUserConnected) {
            Intent loginIntent = new Intent(this,LoginActivity.class);
            startActivity(loginIntent);
        }

        /**
         * Set the XML layout
         */
        setContentView(R.layout.activity_main);

        /**
         * Get the request queue
         */
        requestQueue = application.requestQueue;

        /**
         * Prepare the broadcast receiver
         */
        registrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false);
            }
        };

        /**
         * Prepare the toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Prepare the drawer
         */
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**
         * Register the application with GCM.
         * It uses an intent that is handled by the RegistrationIntentService
         */
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * If the app just got resumed, let's register again the receiver
         */
        LocalBroadcastManager.getInstance(this).registerReceiver(registrationBroadcastReceiver,
                new IntentFilter(GCMPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        /**
         * If the app is in the pause state, let's unregister the receiver.
         */
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        /**
         * If the drawer is open and BACK is pressed, let's just
         * close the drawer.
         */

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disconnect) {
            application.disconnect();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        /**
         * The function will handle the switch between the different fragments.
         * It simply changes it by the demanded one when an option is selected in
         * the drawer.
         */

        Fragment fragment = null;

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_alarm:
                fragment = AlarmFragment.newInstance();
                break;

            case R.id.nav_mower:
                fragment = MowerFragment.newInstance();
                break;

            case R.id.nav_dishwasher:
                fragment = DishWasherFragment.newInstance();
                break;

            case R.id.nav_washing_machine:
                fragment = WashingMachineFragment.newInstance();
                break;

            case R.id.nav_dryer:
                fragment = DryerFragment.newInstance();
                break;

            case R.id.nav_windows:
                fragment = WindowsFragment.newInstance();
                break;

            case R.id.nav_add_module:

                /**
                 * This option is just there to look nice for the project.
                 * It is not really implemented.
                 * We just inform the user with a snackbar.
                 */

                // TODO remove this fab thing if it is no longer used

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                Snackbar.make(fab, "Cette opération n'a pas été implémentée", Snackbar.LENGTH_SHORT).show();
                break;
        }

        /**
         * if the fragment exists, let's swap it with the relevant one.
         */
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_content, fragment).commit();
        }

        /**
         * Display as title the name of the feature the fragment is about.
         */
        setTitle(item.getTitle());

        /**
         * Close the drawer after selection, so that the user can see the content instantaneously
         */
        closeDrawer();

        return true;
    }

    /**
     * Close the drawer
     */
    private void closeDrawer() {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * AUTHOR : GOOGLE
     *
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
