package com.example.neel.myiiit;

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

    TextView mBreakfastLabelTextView;
    TextView mLunchLabelTextView;
    TextView mDinnerLabelTextView;

    TextView mErrorMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_meals, container, false);

        mBreakfastTextView = rootView.findViewById(R.id.breakfast);
        mLunchTextView = rootView.findViewById(R.id.lunch);
        mDinnerTextView = rootView.findViewById(R.id.dinner);

        mDayTextView = rootView.findViewById(R.id.day);

        mBreakfastLabelTextView = rootView.findViewById(R.id.breakfast_label);
        mLunchLabelTextView = rootView.findViewById(R.id.lunch_label);
        mDinnerLabelTextView = rootView.findViewById(R.id.dinner_label);

        mErrorMessage = rootView.findViewById(R.id.errorMessage);

        return rootView;
    }

    public void setMeals(Meals meals) {
        mDayTextView.setVisibility(View.VISIBLE);

        mBreakfastLabelTextView.setVisibility(View.VISIBLE);
        mLunchLabelTextView.setVisibility(View.VISIBLE);
        mDinnerLabelTextView.setVisibility(View.VISIBLE);

        mBreakfastTextView.setVisibility(View.VISIBLE);
        mLunchTextView.setVisibility(View.VISIBLE);
        mDinnerTextView.setVisibility(View.VISIBLE);

        mErrorMessage.setVisibility(View.VISIBLE);

        mBreakfastTextView.setText(meals.breakfast);
        mLunchTextView.setText(meals.lunch);
        mDinnerTextView.setText(meals.dinner);
    }

    public void setDate(Calendar date) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        String formattedDate = dateFormat.format(date.getTimeInMillis());
        mDayTextView.setText(formattedDate);
    }

    public void invalidate() {
        mDayTextView.setVisibility(View.INVISIBLE);

        mBreakfastLabelTextView.setVisibility(View.INVISIBLE);
        mLunchLabelTextView.setVisibility(View.INVISIBLE);
        mDinnerLabelTextView.setVisibility(View.INVISIBLE);

        mBreakfastTextView.setVisibility(View.INVISIBLE);
        mLunchTextView.setVisibility(View.INVISIBLE);
        mDinnerTextView.setVisibility(View.INVISIBLE);

        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    public void setError(Exception exception) {
        invalidate();

        mDayTextView.setVisibility(View.VISIBLE);

        mErrorMessage.setText(exception.getLocalizedMessage());
        mErrorMessage.setVisibility(View.VISIBLE);
    }

}
