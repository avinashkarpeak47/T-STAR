package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
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
    SearchView searchView;
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
        searchView = findViewById(R.id.searchStudyMaterial);
        setupSearch();


        loadStudyMaterial();

        findViewById(R.id.btnCreate).setOnClickListener(v -> {
            Intent intent = new Intent(this, ResourcesUploadActivity.class);
            intent.putExtra("classId", classId);
            startActivity(intent);
        });
    }

    private void setupSearch() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
    }
    private void loadStudyMaterial() {

        FirebaseDatabase.getInstance()
                .getReference("StudyMaterial")
                .child(classId)
                .child("Resources")
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

                        // ❌ OLD (remove)
                        // adapter.notifyDataSetChanged();

                        // ✅ NEW — proper refresh + keep search
                        adapter.setData(materialList);
                        adapter.filter(searchView.getQuery().toString());
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