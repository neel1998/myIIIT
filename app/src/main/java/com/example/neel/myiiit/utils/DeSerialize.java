package com.example.neel.myiiit.utils;

import com.example.neel.myiiit.AttendanceData;

import java.util.ArrayList;

public class DeSerialize {
    public static ArrayList<AttendanceData> deSerializeAttendance(String serial) {
        ArrayList<AttendanceData> result = new ArrayList<>();
        serial = serial.substring(1, serial.length() - 1);
        String[] strings = serial.split(",");
        for (int i = 0; i < strings.length; i += 3 ) {
            result.add(new AttendanceData(strings[i].trim(), strings[i+1], strings[i+2]));
        }
        return result;
    }
}
