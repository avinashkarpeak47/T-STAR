package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

public class TStudentModel {

    private String studentName;
    private String studentEmail;
    private String studentId;
    private int imageRes;

    public TStudentModel(String studentName,
                         String studentEmail,
                         String studentId,
                         int imageRes) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentId = studentId;
        this.imageRes = imageRes;
    }

    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentId() { return studentId; }
    public int getImageRes() { return imageRes; }
}
