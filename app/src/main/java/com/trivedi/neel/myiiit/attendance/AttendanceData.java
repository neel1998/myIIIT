package com.trivedi.neel.myiiit.attendance;


public class AttendanceData {
    private String course_name;
    private Integer session_completed;
    private Integer session_present;
    private Integer session_absent;
    private Double percentage;
    private boolean isCurrent;
    public AttendanceData( String name, Integer total, Integer present, boolean current ) {
        course_name = name;
        session_completed = total;
        session_present = present;
        isCurrent = current;
        session_absent = session_completed - session_present;
        if (session_completed != 0) {
            percentage = (100.0 * session_present) / session_completed;
        }
        else {
            percentage = 0.00;
        }
    }

    public String getCourse_name() {
        return course_name;
    }

    public Integer getSession_completed() {
        return session_completed;
    }

    public Integer getSession_present() {
        return session_present;
    }

    public Integer getSession_absent() {
        return session_absent;
    }

    public Double getPercentage() {
        return percentage;
    }

    public boolean getIsCurrent() { return isCurrent; }


}