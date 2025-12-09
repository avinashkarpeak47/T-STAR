package com.example.t_star.STUDENTACTIVITIES.Models;

public class Attendance {

    public static final String STATUS_PRESENT = "PRESENT";
    public static final String STATUS_ABSENT = "ABSENT";
    public static final String STATUS_LEAVE = "LEAVE";

    private String studentName;
    private String rollNo;
    private String date;      // e.g. "12 Dec 2025"
    private String subject;   // e.g. "Java"
    private String status;    // PRESENT / ABSENT / LEAVE

    public Attendance() {
        // empty constructor needed for Firebase
    }

    public Attendance(String studentName, String rollNo,
                      String date, String subject, String status) {
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.date = date;
        this.subject = subject;
        this.status = status;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRollNo() {
        return rollNo;
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

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
