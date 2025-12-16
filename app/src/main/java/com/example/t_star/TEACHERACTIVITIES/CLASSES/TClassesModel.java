package com.example.t_star.TEACHERACTIVITIES.CLASSES;

public class TClassesModel {

    private String className;
    private String classId;
    private String description;
    private String teacherId;
    private String teacherName;
    private int studentsCount;

    // 🔥 Mandatory empty constructor for Firebase
    public TClassesModel() {}

    // 🔥 Full constructor for creating new class
    public TClassesModel(String className, String classId, String description,
                         String teacherId, String teacherName, int studentsCount) {
        this.className = className;
        this.classId = classId;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.studentsCount = studentsCount;
    }

    // ---------------------- GETTERS ----------------------

    public String getClassName() {
        return className != null ? className : "";
    }

    public String getClassId() {
        return classId != null ? classId : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public String getTeacherId() {
        return teacherId != null ? teacherId : "";
    }

    public String getTeacherName() {
        return teacherName != null ? teacherName : "";
    }

    public int getStudentsCount() {
        return studentsCount;
    }

    // ---------------------- SETTERS (optional but useful) ----------------------

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setStudentsCount(int studentsCount) {
        this.studentsCount = studentsCount;
    }
}
