package com.example.t_star.STUDENTACTIVITIES.Notes;

import android.content.Context;
import androidx.room.*;

@Database(entities = {NotesModel.class}, version = 2)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase instance;

    public abstract NotesDao noteDao();

    public static synchronized NotesDatabase getInstance(Context ctx) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                            ctx.getApplicationContext(),
                            NotesDatabase.class,
                            "student_notes_db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}
