package com.example.neel.myiiit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MessActivity extends AppCompatActivity {


    String username,pswd;
    TextView mess_databox;
    ProgressBar mess_prog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess);

        username = getIntent().getStringExtra("username");
        pswd = getIntent().getStringExtra("pswd");
        mess_databox = findViewById(R.id.mess_textbox);
        mess_prog = findViewById(R.id.mess_progress);
        mess_prog.setVisibility(View.VISIBLE);
    }

    private class MessTask extends AsyncTask<Void,Void,String[]>{
        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mess_prog.setVisibility(View.GONE);
        }
    }
}
