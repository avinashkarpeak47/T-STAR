package com.example.t_star.STUDENTACTIVITIES;

public class S_Fragment_Classes_RC_Model {

    private String classId;
    private String className;
    private String teacherName;
    private String status; // pending / approved

    private boolean seenApproved;

    public S_Fragment_Classes_RC_Model() {}

    public S_Fragment_Classes_RC_Model(
            String classId,
            String className,
            String teacherName,
            String status,
            boolean seenApproved
    ) {
        this.classId = classId;
        this.className = className;
        this.teacherName = teacherName;
        this.status = status;
        this.seenApproved = seenApproved;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSeenApproved(){
        return seenApproved;
    }
}
