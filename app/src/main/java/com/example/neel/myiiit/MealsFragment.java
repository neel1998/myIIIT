package com.example.neel.myiiit;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Meals;

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
    Calendar mDate;
    Integer nextMealTextSize = 22;
    Integer normalMealTextSize = 18;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("EEEE, d MMM y");

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

        resetViews();
        highLightViews();
        mBreakfastTextView.setText(meals.breakfast);
        mLunchTextView.setText(meals.lunch);
        mDinnerTextView.setText(meals.dinner);
    }
    private void highLightViews() {
        if ( mDate.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
             mDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            mDayTextView.setTextColor(getResources().getColor(R.color.main1));
            Calendar curCal = Calendar.getInstance();

            mBreakfastLabelTextView.setVisibility(View.VISIBLE);
            mBreakfastLabelTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            mLunchLabelTextView.setVisibility(View.VISIBLE);
            mLunchLabelTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            mDinnerLabelTextView.setVisibility(View.VISIBLE);
            mDinnerLabelTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            mBreakfastTextView.setVisibility(View.VISIBLE);
            mBreakfastTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            mLunchTextView.setVisibility(View.VISIBLE);
            mLunchTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            mDinnerTextView.setVisibility(View.VISIBLE);
            mDinnerTextView.setTextColor(getResources().getColor(R.color.meal_upcoming));

            if ( curCal.get(Calendar.HOUR_OF_DAY) < 10 ) {
                mBreakfastLabelTextView.setTextColor(getResources().getColor(R.color.meal_next));
                mBreakfastTextView.setTextColor(getResources().getColor(R.color.meal_next));


                mBreakfastLabelTextView.setTextSize(nextMealTextSize);
                mBreakfastTextView.setTextSize(nextMealTextSize);
            }
            else if ( curCal.get(Calendar.HOUR_OF_DAY) < 15  ) {
                mBreakfastLabelTextView.setTextColor(getResources().getColor(R.color.meal_past));
                mBreakfastTextView.setTextColor(getResources().getColor(R.color.meal_past));

                mLunchLabelTextView.setTextColor(getResources().getColor(R.color.meal_next));
                mLunchTextView.setTextColor(getResources().getColor(R.color.meal_next));

                mLunchLabelTextView.setTextSize(nextMealTextSize);
                mLunchTextView.setTextSize(nextMealTextSize);
            }
            else if ( curCal.get(Calendar.HOUR_OF_DAY) < 22 ) {
                mBreakfastLabelTextView.setTextColor(getResources().getColor(R.color.meal_past));
                mBreakfastTextView.setTextColor(getResources().getColor(R.color.meal_past));

                mLunchLabelTextView.setTextColor(getResources().getColor(R.color.meal_past));
                mLunchTextView.setTextColor(getResources().getColor(R.color.meal_past));

                mDinnerLabelTextView.setTextColor(getResources().getColor(R.color.meal_next));
                mDinnerTextView.setTextColor(getResources().getColor(R.color.meal_next));

                mDinnerLabelTextView.setTextSize(nextMealTextSize);
                mDinnerTextView.setTextSize(nextMealTextSize);
            }
            else {
                resetViews();
            }
        }
        else if ( mDate.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1 &&
                  mDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
            if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 22) {
                mBreakfastLabelTextView.setTextColor(getResources().getColor(R.color.main3));
                mBreakfastTextView.setTextColor(getResources().getColor(R.color.main3));
                mBreakfastLabelTextView.setTextSize(nextMealTextSize);
                mBreakfastTextView.setTextSize(nextMealTextSize);
            }
            else {
                resetViews();
            }
        }
    }

    private void resetViews() {
        int mealsColor = getResources().getColor(R.color.meal_upcoming);

        if ( mDate.before(Calendar.getInstance())) {
            mealsColor = getResources().getColor(R.color.meal_past);
        }

        mDayTextView.setVisibility(View.VISIBLE);
        mDayTextView.setTextColor(mealsColor);

        mBreakfastLabelTextView.setVisibility(View.VISIBLE);
        mBreakfastLabelTextView.setTextColor(mealsColor);
        mBreakfastLabelTextView.setTextSize(normalMealTextSize);

        mLunchLabelTextView.setVisibility(View.VISIBLE);
        mLunchLabelTextView.setTextColor(mealsColor);
        mLunchLabelTextView.setTextSize(normalMealTextSize);

        mDinnerLabelTextView.setVisibility(View.VISIBLE);
        mDinnerLabelTextView.setTextColor(mealsColor);
        mDinnerLabelTextView.setTextSize(normalMealTextSize);

        mBreakfastTextView.setVisibility(View.VISIBLE);
        mBreakfastTextView.setTextColor(mealsColor);
        mBreakfastTextView.setTextSize(normalMealTextSize);

        mLunchTextView.setVisibility(View.VISIBLE);
        mLunchTextView.setTextColor(mealsColor);
        mLunchTextView.setTextSize(normalMealTextSize);

        mDinnerTextView.setVisibility(View.VISIBLE);
        mDinnerTextView.setTextColor(mealsColor);
        mDinnerTextView.setTextSize(normalMealTextSize);

        mErrorMessage.setVisibility(View.INVISIBLE);
    }
    public void setDate(Calendar date) {
         mDayTextView.setText(mDateFormat.format(date.getTimeInMillis()));
         mDate = date;
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
