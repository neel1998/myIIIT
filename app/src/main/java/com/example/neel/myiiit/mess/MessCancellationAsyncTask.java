package com.example.neel.myiiit.mess;

import android.content.Context;
import android.util.Log;

import com.example.neel.myiiit.network.AuthenticationException;
import com.example.neel.myiiit.network.Network;
import com.example.neel.myiiit.network.NetworkResponse;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

class MessCancellationAsyncTask extends CallbackAsyncTask<Void, Void, String>  {
    private Context mContext;
    private int mMeals;
    private boolean mUncancel;
    private Calendar mStartDate, mEndDate;

    MessCancellationAsyncTask(Context context, Calendar starDate, Calendar endDate, int meals, boolean uncancel, AsyncTaskCallback<String> callback){
        super(callback);

        mContext = context;
        mMeals = meals;
        mUncancel = uncancel;
        mStartDate = starDate;
        mEndDate = endDate;
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(Void... voids) {
        String result = "";

        String url = "https://mess.iiit.ac.in/mess/web/student_cancel_process.php";
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();

        String startdate = dateFormat.format(mStartDate.getTimeInMillis()).toUpperCase();;
        String enddate = dateFormat.format(mEndDate.getTimeInMillis()).toUpperCase();
        Log.d("start date", startdate);
        Log.d("end date", enddate);

        RequestBody body = new FormBody.Builder()
                .add("startdate", startdate)
                .add("enddate", enddate)
                .add("breakfast[]", ((mMeals & Mess.MEAL_BREAKFAST) != 0)?"1":"0")
                .add("lunch[]", ((mMeals & Mess.MEAL_LUNCH) != 0)?"1":"0")
                .add("dinner[]", ((mMeals & Mess.MEAL_DINNER) != 0)?"1":"0")
                .add("uncancel[]",(mUncancel)?"1":"0")
                .build();

        NetworkResponse response;
        try {
            // Make one request to mess homepage to ensure login
            Network.request(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php");

            response = Network.request(mContext, body, url);
        }  catch (AuthenticationException|IOException e) {
            return new AsyncTaskResult<>(e);
        }

        try {
            result = response.getSoup().getElementsByClass("post").get(1).getElementsByTag("font").get(0).text();
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }

        return new AsyncTaskResult<>(result);
    }
}
