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


public class WashingMachineFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    private Switch switchRunning;
    private Switch switchProgram;

    private SyncHouseApplication application;
    private DomesticMachine washingMachine;

    Poster poster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication) getActivity().getApplication();

        washingMachine = application.house.washingMachine;
        washingMachine.setOnStateChangeListener(this);

        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_washing_machine, container, false);

        switchProgram = (Switch) v.findViewById(R.id.switch_program);
        switchRunning = (Switch) v.findViewById(R.id.switch_is_running);

        switchProgram.setOnCheckedChangeListener(switchProgramListener);
        switchRunning.setOnCheckedChangeListener(switchRunningListener);

        updateLayout();

        return v;
    }

    private void updateLayout() {

        switchProgram.setOnCheckedChangeListener(null);
        switchRunning.setOnCheckedChangeListener(null);

        if (washingMachine.isWorking()) {

            if (!switchRunning.isChecked()) switchRunning.toggle();
            switchProgram.setEnabled(false);
            switchRunning.setEnabled(true);

        }

        else {

            if (switchRunning.isChecked()) switchRunning.toggle();
            switchProgram.setEnabled(true);
            switchRunning.setEnabled(false);

        }

        if (washingMachine.isProgrammed()) {

            if (!switchProgram.isChecked()) switchProgram.toggle();

        } else {

            if (switchProgram.isChecked()) switchProgram.toggle();

        }


        switchProgram.setOnCheckedChangeListener(switchProgramListener);
        switchRunning.setOnCheckedChangeListener(switchRunningListener);

    }

    private CompoundButton.OnCheckedChangeListener switchProgramListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                poster.postState(StatusCodes.WASHING_MACHINE_PROGRAM, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.WASHING_MACHINE_CANCEL_PROGRAM, getActivity(), application, null);

            }

        }
    };

    private CompoundButton.OnCheckedChangeListener switchRunningListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                /**
                 * We can't start the washing machine manually
                 */
                // poster.postState(StatusCodes.WASHING_MACHINE_START, getActivity(), application, null);

            } else {

                poster.postState(StatusCodes.WASHING_MACHINE_STOP, getActivity(), application, null);

            }

        }
    };

    public WashingMachineFragment() {
        // Required empty public constructor
    }

    public static WashingMachineFragment newInstance() {
        WashingMachineFragment fragment = new WashingMachineFragment();
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
