package com.example.neel.myiiit.attendance;

import android.content.Context;

import com.example.neel.myiiit.network.Network;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

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
        Elements course_titles = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("h3");
        Elements course_tables = allAttendance_soup.getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("table");

        //adding object of attendance data to adapter
        int i = 0;
        for ( Element table : course_tables ){

            String course_name = course_titles.get(i).text();
            String session_completed = table.getElementsByClass("cell c1 lastcol").get(0).text();
            String session_present = table.getElementsByClass("cell c1 lastcol").get(1).text();
            AttendanceData data = new AttendanceData(course_name, session_completed, session_present);
            result.add(data);
            i++;
        }
        return new AsyncTaskResult<List<AttendanceData>>(result);
    }
}