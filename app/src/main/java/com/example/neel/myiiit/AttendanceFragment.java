package com.example.neel.myiiit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttendanceFragment extends Fragment {

    String base_url = "https://reverseproxy.iiit.ac.in";
    ListView attd_listview;
    ProgressBar attd_prog;
    AttendanceAdapter attendanceAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendence, container, false);

        attendanceAdapter = new AttendanceAdapter(getContext() ,new ArrayList<AttendanceData>());
        attd_listview = rootView.findViewById(R.id.attd_list);
        attd_prog = rootView.findViewById(R.id.attd_progress);
        attd_prog.setVisibility(View.VISIBLE);
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        AttendanceTask attendanceTask = new AttendanceTask();
        attendanceTask.execute();
    }

    private class AttendanceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Context context = getContext();

            String home_url = "https://moodle.iiit.ac.in/login/index.php?authCAS=CAS";
            Document home_soup = Network.makeRequest(context, null, home_url, false);

            String course_url = "https://moodle.iiit.ac.in/?redirect=0";
            Document course_soup = Network.makeRequest(context, null, course_url, false);

            String single_url = course_soup.getElementById("frontpage-course-list").getElementsByTag("a").get(0).attr("href");
            if (single_url.contains("u=")){
                single_url = single_url.split("u=")[1];
            }
            Document single_soup = Network.makeRequest(context, null, single_url, false);

            String attendance_url = single_soup.getElementsByClass("mod-indent-outer").get(1).getElementsByTag("a").get(0).attr("href");
            if (attendance_url.contains("u=")) {
                attendance_url = attendance_url.split("u=")[1];
            }
            String allattd_url;
            if (attendance_url.contains("&")){
                allattd_url = attendance_url.split("&")[0] + "&mode=1";
            }else{
                allattd_url = attendance_url + "&mode=1";
            }

            Document allAttendance_soup = Network.makeRequest(context, null, allattd_url, false);
//
//            //list of all course title and table
            Elements course_titles = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("h3");
            Elements course_tables = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("table");

            //adding object of attendance data to adapter
            int i = 0;
            for ( Element table : course_tables ){

                String course_name = course_titles.get(i).text();
                String session_completed = table.getElementsByClass("cell c1 lastcol").get(0).text();
                String session_present = table.getElementsByClass("cell c1 lastcol").get(1).text();
                attendanceAdapter.add(new AttendanceData(course_name, session_completed, session_present));
                i++;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            attd_prog.setVisibility(View.GONE);
            attd_listview.setAdapter(attendanceAdapter);
        }
    }

}
