package com.example.neel.myiiit.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.neel.myiiit.LoginActivity;
import com.example.neel.myiiit.network.Client;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {

    public static Document makeRequest(Context context, RequestBody body, String url, boolean login) {

        String base_url = "https://reverseproxy.iiit.ac.in/browse.php?u=";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String credentials = Credentials.basic(
                preferences.getString("username", null),
                preferences.getString("password", null));
        OkHttpClient client = Client.getClient(context);
        Document soup = null;
        boolean intranet = OnIntranet(context);
        if (!login) {
            try {
                if (!intranet) {
                    url = base_url + URLEncoder.encode(url, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {}
        }
        Log.d("url", url);
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

                String result = Login(context);

                if (!result.equals("200")) {
                    Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_LONG).show();
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

    public static String Login(Context context) {
        String base_url = "https://reverseproxy.iiit.ac.in";

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = preferences.getString("username", null);
        String pswd = preferences.getString("password", null);

        String result = "";
        Client.makeNull();
        Document cas_soup;
        if (!Network.OnIntranet(context)) {
            RequestBody body = new FormBody.Builder()
                    .add("u", "login.iiit.ac.in")
                    .add("allowCookies", "on")
                    .build();

            String final_url = "https://reverseproxy.iiit.ac.in/includes/process.php?action=update";

            cas_soup = Network.makeRequest(context, body, final_url, true);
        }
        else {
            base_url = "https://login.iiit.ac.in";
            String final_url = "https://login.iiit.ac.in/cas/login";
            cas_soup = Network.makeRequest(context, null, final_url, true);
        }
        if (cas_soup.title().equals("401 Authorization Required")) {
            result = "401";
            return result;
        }
        Element form = cas_soup.getElementById("fm1");
        String login_url = base_url + form.attr("action");

        Elements fields = form.getElementsByTag("input");

        FormBody.Builder login_builder = new FormBody.Builder();
        for (Element field : fields) {
            if (field.attr("name").equals("username")) {
                login_builder.add(field.attr("name"), username);
            } else if (field.attr("name").equals("password")) {
                login_builder.add(field.attr("name"), pswd);
            } else {
                login_builder.add(field.attr("name"), field.attr("value"));
            }
        }
        RequestBody login_body = login_builder.build();
        Document login_soup = Network.makeRequest(context, login_body, login_url, true);
        result = "200";
        return result;
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


