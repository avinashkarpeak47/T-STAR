package com.example.t_star.STUDENTACTIVITIES;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.SStudyMaterialAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.SStudyMaterialModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;

        public class S_class_dashboard_studymaterial extends AppCompatActivity {

            private RecyclerView recyclerView;
            private TextView txtEmpty;
            private SearchView searchView;

            private ArrayList<SStudyMaterialModel> fullList = new ArrayList<>();
            private ArrayList<SStudyMaterialModel> displayList = new ArrayList<>();
            private SStudyMaterialAdapter adapter;

            private String classId;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_s_class_dashboard_studymaterial);

                classId = getIntent().getStringExtra("classId");
                if (classId == null) {
                    Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                setupToolbar();
                initViews();
                setupRecycler();
                loadMaterials();
                setupSearch();
            }

            private void setupToolbar() {
                MaterialToolbar toolbar = findViewById(R.id.toolbarStudyMaterial);
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> finish());
            }

            private void initViews() {
                recyclerView = findViewById(R.id.rvStudyMaterial);
                txtEmpty = findViewById(R.id.tvEmptyMaterial);
                searchView = findViewById(R.id.searchStudyMaterial);
            }

            private void setupRecycler() {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new SStudyMaterialAdapter(this, displayList);
                recyclerView.setAdapter(adapter);
            }

            private void loadMaterials() {
                FirebaseDatabase.getInstance()
                        .getReference("StudyMaterial")
                        .child(classId)
                        .child("Resources")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                fullList.clear();
                                displayList.clear();

                                for (DataSnapshot s : snapshot.getChildren()) {
                                    SStudyMaterialModel model =
                                            s.getValue(SStudyMaterialModel.class);
                                    if (model != null) {
                                        fullList.add(model);
                                    }
                                }

                                displayList.addAll(fullList);
                                adapter.notifyDataSetChanged();

                                txtEmpty.setVisibility(
                                        displayList.isEmpty() ? View.VISIBLE : View.GONE
                                );
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(
                                        S_class_dashboard_studymaterial.this,
                                        "Failed to load study material",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        });
            }

            private void setupSearch() {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override public boolean onQueryTextSubmit(String query) { return false; }

                    @Override
                    public boolean onQueryTextChange(String text) {

                        displayList.clear();

                        if (text.isEmpty()) {
                            displayList.addAll(fullList);
                        } else {
                            String key = text.toLowerCase();
                            for (SStudyMaterialModel m : fullList) {
                                if (m.getTitle() != null &&
                                        m.getTitle().toLowerCase().contains(key)) {
                                    displayList.add(m);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                        txtEmpty.setVisibility(
                                displayList.isEmpty() ? View.VISIBLE : View.GONE
                        );
                        return true;
                    }

                });

            }
        }