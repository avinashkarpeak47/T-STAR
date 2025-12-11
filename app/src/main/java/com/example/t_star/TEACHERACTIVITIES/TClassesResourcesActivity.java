package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesAdapter;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TClassesResourcesActivity extends AppCompatActivity {

    private RecyclerView rvClasses;
    private TClassesAdapter adapter;
    private ArrayList<TClassesModel> classList;

    private String currentTeacherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tclasses_resources);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvClasses = findViewById(R.id.rvClasses);
        rvClasses.setLayoutManager(new LinearLayoutManager(this));

        currentTeacherId = FirebaseAuth.getInstance().getUid();

        classList = new ArrayList<>();

        adapter = new TClassesAdapter(this, classList, (model, pos) -> {

            Toast.makeText(this, "Opening " + model.getClassName(), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, TResourcesActivity.class);
            i.putExtra("classId", model.getClassId());
            i.putExtra("className", model.getClassName());
            startActivity(i);
        });

        rvClasses.setAdapter(adapter);
        loadClasses();
    }


    // 🔥 FIX: Load classes every time user returns to this screen
    @Override
    protected void onResume() {
        super.onResume();
        loadClasses();  // reload when coming back from CreateClass page
    }

    private void loadClasses() {

        Classes_helper.getAllClasses(list -> {

            classList.clear();

            for (TClassesModel model : list) {

                if (model.getTeacherId() != null &&
                        model.getTeacherId().equals(currentTeacherId)) {

                    classList.add(model);
                }
            }

            adapter.updateList(classList);

            if (classList.isEmpty()) {
                Toast.makeText(this, "No classes created by you!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
