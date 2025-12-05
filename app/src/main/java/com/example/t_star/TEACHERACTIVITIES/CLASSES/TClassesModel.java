package com.example.t_star.TEACHERACTIVITIES.CLASSES;

public class TClassesModel {

    private String className;
    private String classId;
    private int totalStudents;

    public TClassesModel(String className, String classId, int totalStudents) {
        this.className = className;
        this.classId = classId;
        this.totalStudents = totalStudents;
    }

    public String getClassName() {
        return className;
    }

    public String getClassId() {
        return classId;
    }

    public int getTotalStudents() {
        return totalStudents;
    }
}
