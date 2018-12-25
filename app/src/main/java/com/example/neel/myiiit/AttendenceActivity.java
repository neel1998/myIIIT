package com.example.neel.myiiit;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AttendenceActivity extends AppCompatActivity {

    ListView attd_listview;
    String username,pswd;
    ProgressBar attd_prog;
    AttendanceAdapter attendanceAdapter;
    String base_url = "https://reverseproxy.iiit.ac.in";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);

        username = getIntent().getStringExtra("username");
        pswd = getIntent().getStringExtra("pswd");


        attendanceAdapter = new AttendanceAdapter(AttendenceActivity.this ,new ArrayList<AttendanceData>());
        attd_listview = findViewById(R.id.attd_list);
        attd_prog = findViewById(R.id.attd_progress);
        attd_prog.setVisibility(View.VISIBLE);

        AttdTask attdTask = new AttdTask();
        attdTask.execute();

    }

    private class AttdTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String credentials = Credentials.basic(username, pswd);
                OkHttpClient client = Client.getClient(AttendenceActivity.this);
                //request body for reverse proxy
                RequestBody body = new FormBody.Builder()
                        .add("u", "moodle.iiit.ac.in")
                        .add("encodeURL", "on")
                        .add("allowCookies", "on")
                        .add("stripJS", "on")
                        .add("stripObjects", "on")
                        .build();

                //Post request to reverse proxy
                URL final_url = new URL("https://reverseproxy.iiit.ac.in/includes/process.php?action=update");

                Request reverse_request = new Request.Builder()
                        .url(final_url)
                        .post(body)
                        .header("Authorization", credentials)
                        .build();

                Response reverse_response = client.newCall(reverse_request).execute();

                Document reverseproxy_soup = Jsoup.parse(reverse_response.body().string());
                String moodle_url = base_url + reverseproxy_soup.getElementsByClass("login").get(0).getElementsByTag("a").get(0).attr("href");

                //Request for cas
//                Request cas_request = new Request.Builder()
//                        .url(cas_url)
//                        .header("Authorization", credentials)
//                        .build();
//                Response cas_response = client.newCall(cas_request).execute();
//                Document cas_soup = Jsoup.parse(cas_response.body().string());
////
////                //cas login form
//                Element form = cas_soup.getElementById("fm1");
////
//                String moodle_url = base_url + form.attr("action");
////
////                //request body for cas login
//                Elements fields = form.getElementsByTag("input");
//                FormBody.Builder moodle_builder = new FormBody.Builder();
//                for (Element field : fields) {
//                    if (field.attr("name").equals("username")) {
//                        moodle_builder.add(field.attr("name"), username);
//                    } else if (field.attr("name").equals("password")) {
//                        moodle_builder.add(field.attr("name"), pswd);
//                    } else {
//                        moodle_builder.add(field.attr("name"), field.attr("value"));
//                    }
//                }
//                RequestBody moodle_body = moodle_builder.build();

                Request moodle_request = new Request.Builder()
                        .url(moodle_url)
                        .header("Authorization", credentials)
                        .build();
                Response moodle_response = client.newCall(moodle_request).execute();

                Document moodle_soup = Jsoup.parse(moodle_response.body().string());
                Log.d("moodle soup", moodle_soup.getElementsByTag("body").toString());
                //getting list of current courses
//                Elements current_courses = moodle_soup.getElementsByClass("course_title");
//                ArrayList<String> current_course_list = new ArrayList<>();
//                for ( Element course : current_courses ){
//                    current_course_list.add(course.text());
//                }
//                Log.d("course list", current_course_list.toString());
//                //course request
//                String course_url = base_url + moodle_soup.getElementsByClass("course_list").get(0).getElementsByClass("title").get(0).getElementsByTag("a").get(0).attr("href");
//                Request course_request = new Request.Builder()
//                        .url(course_url)
//                        .header("Authorization", credentials)
//                        .build();
//                Response course_response = client.newCall(course_request).execute();
//                Document course_soup = Jsoup.parse(course_response.body().string());
//
//                //single attendence request
//                String sattd_url = base_url + course_soup.getElementsByClass("activityinstance").get(1).getElementsByTag("a").get(0).attr("href");
//                Request singleAttendence_request = new Request.Builder()
//                        .url(sattd_url)
//                        .header("Authorization", credentials)
//                        .build();
//                Response singleAttendence_response = client.newCall(singleAttendence_request).execute();
//                Document singleAttendence_soup = Jsoup.parse(singleAttendence_response.body().string());
//
//                //All course Attendance request
//                String allattd_url = base_url + singleAttendence_soup.getElementsByClass("nav nav-tabs").get(0).getElementsByTag("li").get(1).getElementsByTag("a").get(0).attr("href");
//                Request allAttendance_request = new Request.Builder()
//                        .url(allattd_url)
//                        .header("Authorization", credentials)
//                        .build();
//                Response allAttendance_response = client.newCall(allAttendance_request).execute();
//                Document allAttendance_soup = Jsoup.parse(allAttendance_response.body().string());
//                //list of all course title and table
//                Elements course_titles = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("h3");
//
//                Elements course_tables = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("table");
//
//                //adding object of attendance data to adapter
//                int i = 0;
//                for ( Element table : course_tables ){
//
//                    String course_name = course_titles.get(i).text();
//                    if ( current_course_list.contains(course_name) ) {
//                        String session_completed = table.getElementsByClass("cell c1 lastcol").get(0).text();
//                        String session_present = table.getElementsByClass("cell c1 lastcol").get(1).text();
//                        attendanceAdapter.add(new AttendanceData(course_name, session_completed, session_present));
//                    }
//                    i++;
//                }
            }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            attd_prog.setVisibility(View.GONE);
//            attd_listview.setAdapter(attendanceAdapter);
        }
    }
}
