package com.example.t_star.STUDENTACTIVITIES.Models;

public class StudentClassModel {

    public String className;
    public String teacherId;
    public String status;

    public StudentClassModel() {}

    public StudentClassModel(String className, String teacherId, String status) {
        this.className = className;
        this.teacherId = teacherId;
        this.status = status;
    }
}

