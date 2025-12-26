package com.example.t_star.TEACHERACTIVITIES.Notes;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotesModel.class}, version = 2)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase instance;

    public abstract NotesDao noteDao();

    public static synchronized NotesDatabase getInstance(Context ctx) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                            ctx.getApplicationContext(),
                            NotesDatabase.class,
                            "notes_db"
                    )
                    .fallbackToDestructiveMigration()   // DB schema बदलल्यावर recreate
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }
}
