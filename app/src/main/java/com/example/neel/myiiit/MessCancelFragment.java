package com.example.neel.myiiit;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Mess;
import com.example.neel.myiiit.utils.Callback1;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessCancelFragment extends Fragment {
    private CheckBox breakFastCheckBox, lunchCheckBox, dinnerCheckBox, uncancelCheckBox;
    private Button submitButton, startDateButton, endDateButton;
    private TextView startDateTextView, endDateTextView;
    private DatePickerDialog startDatePickerDialog, endDatePickerDialog;
    private Mess mess;
    private Calendar startDate, endDate;
    ProgressBar cancelProgrssBar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mess = Mess.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mess_cancel, container, false);

        startDateTextView = rootView.findViewById(R.id.startdate_text);
        endDateTextView = rootView.findViewById(R.id.enddate_text);

        startDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate = Calendar.getInstance();
                startDate.set(year, month, dayOfMonth);
                startDateTextView.setText(SimpleDateFormat.getDateInstance().format(startDate.getTimeInMillis()));
            }
        }, Calendar.getInstance().get(Calendar.YEAR),
           Calendar.getInstance().get(Calendar.MONTH),
           Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate = Calendar.getInstance();
                endDate.set(year, month, dayOfMonth);
                endDateTextView.setText(SimpleDateFormat.getDateInstance().format(endDate.getTimeInMillis()));
            }
        }, Calendar.getInstance().get(Calendar.YEAR),
           Calendar.getInstance().get(Calendar.MONTH),
           Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        startDateButton = rootView.findViewById(R.id.startdate_btn);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDatePickerDialog.show();
            }
        });

        endDateButton = rootView.findViewById(R.id.enddate_btn);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDatePickerDialog.show();
            }
        });

        cancelProgrssBar = rootView.findViewById(R.id.cancel_prog);

        breakFastCheckBox = rootView.findViewById(R.id.breakfast_box);
        lunchCheckBox = rootView.findViewById(R.id.lunch_box);
        dinnerCheckBox = rootView.findViewById(R.id.dinner_box);
        uncancelCheckBox = rootView.findViewById(R.id.uncancel_box);

        submitButton = rootView.findViewById(R.id.submit_btn);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  cancelMeals();
            }
        });

        return rootView;
    }

    
    private void cancelMeals(){
        int meals = 0;
        cancelProgrssBar.setVisibility(View.VISIBLE);
        if (breakFastCheckBox.isChecked()) meals |= Mess.MEAL_BREAKFAST;
        if (lunchCheckBox.isChecked()) meals |= Mess.MEAL_LUNCH;
        if (dinnerCheckBox.isChecked()) meals |= Mess.MEAL_DINNER;

        Log.d("fragment start date", startDate.toString());
        Log.d("fragment end date", endDate.toString());
        mess.cancelMeals((Calendar) startDate.clone(), (Calendar) endDate.clone(), meals, uncancelCheckBox.isChecked(), new Callback1<String>() {
            @Override
            public void success(String s) {
                resetLayout(s);
            }

            @Override
            public void error(Exception e) {
                Log.d("MessCancellation", e.getLocalizedMessage());
                resetLayout(e.getLocalizedMessage());
            }
        });
    }
    private void resetLayout(String message) {
        cancelProgrssBar.setVisibility(View.GONE);
        Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackbar.show();
        breakFastCheckBox.setChecked(false);
        lunchCheckBox.setChecked(false);
        dinnerCheckBox.setChecked(false);
        uncancelCheckBox.setChecked(false);



    }
}
