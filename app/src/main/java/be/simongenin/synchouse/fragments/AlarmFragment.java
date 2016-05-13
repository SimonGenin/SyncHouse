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


public class AlarmFragment extends Fragment {

    CheckBox checkboxTotal;
    CheckBox checkboxPartial;
    SwitchCompat switchActivate;

    TextView intrusionMessage;
    Button intrusionButton;

    Alarm alarm;
    SyncHouseApplication application;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication) getActivity().getApplication();

        /**
         * Recupere l'alarme
         */
        alarm = new Alarm();
        alarm.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

    }

    public CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /**
                 * Il faut activer l'alarme.
                 */
                if (isChecked) {

                    /**
                     * En fonction de l'alarme, envoyer un status code different.
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
                 * Il faut désactiver l'alarme
                 */
                else {

                    alarm.setState(Alarm.state.NONE);
                    postAlarm(StatusCodes.ALARM_STOP);

                }

            }
        };

    private void postAlarm(int code) {
        Map<String, String> params = new HashMap<>();

        params.put("status_code", String.valueOf(code));
        params.put("home_id", application.homeID);
        params.put("password", application.password);

        PostRequest alarmRequest = new PostRequest(ServerUtils.STATUS, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (JSONUtils.getSuccess(jsonObject)) {

                        // Succes

                        /**
                         * No more notification. Why ? Cause by not using gcm here, we do not
                         * notifiy ohter devices that the status of the home have changed.
                         * So now, in case of success, no message. Juste a normal gcm notification
                         * that will come.
                         */

                        // NotificationHandler.sendNotification(JSONUtils.getMessage(jsonObject), getActivity(), MainActivity.class);

                        // We save the current alarm data
                        alarm.saveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));
                        setLayout();

                    }

                    else {
                        // Fail
                        setLayout();
                        Toast.makeText(getActivity(), JSONUtils.getError(jsonObject), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    setLayout();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Une erreur est survenue.", Toast.LENGTH_LONG).show();
                setLayout();

            }
        });

        application.requestQueue.add(alarmRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);

        /**
         * Recupere les vues
         */
        checkboxTotal = (CheckBox) v.findViewById(R.id.radio_total);
        checkboxPartial = (CheckBox) v.findViewById(R.id.radio_partial);
        switchActivate = (SwitchCompat) v.findViewById(R.id.switch_activate);
        intrusionMessage = (TextView) v.findViewById(R.id.intrusion_message);
        intrusionButton = (Button) v.findViewById(R.id.intrusion_button);

        /**
         * Get the right layout
         */
        setLayout();

        checkboxTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    alarm.setState(Alarm.state.TOTAL);
                }

                if (!isChecked && !checkboxPartial.isChecked()) {
                    alarm.setState(Alarm.state.NONE);
                }

                /**
                 * Desactive l'autre radio button
                 */
                if (isChecked && checkboxPartial.isChecked()) {
                    checkboxPartial.toggle();
                }

            }
        });

        checkboxPartial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    alarm.setState(Alarm.state.PARTIAL);
                }

                if (!isChecked && !checkboxTotal.isChecked()) {
                    alarm.setState(Alarm.state.NONE);
                }

                /**
                 * Desactive l'autre radio button
                 */
                if (isChecked && checkboxTotal.isChecked()) {
                    checkboxTotal.toggle();
                }

            }
        });

        switchActivate.setOnCheckedChangeListener(switchListener);

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

        return v;
    }

    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Met en place l'interface en fonction des valeurs de l'objet
     */
    private void setLayout() {

        alarm.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        switchActivate.setOnCheckedChangeListener(null);

        if (alarm.getCurrentState() == Alarm.state.PARTIAL) {
            if (!checkboxPartial.isChecked()) checkboxPartial.toggle();
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        if (alarm.getCurrentState() == Alarm.state.TOTAL) {
            if (!checkboxTotal.isChecked()) checkboxTotal.toggle();
            if (!switchActivate.isChecked()) switchActivate.toggle();
        }

        if (alarm.getCurrentState() == Alarm.state.NONE) {
            if (switchActivate.isChecked()) switchActivate.toggle();
        }

        if (alarm.isSirenActive()) {
            intrusionMessage.setVisibility(View.VISIBLE);
            intrusionButton.setVisibility(View.VISIBLE);
        } else {
            intrusionMessage.setVisibility(View.INVISIBLE);
            intrusionButton.setVisibility(View.INVISIBLE);
        }

        switchActivate.setOnCheckedChangeListener(switchListener);

    }


}
