package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.listeners.OnStateChangeListener;
import be.simongenin.synchouse.models.Windows;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.listeners.OnPostFailListener;
import be.simongenin.synchouse.utils.Poster;

/**
 * @author Simon Genin
 *
 * This class is the controller for the windows and shutters.
 * It keeps the state of the alarm sync with the UI and the server.
 *
 */
public class WindowsFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    /**
     * UI
     */
    private Switch shutterSwitch;
    private Switch windowsSwitch;

    private SyncHouseApplication application;
    private Windows windows;

    private Poster poster;


    public WindowsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Retrieve the application
         */
        application = (SyncHouseApplication)getActivity().getApplication();

        /**
         * Retrieve the alarm
         */
        windows = application.house.windows;
        windows.setOnStateChangeListener(this);

        /**
         * Retrieve requests object
         */
        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_windows, container, false);

        /**
         * Get all the views.
         */
        windowsSwitch = (Switch) v.findViewById(R.id.switch_activate_windows);
        shutterSwitch = (Switch) v.findViewById(R.id.switch_activate_shutters);

        /**
         * Listeners
         */
        windowsSwitch.setOnCheckedChangeListener(windowsSwitchListener);
        shutterSwitch.setOnCheckedChangeListener(shutterSwitchListener);

        /**
         * Mach the UI state with the objects state
         */
        updateLayout();

        return v;
    }

    public CompoundButton.OnCheckedChangeListener windowsSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /**
             * Send a request to open or close the windows
             */
            if (isChecked) {

                poster.postState(StatusCodes.WINDOWS_OPEN, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.WINDOWS_CLOSE, getActivity(), application, null);

            }

        }
    };

    public CompoundButton.OnCheckedChangeListener shutterSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /**
             * Send a request to open or close the shutters
             */
            if (isChecked) {

                poster.postState(StatusCodes.SHUTTERS_OPEN, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.SHUTTERS_CLOSE, getActivity(), application, null);

            }

        }
    };

    /**
     * Listener for button to stop the siren
     * This button will be shown only if the state of the siren is at true.
     */
    private void updateLayout() {

        /**
         *  Remove the listener so no request are wrongly post when switch is programmatically
         *  toggled
         */
        windowsSwitch.setOnCheckedChangeListener(null);
        shutterSwitch.setOnCheckedChangeListener(null);

        /**
         * If the windows are opened
         */
        if (windows.getWindowState() == Windows.state.OPEN) {
            if (!windowsSwitch.isChecked()) windowsSwitch.toggle();
        }

        /**
         * If the windows are closed
         */
        if (windows.getWindowState() == Windows.state.CLOSED) {
            if (windowsSwitch.isChecked()) windowsSwitch.toggle();
        }

        /**
         * If the shutters are opened
         */
        if (windows.getShutterState() == Windows.state.OPEN) {
            if (!shutterSwitch.isChecked()) shutterSwitch.toggle();
        }

        /**
         * If the shutters are closed
         */
        if (windows.getShutterState() == Windows.state.CLOSED) {
            if (shutterSwitch.isChecked()) shutterSwitch.toggle();
        }

        /**
         * We put back the listener on the switch when we're finished.
         */
        windowsSwitch.setOnCheckedChangeListener(windowsSwitchListener);
        shutterSwitch.setOnCheckedChangeListener(shutterSwitchListener);

    }

    /**
     * The fragment must be created with this function.
     */
    public static WindowsFragment newInstance() {
        WindowsFragment fragment = new WindowsFragment();
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
