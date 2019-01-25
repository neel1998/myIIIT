package com.example.neel.myiiit.attendance;

public class AttendanceHeader extends AttendanceRow {
    private String mHeader;
    public AttendanceHeader(String header) {
        mHeader = header;
    }

    public String getHeader() {
        return mHeader;
    }
}
