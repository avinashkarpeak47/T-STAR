package com.example.t_star.STUDENTACTIVITIES.Models;

public class Assignment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_CHECKED = "CHECKED";

    private String title;
    private String description;
    private String extraInfo;   // e.g. due date, subject
    private String status;      // use constants above

    public Assignment() {
        // Empty constructor required for Firebase later
    }

    public Assignment(String title, String description, String extraInfo, String status) {
        this.title = title;
        this.description = description;
        this.extraInfo = extraInfo;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
