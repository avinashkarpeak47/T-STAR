package com.example.t_star.TEACHERACTIVITIES.STUDENTS;

public class TStudentModel {
    private String name;
    private String rollNo;
    private int imageRes;

    public TStudentModel(String name, String rollNo, int imageRes) {
        this.name = name;
        this.rollNo = rollNo;
        this.imageRes = imageRes;
    }

    public String getName() { return name; }
    public String getRollNo() { return rollNo; }
    public int getImageRes() { return imageRes; }
}
