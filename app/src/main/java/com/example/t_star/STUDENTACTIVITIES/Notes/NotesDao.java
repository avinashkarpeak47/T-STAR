package com.example.t_star.STUDENTACTIVITIES.Notes;

import androidx.room.*;
import java.util.List;

@Dao
public interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotesModel note);

    @Delete
    void delete(NotesModel note);

    @Query("SELECT * FROM notes WHERE classId = :cid ORDER BY timestamp DESC")
    List<NotesModel> getNotesByClass(String cid);
}
