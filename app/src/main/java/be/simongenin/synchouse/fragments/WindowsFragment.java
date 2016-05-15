package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Windows;
import be.simongenin.synchouse.requests.PostRequest;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.utils.JSONUtils;
import be.simongenin.synchouse.utils.ServerUtils;


public class WindowsFragment extends Fragment {

    private Switch shutterSwitch;
    private Switch windowsSwitch;

    private SyncHouseApplication application;
    private Windows windows;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication)getActivity().getApplication();
        windows = new Windows();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_windows, container, false);

        windowsSwitch = (Switch) v.findViewById(R.id.switch_activate_windows);
        shutterSwitch = (Switch) v.findViewById(R.id.switch_activate_shutters);

        setLayout();

        windowsSwitch.setOnCheckedChangeListener(windowsSwitchListener);
        shutterSwitch.setOnCheckedChangeListener(shutterSwitchListener);

        return v;
    }


    private void postWindows(final int code) {

        Map<String, String> params = new HashMap<>();
        params.put("status_code", String.valueOf(code));
        params.put("home_id", application.homeID);
        params.put("password", application.password);

        PostRequest windowsRequest = new PostRequest(ServerUtils.STATUS, params, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    // Let's make our response a json object
                    JSONObject jsonObject = new JSONObject(response);

                    if (JSONUtils.getSuccess(jsonObject)) {
                        windows.saveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));
                        setLayout();
                    }

                    else {
                        Toast.makeText(getActivity(), JSONUtils.getError(jsonObject), Toast.LENGTH_LONG).show();
                        setLayout();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "La reponse du server est corrompue.", Toast.LENGTH_LONG).show();
                    setLayout();
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "Une erreur est survenue.", Toast.LENGTH_LONG).show();
                setLayout();

            }
        });

        // Send the request
        application.requestQueue.add(windowsRequest);

    }

    private void setLayout() {

        windowsSwitch.setOnCheckedChangeListener(null);
        shutterSwitch.setOnCheckedChangeListener(null);

        windows.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        if (windows.getWindowState() == Windows.state.OPEN) {
            if (!windowsSwitch.isChecked()) windowsSwitch.toggle();
        }

        if (windows.getWindowState() == Windows.state.CLOSED) {
            if (windowsSwitch.isChecked()) windowsSwitch.toggle();
        }

        if (windows.getShutterState() == Windows.state.OPEN) {
            if (!shutterSwitch.isChecked()) shutterSwitch.toggle();
        }

        if (windows.getShutterState() == Windows.state.CLOSED) {
            if (shutterSwitch.isChecked()) shutterSwitch.toggle();
        }

        windowsSwitch.setOnCheckedChangeListener(windowsSwitchListener);
        shutterSwitch.setOnCheckedChangeListener(shutterSwitchListener);

        windows.saveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

    }

    public CompoundButton.OnCheckedChangeListener windowsSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                // windows.setWindowState(Windows.state.OPEN);
                postWindows(StatusCodes.WINDOWS_OPEN);

            } else {

                // windows.setWindowState(Windows.state.CLOSED);
                postWindows(StatusCodes.WINDOWS_CLOSE);

            }

        }
    };

    public CompoundButton.OnCheckedChangeListener shutterSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                // windows.setShutterState(Windows.state.OPEN);
                postWindows(StatusCodes.SHUTTERS_OPEN);

            } else {

                // windows.setShutterState(Windows.state.CLOSED);
                postWindows(StatusCodes.SHUTTERS_CLOSE);

            }

        }
    };


    public WindowsFragment() {
        // Required empty public constructor
    }

    public static WindowsFragment newInstance() {
        WindowsFragment fragment = new WindowsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

}
