package com.example.neel.myiiit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText username_box,pswd_box;
    Button login_btn;
    static String username, pswd;
    static String base_url = "https://reverseproxy.iiit.ac.in";
    TextView username_err, pswd_err;
    ProgressBar login_prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = PreferenceManager.getDefaultSharedPreferences(this).getString("username", null);
        pswd = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);

        if (username != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        username_box = findViewById(R.id.username_box);
        pswd_box = findViewById(R.id.pswd_box);
        login_btn = findViewById(R.id.login_btn);
        login_prog = findViewById(R.id.login_prog);

        username_err = findViewById(R.id.username_err);
        pswd_err = findViewById(R.id.pswd_err);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username_err.setVisibility(View.GONE);
                pswd_err.setVisibility(View.GONE);
                username = username_box.getText().toString();
                pswd = pswd_box.getText().toString();

                if (username.equals("")) {
                    username_err.setVisibility(View.VISIBLE);
                }
                if (pswd.equals("")) {
                    pswd_err.setText("Please Enter Password");
                    pswd_err.setVisibility(View.VISIBLE);
                }
                if ( !username.equals("") && !pswd.equals("")) {
                    login_prog.setVisibility(View.VISIBLE);
                    login_btn.setVisibility(View.GONE);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", pswd);
                    editor.commit();

                    LoginTask loginTask = new LoginTask();
                    loginTask.execute();
                }
            }
        });
    }
    public class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            result = Login(LoginActivity.this);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            login_prog.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
            if (result.equals("401")){
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pswd_err.setText("Invalid Credentials");
                                pswd_err.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                };
                thread.start();
            }
            else {
                CredentialsClass credentialsClass = new CredentialsClass(username, pswd);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public static String Login(Context context) {
       String result = "";
       Client.makeNull();
       Document cas_soup;
       if (!Network.OnIntranet(context)) {
           RequestBody body = new FormBody.Builder()
                   .add("u", "login.iiit.ac.in")
                   .add("allowCookies", "on")
                   .build();

           String final_url = "https://reverseproxy.iiit.ac.in/includes/process.php?action=update";

           cas_soup = Network.makeRequest(context, body, final_url, final_url, true);
       }
       else {
           base_url = "https://login.iiit.ac.in";
           String final_url = "https://login.iiit.ac.in/cas/login";
           cas_soup = Network.makeRequest(context, null, final_url, final_url, true);
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
       Document login_soup = Network.makeRequest(context, login_body, login_url, login_url, true);
       result = "200";
       return result;
    }
}
