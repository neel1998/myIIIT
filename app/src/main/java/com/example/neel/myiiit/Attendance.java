package com.example.neel.myiiit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;

import com.example.neel.myiiit.utils.AsyncTaskResult;
import com.example.neel.myiiit.utils.Callback2;
import com.example.neel.myiiit.utils.DeSerialize;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

public class Attendance {
    private static final String ATTENDANCE_DATA_KEY = "attendance_data";
    private static final String ATTENDANCE_LAST_UPDATE_KEY = "attendance_last_update";

    public static void getAttendance(final Context context, Calendar date, boolean forceUpdate, final Callback2<ArrayList<AttendanceData>, Calendar> callback){

        Pair<ArrayList<AttendanceData>, Calendar> cachedData = getCachedAttendance(context);
        if (!forceUpdate && cachedData.first != null) {

            Calendar expirationDate = Calendar.getInstance();
            expirationDate.add(Calendar.HOUR, -12);
            boolean isExpired = cachedData.second.before(expirationDate);
            callback.success(cachedData.first, cachedData.second);

            if (!isExpired) return;
        }


        AttendanceTask attendanceTask = new AttendanceTask(context){
            @Override
            protected void onPostExecute(AsyncTaskResult<ArrayList<AttendanceData>> result) {
                super.onPostExecute(result);
                if (result.isError()){
                    callback.error(result.getError());
                    return;
                }
                Calendar lastUpdate = Calendar.getInstance();

                cacheAttendance(context, result.getResult(), lastUpdate);
                callback.success(result.getResult(), lastUpdate);
            }
        };
        attendanceTask.execute();
    }
    private static Pair<ArrayList<AttendanceData>, Calendar> getCachedAttendance(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<AttendanceData> result = null;
        Calendar lastUpdate = Calendar.getInstance();
        String serial = pref.getString(ATTENDANCE_DATA_KEY, null);
        if (serial != null){
            result = DeSerialize.deSerializeAttendance(serial);
        }
        lastUpdate.setTimeInMillis(pref.getLong(ATTENDANCE_LAST_UPDATE_KEY, 0));
        return new Pair<>(result, lastUpdate);
    }


    private static void cacheAttendance(Context context, ArrayList<AttendanceData> result, Calendar lastUpdate) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(ATTENDANCE_DATA_KEY, result.toString());
        edit.putLong(ATTENDANCE_LAST_UPDATE_KEY, lastUpdate.getTimeInMillis());
        edit.apply();
    }

    private static class AttendanceTask extends AsyncTask<Void, Void, AsyncTaskResult<ArrayList<AttendanceData>> > {

        private Context mContext;
        private AttendanceTask(Context context) {
            mContext = context;
        }
        @Override
        protected AsyncTaskResult<ArrayList<AttendanceData>> doInBackground(Void... voids) {
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
            return new AsyncTaskResult<>(result);
        }
    }

}
