package com.example.neel.myiiit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;


import com.example.neel.myiiit.Model.Mess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonthlyMealsFragment extends Fragment{
    Integer cur_month;
    String cur_year;
    CalendarView calendarView;
    ProgressBar monthly_prog;
    SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_monthly_mess, container, false);

        calendarView = rootView.findViewById(R.id.monthly_calendar);
        monthly_prog = rootView.findViewById(R.id.monthly_prog);
        pullToRefresh = rootView.findViewById(R.id.monthly_pullToRefresh);

        monthly_prog.setVisibility(View.GONE);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                Calendar cal = Calendar.getInstance();
                cal.set(year,month,dayOfMonth);
                getMeals(false, cal);
            }
        });
        return rootView;
    }

    private void getMeals(boolean forceUpdate, Calendar date) {
        monthly_prog.setVisibility(View.VISIBLE);

        Mess.getMealsForADay(getContext(), date, forceUpdate, new Mess.GetMealCallback() {
            @Override
            public void onMealsReceived(Calendar date, String[] meals, Calendar lastUpdated, boolean maybeCalledAgain) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle( date.get(Calendar.DATE) + "-" + (date.get(Calendar.MONTH)+1) + "-" + date.get(Calendar.YEAR))
                        .setMessage("breakfast : " + meals[0] + "\n" +
                                "lunch : " + meals[1] + "\n" +
                                "dinner : " + meals[2] )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();

                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                monthly_prog.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("MessFragment", errorMessage);
                monthly_prog.setVisibility(View.GONE);
            }
        });
    }

}
