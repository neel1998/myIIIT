package com.example.neel.myiiit.Model;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.example.neel.myiiit.Network;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback3;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mess {
    private static Mess sInstance = null;

    private Context mContext;
    private MessCache messCache;

    private Map<Pair<Integer, Integer>, List<Callback3<List<Meals>, Calendar, Boolean>>>
            mRefreshMonthCallbackMap = new HashMap<>();

    public static Mess getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Mess(context.getApplicationContext());
        }

        return sInstance;
    }

    private Mess(Context context) {
        mContext = context;

        messCache = new MessCache(PreferenceManager.getDefaultSharedPreferences(mContext));
    }

    /**
     * Get meals for a day.
     *
     * @param date Date for which you wish to request the meals
     * @param forceRefresh Don't give cached data
     * @param callback Callback to handle result or error
     */
    public void getMealsForADay(final Calendar date, boolean forceRefresh, final GetMealsCallback callback) {
        getMealsForMonth(date.get(Calendar.MONTH), date.get(Calendar.YEAR), forceRefresh, new Callback3<List<Meals>, Calendar, Boolean>() {
            @Override
            public void success(List<Meals> allMeals, Calendar lastUpdated, Boolean maybeCalledAgain) {
                int dayIndex = date.get(Calendar.DATE) - 1;

                if (dayIndex < allMeals.size()) {
                    callback.onMealsReceived(date, allMeals.get(dayIndex), lastUpdated, maybeCalledAgain);
                } else {
                    callback.onError("Something went wrong. Could not get meals.");
                }
            }

            @Override
            public void error(Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    private void getMealsForMonth(final int month, final int year, final boolean forceRefresh, final Callback3<List<Meals>, Calendar, Boolean> callback) {
        final CachedMonth cachedMonth = messCache.getCachedMonth(month, year);

        // Callback with cached value if not forceUpdate and cache is found
        if (!forceRefresh && cachedMonth != null) {
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.add(Calendar.HOUR, -6);

            boolean isExpired = cachedMonth.lastUpdated.before(expirationDate);

            callback.success(cachedMonth.mealsList, cachedMonth.lastUpdated, isExpired);

            // Only proceed if expired
            if (!isExpired) return;
        }

        final Pair<Integer, Integer> monthKey = new Pair<>(month, year);

        // Run single AsyncTask for a month at a time
        if (mRefreshMonthCallbackMap.containsKey(monthKey)) {
            // If the callback list already exists, add to it and return.
            mRefreshMonthCallbackMap.get(monthKey).add(callback);
            return;
        } else {
            // If callback list does not exists, create one and create AsyncTask
            List<Callback3<List<Meals>, Calendar, Boolean>> callbacks = new ArrayList<>();
            callbacks.add(callback);

            mRefreshMonthCallbackMap.put(monthKey, callbacks);
        }

        MonthlyMealsTask refreshMonth = new MonthlyMealsTask(new AsyncTaskCallback<List<Meals>>() {
            @Override
            public void call(AsyncTaskResult<List<Meals>> result) {
                if (result.isError()) {
                    for (Callback3 callback: mRefreshMonthCallbackMap.get(monthKey)) {
                        callback.error(result.getError());
                    }
                    mRefreshMonthCallbackMap.remove(monthKey);
                    return;
                }

                Calendar lastUpdated = Calendar.getInstance();
                messCache.cacheMonth(month, year, result.getResult(), lastUpdated);

                callback.success(result.getResult(), lastUpdated, false);
                for (Callback3 callback: mRefreshMonthCallbackMap.get(monthKey)) {
                    callback.success(result.getResult(), lastUpdated, false);
                }
                mRefreshMonthCallbackMap.remove(monthKey);
            }
        });

        refreshMonth.execute(month, year);
    }

    private class MonthlyMealsTask extends CallbackAsyncTask<Integer, Void, List<Meals>> {
        MonthlyMealsTask(AsyncTaskCallback<List<Meals>> callback) {
            super(callback);
        }

        @Override
        protected AsyncTaskResult<List<Meals>> doInBackground(Integer... monthYear) {
            int month = monthYear[0] + 1;
            int year = monthYear[1];

            // Make one request to mess homepage to ensure login
            Network.makeRequest(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php", false);

            // Request month-wise registration page
            String url = "https://mess.iiit.ac.in/mess/web/student_view_registration.php?month="
                    + Integer.toString(month) + "&year=" + Integer.toString(year);

            Document soup = Network.makeRequest(mContext, null, url, false);

            if (soup == null) {
                return new AsyncTaskResult<List<Meals>>(new RuntimeException("Error while connecting to mess portal"));
            }

            // Get calendar table
            Elements calendarTable = soup.getElementsByClass("calendar");

            if (calendarTable == null || calendarTable.size() == 0) {
                return new AsyncTaskResult<List<Meals>>(new RuntimeException("Error while connecting to mess portal"));
            }

            // Parse table
            String[] tableTokens = calendarTable.get(0).text().split(" ");

            List<Meals> allMeals = new ArrayList<>();
            for (int date = 1, i = 11; i < tableTokens.length - 3; ++date, i += 4) {
                if (Integer.parseInt(tableTokens[i]) != date) {
                    // Sanity check. The date from text should be same as the calculated one
                    Log.w("Mess", "Something is wrong. The dates don't match");
                }
                Meals meals = new Meals();
                meals.breakfast = tableTokens[i + 1];
                meals.lunch = tableTokens[i + 2];
                meals.dinner = tableTokens[i + 3];
                allMeals.add(meals);
            }

            return new AsyncTaskResult<List<Meals>>(allMeals);
        }
    }

    public interface GetMealsCallback {
        /**
         * Will be called with the meals data for the date
         *
         * @param date Date for which the result is for
         * @param meals String array for mess registration for breakfast, lunch, dinner.
         * @param lastUpdated Last updated time
         * @param maybeCalledAgain Will be true in case cached data is returned and this function may be called again with new data.
         */
        void onMealsReceived(Calendar date, Meals meals, Calendar lastUpdated, boolean maybeCalledAgain);

        /**
         *
         * @param errorMessage Error message
         */
        void onError(String errorMessage);
    }

}