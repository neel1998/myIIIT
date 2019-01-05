package com.example.neel.myiiit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MonthlyMealsFragment extends Fragment{
    Integer cur_month;
    String cur_year;
    CalendarView calendarView;
    String[] meals;
    ProgressBar monthly_prog;
    SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_monthly_mess, container, false);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
        cur_month = Integer.parseInt(dateFormat.format(new Date()).split("-")[0]);
        cur_year = dateFormat.format(new Date()).split("-")[1];

        calendarView = rootView.findViewById(R.id.monthly_calendar);
        monthly_prog = rootView.findViewById(R.id.monthly_prog);
        pullToRefresh = rootView.findViewById(R.id.monthly_pullToRefresh);

        calendarView.setVisibility(View.GONE);
        monthly_prog.setVisibility(View.VISIBLE);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                if (month == cur_month - 1 && year == Integer.valueOf(cur_year)) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(String.valueOf(dayOfMonth) + "-" + String.valueOf(month+1) + "-" + String.valueOf(year))
                            .setMessage("breakfast : " + meals[(dayOfMonth-1)*4 + 12] + "\n" +
                                        "lunch : " + meals[(dayOfMonth-1)*4 + 13] + "\n" +
                                        "dinner : " + meals[(dayOfMonth-1)*4 + 14] )
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
                else {
                    cur_year = String.valueOf(cur_year);
                    cur_month = month + 1;
                    calendarView.setVisibility(View.GONE);
                    monthly_prog.setVisibility(View.VISIBLE);
                    MonthlyMealsTask monthlyMealsTask = new MonthlyMealsTask();
                    monthlyMealsTask.execute();
                }
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                calendarView.setVisibility(View.GONE);
                monthly_prog.setVisibility(View.VISIBLE);
                MonthlyMealsTask monthlyMealsTask = new MonthlyMealsTask();
                monthlyMealsTask.execute();
                pullToRefresh.setRefreshing(false);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        MonthlyMealsTask monthlyMealsTask = new MonthlyMealsTask();
        monthlyMealsTask.execute();
    }

    private class MonthlyMealsTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String url = "https://mess.iiit.ac.in/mess/web/student_view_registration.php?month=" + cur_month + "&year=" + cur_year;
            Document soup = Network.makeRequest(getContext(), null, url, false);
            Element table = soup.getElementsByClass("calendar").get(0);
            meals = table.text().split(" ");
//            int i =0;
//            for (String test : meals){
//                Log.d(String.valueOf(i),test);
//                i++;
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            monthly_prog.setVisibility(View.GONE);
            calendarView.setVisibility(View.VISIBLE);
        }
    }
}
