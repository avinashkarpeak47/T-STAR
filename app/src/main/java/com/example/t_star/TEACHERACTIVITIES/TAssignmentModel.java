package com.example.t_star.TEACHERACTIVITIES;

public class TAssignmentModel {

    private String id;
    private String title;
    private String description;

    public TAssignmentModel(String id, String title, String description){
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId(){ return id; }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }

    public void setTitle(String t){ this.title = t; }
    public void setDescription(String d){ this.description = d; }
}
