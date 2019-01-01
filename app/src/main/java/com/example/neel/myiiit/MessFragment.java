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
    String username,pswd;
    TextView mess_databox;
    ProgressBar mess_prog;
    SharedPreferences preferences;
    MessTask messTask;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.activity_mess, container, false);

            preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            username = preferences.getString("username", null);
            pswd = preferences.getString("password", null);

            mess_databox = rootView.findViewById(R.id.mess_textbox);
            mess_prog = rootView.findViewById(R.id.mess_progress);
            mess_prog.setVisibility(View.VISIBLE);

            Log.d("Mess Fragment", "Created");

            return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        messTask = new MessTask();
        messTask.execute();
    }

    private class MessTask extends AsyncTask<Void,Void,String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {

            String[] result = new String [3];
            try {
                String credentials = Credentials.basic(username, pswd);
                OkHttpClient client = Client.getClient(getContext());
                String url = "https://reverseproxy.iiit.ac.in/browse.php?u=https%3A%2F%2Fmess.iiit.ac.in%2Fmess%2Fweb%2Findex.php&b=20";

                Log.d("mess url", url);
                Request request2 = new Request.Builder()
                        .url(url)
                        .header("Authorization", credentials)
                        .build();

                Response response2 = client.newCall(request2).execute();

                Document mess_soup = Jsoup.parse(response2.body().string());

                if (mess_soup.title().equals(getString(R.string.cas_title)) || mess_soup.title().equals(getString(R.string.rev_title)) ) {
                    String r =  LoginActivity.Login(getContext());
                    Log.d("login result", r);

                    client = Client.getClient(getContext());
                    response2 = client.newCall(request2).execute();
                    mess_soup = Jsoup.parse(response2.body().string());
                }

                Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");

                result[0] = meals.get(5).getElementsByTag("td").get(1).text();
                result[1] = meals.get(6).getElementsByTag("td").get(1).text();
                result[2] = meals.get(7).getElementsByTag("td").get(1).text();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
