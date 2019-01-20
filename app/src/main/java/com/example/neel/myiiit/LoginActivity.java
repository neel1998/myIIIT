package com.example.neel.myiiit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.network.Client;
import com.example.neel.myiiit.network.Network;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    EditText username_box,pswd_box;
    Button login_btn;
    static String username, pswd;
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
            result = Network.Login(LoginActivity.this);
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }


}
