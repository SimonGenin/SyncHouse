package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Mower;
import be.simongenin.synchouse.listeners.OnStateChangeListener;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.listeners.OnPostFailListener;
import be.simongenin.synchouse.utils.Poster;

/**
 * @author Simon Genin
 *
 * This class is the controller for the dishwasher.
 * It keeps the state of the alarm sync with the UI and the server.
 *
 */
public class MowerFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    /**
     * UI
     */
    private EditText grassSizeEditText;
    private Switch switchMower;
    private TextView interruptText;

    private Mower mower;
    private SyncHouseApplication application;

    private Poster poster;

    public MowerFragment() {
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
         * Retrieve the mower
         */
        mower = application.house.mower;
        mower.setOnStateChangeListener(this);

        /**
         * Retrieve requests object
         */
        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mower, container, false);

        /**
         * Get all the views.
         */
        grassSizeEditText = (EditText) v.findViewById(R.id.grass_size);
        switchMower = (Switch) v.findViewById(R.id.switch_activate);
        interruptText = (TextView) v.findViewById(R.id.interrupt_text);

        /**
         * The listener
         */
        switchMower.setOnCheckedChangeListener(mowerSwitchListener);

        /**
         * Mach the UI state with the objects state
         */
        updateLayout();

        return v;
    }

    private CompoundButton.OnCheckedChangeListener mowerSwitchListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            /**
             * Send the post requests to program or cancel the programming of the mower
             */
            if (isChecked) {

                /**
                 * Get the grass side.
                 * Make sure the value is valid.
                 * If it is not, show a message for the user.
                 * And updateLayout()
                 */
                int grassSize = 0;

                /**
                 * Make sure it is an number
                 */
                try {

                   grassSize = Integer.parseInt(grassSizeEditText.getText().toString().trim());

                } catch (Exception e) {

                    Toast.makeText(getActivity(), "Votre taille de tonte ne semble pas valide.", Toast.LENGTH_LONG).show();
                    updateLayout();
                    return;

                }

                /**
                 * Bigger than 0
                 */
                if (grassSize < 0 ) {

                    Toast.makeText(getActivity(), "Votre taille de tonte ne semble pas valide.", Toast.LENGTH_LONG).show();
                    updateLayout();
                    return;

                }

                poster.postState(StatusCodes.MOWER_START, getActivity(), application, getGrassArgs(grassSize));


            }

            else {

                poster.postState(StatusCodes.MOWER_STOP, getActivity(), application, null);

            }

        }
    };

    /**
     * Get a map from the grass height, to insert into the post request
     */
    private Map<String, String> getGrassArgs(int grassHeight) {

        Map<String, String> args = new HashMap<>();
        args.put("grass_size", String.valueOf(grassHeight));
        return args;

    }

    /**
     * Update the UI to match the current state of the alarm.
     */
    private void updateLayout() {

        /**
         *  Remove the listener so no request are wrongly post when switch is programmatically
         *  toggled
         */
        switchMower.setOnCheckedChangeListener(null);

        /**
         * Set the grass height in the edit text.
         */
        grassSizeEditText.setText(String.valueOf(mower.getSizeGrass()));

        /**
         * If the mower is active
         */
        if (mower.isWorking()) {

            if (!switchMower.isChecked()) switchMower.toggle();

        }

        /**
         * If the mower ain't active
         */
        if (!mower.isWorking()) {

            if (switchMower.isChecked()) switchMower.toggle();

        }

        /**
         * If the mower is in the interrupted state or not.
         */
        if (mower.isInterrupted()) {
            interruptText.setVisibility(View.VISIBLE);
        } else {
            interruptText.setVisibility(View.INVISIBLE);
        }

        /**
         * We put back the listener on the switch when we're finished.
         */
        switchMower.setOnCheckedChangeListener(mowerSwitchListener);

    }

    /**
     * The fragment must be created with this function.
     */
    public static MowerFragment newInstance() {
        MowerFragment fragment = new MowerFragment();
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
