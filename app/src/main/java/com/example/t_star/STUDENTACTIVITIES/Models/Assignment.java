package com.example.t_star.STUDENTACTIVITIES.Models;

public class Assignment {

    // ---- SINGLE SOURCE OF STATUS TRUTH ----
    public static final String STATUS_PENDING   = "PENDING";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_EVALUATED = "EVALUATED";

    private String assignmentId;
    private String title;
    private String subject;

    // Use millis everywhere for consistency
    private long dueDateMillis;

    // Derived status (NOT random)
    private String status;

    private int marksObtained;
    private int totalMarks;

    // Submission metadata (VERY IMPORTANT)
    private String submittedFileName;
    private String driveFileUrl;
    private long submittedAt;

    // Required empty constructor for Firebase
    public Assignment() {}

    public Assignment(
            String assignmentId,
            String title,
            String subject,
            long dueDateMillis,
            String status,
            int marksObtained,
            int totalMarks,
            String submittedFileName,
            String driveFileUrl,
            long submittedAt
    ) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.subject = subject;
        this.dueDateMillis = dueDateMillis;
        this.status = status;
        this.marksObtained = marksObtained;
        this.totalMarks = totalMarks;
        this.submittedFileName = submittedFileName;
        this.driveFileUrl = driveFileUrl;
        this.submittedAt = submittedAt;
    }


    // Constructor for Assignment List / Dashboard usage
    public Assignment(
            String assignmentId,
            String title,
            String subject,
            long dueDateMillis,
            String status,
            int marksObtained,
            int totalMarks
    ) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.subject = subject;
        this.dueDateMillis = dueDateMillis;
        this.status = status;
        this.marksObtained = marksObtained;
        this.totalMarks = totalMarks;

        // Defaults for submission-related fields
        this.submittedFileName = null;
        this.driveFileUrl = null;
        this.submittedAt=0L;
    }

    // ---- GETTERS ----
    public String getAssignmentId() { return assignmentId; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public long getDueDateMillis() { return dueDateMillis; }
    public String getStatus() { return status; }
    public int getMarksObtained() { return marksObtained; }
    public int getTotalMarks() { return totalMarks; }
    public String getSubmittedFileName() { return submittedFileName; }
    public String getDriveFileUrl() { return driveFileUrl; }
    public long getSubmittedAt() { return submittedAt; }

    // ---- HELPER METHODS (USED BY UI) ----
    public boolean isSubmitted() {
        return STATUS_SUBMITTED.equals(status) || STATUS_EVALUATED.equals(status);
    }

    public boolean isEvaluated() {
        return STATUS_EVALUATED.equals(status);
    }

    public boolean isLate() {
        return submittedAt > 0 && dueDateMillis > 0 && submittedAt > dueDateMillis;

    }
}