package com.trivedi.neel.myiiit.mess;

import android.content.Context;
import android.util.Log;

import com.trivedi.neel.myiiit.network.AuthenticationException;
import com.trivedi.neel.myiiit.network.Network;
import com.trivedi.neel.myiiit.network.NetworkResponse;
import com.trivedi.neel.myiiit.utils.AsyncTaskCallback;
import com.trivedi.neel.myiiit.utils.AsyncTaskResult;
import com.trivedi.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class MessChangeDatewiseAsyncTask extends CallbackAsyncTask<Void, Void, String> {
    private Context mContext;
    private int mMeals;
    private int mMess;
    private Calendar mStartDate, mEndDate;

    MessChangeDatewiseAsyncTask(Context context, Calendar startDate, Calendar endDate, int meals, int mess, AsyncTaskCallback<String> callback) {
        super(callback);

        mContext = context;
        mStartDate =startDate;
        mEndDate = endDate;
        mMeals = meals;
        mMess = mess;
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(Void... voids) {
        String result = "";

        String url = "https://mess.iiit.ac.in/mess/web/student_change_mess_process.php";
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();

        String startdate = dateFormat.format(mStartDate.getTimeInMillis()).toUpperCase();;
        String enddate = dateFormat.format(mEndDate.getTimeInMillis()).toUpperCase();
        Log.d("start date", startdate);
        Log.d("end date", enddate);

        RequestBody body = new FormBody.Builder()
                .add("startdate", startdate)
                .add("enddate", enddate)
                .add("breakfast[]", ((mMeals & Mess.MEAL_BREAKFAST) != 0) ? "1" : "0")
                .add("lunch[]", ((mMeals & Mess.MEAL_LUNCH) != 0) ? "1" : "0")
                .add("dinner[]", ((mMeals & Mess.MEAL_DINNER) != 0) ? "1" : "0")
                .add("mess_name", Integer.toString(mMess))
                .add("Normal", "1").build();

        NetworkResponse response;
        try {
            // Make one request to mess homepage to ensure login
            Network.request(mContext, null, "https://mess.iiit.ac.in/mess/web/index.php");

            response = Network.request(mContext, body, url);
        }  catch (AuthenticationException | IOException e) {
            return new AsyncTaskResult<>(e);
        }

        try {
            result = response.getSoup().getElementsByClass("post").get(1).getElementsByTag("font").get(1).text();
        } catch (Exception e) {
            return new AsyncTaskResult<>(e);
        }

        return new AsyncTaskResult<>(result);
    }
}
