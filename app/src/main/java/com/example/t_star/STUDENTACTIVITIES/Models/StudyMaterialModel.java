package com.example.t_star.STUDENTACTIVITIES.Models;

public class StudyMaterialModel {

    private String materialId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;
    private long uploadedOn;

    public StudyMaterialModel() {
        // Required for Firebase
    }

    public StudyMaterialModel(String materialId, String title, String description,
                              String fileUrl, String fileType, long uploadedOn) {
        this.materialId = materialId;
        this.title = title;
        this.description = description;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.uploadedOn = uploadedOn;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public long getUploadedOn() {
        return uploadedOn;
    }
}

