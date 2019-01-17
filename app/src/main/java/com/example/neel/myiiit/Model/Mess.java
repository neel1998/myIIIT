package com.example.neel.myiiit.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.example.neel.myiiit.Network;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback3;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Mess {

    private  static String DIRTY_MONTHS_KEY = "dirty_months";
    /**
     * Get meals for a day.
     *
     * @param context Android context
     * @param date Date for which you wish to request the meals
     * @param forceRefresh Don't give cached data
     * @param callback Callback to handle result or error
     */
    public static void getMealsForADay(Context context, final Calendar date, boolean forceRefresh, final GetMealCallback callback) {

        if (checkDirtyMonths(context, date)) {
            forceRefresh = true;
        }
        getMealsForMonth(context, date, forceRefresh, new Callback3<String, Calendar, Boolean>() {
            @Override
            public void success(String result, Calendar lastUpdated, Boolean maybeCalledAgain) {
                String[] allMeals = result.split(" ");

                int dayOfMonth = date.get(Calendar.DATE);
                String[] meals = {
                        allMeals[(dayOfMonth - 1) * 4 + 12],
                        allMeals[(dayOfMonth - 1) * 4 + 13],
                        allMeals[(dayOfMonth - 1) * 4 + 14],

                        allMeals[(dayOfMonth) * 4 + 12],
                        allMeals[(dayOfMonth) * 4 + 13],
                        allMeals[(dayOfMonth) * 4 + 14],

                        allMeals[(dayOfMonth + 1) * 4 + 12],
                        allMeals[(dayOfMonth + 1) * 4 + 13],
                        allMeals[(dayOfMonth + 1) * 4 + 14],
                };

                callback.onMealsReceived(date, meals, lastUpdated, maybeCalledAgain);
            }

            @Override
            public void error(Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    private static boolean checkDirtyMonths(Context context, Calendar date) {
        boolean result = false;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String serial = pref.getString(DIRTY_MONTHS_KEY, null);
        if (serial != null){
            Type type = new TypeToken<ArrayList<Pair<Integer, Integer>>>(){}.getType();
            ArrayList<Pair<Integer, Integer>> dirtyMonths = new Gson().fromJson(serial, type);
            if (dirtyMonths.contains(new Pair<Integer, Integer>(date.get(Calendar.MONTH), date.get(Calendar.YEAR)))){
                result = true;
                Log.d("Mess.java", "Current Month is dirty");
                dirtyMonths.remove(new Pair<Integer, Integer>(date.get(Calendar.MONTH), date.get(Calendar.YEAR)));
                SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
                Gson gson = new Gson();
                serial = gson.toJson(dirtyMonths);
                edit.putString(DIRTY_MONTHS_KEY, serial);
                edit.apply();
            }
        }
        return result;
    }

    private static void getMealsForMonth(final Context context, final Calendar date, final boolean forceRefresh, final Callback3<String, Calendar, Boolean> callback) {
        final Pair<String, Calendar> cachedMonth = getCachedMonth(context, date);

        // Callback with cached value if not forceUpdate and cache is found
        if (!forceRefresh && cachedMonth != null) {
            Calendar expirationDate = Calendar.getInstance();
            expirationDate.add(Calendar.HOUR, -6);

            boolean isExpired = cachedMonth.second.before(expirationDate);

            callback.success(cachedMonth.first, cachedMonth.second, isExpired);

            // Only proceed if expired
            if (!isExpired) return;
        }

        MonthlyMealsTask refreshMonth = new MonthlyMealsTask(context) {
            @Override
            protected void onPostExecute(AsyncTaskResult<String> result) {
                super.onPostExecute(result);

                if (result.isError()) {
                    callback.error(result.getError());
                    return;
                }

                Calendar lastUpdated = Calendar.getInstance();
                cacheMonth(context, date, result.getResult(), lastUpdated);

                callback.success(result.getResult(), lastUpdated, false);
            }
        };

        refreshMonth.execute(date.get(Calendar.MONTH), date.get(Calendar.YEAR));
    }

    private static Pair<String, Calendar> getCachedMonth(Context context, Calendar date) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Calendar lastUpdated = Calendar.getInstance();

        Pair<String, String> cachingKeys = getCachingKeysForMonth(date);

        String cachedMonth = sharedPreferences.getString(cachingKeys.first, null);
        lastUpdated.setTimeInMillis(sharedPreferences.getLong(cachingKeys.second, 0));

        if (cachedMonth == null) {
            return null;
        }

        return new Pair<>(cachedMonth, lastUpdated);
    }

    private static void cacheMonth(Context context, Calendar date, String meals, Calendar lastUpdated) {
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();

        Pair<String, String> cachingKeys = getCachingKeysForMonth(date);

        sharedPreferencesEditor.putString(cachingKeys.first, meals);
        sharedPreferencesEditor.putLong(cachingKeys.second, lastUpdated.getTimeInMillis());

        sharedPreferencesEditor.apply();
    }

    private static Pair<String, String> getCachingKeysForMonth(Calendar date) {
        String month = Integer.toString(date.get(Calendar.MONTH));
        String year = Integer.toString(date.get(Calendar.YEAR));
        return new Pair<>(
                "cache-mess-month-" + month + "-" + year,
                "cache-mess-month-" + month + "-" + year + "-last-updated"
        );
    }

    private static class MonthlyMealsTask extends AsyncTask<Integer, Void, AsyncTaskResult<String>> {
        private Context mContext;

        MonthlyMealsTask(Context context) {
            mContext = context;
        }

        @Override
        protected AsyncTaskResult<String> doInBackground(Integer... monthYear) {
            int month = monthYear[0] + 1;
            int year = monthYear[1];

            // Make one request to mess homepage to ensure login
            Network.makeRequest(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php", false);

            // Request month-wise registration page
            String url = "https://mess.iiit.ac.in/mess/web/student_view_registration.php?month="
                    + Integer.toString(month) + "&year=" + Integer.toString(year);

            Document soup = Network.makeRequest(mContext, null, url, false);

            if (soup == null) {
                return new AsyncTaskResult<String>(new RuntimeException("Error while connecting to mess portal"));
            }

            // Get calendar table
            Elements calendarTable = soup.getElementsByClass("calendar");

            if (calendarTable == null || calendarTable.size() == 0) {
                return new AsyncTaskResult<String>(new RuntimeException("Error while connecting to mess portal"));
            }

            return new AsyncTaskResult<>(calendarTable.get(0).text());
        }
    }

    public interface GetMealCallback {
        /**
         * Will be called with the meals data for the date
         *
         * @param date Date for which the result is for
         * @param meals String array for mess registration for breakfast, lunch, dinner.
         * @param maybeCalledAgain Will be true in case cached data is returned and this function may be called again with new data.
         */
        void onMealsReceived(Calendar date, String[] meals, Calendar lastUpdated, boolean maybeCalledAgain);

        /**
         *
         * @param errorMessage Error message
         */
        void onError(String errorMessage);
    }

}