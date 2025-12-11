package com.example.t_star.STUDENTACTIVITIES;

public class S_Fragment_Classes_RC_Model {

    private String classId;
    private String className;
    private String teacherName;
    private String subjectName;

    public S_Fragment_Classes_RC_Model() {
    }

    public S_Fragment_Classes_RC_Model(String classId, String className, String teacherName, String subjectName) {
        this.classId = classId;
        this.className = className;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
