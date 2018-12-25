package com.example.neel.myiiit;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessActivity extends AppCompatActivity {

    String username,pswd;
    TextView mess_databox;
    ProgressBar mess_prog;
    String base_url = "https://reverseproxy.iiit.ac.in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        username = getIntent().getStringExtra("username");
        pswd = getIntent().getStringExtra("pswd");

        mess_databox = findViewById(R.id.mess_textbox);
        mess_prog = findViewById(R.id.mess_progress);
        mess_prog.setVisibility(View.VISIBLE);

        MessTask messTask = new MessTask();
        messTask.execute();
    }

    private class MessTask extends AsyncTask<Void,Void,String[]>{
        @Override
        protected String[] doInBackground(Void... voids) {

            String[] result = new String [3];
            try {
//                
//                RequestBody body = new FormBody.Builder()
//                                   .add("u","mess.iiit.ac.in")
////                                   .add("encodeURL","on")
//                                   .add("allowCookies","on")
////                                   .add("stripJS","on")
//                                   .add("stripObjects","on")
//                                   .build();

                //Post request to reverse proxy
//                URL final_url = new URL("https://reverseproxy.iiit.ac.in/includes/process.php?action=update");




//                ClearableCookieJar cookieJar =
//                        new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MessActivity.this));
//
//                OkHttpClient client = new OkHttpClient.Builder().cookieJar(cookieJar).build();


//                Request request = new Request.Builder()
//                        .url(final_url)
//                        .post(body)
//                        .header("Authorization", credentials)
//                        .build();

//                Response response = client.newCall(request).execute();

//                final Document soup = Jsoup.parse(response.body().string());

//                String url = base_url + soup.selectFirst("meta[http-equiv=REFRESH]").attr("content").replace("0;url=", "");
                String credentials = Credentials.basic(username, pswd);
                OkHttpClient client = Client.getClient(MessActivity.this);
                String url = "https://reverseproxy.iiit.ac.in/browse.php?u=https%3A%2F%2Fmess.iiit.ac.in%2Fmess%2Fweb%2Findex.php&b=20";

                Log.d("mess url", url);
                Request request2 = new Request.Builder()
                        .url(url)
                        .header("Authorization", credentials)
                        .build();

                Response response2 = client.newCall(request2).execute();

                final Document mess_soup = Jsoup.parse(response2.body().string());


                Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");

                result[0] = meals.get(5).getElementsByTag("td").get(1).text();
                result[1] = meals.get(6).getElementsByTag("td").get(1).text();
                result[2] = meals.get(7).getElementsByTag("td").get(1).text();

//                Thread thread = new Thread(){
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mess_databox.setText(mess_soup.toString());
//                            }
//                        });
//                    }
//                };
//                thread.start();


//                Element div = mess_soup.getElementById("content");
//                Log.d("div", div.toString());

//                Element form = mess_soup.getElementById("fm1");
//                Log.d("form", form.toString());
//                Log.d("mess soup", mess_soup.toString());

//                Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");

//                Log.d("meals", meals.toString());
//                Document reverseproxy_soup = Jsoup.parse(response.body().string());
//
//                String cas_url = base_url + reverseproxy_soup.selectFirst("meta[http-equiv=REFRESH]").attr("content").replace("0;url=", "");
//
//                Request cas_request = new Request.Builder()
//                        .url(cas_url)
//                        .header("Authorization", credentials)
//                        .build();
//                Response cas_response = client.newCall(cas_request).execute();
//                Document cas_soup = Jsoup.parse(cas_response.body().string());
//
//
//                Element form = cas_soup.getElementById("fm1");
//
//                String mess_url = base_url + form.attr("action");
//
//                Elements fields = form.getElementsByTag("input");
//
//                FormBody.Builder mess_builder = new FormBody.Builder();
//                for ( Element field:  fields ){
//                    if (field.attr("name").equals("username")) {
//                        mess_builder.add(field.attr("name"), username);
//                    }
//                    else if (field.attr("name").equals("password")) {
//                        mess_builder.add(field.attr("name"), pswd);
//                    }
//                    else {
//                        mess_builder.add(field.attr("name"), field.attr("value"));
//                    }
//                }
//                RequestBody mess_body = mess_builder.build();
//
//                Request mess_request = new Request.Builder()
//                        .url(mess_url)
//                        .post(mess_body)
//                        .header("Authorization", credentials)
//                        .build();
//                Response mess_response = client.newCall(mess_request).execute();
//
//
//                Document mess_soup = Jsoup.parse( mess_response.body().string());
//
//                Elements meals = mess_soup.getElementById("content").getElementsByTag("tr");
//
//                result[0] = meals.get(5).getElementsByTag("td").get(1).text();
//                result[1] = meals.get(6).getElementsByTag("td").get(1).text();
//                result[2] = meals.get(7).getElementsByTag("td").get(1).text();

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
