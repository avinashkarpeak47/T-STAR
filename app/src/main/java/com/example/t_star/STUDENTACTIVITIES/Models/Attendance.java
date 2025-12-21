package com.example.t_star.STUDENTACTIVITIES.Models;

public class Attendance {

    public static final String STATUS_PRESENT = "present";
    public static final String STATUS_ABSENT = "absent";
    public static final String STATUS_LEAVE = "leave";

    private String date;        // e.g. 09 Dec 2025
    private String subject;     // e.g. Java
    private String status;      // present / absent / leave
    private long timestamp;     // for sorting (optional)

    // Required empty constructor for Firebase
    public Attendance() {}

    public Attendance(String date, String subject, String status, long timestamp) {
        this.date = date;
        this.subject = subject;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
