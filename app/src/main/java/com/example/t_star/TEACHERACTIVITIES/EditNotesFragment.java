package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.Notes.NotesDatabase;
import com.example.t_star.TEACHERACTIVITIES.Notes.NotesModel;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class EditNotesFragment extends Fragment {

    EditText title, content;
    AppCompatButton save, delete;

    NotesModel note;
    String classId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edit_notes, container, false);

        title = v.findViewById(R.id.noteTitle);
        content = v.findViewById(R.id.noteContent);
        save = v.findViewById(R.id.saveBtn);
        delete = v.findViewById(R.id.deleteBtn);

        if (getArguments() != null)
            classId = getArguments().getString("classId", "");

        // --- EDIT MODE (LOAD FROM LOCAL DB) ---
        String noteId = getArguments() != null
                ? getArguments().getString("id", null)
                : null;

        if (noteId != null) {
            for (NotesModel n : NotesDatabase.getInstance(getContext())
                    .noteDao()
                    .getNotesByClass(classId)) {
                if (n.id.equals(noteId)) {
                    note = n;
                    break;
                }
            }

            if (note != null) {
                title.setText(note.title);
                content.setText(note.content);
            }
        }

        save.setOnClickListener(v1 -> saveNote());
        delete.setOnClickListener(v2 -> deleteNote());

        return v;
    }

    void saveNote() {

        if (note == null) {
            note = new NotesModel();
            note.id = UUID.randomUUID().toString();
        }

        note.classId = classId;
        note.title = title.getText().toString().trim();
        note.content = content.getText().toString().trim();
        note.timestamp = System.currentTimeMillis();

        // SAVE → ROOM (offline)
        NotesDatabase.getInstance(getContext())
                .noteDao()
                .insert(note);

        // SAVE → REALTIME DATABASE
        FirebaseDatabase.getInstance()
                .getReference("Notes")
                .child(classId)
                .child("TeacherNotes")
                .child(note.id)
                .setValue(note)

                .addOnSuccessListener(unused ->
                        Log.d("RTDB_NOTES", "Saved to Realtime DB"))
                .addOnFailureListener(e ->
                        Log.e("RTDB_NOTES", "Save failed: " + e.getMessage()));

        requireActivity().onBackPressed();
    }

    void deleteNote() {

        if (note == null) return;

        // DELETE local
        NotesDatabase.getInstance(getContext())
                .noteDao()
                .delete(note);

        // DELETE realtime
        FirebaseDatabase.getInstance()
                .getReference("Notes")
                .child(classId)
                .child("TeacherNotes")
                .child(note.id)
                .removeValue();

        requireActivity().onBackPressed();
    }
}
