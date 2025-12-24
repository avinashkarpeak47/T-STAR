package com.example.t_star.STUDENTACTIVITIES.Models;

public class Attendance {

    public static final String STATUS_PRESENT = "present";
    public static final String STATUS_ABSENT = "absent";
    public static final String STATUS_LEAVE = "leave";

    private String date;
    private String subject;
    private String status;
    private long timestamp;

    public Attendance() {}

    public Attendance(String date, String subject,
                      String status, long timestamp) {
        this.date = date;
        this.subject = subject;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getDate() { return date; }
    public String getSubject() { return subject; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}
