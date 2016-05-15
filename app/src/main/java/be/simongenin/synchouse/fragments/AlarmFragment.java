package be.simongenin.synchouse.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.listeners.OnStateChangeListener;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.listeners.OnPostFailListener;
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
                 * TODO check distance from the house
                 */

                /**
                 * Send a post request to stop the siren
                 */
                poster.postState(StatusCodes.ALARM_RING_STOP, getActivity(), application, null);

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
