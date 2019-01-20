package com.example.neel.myiiit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Meals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MealsFragment extends Fragment {
    TextView mBreakfastTextView;
    TextView mLunchTextView;
    TextView mDinnerTextView;
    TextView mDayTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);

        mBreakfastTextView = rootView.findViewById(R.id.breakfast);
        mLunchTextView = rootView.findViewById(R.id.lunch);
        mDinnerTextView = rootView.findViewById(R.id.dinner);
        mDayTextView = rootView.findViewById(R.id.day);

        return rootView;
    }

    public void setMeals(Meals meals) {
        mBreakfastTextView.setText(meals.breakfast);
        mLunchTextView.setText(meals.lunch);
        mDinnerTextView.setText(meals.dinner);
    }

    public void setDate(Calendar date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        String formattedDate = dateFormat.format(date.getTimeInMillis());
        mDayTextView.setText(formattedDate);
    }

}
