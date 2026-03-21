package com.example.t_star.Notification;

public class NotificationModel {
    String title, message;
    long time;

    public NotificationModel() {}

    public NotificationModel(String title, String message, long time) {
        this.title = title;
        this.message = message;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public long getTime() { return time; }
}

