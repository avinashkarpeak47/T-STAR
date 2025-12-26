package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import android.view.*;
import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Notes.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.*;

public class S_NotesListFragment extends Fragment {

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

        if (getArguments() != null)
            classId = getArguments().getString("classId", "");

        notes = NotesDatabase.getInstance(getContext())
                .noteDao()
                .getNotesByClass(classId);

        adapter = new NotesAdapter(notes, getContext(), this::openEdit);

        rv.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        rv.setAdapter(adapter);

        syncFromFirebase();

        fab.setOnClickListener(a -> openEdit(null));

        return v;
    }

    void openEdit(NotesModel note) {

        S_EditNotesFragment f = new S_EditNotesFragment();

        Bundle b = new Bundle();
        b.putString("classId", classId);

        if (note != null)
            b.putString("id", note.id);

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
                .child("StudentNotes")
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
