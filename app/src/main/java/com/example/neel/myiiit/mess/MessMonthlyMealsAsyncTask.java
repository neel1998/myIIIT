package com.example.neel.myiiit.mess;

import android.content.Context;
import android.util.Log;

import com.example.neel.myiiit.network.AuthenticationException;
import com.example.neel.myiiit.network.Network;
import com.example.neel.myiiit.network.NetworkResponse;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MessMonthlyMealsAsyncTask extends CallbackAsyncTask<Integer, Void, List<Meals>> {
    private Context mContext;

    MessMonthlyMealsAsyncTask(Context context, AsyncTaskCallback<List<Meals>> callback) {
        super(callback);

        mContext = context;
    }

    private String cleanMessName(String messName) {
        return messName.replaceAll("(.*)\\([BLD]\\)", "$1");
    }

    @Override
    protected AsyncTaskResult<List<Meals>> doInBackground(Integer... monthYear) {
        int month = monthYear[0] + 1;
        int year = monthYear[1];

        // Request month-wise registration page
        String url = "https://mess.iiit.ac.in/mess/web/student_view_registration.php?month="
                + Integer.toString(month) + "&year=" + Integer.toString(year);

        NetworkResponse request = null;
        try {
            // Make one request to mess homepage to ensure login
            Network.request(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php");

            request = Network.request(mContext, null, url);
        } catch (AuthenticationException|IOException e) {
            return new AsyncTaskResult<>(e);
        }

        if (request.getSoup() == null) {
            return new AsyncTaskResult<List<Meals>>(new RuntimeException("Error while connecting to mess portal"));
        }

        // Get calendar table
        Elements calendarTable = request.getSoup().getElementsByClass("calendar");

        if (calendarTable == null || calendarTable.size() == 0) {
            return new AsyncTaskResult<List<Meals>>(new RuntimeException("Error while extracting data from mess portal"));
        }

        String tableText = calendarTable.get(0).text().replace(" Non-", "Non-");
        // Parse table
        String[] tableTokens = tableText.split(" ");


        List<Meals> allMeals = new ArrayList<>();
        for (int date = 1, i = 11; i < tableTokens.length - 3; ++date, i += 4) {
            if (Integer.parseInt(tableTokens[i]) != date) {
                // Sanity check. The date from text should be same as the calculated one
                Log.w("Mess", "Something is wrong. The dates don't match");
            }
            Meals meals = new Meals();
            meals.breakfast = cleanMessName(tableTokens[i + 1]);
            meals.lunch = cleanMessName(tableTokens[i + 2]);
            meals.dinner = cleanMessName(tableTokens[i + 3]);
            allMeals.add(meals);
        }

        return new AsyncTaskResult<List<Meals>>(allMeals);
    }
}