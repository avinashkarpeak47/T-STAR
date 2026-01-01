package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS;

public class TAssignStudentStatusModel {

    String uid, name, fileUrl;
    boolean submitted;

    public TAssignStudentStatusModel() {}

    public TAssignStudentStatusModel(String uid, String name,
                                     boolean submitted, String fileUrl) {

        this.uid = uid;
        this.name = name;
        this.submitted = submitted;
        this.fileUrl = fileUrl;
    }

    public boolean isSubmitted() { return submitted; }
    public String getName() { return name; }
    public String getFileUrl() { return fileUrl; }
}
