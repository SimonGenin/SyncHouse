package be.simongenin.synchouse.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.listeners.OnPostFailListener;
import be.simongenin.synchouse.listeners.OnStateChangeListener;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.utils.Poster;

/**
 * @author Simon Genin
 *
 * This class is the controller for the alarm.
 * It keeps the state of the alarm sync with the UI and the server.
 *
 */
public class AlarmFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    /**
     * UI
     */
    private CheckBox checkboxTotal;
    private CheckBox checkboxPartial;
    private SwitchCompat switchActivate;
    private TextView intrusionMessage;
    private Button intrusionButton;

    private Alarm alarm;
    private SyncHouseApplication application;

    private Poster poster;

    private LocationManager lm;

    private Location location;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Retrieve the application
         */
        application = (SyncHouseApplication) getActivity().getApplication();

        /**
         * Setup the location manager
         */
        locationManagerSetUp();

        /**
         * Retrieve the alarm
         */
        alarm = application.house.alarm;
        alarm.setOnStateChangeListener(this);

        /**
         * Retrieve requests object
         */
        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    /**
     * Set up the location manager.
     * Asks for permission
     */
    private void locationManagerSetUp() {

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        /**
         * Check we are concerned by the right request code
         */
        if (requestCode == 10) {

            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                }

                /**
                 * Set up the location listener
                 */
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }

        }

    }

    /**
     * The location listener, to update our position
     */
    private final LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            AlarmFragment.this.location = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);

        /**
         * Get all the views.
         */
        checkboxTotal = (CheckBox) v.findViewById(R.id.radio_total);
        checkboxPartial = (CheckBox) v.findViewById(R.id.radio_partial);
        switchActivate = (SwitchCompat) v.findViewById(R.id.switch_activate);
        intrusionMessage = (TextView) v.findViewById(R.id.intrusion_message);
        intrusionButton = (Button) v.findViewById(R.id.intrusion_button);

        /**
         * Mach the UI state with the objects state
         */
        updateLayout();

        /**
         * Total alarm checkbox listener
         */
        checkboxTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * Make sure that only one checkbox is checked
                 */
                if (isChecked && checkboxPartial.isChecked()) {
                    checkboxPartial.toggle();
                }

            }
        });

        /**
         * Partial alarm checkbox listener
         */
        checkboxPartial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * Make sure that only one checkbox is checked
                 */
                if (isChecked && checkboxTotal.isChecked()) {
                    checkboxTotal.toggle();
                }

            }
        });

        /**
         * Listener for button to stop the siren
         * This button will be shown only if the state of the siren is at true.
         */
        intrusionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * We need to be at a certain distance from the house to deactivate it.
                 * So, we have to check for our position and hand it to the server.
                 */

                /**
                 * Let's first check the user has the right permissions.
                 * If not, we stop him.
                 */
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getActivity().requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
                    }

                    Toast.makeText(getActivity(), "Permission de localisation manquante", Toast.LENGTH_SHORT).show();
                    return;
                }

                /**
                 * Create the args
                 */

                if (location == null) {

                    Toast.makeText(getActivity(), "Désolé, une erreur est survenue en cherchant votre position", Toast.LENGTH_SHORT).show();
                    return;

                }

                Map<String, String> positionArgs = new HashMap<>();
                positionArgs.put("longitude", String.valueOf(location.getLongitude()));
                positionArgs.put("latitude", String.valueOf(location.getLatitude()));

                Log.d("Coucou", "Longitude => " + location.getLongitude());
                Log.d("Coucou", "Latitude => " + location.getLatitude());

                poster.postState(StatusCodes.ALARM_RING_STOP, getActivity(), application, positionArgs);

            }
        });

        /**
         * Set the listener for the switch.
         */
        switchActivate.setOnCheckedChangeListener(switchListener);

        return v;
    }


    /**
     * Listener for the switch used to activate/deactivate the alarms.
     */
    public CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * If it the switch is checked, we look at the checkboxes and send the post
                 * requests based on these.
                 */
                if (isChecked) {

                    if (checkboxPartial.isChecked()) {
                        poster.postState(StatusCodes.ALARM_PARTIAL_START, getActivity(), application, null);
                    }

                    if (checkboxTotal.isChecked()) {
                        poster.postState(StatusCodes.ALARM_TOTAL_START, getActivity(), application, null);
                    }

                }

                /**
                 * If it is not checked, let's just send a post request to deactivate the alarm.
                 */
                else {
                    poster.postState(StatusCodes.ALARM_STOP, getActivity(), application, null);
                }
            }
    };

    /**
     * Update the UI to match the current state of the alarm.
     */
    private void updateLayout() {

        /**
         *  Remove the listener so no request are wrongly post when switch is programmatically
         *  toggled
         */
        switchActivate.setOnCheckedChangeListener(null);

        /**
         * If partial alarm
         */
        if (alarm.getCurrentState() == Alarm.state.PARTIAL) {
            if (!checkboxPartial.isChecked()) checkboxPartial.toggle();
            if (checkboxTotal.isChecked()) checkboxTotal.toggle();
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        /**
         * If total alarm
         */
        if (alarm.getCurrentState() == Alarm.state.TOTAL) {
            if (!checkboxTotal.isChecked()) checkboxTotal.toggle();
            if (checkboxPartial.isChecked()) checkboxPartial.toggle();;
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        /**
         * If no alarm
         */
        if (alarm.getCurrentState() == Alarm.state.NONE) {
            if (checkboxTotal.isChecked()) checkboxTotal.toggle();
            if (checkboxPartial.isChecked()) checkboxPartial.toggle();;
            if (switchActivate.isChecked()) switchActivate.toggle();
        }

        /**
         * Intrusion components
         */
        if (alarm.isSirenActive()) {
            intrusionMessage.setVisibility(View.VISIBLE);
            intrusionButton.setVisibility(View.VISIBLE);
        } else {
            intrusionMessage.setVisibility(View.INVISIBLE);
            intrusionButton.setVisibility(View.INVISIBLE);
        }

        /**
         * Doesn't let the user change the alarm type
         * while it is active.
         */
        if (switchActivate.isChecked()) {
            checkboxTotal.setEnabled(false);
            checkboxPartial.setEnabled(false);
        } else {
            checkboxTotal.setEnabled(true);
            checkboxPartial.setEnabled(true);
        }

        // We put back the listener on the switch when we're finished.
        switchActivate.setOnCheckedChangeListener(switchListener);

    }

    /**
     * The fragment must be created with this function.
     */
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        return fragment;
    }


    @Override
    public void onStateChange() {

        updateLayout();

    }

    @Override
    public void onPostFail() {

        updateLayout();

    }
}
