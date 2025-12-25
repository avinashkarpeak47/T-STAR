package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

public class TAssignmentModel {

    String id, classId, title, description, dueDate;
    int totalStudents, submittedCount;

    public TAssignmentModel() {}

    public TAssignmentModel(String id, String classId, String title,
                            String description, String dueDate,
                            int totalStudents, int submittedCount) {
        this.id = id;
        this.classId = classId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.totalStudents = totalStudents;
        this.submittedCount = submittedCount;
    }

    public String getId() { return id; }
    public String getClassId() { return classId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public int getTotalStudents() { return totalStudents; }
    public int getSubmittedCount() { return submittedCount; }
}
