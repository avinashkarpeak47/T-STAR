package com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS;

import java.util.List;

public class TAssignmentModel {

    private String id;
    private String classId;
    private String title;
    private String description;
    private String dueDate;
    private boolean hasAttachment;
    private String attachmentName;
    private int totalStudents;
    private int submittedCount;
    private List<String> attachmentUrls;

    public TAssignmentModel() {}

    public TAssignmentModel(String id, String classId, String title, String description,
                            String dueDate, boolean hasAttachment, String attachmentName,
                            int totalStudents, int submittedCount,
                            List<String> attachmentUrls) {

        this.id = id;
        this.classId = classId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.hasAttachment = hasAttachment;
        this.attachmentName = attachmentName;
        this.totalStudents = totalStudents;
        this.submittedCount = submittedCount;
        this.attachmentUrls = attachmentUrls;
    }

    public String getId() { return id; }
    public String getClassId() { return classId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDueDate() { return dueDate; }
    public boolean isHasAttachment() { return hasAttachment; }
    public String getAttachmentName() { return attachmentName; }
    public int getTotalStudents() { return totalStudents; }
    public int getSubmittedCount() { return submittedCount; }
    public List<String> getAttachmentUrls() { return attachmentUrls; }
}
