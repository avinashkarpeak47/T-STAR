package com.example.t_star.STUDENTACTIVITIES;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.StudyMaterialAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.StudyMaterialModel;

import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class S_class_dashboard_studymaterial extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudyMaterialAdapter adapter;
    private ArrayList<StudyMaterialModel> materialList;

    private DatabaseReference materialRef;

    private SearchView searchView;
    private TextView tvEmpty;

    private String classId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_studymaterial);

        classId = getIntent().getStringExtra("classId");

        initViews();
        setupToolbar();
        setupRecycler();
        fetchStudyMaterial();
        setupSearch();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvStudyMaterial);
        searchView = findViewById(R.id.searchStudyMaterial);
        tvEmpty = findViewById(R.id.tvEmptyMaterial);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarStudyMaterial);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecycler() {
        materialList = new ArrayList<>();
        adapter = new StudyMaterialAdapter(this, materialList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void fetchStudyMaterial() {

        materialRef = FirebaseDatabase.getInstance()
                .getReference("Study_Materials")
                .child(classId);

        materialRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                materialList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        StudyMaterialModel model =
                                ds.getValue(StudyMaterialModel.class);
                        materialList.add(model);
                    }
                }

                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(materialList.isEmpty() ? TextView.VISIBLE : TextView.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });
    }
}
