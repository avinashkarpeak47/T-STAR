package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS;

public class TAssignStudentStatusModel {

    private String uid;
    private String name;
    private boolean submitted;

    public TAssignStudentStatusModel() {}

    public TAssignStudentStatusModel(String uid, String name, boolean submitted) {
        this.uid = uid;
        this.name = name;
        this.submitted = submitted;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public boolean isSubmitted() { return submitted; }
}
