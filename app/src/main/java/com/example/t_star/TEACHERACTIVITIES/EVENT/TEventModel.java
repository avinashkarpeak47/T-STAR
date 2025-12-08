package com.example.t_star.TEACHERACTIVITIES.EVENT;

public class TEventModel {
    private String id;
    private String title;
    private String date;
    private String description;

    public TEventModel() {}

    public TEventModel(String id, String title, String date, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getDescription() { return description; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
}
