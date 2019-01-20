package com.example.neel.myiiit.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Attendance {
    private static final String ATTENDANCE_DATA_KEY = "attendance_data";
    private static final String ATTENDANCE_LAST_UPDATE_KEY = "attendance_last_update";

    public static void getAttendance(final Context context, boolean forceUpdate, final Callback2<List<AttendanceData>, Calendar> callback){

        Pair<ArrayList<AttendanceData>, Calendar> cachedData = getCachedAttendance(context);
        if (!forceUpdate && cachedData.first != null) {

            Calendar expirationDate = Calendar.getInstance();
            expirationDate.add(Calendar.HOUR, -12);
            boolean isExpired = cachedData.second.before(expirationDate);
            callback.success(cachedData.first, cachedData.second);

            if (!isExpired) return;
        }


        AttendanceTask attendanceTask = new AttendanceTask(context, new AsyncTaskCallback<List<AttendanceData>>() {
            @Override
            public void call(AsyncTaskResult<List<AttendanceData>> result) {
                if (result.isError()){
                    callback.error(result.getError());
                    return;
                }
                Calendar lastUpdate = Calendar.getInstance();

                cacheAttendance(context, result.getResult(), lastUpdate);
                callback.success(result.getResult(), lastUpdate);
            }
        });

        attendanceTask.execute();
    }
    private static Pair<ArrayList<AttendanceData>, Calendar> getCachedAttendance(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<AttendanceData> result = null;
        Calendar lastUpdate = Calendar.getInstance();
        String serial = pref.getString(ATTENDANCE_DATA_KEY, null);
        if (serial != null){
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<AttendanceData>>(){}.getType();
            result = gson.fromJson(serial, type);
        }
        lastUpdate.setTimeInMillis(pref.getLong(ATTENDANCE_LAST_UPDATE_KEY, 0));
        return new Pair<>(result, lastUpdate);
    }


    private static void cacheAttendance(Context context, List<AttendanceData> result, Calendar lastUpdate) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        Gson gson = new Gson();
        String serial = gson.toJson(result);
        edit.putString(ATTENDANCE_DATA_KEY, serial);
        edit.putLong(ATTENDANCE_LAST_UPDATE_KEY, lastUpdate.getTimeInMillis());
        edit.apply();
    }

}
