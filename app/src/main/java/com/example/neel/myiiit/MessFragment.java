package com.example.neel.myiiit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Meals;
import com.example.neel.myiiit.mess.Mess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessFragment extends Fragment {
    TextView  lastUpdatedTextView, today_meal1,today_meal2,today_meal3,tom_meal1,tom_meal2,tom_meal3,th_meal1,th_meal2,th_meal3,th_date;
    ProgressBar progressBar;
    SwipeRefreshLayout pullToRefresh;
    Integer responseCount = 0;
    Calendar lastUpdatedBase = Calendar.getInstance();
    Mess mess;

    /*TODO
    * upcoming meals
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_mess, container, false);

        lastUpdatedTextView = rootView.findViewById(R.id.mess_last_update);
        progressBar = rootView.findViewById(R.id.mess_progress);

        pullToRefresh = rootView.findViewById(R.id.mess_pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            pullToRefresh.setRefreshing(false);

            updateMeals(true);
            }
        });

        today_meal1 = rootView.findViewById(R.id.today_meal1);
        today_meal2 = rootView.findViewById(R.id.today_meal2);
        today_meal3 = rootView.findViewById(R.id.today_meal3);

        tom_meal1 = rootView.findViewById(R.id.tomorrow_meal1);
        tom_meal2 = rootView.findViewById(R.id.tomorrow_meal2);
        tom_meal3 = rootView.findViewById(R.id.tomorrow_meal3);

        th_meal1 = rootView.findViewById(R.id.third_meal1);
        th_meal2 = rootView.findViewById(R.id.third_meal2);
        th_meal3 = rootView.findViewById(R.id.third_meal3);

        th_date = rootView.findViewById(R.id.third_label);


        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mess = Mess.getInstance(context);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMeals(false);
    }

    private void updateMeals(boolean forceUpdate) {
        progressBar.setVisibility(View.VISIBLE);
        Calendar today = Calendar.getInstance();
        mess.getMealsForADay(today, forceUpdate, new Mess.GetMealsCallback() {
            @Override
            public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                today_meal1.setText("Breakfast: " + meals.breakfast);
                today_meal2.setText("Lunch: " + meals.lunch);
                today_meal3.setText("Dinner: " + meals.dinner);

                responsesReceived(lastUpdated);

            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());
                responsesReceived(null);

            }
        });

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        mess.getMealsForADay(tomorrow, forceUpdate, new Mess.GetMealsCallback() {
            @Override
            public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                tom_meal1.setText("Breakfast: " + meals.breakfast);
                tom_meal2.setText("Lunch: " + meals.lunch);
                tom_meal3.setText("Dinner: " + meals.dinner);
                responsesReceived(lastUpdated);


            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());
                responsesReceived(null);
            }
        });


        Calendar dayAfter = Calendar.getInstance();
        dayAfter.add(Calendar.DATE, 2);
        mess.getMealsForADay(tomorrow, forceUpdate, new Mess.GetMealsCallback() {
            @Override
            public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                th_meal1.setText("Breakfast: " + meals.breakfast);
                th_meal2.setText("Lunch: " + meals.lunch);
                th_meal3.setText("Dinner: " + meals.dinner);
                responsesReceived(lastUpdated);

            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());
                responsesReceived(null);
            }
        });

        DateFormat tempFormat = SimpleDateFormat.getDateInstance();
        th_date.setText(tempFormat.format(dayAfter.getTimeInMillis()).toUpperCase());
    }
    private void responsesReceived(Calendar lastUpdated) {
        if (lastUpdated != null && lastUpdated.before(lastUpdatedBase)) {
            lastUpdatedBase = lastUpdated;
        }
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        responseCount ++;
        if (responseCount == 3) {
            lastUpdatedTextView.setText("Last Updated : " + dateFormat.format(lastUpdatedBase.getTimeInMillis()));
            progressBar.setVisibility(View.GONE);
        }
    }
}
