package com.example.neel.myiiit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessFragment extends Fragment {
    TextView mess_databox, mess_last_update;
    ProgressBar mess_prog;
    SharedPreferences preferences;
    String date_cur, date_stor;
    JSONObject jsonObject;
    SwipeRefreshLayout pullToRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_mess, container, false);

        mess_last_update = rootView.findViewById(R.id.mess_last_update);
        mess_databox = rootView.findViewById(R.id.mess_textbox);
        mess_prog = rootView.findViewById(R.id.mess_progress);
        pullToRefresh = rootView.findViewById(R.id.mess_pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            mess_prog.setVisibility(View.VISIBLE);
            MessTask messTask = new MessTask();
            messTask.execute();
            pullToRefresh.setRefreshing(false);
            }
        });


        jsonObject = new JSONObject();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date_cur = dateFormat.format(new Date());
        date_stor = preferences.getString("meals_date", null);
        mess_last_update.setText("Last Updated : " + date_stor);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mess_databox.clearComposingText();
        if (date_stor != null) {
            try {
                JSONObject meals = new JSONObject(preferences.getString("meals", null));
                mess_databox.setText(meals.getString("meals"));
            } catch (JSONException e) {
                mess_prog.setVisibility(View.VISIBLE);
                MessTask messTask = new MessTask();
                messTask.execute();
                e.printStackTrace();
            }
        }
        if (date_stor == null || !date_stor.equals(date_cur)){
            mess_prog.setVisibility(View.VISIBLE);
            MessTask messTask = new MessTask();
            messTask.execute();
        }
    }

    private class MessTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {

            String[] meal = new String [3];
            String result = "";

            try {
                String url = "https://mess.iiit.ac.in/mess/web/index.php";
                Document mess_soup = Network.makeRequest(getContext(), null, url, false);
                Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");

                meal[0] = meals.get(5).getElementsByTag("td").get(1).text();
                meal[1] = meals.get(6).getElementsByTag("td").get(1).text();
                meal[2] = meals.get(7).getElementsByTag("td").get(1).text();
                result = "Breakfast: " + meal[0] + "\n"
                        + "Lunch: " + meal[1] + "\n"
                        + "Dinner: " + meal[2] + "\n";


            }
            catch (Exception e){
                result = "Connection Error";
            }
            try {
                jsonObject.put("meals", result);
            } catch (JSONException e) {}
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("meals_date", date_cur);
            editor.putString("meals", jsonObject.toString());
            editor.commit();
            mess_last_update.setText("Last Updated : " + date_cur);
            mess_prog.setVisibility(View.GONE);
            mess_databox.setText(result);
        }
    }

}
