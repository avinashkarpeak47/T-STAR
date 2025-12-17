package com.example.t_star.STUDENTACTIVITIES.Models;

public class StudentAttendance {

    public static final String STATUS_PRESENT = "present";
    public static final String STATUS_ABSENT = "absent";
    public static final String STATUS_LEAVE = "leave";

    private String date;
    private String status;

    public StudentAttendance() {}

    public StudentAttendance(String date, String status) {
        this.date = date;
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
