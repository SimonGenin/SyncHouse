package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import be.simongenin.synchouse.models.Mower;
import be.simongenin.synchouse.requests.PostRequest;
import be.simongenin.synchouse.requests.StatusCodes;
import be.simongenin.synchouse.utils.JSONUtils;
import be.simongenin.synchouse.utils.ServerUtils;


public class MowerFragment extends Fragment {

    private EditText grassSizeEditText;
    private Switch switchMower;

    private Mower mower;
    private SyncHouseApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mower = new Mower();
        mower.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));

        application = (SyncHouseApplication) getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mower, container, false);

        grassSizeEditText = (EditText) v.findViewById(R.id.grass_size);
        switchMower = (Switch) v.findViewById(R.id.switch_activate);

        switchMower.setOnCheckedChangeListener(mowerSwitchListener);

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

                mower.setWorking(true);
                postMower(StatusCodes.MOWER_START, grassSize);


            } else {

                mower.setWorking(false);
                postMower(StatusCodes.MOWER_STOP, 0);


            }

        }
    };

    private void postMower(int code, int grassSize) {

        Map<String, String> params = new HashMap<>();
        params.put("status_code", String.valueOf(code));
        params.put("home_id", application.homeID);
        params.put("password", application.password);
        params.put("grass_size", String.valueOf(grassSize));

        PostRequest mowerRequest = new PostRequest(ServerUtils.STATUS, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    // Let's make our response a json object
                    JSONObject jsonObject = new JSONObject(response);

                    if (JSONUtils.getSuccess(jsonObject)) {

                        mower.saveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));
                        setLayout();
                    }

                    else {

                        setLayout();
                        Toast.makeText(getActivity(), JSONUtils.getError(jsonObject), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    Toast.makeText(getActivity(), "La reponse du server est corrompue.", Toast.LENGTH_LONG).show();
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

        // Send the request
        application.requestQueue.add(mowerRequest);

    }

    private void setLayout() {

        mower.retrieveState(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        switchMower.setOnCheckedChangeListener(null);

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

}
