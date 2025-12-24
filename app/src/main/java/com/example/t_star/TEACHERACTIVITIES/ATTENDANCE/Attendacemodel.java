package com.example.t_star.TEACHERACTIVITIES.ATTENDANCE;

public class Attendacemodel {

    public String studentId;
    public int rollNo;
    public String name;
    public String status;   // present / absent / leave
    public int percentage;

    public Attendacemodel() {}

    public Attendacemodel(int rollNo, String studentId, String name) {
        this.rollNo = rollNo;
        this.studentId = studentId;
        this.name = name;
        this.status = "present"; // default
    }
}
