package be.simongenin.synchouse.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.requests.PostRequest;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.utils.JSONUtils;
import be.simongenin.synchouse.utils.ServerUtils;

/**
 * @author Simon Genin
 *
 * This class is the controller for the alarm.
 * It keeps the state of the alarm sync with the UI and the server.
 *
 */
public class AlarmFragment extends Fragment {

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

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication) getActivity().getApplication();

        /**
         * Get the alarm
         */
        alarm = new Alarm();
        alarm.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

    }

    /**
     * Listener for the switch used to activate/deactivate the alarms.
     */
    public CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * Activate the alarm
                 */
                if (isChecked) {

                    /**
                     * Depending on the checked alarm, set the new states and send the relevant
                     * status code to the server.
                     */

                    if (checkboxPartial.isChecked()) {

                        alarm.setState(Alarm.state.PARTIAL);
                        postAlarm(StatusCodes.ALARM_PARTIAL_START);

                    }

                    if (checkboxTotal.isChecked()) {

                        alarm.setState(Alarm.state.TOTAL);
                        postAlarm(StatusCodes.ALARM_TOTAL_START);

                    }

                }

                /**
                 * Deactivate the alarm
                 */
                else {

                    alarm.setState(Alarm.state.NONE);
                    postAlarm(StatusCodes.ALARM_STOP);

                }

            }
        };

    /**
     * This method is used to send a status code to the server.
     * It then interprets the result and take action.
     *
     * In case of error, a toast will be shown to tell the user.
     * In case the server responds that the request is invalid, shows as well a toast explaining
     * the problem.
     * Finally, of the server were to responds positively, the new state of the alarm is persisted,
     * and no further actions are taken.
     * The server will then send through GCM a message to all the devices to notify them if the
     * changes. See SyncHouseGCMListenerService for more about that.
     *
     * @param code the status code to send
     */
    private void postAlarm(int code) {

        /**
         * Creates the post request parameters.
         * The server requires the status code, the home ID and the password.
         *
         * The home id is required so the server knows the modified house (obviously) and
         * the password is used to identify the client.
         * Indeed, if there was no password, anybody could have send post request to the server
         * and change the houses state as long as this person knows the right URL.         *
         */
        Map<String, String> params = new HashMap<>();
        params.put("status_code", String.valueOf(code));
        params.put("home_id", application.homeID);
        params.put("password", application.password);

        /**
         * The actual request + its listener
         */
        PostRequest alarmRequest = new PostRequest(ServerUtils.STATUS, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                /**
                 * A response from this request is as follows :
                 *
                 * {
                 *      success : true|false,
                 *      message : "",
                 *      error_message : ""
                 * }
                 *
                 * The success param represents whether or not the server accepted the changes.
                 * If yes, there is a success message in the message param. => READ BELOW
                 * If false, the error_message param contains a relevant message defining what's
                 * the problem.
                 *
                 * THE "MESSAGE" PARAM IS NO LONGER USED. IN CASE OF SUCCESS, THE SERVER WILL
                 * NOTIFY THE CHANGES THROUGH GCM.
                 *
                 * In any resulting case, we will use the setLayout method. With that, our UI is
                 * sync with the state of the alarm, whatever the server response.
                 *
                 */

                try {

                    // Let's make our response a json object
                    JSONObject jsonObject = new JSONObject(response);

                    if (JSONUtils.getSuccess(jsonObject)) {

                        /**
                         * Success !
                         * The server accepted the new state.
                         * Let's persist it.
                         */

                        alarm.saveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));
                        setLayout();

                    }

                    else {

                        /**
                         * Failure.
                         * The server was reached, but refused the new state.
                         * Print (as a toast) the error message.
                         */

                        setLayout();
                        Toast.makeText(getActivity(), JSONUtils.getError(jsonObject), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    /**
                     * Error with the JSON.
                     * It shouldn't append, unless the server changes something in the response.
                     */
                    Toast.makeText(getActivity(), "La reponse du server est corrompue.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    setLayout();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /**
                 * Error !
                 * We couldn't reach the server.
                 */

                Toast.makeText(getActivity(), "Une erreur est survenue.", Toast.LENGTH_LONG).show();
                setLayout();

            }
        });

        // Send the request
        application.requestQueue.add(alarmRequest);

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
         * Tweak the layout of those views.
         */
        setLayout();

        /**
         * Total alarm checkbox listener
         */
        checkboxTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * If it is checked, activate the alarm
                 */
                if (isChecked) {
                    alarm.setState(Alarm.state.TOTAL);
                }

                /**
                 * If it is not checked, and the total alarm ain't neither,
                 * let's set the alarm to NONE state.
                 */
                if (!isChecked && !checkboxPartial.isChecked()) {
                    alarm.setState(Alarm.state.NONE);
                }

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
                 * If it is checked, activate the alarm
                 */
                if (isChecked) {
                    alarm.setState(Alarm.state.PARTIAL);
                }

                /**
                 * If it is not checked, and the total alarm ain't neither,
                 * let's set the alarm to NONE state.
                 */
                if (!isChecked && !checkboxTotal.isChecked()) {
                    alarm.setState(Alarm.state.NONE);
                }

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

                 alarm.deactivateSiren();
                 postAlarm(StatusCodes.ALARM_RING_STOP);

            }
        });

        /**
         * Set the listener for the switch.
         * Has it's own object defined outside this function because it is a bit long.
         */
        switchActivate.setOnCheckedChangeListener(switchListener);

        return v;
    }

    /**
     * The fragment must be created with this function.
     */
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        return fragment;
    }

    /**
     * Update the UI to match the current state of the alarm.
     */
    private void setLayout() {

        // Get the right state
        alarm.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        // Remove the listener so no request are wrongly post when switch is programmatically
        // toggled
        switchActivate.setOnCheckedChangeListener(null);

        // Handles the partial alarm
        if (alarm.getCurrentState() == Alarm.state.PARTIAL) {
            if (!checkboxPartial.isChecked()) checkboxPartial.toggle();
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        // Handles the total alarm
        if (alarm.getCurrentState() == Alarm.state.TOTAL) {
            if (!checkboxTotal.isChecked()) checkboxTotal.toggle();
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        // Handle the case when there's no alarm
        if (alarm.getCurrentState() == Alarm.state.NONE) {
            if (switchActivate.isChecked()) switchActivate.toggle();
        }

        // Handles the sirene case
        if (alarm.isSirenActive()) {
            intrusionMessage.setVisibility(View.VISIBLE);
            intrusionButton.setVisibility(View.VISIBLE);
        } else {
            intrusionMessage.setVisibility(View.INVISIBLE);
            intrusionButton.setVisibility(View.INVISIBLE);
        }

        // We put back the listener on the switch when we're finished.
        switchActivate.setOnCheckedChangeListener(switchListener);

    }


}
