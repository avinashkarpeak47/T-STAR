package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.Notes.NotesAdapter;
import com.example.t_star.TEACHERACTIVITIES.Notes.NotesDatabase;
import com.example.t_star.TEACHERACTIVITIES.Notes.NotesModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotesListFragment extends Fragment {

    RecyclerView rv;
    NotesAdapter adapter;
    List<NotesModel> notes = new ArrayList<>();
    FloatingActionButton fab;

    String classId = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_notes_list2, container, false);

        rv = v.findViewById(R.id.notesRecycler);
        fab = v.findViewById(R.id.addNoteBtn);

        // ********** NULL SAFE CLASS ID **********
        if (getArguments() != null) {
            classId = getArguments().getString("classId", "");
        }

        // ********** LOAD OFFLINE NOTES **********
        notes = NotesDatabase.getInstance(getContext())
                .noteDao()
                .getNotesByClass(classId);

        adapter = new NotesAdapter(notes, getContext(), this::openEdit);

        StaggeredGridLayoutManager lm =
                new StaggeredGridLayoutManager(
                        2,
                        StaggeredGridLayoutManager.VERTICAL
                );

        rv.setLayoutManager(lm);
        rv.setAdapter(adapter);

        // ********** SYNC FIREBASE → ROOM **********
        syncFromFirebase();

        fab.setOnClickListener(a -> openEdit(null));

        return v;
    }

    void openEdit(NotesModel note) {

        EditNotesFragment f = new EditNotesFragment();

        Bundle b = new Bundle();
        b.putString("classId", classId);
        if (note != null) {
            b.putString("id", note.id);
        }

        f.setArguments(b);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.notesContainer, f)
                .addToBackStack(null)
                .commit();
    }

    void syncFromFirebase() {

        FirebaseDatabase.getInstance()
                .getReference("Notes")
                .child(classId)
                .child("TeacherNotes")
                .get()
                .addOnSuccessListener(snap -> {

                    for (DataSnapshot d : snap.getChildren()) {
                        NotesModel n = d.getValue(NotesModel.class);

                        NotesDatabase.getInstance(getContext())
                                .noteDao()
                                .insert(n);
                    }

                    notes = NotesDatabase.getInstance(getContext())
                            .noteDao()
                            .getNotesByClass(classId);

                    adapter.list = notes;
                    adapter.notifyDataSetChanged();
                });
    }
}