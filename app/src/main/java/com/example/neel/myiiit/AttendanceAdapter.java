package com.example.neel.myiiit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.neel.myiiit.attendance.AttendanceData;
import com.example.neel.myiiit.attendance.AttendanceHeader;
import com.example.neel.myiiit.attendance.AttendanceRow;

public class AttendanceAdapter extends ArrayAdapter<AttendanceRow> {
    public AttendanceAdapter(Context context){
        super(context,0);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView;
        listView = convertView;

        int type = getItemViewType(position);
        if (type == 1){
            if (listView == null) {
                listView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_list_header, parent, false);
            }
            AttendanceHeader current = (AttendanceHeader)getItem(position);
            TextView header = listView.findViewById(R.id.header_textView);
            header.setText(current.getHeader());
        }
        else {
            if (listView == null){
                listView = LayoutInflater.from(getContext()).inflate(R.layout.attendance_layout, parent, false);
            }
            AttendanceData current = (AttendanceData) getItem(position);

            TextView name = listView.findViewById(R.id.course_name);
            TextView total = listView.findViewById(R.id.session_completed);
            TextView present = listView.findViewById(R.id.session_present);
            TextView absent = listView.findViewById(R.id.session_absent);
            TextView percentage = listView.findViewById(R.id.percentage);

            name.setText(current.getCourse_name());
            total.setText("Sessions Completed : " + current.getSession_completed());
            present.setText("Present : " + current.getSession_present());
            absent.setText("Absent : " + current.getSession_absent());
            percentage.setText("Percentage : " +  String.format("%.2f", current.getPercentage()));
        }
        return listView;
    }

    @Override
    public int getViewTypeCount() {
        return  2;
    }

    @Override
    public int getItemViewType(int position) {

        if (getItem(position).getClass().equals(AttendanceHeader.class)){
            return 1;
        }
        else{
            return 0;
        }
    }
}
