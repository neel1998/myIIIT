package com.example.neel.myiiit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.neel.myiiit.attendance.AttendanceData;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<AttendanceData> {
    public AttendanceAdapter(Context context, List<AttendanceData> questionData){
        super(context,0,questionData);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView;
        listView = convertView;
        AttendanceData current = getItem(position);
        int type = getItemViewType(position);
        if (listView == null) {
            if (type == 1) {
                listView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_list_header, parent, false);
            } else {
                listView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_layout, parent, false);
            }
        }
        if (type == 1){

            TextView header = listView.findViewById(R.id.header_textView);
            header.setText(current.getCourse_name());
        }
        else {

            String course_name = current.getCourse_name();
            String session_completed = current.getSession_completed();
            String session_present = current.getSession_present();
            String session_absent = current.getSession_absent();
            String session_percentage = current.getPercentage();

            TextView name = listView.findViewById(R.id.course_name);
            TextView total = listView.findViewById(R.id.session_completed);
            TextView present = listView.findViewById(R.id.session_present);
            TextView absent = listView.findViewById(R.id.session_absent);
            TextView percentage = listView.findViewById(R.id.percentage);

            name.setText(course_name);
            total.setText("Sessions Completed : " + session_completed);
            present.setText("Present : " + session_present);
            absent.setText("Absent : " + session_absent);
            percentage.setText("Percentage : " + session_percentage);
        }
        return listView;
    }

    @Override
    public int getViewTypeCount() {
        return  2;
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position).isHeader())?1:0;
    }
}
