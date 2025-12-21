package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS;

public class TAssignStudentStatusModel {

    private String uid;
    private String name;
    private boolean submitted;
    private String driveUrl; // ✅ NEW

    public TAssignStudentStatusModel() {}

    public TAssignStudentStatusModel(
            String uid,
            String name,
            boolean submitted,
            String driveUrl
    ) {
        this.uid = uid;
        this.name = name;
        this.submitted = submitted;
        this.driveUrl = driveUrl;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public boolean isSubmitted() { return submitted; }
    public String getDriveUrl() { return driveUrl; }
}
