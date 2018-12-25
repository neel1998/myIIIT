package com.example.neel.myiiit;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    String username, pswd;
    String base_url = "https://reverseproxy.iiit.ac.in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        username_box=findViewById(R.id.username_box);
        pswd_box=findViewById(R.id.pswd_box);
        login_btn=findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=username_box.getText().toString();
                pswd=pswd_box.getText().toString();
                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
//                startActivity(intent);
                LoginTask loginTask = new LoginTask();
                loginTask.execute();
            }
        });
    }
    public class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Client.makeNull();
                OkHttpClient client = Client.getClient(LoginActivity.this);

                RequestBody body = new FormBody.Builder()
                        .add("u", "login.iiit.ac.in")
//                        .add("encodeURL", "on")
                        .add("allowCookies", "on")
//                        .add("stripJS", "on")
                        .add("stripObjects", "on")
                        .build();

                URL final_url = new URL("https://reverseproxy.iiit.ac.in/includes/process.php?action=update");
                String credentials = Credentials.basic(username, pswd);

                Request request = new Request.Builder()
                        .url(final_url)
                        .post(body)
                        .header("Authorization", credentials)
                        .build();

                Response response = client.newCall(request).execute();
                Document cas_soup = Jsoup.parse(response.body().string());

                Element form = cas_soup.getElementById("fm1");
                String login_url = base_url + form.attr("action");

                Elements fields = form.getElementsByTag("input");

                FormBody.Builder login_builder = new FormBody.Builder();
                for ( Element field:  fields ){
                    if (field.attr("name").equals("username")) {
                        login_builder.add(field.attr("name"), username);
                    }
                    else if (field.attr("name").equals("password")) {
                        login_builder.add(field.attr("name"), pswd);
                    }
                    else {
                        login_builder.add(field.attr("name"), field.attr("value"));
                    }
                }
                RequestBody login_body = login_builder.build();

                Request login_request = new Request.Builder()
                        .url(login_url)
                        .post(login_body)
                        .header("Authorization", credentials)
                        .build();

                Response login_response = client.newCall(login_request).execute();

                Document login_soup = Jsoup.parse( login_response.body().string());

                Log.d("soup", login_soup.toString());

                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("pswd",pswd);
                startActivity(intent);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
