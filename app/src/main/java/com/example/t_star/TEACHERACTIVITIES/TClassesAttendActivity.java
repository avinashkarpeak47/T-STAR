package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesAdapter;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TClassesAttendActivity extends AppCompatActivity {

    private RecyclerView rvClasses;
    private TClassesAdapter adapter;
    private List<TClassesModel> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tclasses_attend);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // TOP CREATE BUTTON
        ImageButton btnCreateTop = findViewById(R.id.btnCreate);
        btnCreateTop.setOnClickListener(v -> {
            // OPEN CREATE ATTENDANCE PAGE
            Intent i = new Intent(TClassesAttendActivity.this, TCreateClassActivity.class);
            startActivity(i);
        });

        rvClasses = findViewById(R.id.rvClasses);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));

        classList = new ArrayList<>();
        classList.add(new TClassesModel("Class 5", "501", 40));
        classList.add(new TClassesModel("Class 6", "601", 42));
        classList.add(new TClassesModel("Class 7", "701", 38));
        classList.add(new TClassesModel("Class 8", "801", 35));
        classList.add(new TClassesModel("Class 9", "901", 41));

        adapter = new TClassesAdapter(this, classList, (model, pos) -> {

            Toast.makeText(this,
                    "Opening " + model.getClassName(),
                    Toast.LENGTH_SHORT).show();

            // OPEN NEXT PAGE WITH CLASS DATA
            Intent i = new Intent(TClassesAttendActivity.this, TAttendanceActivity.class);
            i.putExtra("className", model.getClassName());
            i.putExtra("classId", model.getClassId());
            startActivity(i);
        });

        rvClasses.setAdapter(adapter);
    }
}
