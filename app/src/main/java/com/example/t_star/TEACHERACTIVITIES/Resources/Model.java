package com.example.t_star.TEACHERACTIVITIES.Resources;

public class Model {

    private String materialId;
    private String title;
    private String description;
    private String date;
    private String fileUrl;

    public Model() {
    }

    // getters & setters
    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getFileUrl() {
        return fileUrl;
    }


}

