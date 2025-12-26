package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

public class TNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnotes);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ***** GET CLASS ID FROM INTENT *****
        String classId = getIntent().getStringExtra("classId");

        // ***** PASS TO FRAGMENT *****
        NotesListFragment f = new NotesListFragment();

        Bundle b = new Bundle();
        b.putString("classId", classId);
        f.setArguments(b);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.notesContainer, f)
                .commit();
    }
}
