package com.example.neel.myiiit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.neel.myiiit.Model.Attendance;
import com.example.neel.myiiit.utils.Callback2;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AttendanceFragment extends Fragment {

    TextView last_update;
    ListView attd_listview;
    ProgressBar attd_prog;
    AttendanceAdapter attendanceAdapter;
    SwipeRefreshLayout pullToRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendence, container, false);

        last_update = rootView.findViewById(R.id.attd_last_update);
        attd_listview = rootView.findViewById(R.id.attd_list);
        attd_prog = rootView.findViewById(R.id.attd_progress);
        pullToRefresh = rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                attd_prog.setVisibility(View.VISIBLE);
                updateAttendance(true);
                pullToRefresh.setRefreshing(false);
            }
        });
        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateAttendance(false);

    }
    private void updateAttendance(boolean forceUpdate){
        Attendance.getAttendance(getContext(), forceUpdate, new Callback2<ArrayList<AttendanceData>, Calendar>() {
            @Override
            public void success(ArrayList<AttendanceData> attendanceData, Calendar lastUpdated) {
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                last_update.setText("Last Updated : " + dateFormat.format(lastUpdated.getTimeInMillis()));
                attendanceAdapter = new AttendanceAdapter(getContext(), attendanceData);
                attd_listview.setAdapter(attendanceAdapter);
                attd_prog.setVisibility(View.GONE);
            }

            @Override
            public void error(Exception e) {
                Log.d("error", e.getLocalizedMessage());
                attd_prog.setVisibility(View.GONE);
            }
        });
    }
}
