package com.example.neel.myiiit.attendance;

import android.content.Context;
import android.util.Log;

import com.example.neel.myiiit.network.AuthenticationException;
import com.example.neel.myiiit.network.Network;
import com.example.neel.myiiit.network.NetworkResponse;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

class AttendanceTask extends CallbackAsyncTask<Void, Void, List<AttendanceData>> {
    private Context mContext;

    AttendanceTask(Context context, AsyncTaskCallback<List<AttendanceData>> callback) {
        super(callback);
        mContext = context;
    }

    @Override
    protected AsyncTaskResult<List<AttendanceData>> doInBackground(Void... voids) {
        ArrayList<AttendanceData> result = new ArrayList<>();
        Context context = mContext;
        ArrayList<String> current_courses = new ArrayList<>();

        NetworkResponse response = null;

        try {
            String home_url = "https://moodle.iiit.ac.in/login/index.php?authCAS=CAS";
            response = Network.request(context, null, home_url);
            try {
                Elements course_list = response.getSoup().getElementsByClass("course_title");
                for (Element element : course_list) {
                    current_courses.add(element.text());
                }
            }catch (Exception e){}

            String course_url = "https://moodle.iiit.ac.in/?redirect=0";
            response = Network.request(context, null, course_url);

            URI baseUrl = response.getResponse().request().url().uri();

            Elements courses = response.getSoup().getElementById("frontpage-course-list").getElementsByTag("a");

            String attendance_url = "";
            boolean found = false;
            for (Element course : courses) {
                String single_url = course.attr("href");
                single_url = baseUrl.resolve(single_url).toString();
                response = Network.request(context, null, single_url);
                try {
                    Elements course_class = response.getSoup().getElementsByClass("mod-indent-outer");
                    for (Element element : course_class) {
                        if (element.text().equals("Attendance")){
                           attendance_url =  element.getElementsByTag("a").get(0).attr("href");
                           found = true;
                           break;
                        }
                    }
                    if (found) {
                        break;
                    }
                } catch (Exception e) {
                }
            }

            attendance_url = baseUrl.resolve(attendance_url).toString();

            HttpUrl url = HttpUrl.parse(attendance_url);
            String allattd_url;
            if (url.host().equals("reverseproxy.iiit.ac.in")){
                String orignalUrl = url.queryParameter("u") + "&mode=1";
                allattd_url = url.newBuilder().setQueryParameter("u", orignalUrl).build().toString();
            }else{
                allattd_url = attendance_url + "&mode=1";
            }

            response = Network.request(context, null, allattd_url);
        } catch (AuthenticationException|IOException e) {
            e.printStackTrace();
            return new AsyncTaskResult<>(e);
        }

        Elements course_titles = response.getSoup().getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("h3");
        Elements course_tables = response.getSoup().getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("table");

        //adding object of attendance data to adapter
        int i = 0;
        for ( Element table : course_tables ){
            String course_name = course_titles.get(i).text();
            Integer session_completed;
            Integer session_present;
            try {
                session_completed = Integer.parseInt(table.getElementsByClass("cell c1 lastcol").get(0).text());
                session_present =  Integer.parseInt(table.getElementsByClass("cell c1 lastcol").get(1).text());
                AttendanceData data = new AttendanceData(course_name,session_completed, session_present, current_courses.contains(course_name));
                result.add(data);
            }catch (NumberFormatException | NullPointerException e) {
                return new AsyncTaskResult<>(e);
            }
            i++;
        }
        return new AsyncTaskResult<List<AttendanceData>>(result);
    }
}