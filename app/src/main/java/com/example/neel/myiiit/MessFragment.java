package com.example.neel.myiiit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessFragment extends Fragment {
    TextView mess_databox;
    ProgressBar mess_prog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_mess, container, false);

            mess_databox = rootView.findViewById(R.id.mess_textbox);
            mess_prog = rootView.findViewById(R.id.mess_progress);
            mess_prog.setVisibility(View.VISIBLE);

            return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        MessTask messTask = new MessTask();
        messTask.execute();
    }

    private class MessTask extends AsyncTask<Void,Void,String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {

            String[] result = new String [3];

            String rev_url = "https://reverseproxy.iiit.ac.in/browse.php?u=https%3A%2F%2Fmess.iiit.ac.in%2Fmess%2Fweb%2Findex.php&b=20";
            String intra_url = "https://mess.iiit.ac.in/mess/web/index.php";

            Document mess_soup = Network.makeRequest(getContext(), null, rev_url, intra_url, false);
            Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");

            result[0] = meals.get(5).getElementsByTag("td").get(1).text();
            result[1] = meals.get(6).getElementsByTag("td").get(1).text();
            result[2] = meals.get(7).getElementsByTag("td").get(1).text();

            return result;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mess_prog.setVisibility(View.GONE);
            String result = "Breakfast: " + strings[0] + "\n"
                    + "Lunch: " + strings[1] + "\n"
                    + "Dinner: " + strings[2] + "\n";
            mess_databox.setText(result);
        }

    }

}
