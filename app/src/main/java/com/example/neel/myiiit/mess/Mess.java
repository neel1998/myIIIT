package com.example.neel.myiiit.mess;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback1;
import com.example.neel.myiiit.utils.Callback3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mess {
    public static final int MEAL_BREAKFAST = 1;
    public static final int MEAL_LUNCH = 2;
    public static final int MEAL_DINNER = 4;

    private static Mess sInstance = null;

    private Context mContext;
    private MessCacheManager mCacheManager;

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

        mCacheManager = new MessCacheManager(PreferenceManager.getDefaultSharedPreferences(mContext));
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

    /**
     * Cancel/Uncancel meals
     * @param startDate Start date range
     * @param endDate End date range
     * @param meals Which meals to cancel. Mess.MEAL_BREAKFAST | Mess.MEAL_LUNCH | Mess.MEAL_DINNER
     * @param uncancel Whether to uncancel the meals
     * @param callback Callback
     */
    public void cancelMeals(final Calendar startDate, final Calendar endDate, int meals, boolean uncancel, final Callback1<String> callback){
        MessCancellationAsyncTask messCancelTask = new MessCancellationAsyncTask(mContext, startDate, endDate, meals, uncancel, new AsyncTaskCallback<String>() {
            @Override
            public void call(AsyncTaskResult<String> result) {
                if (result.isError()) {
                    callback.error(result.getError());
                    return;
                }
                markMonthsDirty(startDate, endDate);
                callback.success(result.getResult());
            }
        });
        messCancelTask.execute();
    }

    public void clearCache() {
        mCacheManager.clearCache();
    }

    private void markMonthsDirty(Calendar startDate, Calendar endDate) {
        while (startDate.before(endDate)) {
            mCacheManager.markMonthDirty(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));
            startDate.roll(Calendar.MONTH, 1);
        }
    }

    private void getMealsForMonth(final int month, final int year, final boolean forceRefresh, final Callback3<List<Meals>, Calendar, Boolean> callback) {
        final CachedMonth cachedMonth = mCacheManager.getCachedMonth(month, year);

        // Callback with cached value if not forceUpdate and not dirty
        if (cachedMonth != null && !cachedMonth.isDirty && !forceRefresh) {
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

        MessMonthlyMealsAsyncTask refreshMonth = new MessMonthlyMealsAsyncTask(mContext, new AsyncTaskCallback<List<Meals>>() {
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
                mCacheManager.cacheMonth(month, year, result.getResult(), lastUpdated);

                for (Callback3 callback: mRefreshMonthCallbackMap.get(monthKey)) {
                    callback.success(result.getResult(), lastUpdated, false);
                }
                mRefreshMonthCallbackMap.remove(monthKey);
            }
        });

        refreshMonth.execute(month, year);
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