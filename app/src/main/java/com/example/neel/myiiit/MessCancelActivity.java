package com.example.neel.myiiit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessCancelActivity extends AppCompatActivity {

    String username, pswd, date1, month1, year1, date2, month2, year2 ;
    Spinner date_select1, month_select1, year_select1, date_select2, month_select2, year_select2;
    CheckBox breakfast_box, lunch_box, dinner_box;
    Button submit_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mess_cancel);
        username = getIntent().getStringExtra("username");
        pswd = getIntent().getStringExtra("pswd");

        date_select1 = findViewById(R.id.date_select1);
        month_select1 = findViewById(R.id.month_select1);
        year_select1 = findViewById(R.id.year_select1);
        date_select2 = findViewById(R.id.date_select2);
        month_select2 = findViewById(R.id.month_select2);
        year_select2 = findViewById(R.id.year_select2);

        breakfast_box = findViewById(R.id.breakfast_box);
        lunch_box = findViewById(R.id.lunch_box);
        dinner_box = findViewById(R.id.dinner_box);

        submit_btn = findViewById(R.id.submit_btn);

        String[] dates=new String[31];
        for(int x=1;x<=31;x++){
            dates[x-1]=String.valueOf(x);
        }
        ArrayAdapter<String> dateAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, dates);
        date_select1.setAdapter(dateAdapter);
        date_select2.setAdapter(dateAdapter);
        date_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date1 = (String) parent.getItemAtPosition(position);
                Log.d("date1", date1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        date_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date2 = (String) parent.getItemAtPosition(position);
                Log.d("date2", date2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] months={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,months);
        month_select1.setAdapter(monthAdapter);
        month_select2.setAdapter(monthAdapter);
        month_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month1 = (String)parent.getItemAtPosition(position);
                Log.d("month1", month1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        month_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month2 = (String)parent.getItemAtPosition(position);
                Log.d("month2", month2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] years=new String[10];
        for(int x=2018;x<=2027;x++){
            years[x-2018]=String.valueOf(x);
        }
        ArrayAdapter<String> yearAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,years);
        year_select1.setAdapter(yearAdapter);
        year_select2.setAdapter(yearAdapter);
        year_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year1 = (String)parent.getItemAtPosition(position);
                Log.d("year1", year1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        year_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year2 = (String)parent.getItemAtPosition(position);
                Log.d("year2", year2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessCancelTask messCancelTask = new MessCancelTask();
                messCancelTask.execute();
            }
        });
    }

    public class MessCancelTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";
            try{
                String credentials = Credentials.basic(username, pswd);
                OkHttpClient client = Client.getClient(MessCancelActivity.this);
                String url = "https://reverseproxy.iiit.ac.in/browse.php?u=https%3A%2F%2Fmess.iiit.ac.in%2Fmess%2Fweb%2Fstudent_cancel_process.php&b=4";

                if(date1.length() == 1) {
                    date1 = "0" + date1;
                }
                if(date2.length() == 1) {
                    date2 = "0" + date2;
                }

                String startdate = date1 + "-" + month1 + "-" + year1;
                String enddate = date2 + "-" + month2 + "-" + year2;

                Log.d("start date", startdate);
                Log.d("end date", enddate);

                RequestBody body = new FormBody.Builder()
                        .add("startdate", startdate)
                        .add("enddate", enddate)
                        .add("breakfast[]", (breakfast_box.isChecked())?"1":"0")
                        .add("lunch[]", (dinner_box.isChecked())?"1":"0")
                        .add("dinner[]", (dinner_box.isChecked())?"1":"0")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Authorization", credentials)
                        .build();

                Response response = client.newCall(request).execute();

                Log.d("response", response.body().string());

            }catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
