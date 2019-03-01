package com.trivedi.neel.myiiit;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.trivedi.neel.myiiit.attendance.AttendanceData;

import java.util.ArrayList;
import java.util.List;

public class AttendanceAdapter extends BaseExpandableListAdapter {
    private final Context mContext;

    private List<Pair<String, List<AttendanceData>>> mGroups = new ArrayList<>();

    public AttendanceAdapter(Context context, List<AttendanceData> currentCourses, List<AttendanceData> pastCourses){
        mContext = context;

        if (currentCourses.size() > 0) {
            mGroups.add(new Pair<String, List<AttendanceData>>("Current Courses", currentCourses));
        }

        if (pastCourses.size() > 0) {
            mGroups.add(new Pair<String, List<AttendanceData>>("Past Courses", pastCourses));
        }
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).second.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).second.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.attendance_list_header, parent, false);
        }

        Pair<String, List<AttendanceData>> current = (Pair<String, List<AttendanceData>>)getGroup(groupPosition);

        TextView header = convertView.findViewById(R.id.header_textView);
        header.setText(current.first);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.attendance_layout, parent, false);
        }

        AttendanceData current = (AttendanceData) getChild(groupPosition, childPosition);

        TextView name = convertView.findViewById(R.id.course_name);
        TextView total = convertView.findViewById(R.id.session_completed);
        TextView absent = convertView.findViewById(R.id.session_absent);
        TextView percentage = convertView.findViewById(R.id.percentage);

        name.setText(current.getCourse_name());
        total.setText("Sessions Completed : " + current.getSession_completed());
        absent.setText("Absent : " + current.getSession_absent());
        percentage.setText(String.format("%.2f%%", current.getPercentage()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
