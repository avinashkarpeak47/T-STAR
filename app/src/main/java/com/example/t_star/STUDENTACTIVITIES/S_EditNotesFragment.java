package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import androidx.annotation.*;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Notes.*;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class S_EditNotesFragment extends Fragment {

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

        String noteId = getArguments() != null
                ? getArguments().getString("id", null)
                : null;

        if (noteId != null) {
            for (NotesModel n : NotesDatabase.getInstance(getContext())
                    .noteDao()
                    .getNotesByClass(classId)) {
                if (n.id.equals(noteId)) note = n;
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

        NotesDatabase.getInstance(getContext())
                .noteDao()
                .insert(note);

        FirebaseDatabase.getInstance()
                .getReference("Notes")
                .child(classId)
                .child("StudentNotes")
                .child(note.id)
                .setValue(note);


        requireActivity().onBackPressed();
    }

    void deleteNote() {

        if (note == null) return;

        NotesDatabase.getInstance(getContext())
                .noteDao()
                .delete(note);

        FirebaseDatabase.getInstance()
                .getReference("Notes")
                .child(classId)
                .child("StudentNotes")
                .child(note.id)
                .removeValue();

        requireActivity().onBackPressed();
    }
}
