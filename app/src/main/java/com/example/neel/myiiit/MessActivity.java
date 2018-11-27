package com.example.neel.myiiit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Credentials;
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

//        username = getIntent().getStringExtra("username");
//        pswd = getIntent().getStringExtra("pswd");

        username="neel.trivedi@research.iiit.ac.in";
        pswd="@Neel1998";

        mess_databox = findViewById(R.id.mess_textbox);
        mess_prog = findViewById(R.id.mess_progress);
        mess_prog.setVisibility(View.VISIBLE);

        MessTask messTask = new MessTask();
        messTask.execute();
    }

    private class MessTask extends AsyncTask<Void,Void,String[]>{
        @Override
        protected String[] doInBackground(Void... voids) {
            //fetching CAS url from reverse proxy
//            JSONObject data = new JSONObject();
            try {
//                data.put("u","mess.iiit.ac.in");
//                data.put("encodeURL","on");
//                data.put("allowCookies","on");
//                data.put("stripJS","on");
//                data.put("stripObjects","on");

                RequestBody body = new MultipartBody.Builder()
                                   .setType(MultipartBody.FORM)
                                   .addFormDataPart("u","mess.iiit.ac.in")
                                   .addFormDataPart("encodeURL","on")
                                   .addFormDataPart("allowCookies","on")
                                   .addFormDataPart("stripJS","on")
                                   .addFormDataPart("stripObjects","on")
                                   .build();


                String url_extension="/includes/process.php?action=update";

                //Post request to reverse proxy
                URL final_url= new URL("https://reverseproxy.iiit.ac.in/includes/process.php?action=update");

                String credentials = Credentials.basic(username,pswd);

//                final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client=new OkHttpClient();
//                RequestBody body= RequestBody.create(JSON,data.toString());
                Request request=new Request.Builder()
                        .url(final_url)
                        .post(body)
                        .header("Authorization",credentials)
                        .build();
                Response response=client.newCall(request).execute();

//                Log.d("response",response.body().string());
                Log.d("response",response.toString());
//                Document reverseproxy_soup = Jsoup.parse(response.body().string());
//                Log.d("soup",reverseproxy_soup.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mess_prog.setVisibility(View.GONE);
        }
    }
}
