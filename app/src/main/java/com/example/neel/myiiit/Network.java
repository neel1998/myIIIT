package com.example.neel.myiiit;

import android.content.Context;
import android.content.Intent;
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

        String credentials = Credentials.basic(
                PreferenceManager.getDefaultSharedPreferences(context).getString("username", null),
                PreferenceManager.getDefaultSharedPreferences(context).getString("password", null));
        OkHttpClient client = Client.getClient(context);
        Document soup = null;
        Request request;
        if (body != null) {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", credentials)
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(url)
                    .header("Authorization", credentials)
                    .build();
        }
        try {
            Response response = client.newCall(request).execute();
            soup = Jsoup.parse(response.body().string());
            if (soup.title().equals(context.getString(R.string.cas_title)) || soup.title().equals(context.getString(R.string.rev_title))) {

                String result = LoginActivity.Login(context);

                if (!result.equals("200")) {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
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


