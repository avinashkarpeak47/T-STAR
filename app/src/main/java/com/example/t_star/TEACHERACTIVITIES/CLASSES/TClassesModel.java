package com.example.t_star.TEACHERACTIVITIES.CLASSES;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class TClassesModel {

    private String classId;
    private String className;
    private String description;
    private String teacherId;
    private String teacherName;
    private int studentsCount;

    // Optional – ignored in getAllClasses()
    private Map<String, Object> students;
    private Map<String, Object> joinRequests;

    // 🔥 Empty constructor required by Firebase
    public TClassesModel() {
        students = new HashMap<>();
        joinRequests = new HashMap<>();
    }

    // -------------------- GETTERS --------------------
    public String getClassId() { return classId; }
    public String getClassName() { return className; }
    public String getDescription() { return description; }
    public String getTeacherId() { return teacherId; }
    public String getTeacherName() { return teacherName; }
    public int getStudentsCount() { return studentsCount; }

    // -------------------- SETTERS --------------------
    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setClassName(String className) {
        this.className = className;
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

    // Optional setters (safe)
    public void setStudents(Map<String, Object> students) {
        this.students = students;
    }

    public void setJoinRequests(Map<String, Object> joinRequests) {
        this.joinRequests = joinRequests;
    }
}
