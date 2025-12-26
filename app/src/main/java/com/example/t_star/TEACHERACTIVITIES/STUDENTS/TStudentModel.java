package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

public class TStudentModel {

    private String studentName;
    private String studentEmail;
    private String studentId;
    private String profileImageUrl; // ✅ NEW

    public TStudentModel(String studentName,
                         String studentEmail,
                         String studentId,
                         String profileImageUrl) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.studentId = studentId;
        this.profileImageUrl = profileImageUrl;
    }

    public String getStudentName() { return studentName; }
    public String getStudentEmail() { return studentEmail; }
    public String getStudentId() { return studentId; }
    public String getProfileImageUrl() { return profileImageUrl; }
}

/*
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

 */
