package com.example.neel.myiiit.mess;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

class MessCacheManager {
    private static final String MESS_CACHE_KEY = "mess_cache";

    private CachedMonthMap mCachedMonthMap;

    private SharedPreferences mSharedPref;

    MessCacheManager(SharedPreferences sharedPreferences) {
        mSharedPref = sharedPreferences;

        load();
    }

    void cacheMonth(int month, int year, @NotNull List<Meals> mealsList, @NotNull Calendar lastUpdated) {
        String monthKey = getMonthKey(month, year);

        CachedMonth cachedMonth = new CachedMonth();
        cachedMonth.mealsList = mealsList;
        cachedMonth.lastUpdated = lastUpdated;
        cachedMonth.isDirty = false;

        mCachedMonthMap.put(monthKey, cachedMonth);

        save();
    }

    @Nullable
    CachedMonth getCachedMonth(int month, int year) {
        String monthKey = getMonthKey(month, year);
        if (mCachedMonthMap.containsKey(monthKey)) {
            return mCachedMonthMap.get(monthKey);
        }
        else {
            return null;
        }
    }

    void markMonthDirty(int month, int year) {
        CachedMonth cachedMonth = getCachedMonth(month, year);

        if (cachedMonth != null && !cachedMonth.isDirty) {
            cachedMonth.isDirty = true;

            save();
        }
    }

    void clearCache() {
        SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.remove(MESS_CACHE_KEY);
        sharedPrefEditor.apply();
    }

    private String getMonthKey(int month, int year) {
        return year + "-" + month;
    }

    private void save() {
        Gson gson = new Gson();
        String cacheString = gson.toJson(mCachedMonthMap);

        Log.d("MessCacheManager", "Storing: " + cacheString);

        SharedPreferences.Editor sharedPrefEditor = mSharedPref.edit();
        sharedPrefEditor.putString(MESS_CACHE_KEY, cacheString);
        sharedPrefEditor.apply();
    }

    private void load() {
        String cacheString = mSharedPref.getString(MESS_CACHE_KEY, null);

        Log.d("MessCacheManager", "Loading: " + cacheString);

        Gson gson = new Gson();
        CachedMonthMap cachedMonthMap = gson.fromJson(cacheString, CachedMonthMap.class);

        if (cachedMonthMap == null) {
            cachedMonthMap = new CachedMonthMap();
        }

        mCachedMonthMap = cachedMonthMap;
    }
}

class CachedMonthMap extends HashMap<String, CachedMonth> { }