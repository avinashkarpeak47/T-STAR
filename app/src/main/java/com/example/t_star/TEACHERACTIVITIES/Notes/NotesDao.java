package com.example.t_star.TEACHERACTIVITIES.Notes;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NotesModel note);

    @Update
    void update(NotesModel note);

    @Delete
    void delete(NotesModel note);

    @Query("SELECT * FROM notes WHERE classId = :cid ORDER BY timestamp DESC")
    List<NotesModel> getNotesByClass(String cid);

    @Query("SELECT * FROM notes WHERE classId = :cid AND (title LIKE :q OR content LIKE :q)")
    List<NotesModel> search(String cid, String q);
}
