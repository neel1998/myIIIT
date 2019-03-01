package com.trivedi.neel.myiiit;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_home, container, false);
        TextView userNameTextView = rootView.findViewById(R.id.home_user_name);
        String userName = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("username", "user.name@iiit.ac.in");
        userNameTextView.setText(userName.split("@")[0]);
        return rootView;
    }

}
