package com.example.neel.myiiit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.neel.myiiit.Model.MessCancellation;
import com.example.neel.myiiit.utils.Callback1;

import java.util.Calendar;

public class MessCancelFragment extends Fragment {
    CheckBox breakfast_box, lunch_box, dinner_box, uncancel_box;
    Button submit_btn;
    TextView cancel_msg;
    DatePicker datePicker1, datePicker2;
    /*TODO
    * Reset Fragment to default on changin tab
    * */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mess_cancel, container, false);



        cancel_msg = rootView.findViewById(R.id.cancel_msg);

        breakfast_box = rootView.findViewById(R.id.breakfast_box);
        lunch_box = rootView.findViewById(R.id.lunch_box);
        dinner_box = rootView.findViewById(R.id.dinner_box);
        uncancel_box = rootView.findViewById(R.id.uncancel_box);

        submit_btn = rootView.findViewById(R.id.submit_btn);

        datePicker1 = rootView.findViewById(R.id.datePicker1);
        datePicker2 = rootView.findViewById(R.id.datePicker2);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  cancelMeals();
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void cancelMeals(){
        int meals = 0;
        if (breakfast_box.isChecked()) meals += MessCancellation.MEAL_BREAKFAST;
        if (lunch_box.isChecked()) meals += MessCancellation.MEAL_LUNCH;
        if (dinner_box.isChecked()) meals += MessCancellation.MEAL_DINNER;

        Calendar startdate = Calendar.getInstance();
        startdate.set(Calendar.DATE, datePicker1.getDayOfMonth());
        startdate.set(Calendar.MONTH, datePicker1.getMonth());
        startdate.set(Calendar.YEAR, datePicker1.getYear());

        Calendar enddate = Calendar.getInstance();
        enddate.set(Calendar.DATE, datePicker2.getDayOfMonth());
        enddate.set(Calendar.MONTH, datePicker2.getMonth());
        enddate.set(Calendar.YEAR, datePicker2.getYear());

        MessCancellation.cancelMeals(getContext(), startdate, enddate, meals, uncancel_box.isChecked(), new Callback1<String>() {
            @Override
            public void success(String s) {
                cancel_msg.setText(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessCancellation", e.getLocalizedMessage());
            }
        });
    }
}
