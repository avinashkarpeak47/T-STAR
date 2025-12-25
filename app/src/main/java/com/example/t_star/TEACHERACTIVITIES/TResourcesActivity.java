package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.StudyMaterialAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.StudyMaterialModel;
import com.example.t_star.TEACHERACTIVITIES.Resources.Adapter;
import com.example.t_star.TEACHERACTIVITIES.Resources.Model;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TResourcesActivity extends AppCompatActivity {

    RecyclerView rvStudyMaterial;
    ArrayList<Model> materialList = new ArrayList<>();
    Adapter adapter;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tresources);

        classId = getIntent().getStringExtra("classId");
        if (classId == null) {
            Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbarStudyMaterial);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvStudyMaterial = findViewById(R.id.rvStudyMaterial);
        rvStudyMaterial.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter(this, materialList, classId);
        rvStudyMaterial.setAdapter(adapter);


        loadStudyMaterial();

        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResourcesUploadActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
        });
    }

    private void loadStudyMaterial() {

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("StudyMaterial")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        materialList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {

                            Model model = s.getValue(Model.class);

                            if (model != null) {
                                model.setMaterialId(s.getKey()); // 🔥 KEY SET
                                materialList.add(model);
                            }
                        }


                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(
                                TResourcesActivity.this,
                                "Failed to load materials",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}