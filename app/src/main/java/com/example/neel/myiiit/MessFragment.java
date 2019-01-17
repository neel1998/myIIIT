package com.example.neel.myiiit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.Model.Mess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MessFragment extends Fragment {
    TextView  lastUpdatedTextView, today_meal1,today_meal2,today_meal3,tom_meal1,tom_meal2,tom_meal3,th_meal1,th_meal2,th_meal3,th_date;
    ProgressBar progressBar;
    SwipeRefreshLayout pullToRefresh;
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
    public void onStart() {
        super.onStart();

        updateMeals(false);
    }

    private void updateMeals(boolean forceUpdate) {
        progressBar.setVisibility(View.VISIBLE);

        Mess.getMealsForADay(getContext(), Calendar.getInstance(), forceUpdate, new Mess.GetMealCallback() {
            @Override
            public void onMealsReceived(Calendar date, String[] meals, Calendar lastUpdated, boolean maybeCalledAgain) {

                today_meal1.setText("Breakfast: " + meals[0]);
                today_meal2.setText("Lunch: " + meals[1]);
                today_meal3.setText("Dinner: " + meals[2]);

                tom_meal1.setText("Breakfast: " + meals[3]);
                tom_meal2.setText("Lunch: " + meals[4]);
                tom_meal3.setText("Dinner: " + meals[5]);

                th_meal1.setText("Breakfast: " + meals[6]);
                th_meal2.setText("Breakfast: " + meals[7]);
                th_meal3.setText("Breakfast: " + meals[8]);

                Calendar temp = Calendar.getInstance();
                temp.add(Calendar.DATE, 2);
                DateFormat tempFormat = SimpleDateFormat.getDateInstance();
                th_date.setText(tempFormat.format(temp.getTimeInMillis()).toUpperCase());

                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

                lastUpdatedTextView.setText("Last Updated : " + dateFormat.format(lastUpdated.getTimeInMillis()));

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MessFragment", errorMessage);

                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
