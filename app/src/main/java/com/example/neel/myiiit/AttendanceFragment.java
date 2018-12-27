package com.example.neel.myiiit;

import android.os.AsyncTask;
import android.os.Bundle;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AttendanceFragment extends Fragment {

    String username, pswd;
    String base_url = "https://reverseproxy.iiit.ac.in";
    ListView attd_listview;
    ProgressBar attd_prog;
    AttendanceAdapter attendanceAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendence, container, false);
        username = CredentialsClass.getUsername();
        pswd = CredentialsClass.getPswd();


        attendanceAdapter = new AttendanceAdapter(getContext() ,new ArrayList<AttendanceData>());
        attd_listview = rootView.findViewById(R.id.attd_list);
        attd_prog = rootView.findViewById(R.id.attd_progress);
        attd_prog.setVisibility(View.VISIBLE);

        AttendanceTask attendanceTask = new AttendanceTask();
        attendanceTask.execute();
        return  rootView;
    }

    private class AttendanceTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                OkHttpClient client = Client.getClient(getContext());
                String credentials = Credentials.basic(username, pswd);

                URL home_url = new URL("https://reverseproxy.iiit.ac.in/browse.php?u=https%3A%2F%2Fmoodle.iiit.ac.in%2Fmy%2F&b=4");

                Request home_request = new Request.Builder()
                        .url(home_url)
                        .header("Authorization", credentials)
                        .build();
                Response home_response = client.newCall(home_request).execute();

                URL course_url = new URL("https://reverseproxy.iiit.ac.in//browse.php?u=https%3A%2F%2Fmoodle.iiit.ac.in%2F%3Fredirect%3D0&amp;b=4");
                Request course_request = new Request.Builder()
                        .url(course_url)
                        .header("Authorization", credentials)
                        .build();
                Response course_response = client.newCall(course_request).execute();

                Document course_soup = Jsoup.parse(course_response.body().string());

                String single_url = base_url + course_soup.getElementById("frontpage-course-list").getElementsByTag("a").get(0).attr("href");

                Request single_request = new Request.Builder()
                        .url(single_url)
                        .header("Authorization", credentials)
                        .build();
                Response single_response = client.newCall(single_request).execute();
                Document single_soup = Jsoup.parse(single_response.body().string());


                String attendance_url = base_url + single_soup.getElementsByClass("mod-indent-outer").get(1).getElementsByTag("a").get(0).attr("href");


                String allattd_url = attendance_url.split("&")[0] + "%26mode%3D1&" + attendance_url.split("&")[1];

                Request allAttendance_request = new Request.Builder()
                        .url(allattd_url)
                        .header("Authorization", credentials)
                        .build();
                Response allAttendance_response = client.newCall(allAttendance_request).execute();
                Document allAttendance_soup = Jsoup.parse(allAttendance_response.body().string());
                //list of all course title and table
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
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
