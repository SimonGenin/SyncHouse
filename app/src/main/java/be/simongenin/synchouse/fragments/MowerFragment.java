package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Mower;
import be.simongenin.synchouse.models.OnStateChangeListener;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.utils.OnPostFailListener;
import be.simongenin.synchouse.utils.Poster;


public class MowerFragment extends Fragment implements OnStateChangeListener, OnPostFailListener {

    private EditText grassSizeEditText;
    private Switch switchMower;

    private Mower mower;
    private SyncHouseApplication application;

    private Poster poster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication) getActivity().getApplication();
        mower = application.house.mower;
        mower.setOnStateChangeListener(this);

        poster = new Poster();
        poster.setOnPostFailListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mower, container, false);

        grassSizeEditText = (EditText) v.findViewById(R.id.grass_size);
        switchMower = (Switch) v.findViewById(R.id.switch_activate);

        switchMower.setOnCheckedChangeListener(mowerSwitchListener);

        updateLayout();

        return v;
    }

    private CompoundButton.OnCheckedChangeListener mowerSwitchListener =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {

                /**
                 * Grass size
                 */
                int grassSize = 0;

                try {

                   grassSize = Integer.parseInt(grassSizeEditText.getText().toString().trim());

                } catch (Exception e) {

                    Toast.makeText(getActivity(), "Votre taille de tonte ne semble pas valide.", Toast.LENGTH_LONG).show();
                    return;

                }

                if (grassSize < 0 ) {

                    Toast.makeText(getActivity(), "Votre taille de tonte ne semble pas valide.", Toast.LENGTH_LONG).show();
                    return;

                }

                poster.postState(StatusCodes.MOWER_START, getActivity(), application, getGrassArgs(grassSize));


            } else {

                poster.postState(StatusCodes.MOWER_STOP, getActivity(), application, null);

            }

        }
    };

    private Map<String, String> getGrassArgs(int grassHeight) {

        Map<String, String> args = new HashMap<>();
        args.put("grass_size", String.valueOf(grassHeight));
        return args;

    }

    private void updateLayout() {

        switchMower.setOnCheckedChangeListener(null);

        grassSizeEditText.setText(mower.getSizeGrass());

        if (mower.isWorking()) {

            if (!switchMower.isChecked()) switchMower.toggle();

        }

        if (!mower.isWorking()) {

            if (switchMower.isChecked()) switchMower.toggle();

        }

        switchMower.setOnCheckedChangeListener(mowerSwitchListener);

    }

    public MowerFragment() {
        // Required empty public constructor
    }

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
