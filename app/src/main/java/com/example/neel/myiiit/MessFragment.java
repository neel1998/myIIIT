package com.example.neel.myiiit;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.mess.Meals;
import com.example.neel.myiiit.mess.Mess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessFragment extends Fragment {
    private TextView  lastUpdatedTextView;
    private CalendarView mCalendar;

    private ProgressBar progressBar;
    private SwipeRefreshLayout pullToRefresh;
    private FloatingActionButton mFab;

    private Integer mResponseCount = 0;
    private Calendar lastUpdatedBase = Calendar.getInstance();

    private Calendar mDate = Calendar.getInstance();

    private List<MealsFragment> mealsFragmentList = new ArrayList<>();

    private Mess mess;

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

        mealsFragmentList.add((MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_today));
        mealsFragmentList.add((MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_tomorrow));
        mealsFragmentList.add((MealsFragment)getChildFragmentManager().findFragmentById(R.id.meals_day_after));

        mCalendar = rootView.findViewById(R.id.calendar);
        mCalendar.setVisibility(View.GONE);

        mFab = rootView.findViewById(R.id.open_calendar);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCalendar.getVisibility() == View.VISIBLE) {
                    mCalendar.setVisibility(View.GONE);
                    mFab.setImageResource(R.drawable.ic_date_range_black_24dp);
                } else {
                    mCalendar.setVisibility(View.VISIBLE);
                    mFab.setImageResource(R.drawable.ic_close_black_24dp);
                }
            }
        });

        mCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mDate.set(year, month, dayOfMonth);
                updateMeals(false);
            }
        });

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
        lastUpdatedBase = Calendar.getInstance();

        progressBar.setVisibility(View.VISIBLE);
        mResponseCount = 0;

        for (int i = 0; i < mealsFragmentList. size(); ++i) {
            final MealsFragment mealsFragment = mealsFragmentList.get(i);

            Calendar date = (Calendar)mDate.clone();
            date.add(Calendar.DATE, i);

            mess.getMealsForADay(date, forceUpdate, new Mess.GetMealsCallback() {
                @Override
                public void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                    mealsFragment.setMeals(meals);
                    mealsFragment.setDate(date);
                    responsesReceived(lastUpdated);
                }

                @Override
                public void onError(Exception error) {
                    Log.e("MessFragment", error.getLocalizedMessage());
                    responsesReceived(null);
                }
            });
        }
    }

    private void responsesReceived(Calendar lastUpdated) {
        if (lastUpdated != null && lastUpdated.before(lastUpdatedBase)) {
            lastUpdatedBase = lastUpdated;
        }
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

        mResponseCount++;
        if (mResponseCount >= mealsFragmentList.size()) {
            lastUpdatedTextView.setText("Last Updated : " + dateFormat.format(lastUpdatedBase.getTimeInMillis()));
            progressBar.setVisibility(View.GONE);
        }
    }
}
