package com.example.t_star.TEACHERACTIVITIES.CLASSES;

import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class TClassesModel {

    private String classId;
    private String className;
    private String subject;      // ✅ NEW FIELD
    private String teacherId;
    private int studentsCount;

    // Optional Firebase nodes
    private Map<String, Object> students;
    private Map<String, Object> joinRequests;

    // 🔥 Empty constructor required by Firebase
    public TClassesModel() {
        students = new HashMap<>();
        joinRequests = new HashMap<>();
    }

    // -------------------- GETTERS --------------------

    public String getClassId() {
        return classId != null ? classId : "";
    }

    public String getClassName() {
        return className != null ? className : "";
    }

    public String getSubject() {
        return subject != null ? subject : "";
    }

    public String getTeacherId() {
        return teacherId != null ? teacherId : "";
    }

    public int getStudentsCount() {
        return studentsCount;
    }

    public Map<String, Object> getStudents() {
        return students;
    }

    public Map<String, Object> getJoinRequests() {
        return joinRequests;
    }

    // -------------------- SETTERS --------------------

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSubject(String subject) {   // ✅ NEW
        this.subject = subject;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public void setStudentsCount(int studentsCount) {
        this.studentsCount = studentsCount;
    }

    // Optional setters (safe for Firebase)
    public void setStudents(Map<String, Object> students) {
        this.students = students;
    }

    public void setJoinRequests(Map<String, Object> joinRequests) {
        this.joinRequests = joinRequests;
    }
}