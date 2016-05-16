package be.simongenin.synchouse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import be.simongenin.synchouse.fragments.AlarmFragment;
import be.simongenin.synchouse.fragments.DishWasherFragment;
import be.simongenin.synchouse.fragments.DryerFragment;
import be.simongenin.synchouse.fragments.MenuFragment;
import be.simongenin.synchouse.fragments.MowerFragment;
import be.simongenin.synchouse.fragments.WashingMachineFragment;
import be.simongenin.synchouse.fragments.WindowsFragment;
import be.simongenin.synchouse.gcm.RegistrationIntentService;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.models.ConnectedHouse;
import be.simongenin.synchouse.models.DomesticMachine;
import be.simongenin.synchouse.models.Mower;
import be.simongenin.synchouse.models.Windows;

import static be.simongenin.synchouse.requests.StatusCodes.ALARM_PARTIAL_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_TOTAL_START;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_CANCEL_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_START;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_CANCEL_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_START;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.MOWER_INTERRUPT;
import static be.simongenin.synchouse.requests.StatusCodes.MOWER_START;
import static be.simongenin.synchouse.requests.StatusCodes.MOWER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.SHUTTERS_CLOSE;
import static be.simongenin.synchouse.requests.StatusCodes.SHUTTERS_OPEN;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_CANCEL_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_START;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.WINDOWS_CLOSE;
import static be.simongenin.synchouse.requests.StatusCodes.WINDOWS_OPEN;


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

    // Google play services
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // Application instance
    private SyncHouseApplication application;

    // Queue of the requests for the server
    private RequestQueue requestQueue;

    // The broadcast receiver
    private BroadcastReceiver statusCodeBroadcastReceiver;

    // The drawer
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Get the instance of the application (it contains the request queue)
         */
        application = (SyncHouseApplication) getApplication();

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
         * This handles the status. It is sent from the GSMListenerService.
         * We need this cause it has to be done on the main thread.
         */
        statusCodeBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                Log.i(TAG, "Status code broadcast received : " + intent.toString());

                /**
                 * Retrieve the data
                 */
                int statusCode = intent.getIntExtra("status_code", 0);
                Bundle args = intent.getBundleExtra("args");

                /**
                 * Dispatch !
                 */
                applyStatusCode(statusCode, args);

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

        /**
         * Set the menu fragment
         */
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_content, MenuFragment.newInstance()).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * If the app just got resumed, let's register again the receiver
         */
        application.retrieveState();

        /**
         * Let's register our broadcast manager
         */
        LocalBroadcastManager.getInstance(this).registerReceiver(statusCodeBroadcastReceiver,
                new IntentFilter("status_code"));
    }

     @Override
    protected void onPause() {

        /**
         * If the app is in the pause state, let's unregister the receiver.
         */
        application.persistState();

         /**
          * Let's unregister our broadcast manager
          */
         LocalBroadcastManager.getInstance(this).unregisterReceiver(statusCodeBroadcastReceiver);
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
            application.disconnect(false);
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

            case R.id.nav_menu:
                fragment = MenuFragment.newInstance();
                break;

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

                Toast.makeText(this, "Cette opération n'a pas été implémentée", Toast.LENGTH_SHORT).show();
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

    /**
     * This method dispatch the status code.
     * Then, it processes them.
     *
     * @param statusCode the status code
     * @param data additional data are in there. Such as the grass height
     */
    private void applyStatusCode(int statusCode, Bundle data) {


        SyncHouseApplication application = (SyncHouseApplication) getApplication();
        ConnectedHouse house = application.house;

        Alarm alarm = house.alarm;
        Windows windows = house.windows;
        Mower mower = house.mower;
        DomesticMachine dryer = house.dryer;
        DomesticMachine washingMachine = house.washingMachine;
        DomesticMachine dishWasher = house.dishWasher;

        switch (statusCode) {

            /**
             * Alarms
             */

            case ALARM_TOTAL_START:
                alarm.setState(Alarm.state.TOTAL);
                break;

            case ALARM_PARTIAL_START :
                alarm.setState(Alarm.state.PARTIAL);
                break;

            case ALARM_STOP:
                alarm.setState(Alarm.state.NONE);
                break;

            case ALARM_RING_START:
                alarm.activeSiren();
                break;

            case ALARM_RING_STOP:
                alarm.turnOffAlarmSound();
                break;

            /**
             * Windows
             */

            case WINDOWS_OPEN:
                windows.setWindowState(Windows.state.OPEN);
                break;

            case WINDOWS_CLOSE:
                windows.setWindowState(Windows.state.CLOSED);
                break;

            case SHUTTERS_OPEN:
                Log.d("Coucou", "Le serveur dit de'ouvrir les volets avec GCM.");
                windows.setShutterState(Windows.state.OPEN);
                break;

            case SHUTTERS_CLOSE:
                windows.setShutterState(Windows.state.CLOSED);
                break;

            /**
             * Mower
             */

            case MOWER_START:
                // When we start the mower, we need to know the grass height
                mower.setSizeGrass(Integer.parseInt(data.getString("grass_size")));
                mower.setWorking(true);
                break;

            case MOWER_STOP:
                mower.setWorking(false);
                break;

            case MOWER_INTERRUPT:
                mower.interrupt(true);
                break;

            /**
             * Dryer
             */
            case DRYER_PROGRAM:
                dryer.setProgrammed(true);
                break;

            case DRYER_CANCEL_PROGRAM:
                dryer.setProgrammed(false);
                break;

            case DRYER_START:
                dryer.start();
                break;

            case DRYER_STOP:
                dryer.stop();
                break;

            case DRYER_WATER_PROBLEM:
                dryer.stop();
                break;

            case DRYER_ELECTRICAL_PROBLEM:
                dryer.stop();
                break;

            /**
             * Washing machine
             */

            case WASHING_MACHINE_PROGRAM:
                washingMachine.setProgrammed(true);
                break;

            case WASHING_MACHINE_CANCEL_PROGRAM:
                washingMachine.setProgrammed(false);
                break;

            case WASHING_MACHINE_START:
                washingMachine.start();
                break;

            case WASHING_MACHINE_STOP:
                washingMachine.stop();
                break;

            case WASHING_MACHINE_WATER_PROBLEM:
                washingMachine.stop();
                break;

            case WASHING_MACHINE_ELECTRICAL_PROBLEM:
                washingMachine.stop();
                break;

            /**
             * Dish washer
             */

            case DISH_WASHER_PROGRAM:
                dishWasher.setProgrammed(true);
                break;

            case DISH_WASHER_CANCEL_PROGRAM:
                dishWasher.setProgrammed(false);
                break;

            case DISH_WASHER_START:
                dishWasher.start();
                break;

            case DISH_WASHER_STOP:
                dishWasher.stop();
                break;

            case DISH_WASHER_WATER_PROBLEM:
                dishWasher.stop();
                break;

            case DISH_WASHER_ELECTRICAL_PROBLEM:
                dishWasher.stop();
                break;

        }

    }

}
