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

import com.example.neel.myiiit.attendance.Attendance;
import com.example.neel.myiiit.attendance.AttendanceData;
import com.example.neel.myiiit.utils.Callback2;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AttendanceFragment extends Fragment {

    TextView last_update;
    ListView coursesListview;
    ProgressBar attd_prog;
    AttendanceAdapter courseAdapter;
    SwipeRefreshLayout pullToRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_attendence, container, false);

        last_update = rootView.findViewById(R.id.attd_last_update);
        coursesListview = rootView.findViewById(R.id.course_list);
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
        Attendance.getAttendance(getContext(), forceUpdate, new Callback2<List<AttendanceData>, Calendar>() {
            @Override
            public void success(List<AttendanceData> attendanceData, Calendar lastUpdated) {
                DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
                last_update.setText("Last Updated : " + dateFormat.format(lastUpdated.getTimeInMillis()));
                ArrayList<AttendanceData> currentCourse = new ArrayList<>();
                currentCourse.add(new AttendanceData("Current Courses", "1", "1",false, true));
                ArrayList<AttendanceData> otherCourse = new ArrayList<>();
                for (AttendanceData course : attendanceData){
                    if (course.getIsCurrent()){
                        currentCourse.add(course);
                    }
                    else{
                        otherCourse.add(course);
                    }
                }
                courseAdapter = new AttendanceAdapter(getContext(), currentCourse);
                courseAdapter.add(new AttendanceData("Other Courses", "1", "1", false, true));
                courseAdapter.addAll(otherCourse);
                coursesListview.setAdapter(courseAdapter);
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
