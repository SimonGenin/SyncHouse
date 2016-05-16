package be.simongenin.synchouse.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;

public class MenuFragment extends Fragment {

    private SyncHouseApplication application;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (SyncHouseApplication) getActivity().getApplication();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fragment, container, false);

        /**
         * Set the title home ID field
         */
        TextView homeIdTextView = (TextView) v.findViewById(R.id.home_id_tv);
        homeIdTextView.setText(application.homeID);

        return v;
    }

    /**
     * The fragment must be created with this function.
     */
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }
}
