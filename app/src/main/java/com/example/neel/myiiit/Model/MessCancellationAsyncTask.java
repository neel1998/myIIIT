package com.example.neel.myiiit.Model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.neel.myiiit.Network;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.nodes.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MessCancellationAsyncTask extends CallbackAsyncTask<Void, Void, String>  {
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
                .add("breakfast[]", ((mMeals & Mess.MEAL_BREAKFAST) != 0)?"1":"0")
                .add("lunch[]", ((mMeals & Mess.MEAL_LUNCH) != 0)?"1":"0")
                .add("dinner[]", ((mMeals & Mess.MEAL_DINNER) != 0)?"1":"0")
                .add("uncancel[]",(mUncancel)?"1":"0")
                .build();

        Document cancel_soup = Network.makeRequest(mContext, body, url, false);
        result = cancel_soup.getElementsByClass("post").get(1).getElementsByTag("font").get(0).text();
        return new AsyncTaskResult<>(result);
    }
}
