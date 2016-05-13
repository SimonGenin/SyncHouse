package be.simongenin.synchouse.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.simongenin.synchouse.R;


public class WindowsFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_windows, container, false);
    }


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
