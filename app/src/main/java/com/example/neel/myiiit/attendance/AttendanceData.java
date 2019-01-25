package com.example.neel.myiiit.attendance;


public class AttendanceData extends AttendanceRow{
    private String course_name;
    private String session_completed;
    private String session_present;
    private String session_absent;
    private String percentage;
    private boolean isCurrent;
    private boolean isHeader;
    public AttendanceData( String name, String total, String present, boolean current ) {
        course_name = name;
        session_completed = total;
        session_present = present;
        isCurrent = current;
        session_absent = String.valueOf( Integer.valueOf(session_completed) - Integer.valueOf(session_present) );
        Double perc = ( Double.valueOf(session_present) / Double.valueOf(session_completed) )*100;
        percentage = String.format("%.2f", perc);
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getSession_completed() {
        return session_completed;
    }

    public String getSession_present() {
        return session_present;
    }

    public String getSession_absent() {
        return session_absent;
    }

    public String getPercentage() {
        return percentage;
    }

    public boolean getIsCurrent() { return isCurrent; }


}