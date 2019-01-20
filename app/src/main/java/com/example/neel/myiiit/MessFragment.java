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
    TextView  lastUpdatedTextView;
    MealsFragment mMealsToday, mMealsTomorrow, mMealsDayAfter;
    ProgressBar progressBar;
    SwipeRefreshLayout pullToRefresh;

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

        mMealsToday = (MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_today);
        mMealsTomorrow = (MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_tomorrow);
        mMealsDayAfter = (MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_day_after);

        Calendar date = Calendar.getInstance();
        mMealsToday.setDate(date);
        date.add(Calendar.DATE, 1);
        mMealsTomorrow.setDate(date);
        date.add(Calendar.DATE, 1);
        mMealsDayAfter.setDate(date);

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
                mMealsToday.setMeals(meals);

//                TODO
//                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
//
//                lastUpdatedTextView.setText("Last Updated : " + dateFormat.format(lastUpdated.getTimeInMillis()));
//
//                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());

//                progressBar.setVisibility(View.GONE);
            }
        });

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        mess.getMealsForADay(tomorrow, forceUpdate, new Mess.GetMealsCallback() {
            @Override
            public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                mMealsTomorrow.setMeals(meals);
            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());
            }
        });


        Calendar dayAfter = Calendar.getInstance();
        dayAfter.add(Calendar.DATE, 2);
        mess.getMealsForADay(tomorrow, forceUpdate, new Mess.GetMealsCallback() {
            @Override
            public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                mMealsDayAfter.setMeals(meals);
            }

            @Override
            public void onError(Exception error) {
                Log.e("MessFragment", error.getLocalizedMessage());
            }
        });

        // TODO: fix this.
        progressBar.setVisibility(View.GONE);
    }

}
