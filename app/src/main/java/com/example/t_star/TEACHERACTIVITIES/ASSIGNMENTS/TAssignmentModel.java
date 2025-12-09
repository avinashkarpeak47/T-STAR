package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

public class TAssignmentModel {

    private String id;
    private String title;
    private String description;
    private String dueDate;
    private boolean hasAttachment;
    private String attachmentName;
    private int totalStudents;
    private int submittedCount;

    public TAssignmentModel(String id,
                            String title,
                            String description,
                            String dueDate,
                            boolean hasAttachment,
                            String attachmentName,
                            int totalStudents,
                            int submittedCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.hasAttachment = hasAttachment;
        this.attachmentName = attachmentName;
        this.totalStudents = totalStudents;
        this.submittedCount = submittedCount;
    }

    // ---- Getters ----
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public boolean isHasAttachment() { return hasAttachment; }
    public String getAttachmentName() { return attachmentName; }
    public int getTotalStudents() { return totalStudents; }
    public int getSubmittedCount() { return submittedCount; }
    public int getUnsubmittedCount() { return totalStudents - submittedCount; }
}
