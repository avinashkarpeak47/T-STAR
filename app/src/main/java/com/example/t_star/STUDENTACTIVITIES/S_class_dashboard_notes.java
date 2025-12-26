package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

public class S_class_dashboard_notes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_notes);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String classId = getIntent().getStringExtra("classId");

        S_NotesListFragment f = new S_NotesListFragment();
        Bundle b = new Bundle();
        b.putString("classId", classId);
        f.setArguments(b);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.notesContainer, f)
                .commit();
    }
}
