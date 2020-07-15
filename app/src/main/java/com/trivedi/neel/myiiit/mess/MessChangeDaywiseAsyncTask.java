package com.trivedi.neel.myiiit.mess;

import android.content.Context;
import android.util.Log;

import com.trivedi.neel.myiiit.network.AuthenticationException;
import com.trivedi.neel.myiiit.network.Network;
import com.trivedi.neel.myiiit.network.NetworkResponse;
import com.trivedi.neel.myiiit.utils.AsyncTaskCallback;
import com.trivedi.neel.myiiit.utils.AsyncTaskResult;
import com.trivedi.neel.myiiit.utils.CallbackAsyncTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MessChangeDaywiseAsyncTask extends CallbackAsyncTask<Void, Void, String> {
    private Context mContext;
    private String mDay;
    private int mMess, mMeal;

    MessChangeDaywiseAsyncTask(Context context, String day, int meal, int mess, AsyncTaskCallback<String> callback) {
        super(callback);

        mContext = context;
        mDay = day;
        mMeal = meal;
        mMess = mess;
    }

    @Override
    protected AsyncTaskResult<String> doInBackground(Void... voids) {
        String result = "";

        String url = "https://mess.iiit.ac.in/mess/web/student_change_mess_process.php";

        RequestBody body = new FormBody.Builder()
                .add("day", mDay.substring(0,3))
                .add("meal_name", Integer.toString(mMeal))
                .add("mess_name", Integer.toString(mMess))
                .add("Daily", "1").build();

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
