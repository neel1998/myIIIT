package com.example.neel.myiiit;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessCancelFragment extends Fragment {
    String date1, month1, year1, date2, month2, year2 ;
    Spinner date_select1, month_select1, year_select1, date_select2, month_select2, year_select2;
    CheckBox breakfast_box, lunch_box, dinner_box, uncancel_box;
    Button submit_btn;
    TextView cancel_msg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_mess_cancel, container, false);


        date_select1 = rootView.findViewById(R.id.date_select1);
        month_select1 = rootView.findViewById(R.id.month_select1);
        year_select1 = rootView.findViewById(R.id.year_select1);
        date_select2 = rootView.findViewById(R.id.date_select2);
        month_select2 = rootView.findViewById(R.id.month_select2);
        year_select2 = rootView.findViewById(R.id.year_select2);
        cancel_msg = rootView.findViewById(R.id.cancel_msg);

        breakfast_box = rootView.findViewById(R.id.breakfast_box);
        lunch_box = rootView.findViewById(R.id.lunch_box);
        dinner_box = rootView.findViewById(R.id.dinner_box);
        uncancel_box = rootView.findViewById(R.id.uncancel_box);

        submit_btn = rootView.findViewById(R.id.submit_btn);

        String[] dates=new String[31];
        for(int x=1;x<=31;x++){
            dates[x-1]=String.valueOf(x);
        }
        ArrayAdapter<String> dateAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, dates);
        date_select1.setAdapter(dateAdapter);
        date_select2.setAdapter(dateAdapter);
        date_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date1 = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        date_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date2 = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String[] months={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        ArrayAdapter<String> monthAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,months);
        month_select1.setAdapter(monthAdapter);
        month_select2.setAdapter(monthAdapter);
        month_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month1 = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        month_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month2 = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] years=new String[10];
        for(int x=2018;x<=2027;x++){
            years[x-2018]=String.valueOf(x);
        }
        ArrayAdapter<String> yearAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,years);
        year_select1.setAdapter(yearAdapter);
        year_select2.setAdapter(yearAdapter);
        year_select1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year1 = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        year_select2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year2 = (String)parent.getItemAtPosition(position);
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

        return rootView;
    }

    private class MessCancelTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String result = "";

            String url = "https://mess.iiit.ac.in/mess/web/student_cancel_process.php";
            if(date1.length() == 1) {
               date1 = "0" + date1;
            }
            if(date2.length() == 1) {
               date2 = "0" + date2;
            }

            String startdate = date1 + "-" + month1 + "-" + year1;
            String enddate = date2 + "-" + month2 + "-" + year2;


            RequestBody body = new FormBody.Builder()
                  .add("startdate", startdate)
                  .add("enddate", enddate)
                  .add("breakfast[]", (breakfast_box.isChecked())?"1":"0")
                  .add("lunch[]", (lunch_box.isChecked())?"1":"0")
                  .add("dinner[]", (dinner_box.isChecked())?"1":"0")
                  .add("uncancel[]",(uncancel_box.isChecked())?"1":"0")
                  .build();

            Document cancel_soup = Network.makeRequest(getContext(), body, url, false);
            result = cancel_soup.getElementsByClass("post").get(1).getElementsByTag("font").get(0).text();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            cancel_msg.setText(result);
        }
    }
}
