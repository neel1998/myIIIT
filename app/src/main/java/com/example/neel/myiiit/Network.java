package com.example.neel.myiiit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    public static Document makeRequest(Context context, RequestBody body, String rev_url, String intra_url, boolean login){


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String credentials = Credentials.basic(
                preferences.getString("username", null),
                preferences.getString("password", null));
        OkHttpClient client = Client.getClient(context);
        Document soup = null;
        String url;
        boolean intranet = OnIntranet(context);
//        boolean intranet = true;
        if (intranet) {
            url = intra_url;
        }
        else {
            url = rev_url;
        }
//        Log.d("url", url);
        Request.Builder builder = new Request.Builder()
                .url(url);

        if (body != null) {
            builder.post(body);
        }
        if (!intranet) {
            builder.header("Authorization", credentials);
        }
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            soup = Jsoup.parse(response.body().string());
            if ( (soup.title().equals("Central Authentication Service - IIIT Hyderabad")
                    || soup.title().equals("reverseproxy.iiit.ac.in Glype® proxy"))
                    && !login ) {

                String result = LoginActivity.Login(context);

                if (!result.equals("200")) {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(context, LoginActivity.class);
//                    context.startActivity(intent);
                }
                client = Client.getClient(context);
                response = client.newCall(request).execute();
                soup = Jsoup.parse(response.body().string());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(url, soup.toString());
        return soup;
    }

    public static boolean OnIntranet(Context context) {
        boolean result = false;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://reverseproxy.iiit.ac.in/")
                    .build();
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.d("error", "occured");
            result = true;
        }
        return  result;
    }
}


