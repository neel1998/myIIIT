package com.trivedi.neel.myiiit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ChangeMessFragment extends Fragment {
    View dateWiseChangeLayout, dayWiseChangeLayout, monthlyChangeLayout;
    RadioButton dateWiseRadio, dayWiseRadio, monthlyRadio;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_mess, container, false);
        dateWiseRadio = rootView.findViewById(R.id.change_mess_date_wise);
        dayWiseRadio = rootView.findViewById(R.id.change_mess_day_wise);
        monthlyRadio = rootView.findViewById(R.id.change_mess_monthly);
        dateWiseChangeLayout = rootView.findViewById(R.id.date_wise_change_mess_layout);
        dayWiseChangeLayout = rootView.findViewById(R.id.day_wise_change_mess_layout);
        monthlyChangeLayout = rootView.findViewById(R.id.montly_change_mess_layout);
        RadioGroup changeRadioGroup = rootView.findViewById(R.id.change_mess_type_radio_group);
        changeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.change_mess_date_wise:
                        dateWiseChangeLayout.setVisibility(View.VISIBLE);
                        dayWiseChangeLayout.setVisibility(View.GONE);
                        monthlyChangeLayout.setVisibility(View.GONE);
                        break;
                    case R.id.change_mess_day_wise:
                        dateWiseChangeLayout.setVisibility(View.GONE);
                        dayWiseChangeLayout.setVisibility(View.VISIBLE);
                        monthlyChangeLayout.setVisibility(View.GONE);
                        break;
                    case R.id.change_mess_monthly:
                        dateWiseChangeLayout.setVisibility(View.GONE);
                        dayWiseChangeLayout.setVisibility(View.GONE);
                        monthlyChangeLayout.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
        return rootView;
    }

}
