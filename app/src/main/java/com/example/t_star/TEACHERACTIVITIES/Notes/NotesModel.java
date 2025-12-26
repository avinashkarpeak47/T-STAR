package com.example.t_star.TEACHERACTIVITIES.Notes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class NotesModel {

    @PrimaryKey
    @NonNull
    public String id;

    public String classId;   // ⭐ class-wise notes

    public String title;
    public String content;
    public long timestamp;

    public NotesModel() {} // Needed for Firebase

    @Ignore
    public String getFormattedDate() {
        return new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a")
                .format(new java.util.Date(timestamp));
    }

}
