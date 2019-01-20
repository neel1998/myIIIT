package com.example.neel.myiiit.attendance;

import android.content.Context;

import com.example.neel.myiiit.network.Network;
import com.example.neel.myiiit.network.NetworkResponse;
import com.example.neel.myiiit.utils.AsyncTaskCallback;
import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.CallbackAsyncTask;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
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

        String home_url = "https://moodle.iiit.ac.in/login/index.php?authCAS=CAS";
        Network.request(context, null, home_url);

        NetworkResponse response;

        String course_url = "https://moodle.iiit.ac.in/?redirect=0";
        response = Network.request(context, null, course_url);

        URI baseUrl = response.getResponse().request().url().uri();

        String single_url = response.getSoup().getElementById("frontpage-course-list").getElementsByTag("a").get(0).attr("href");
        single_url = baseUrl.resolve(single_url).toString();

        response = Network.request(context, null, single_url);

        String attendance_url = response.getSoup().getElementsByClass("mod-indent-outer").get(1).getElementsByTag("a").get(0).attr("href");
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
        Elements course_titles = response.getSoup().getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("h3");
        Elements course_tables = response.getSoup().getElementsByClass("cell c1 lastcol").get(0).getElementsByTag("table");

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