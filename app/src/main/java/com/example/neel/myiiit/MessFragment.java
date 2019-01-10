package com.example.neel.myiiit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessFragment extends Fragment {
    TextView mealsTextView, lastUpdatedTextView;
    ProgressBar progressBar;
    SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_mess, container, false);

        lastUpdatedTextView = rootView.findViewById(R.id.mess_last_update);
        mealsTextView = rootView.findViewById(R.id.mess_textbox);
        progressBar = rootView.findViewById(R.id.mess_progress);

        pullToRefresh = rootView.findViewById(R.id.mess_pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            pullToRefresh.setRefreshing(false);

            updateMeals(true);
            }
        });

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
                mealsTextView.setText(
                        "Breakfast: " + meals[0] + "\n"
                                + "Lunch: " + meals[1] + "\n"
                                + "Dinner: " + meals[2] + "\n"
                );

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
