package com.example.neel.myiiit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    public static Document makeRequest(Context context, RequestBody body, String url){


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String credentials = Credentials.basic(
                preferences.getString("username", null),
                preferences.getString("password", null));
        OkHttpClient client = Client.getClient(context);
        Document soup = null;

        Request.Builder builder = new Request.Builder()
                .url(url)
                .header("Authorization", credentials);

        if (body != null) {
            builder.post(body);
        }
        
        Request request = builder.build();
        try {
            Response response = client.newCall(request).execute();
            soup = Jsoup.parse(response.body().string());
            if (soup.title().equals("Central Authentication Service - IIIT Hyderabad") || soup.title().equals("reverseproxy.iiit.ac.in Glype® proxy")) {

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
        return soup;
    }
}


