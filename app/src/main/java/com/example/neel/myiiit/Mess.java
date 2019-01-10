package com.example.neel.myiiit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback3;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class Mess {
    public static final int MEAL_BREAKFAST = 1;
    public static final int MEAL_LUNCH = 2;
    public static final int MEAL_DINNER = 4;

    /**
     * Get meals for a day.
     *
     * @param context Android context
     * @param date Date for which you wish to request the meals
     * @param forceRefresh Don't give cached data
     * @param callback Callback to handle result or error
     */
    public static void getMealsForADay(Context context, final Calendar date, boolean forceRefresh, final GetMealCallback callback) {
        getMealsForMonth(context, date, forceRefresh, new Callback3<String, Calendar, Boolean>() {
            @Override
            public void success(String result, Calendar lastUpdated, Boolean maybeCalledAgain) {
                String[] allMeals = result.split(" ");

                int dayOfMonth = date.get(Calendar.DATE);
                String[] meals = {
                        allMeals[(dayOfMonth - 1) * 4 + 12],
                        allMeals[(dayOfMonth - 1) * 4 + 13],
                        allMeals[(dayOfMonth - 1) * 4 + 14],
                };

                callback.onMealsReceived(date, meals, lastUpdated, maybeCalledAgain);
            }

            @Override
            public void error(Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public void cancelMeal(Calendar startDate, Calendar endDate, int meals, CancellationCallback callback) {

    }

    public void uncancelMeal(Calendar startDate, Calendar endDate, int meals, CancellationCallback callback) {

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

    public interface CancellationCallback {
        /**
         * Called on success
         */
        void onSuccess();

        /**
         * Called on failure
         * @param errorMessage Error message
         */
        void onError(String errorMessage);
    }
}