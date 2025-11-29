package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TAsssignmentActivity extends AppCompatActivity {

    private RecyclerView rvAssignments;
    private TAssignmentAdapter adapter;
    private List<TAssignmentModel> dataList;
    private ImageButton btnCreateTop, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasssignment);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnCreateTop = findViewById(R.id.btnCreateTop);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnCreateTop.setOnClickListener(v -> {
            startActivity(new Intent(this, TCreateNewAssignActivity.class));
        });

        // RecyclerView
        rvAssignments = findViewById(R.id.rvAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        // Data List
        dataList = new ArrayList<>();
        dataList.add(new TAssignmentModel("1","Math Homework","Solve problems 1–10 from chapter 5"));
        dataList.add(new TAssignmentModel("2","Science Project","Prepare volcano model and report"));
        dataList.add(new TAssignmentModel("3","English Essay","Write an essay on climate change"));
        dataList.add(new TAssignmentModel("4","History Worksheet","Read pages 34–48 and answer questions"));

        // Adapter
        adapter = new TAssignmentAdapter(this, dataList, item ->
                Toast.makeText(this, "Clicked: " + item.getTitle(), Toast.LENGTH_SHORT).show());

        rvAssignments.setAdapter(adapter);
    }
}
