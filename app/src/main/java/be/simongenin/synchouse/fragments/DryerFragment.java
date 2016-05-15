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
import be.simongenin.synchouse.models.DomesticMachine;
import be.simongenin.synchouse.listeners.OnStateChangeListener;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.listeners.OnPostFailListener;
import be.simongenin.synchouse.utils.Poster;

/**
 * @author Simon Genin
 *
 * This class is the controller for the dryer.
 * It keeps the state of the alarm sync with the UI and the server.
 *
 */
public class DryerFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    /**
     * UI
     */
    private Switch switchRunning;
    private Switch switchProgram;

    private SyncHouseApplication application;
    private DomesticMachine dryer;

    Poster poster;

    public DryerFragment() {
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
         * Retrieve the dryer
         */
        dryer = application.house.dryer;
        dryer.setOnStateChangeListener(this);

        /**
         * Retrieve requests object
         */
        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dryer, container, false);

        /**
         * Get all the views.
         */
        switchProgram = (Switch) v.findViewById(R.id.switch_program);
        switchRunning = (Switch) v.findViewById(R.id.switch_is_running);

        /**
         * Listeners
         */
        switchProgram.setOnCheckedChangeListener(switchProgramListener);
        switchRunning.setOnCheckedChangeListener(switchRunningListener);

        /**
         * Mach the UI state with the objects state
         */
        updateLayout();

        return v;
    }


    private CompoundButton.OnCheckedChangeListener switchProgramListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /**
             * Send the post requests to program or cancel the programming of the device
             */
            if (isChecked) {

                poster.postState(StatusCodes.DRYER_PROGRAM, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.DRYER_CANCEL_PROGRAM, getActivity(), application, null);

            }

        }
    };

    private CompoundButton.OnCheckedChangeListener switchRunningListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /**
             * Send the post requests to program or cancel the programming of the device
             */
            if (isChecked) {

                /**
                 * We can't start the dryer machine manually
                 */
                // poster.postState(StatusCodes.DRYER_START, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.DRYER_STOP, getActivity(), application, null);

            }

        }
    };

    /**
     * Update the UI to match the current state of the dryer.
     */
    private void updateLayout() {

        /**
         *  Remove the listeners so no request are wrongly post when switch is programmatically
         *  toggled
         */
        switchProgram.setOnCheckedChangeListener(null);
        switchRunning.setOnCheckedChangeListener(null);

        /**
         * If the dryer is working or not
         */
        if (dryer.isWorking()) {

            if (!switchRunning.isChecked()) switchRunning.toggle();
            switchProgram.setEnabled(false);
            switchRunning.setEnabled(true);

        }

        else {

            if (switchRunning.isChecked()) switchRunning.toggle();
            switchProgram.setEnabled(true);
            switchRunning.setEnabled(false);

        }

        /**
         * If the dryer is programmed or not
         */
        if (dryer.isProgrammed()) {

            if (!switchProgram.isChecked()) switchProgram.toggle();

        } else {

            if (switchProgram.isChecked()) switchProgram.toggle();

        }

        /**
         * We put back the listeners on the switch when we're finished.
         */
        switchProgram.setOnCheckedChangeListener(switchProgramListener);
        switchRunning.setOnCheckedChangeListener(switchRunningListener);

    }

    /**
     * The fragment must be created with this function.
     */
    public static DryerFragment newInstance() {
        DryerFragment fragment = new DryerFragment();
        return fragment;
    }

    @Override
    public void onPostFail() {

        updateLayout();

    }

    @Override
    public void onStateChange() {

        updateLayout();

    }

}
