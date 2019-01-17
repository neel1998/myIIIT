package com.example.neel.myiiit.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import com.example.neel.myiiit.AttendanceData;
import com.example.neel.myiiit.Network;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.nodes.Document;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MessCancellation {
    public static final int MEAL_BREAKFAST = 1;
    public static final int MEAL_LUNCH = 2;
    public static final int MEAL_DINNER = 4;
    private  static String DIRTY_MONTHS_KEY = "dirty_months";
    public static void cancelMeals(final Context context, final Calendar startDate,final Calendar endDate, int meals, boolean uncancel, final Callback1<String> callback){
        MessCancelTask messCancelTask = new MessCancelTask(context, meals, uncancel, startDate,endDate ) {
            @Override
            protected void onPostExecute(AsyncTaskResult<String> result) {
                if (result.isError()) {
                    callback.error(result.getError());
                    return;
                }
                callback.success(result.getResult());
                cacheDirtyMonths(context, startDate, endDate);
            }
        };
        messCancelTask.execute();
    }
    private static void cacheDirtyMonths(Context context, Calendar startDate, Calendar endDate) {
        ArrayList<Pair<Integer, Integer>> dirtyMonths;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String serial = pref.getString(DIRTY_MONTHS_KEY, null);
        if (serial != null){
            Type type = new TypeToken<ArrayList<Pair<Integer, Integer>>>(){}.getType();
            dirtyMonths = new Gson().fromJson(serial, type);
        }
        else {
            dirtyMonths = new ArrayList<>();
        }
        while (startDate.get(Calendar.MONTH) != endDate.get(Calendar.MONTH) ||
                startDate.get(Calendar.YEAR) != endDate.get(Calendar.YEAR) ) {
            dirtyMonths.add(new Pair<Integer, Integer>(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)));
            startDate.add(Calendar.MONTH, 1);
        }
        dirtyMonths.add(new Pair<Integer, Integer>(startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR)));
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        Gson gson = new Gson();
        serial = gson.toJson(dirtyMonths);
        edit.putString(DIRTY_MONTHS_KEY, serial);
        edit.apply();
    }
    private static class MessCancelTask extends AsyncTask<Void, Void, AsyncTaskResult<String>> {

        private Context mContext;
        private int mMeals;
        private boolean mUncancel;
        Calendar mStartDate, mEndDate;
        private MessCancelTask(Context context, int meals, boolean uncancel, Calendar starDate, Calendar endDate){
            mContext = context;
            mMeals = meals;
            mUncancel = uncancel;
            mStartDate = starDate;
            mEndDate = endDate;
        }
        @Override
        protected AsyncTaskResult<String> doInBackground(Void... voids) {
            String result = "";

            Network.makeRequest(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php", false);

            String url = "https://mess.iiit.ac.in/mess/web/student_cancel_process.php";
            DateFormat dateFormat = SimpleDateFormat.getDateInstance();

            String startdate = dateFormat.format(mStartDate.getTimeInMillis()).toUpperCase();;
            String enddate = dateFormat.format(mEndDate.getTimeInMillis()).toUpperCase();
            Log.d("start date", startdate);
            Log.d("end date", enddate);

            RequestBody body = new FormBody.Builder()
                    .add("startdate", startdate)
                    .add("enddate", enddate)
                    .add("breakfast[]", ((mMeals & MEAL_BREAKFAST) != 0)?"1":"0")
                    .add("lunch[]", ((mMeals & MEAL_LUNCH) != 0)?"1":"0")
                    .add("dinner[]", ((mMeals & MEAL_DINNER) != 0)?"1":"0")
                    .add("uncancel[]",(mUncancel)?"1":"0")
                    .build();

            Document cancel_soup = Network.makeRequest(mContext, body, url, false);
            result = cancel_soup.getElementsByClass("post").get(1).getElementsByTag("font").get(0).text();
            return new AsyncTaskResult<>(result);
        }
    }

}
