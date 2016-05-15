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


public class WindowsFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    private Switch shutterSwitch;
    private Switch windowsSwitch;

    private SyncHouseApplication application;
    private Windows windows;

    private Poster poster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication)getActivity().getApplication();
        windows = application.house.windows;
        windows.setOnStateChangeListener(this);

        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_windows, container, false);

        windowsSwitch = (Switch) v.findViewById(R.id.switch_activate_windows);
        shutterSwitch = (Switch) v.findViewById(R.id.switch_activate_shutters);

        windowsSwitch.setOnCheckedChangeListener(windowsSwitchListener);
        shutterSwitch.setOnCheckedChangeListener(shutterSwitchListener);

        updateLayout();




        return v;
    }


    private void updateLayout() {

        windowsSwitch.setOnCheckedChangeListener(null);
        shutterSwitch.setOnCheckedChangeListener(null);

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

    }

    public CompoundButton.OnCheckedChangeListener windowsSwitchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

            if (isChecked) {

                poster.postState(StatusCodes.SHUTTERS_OPEN, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.SHUTTERS_CLOSE, getActivity(), application, null);

            }

        }
    };


    public WindowsFragment() {
        // Required empty public constructor
    }

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
