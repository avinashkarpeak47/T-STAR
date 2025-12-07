package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentAdapter;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TAsssignmentActivity extends AppCompatActivity {

    private RecyclerView rvAssignments;
    private TAssignmentAdapter adapter;
    private List<TAssignmentModel> dataList;
    private ImageButton btnCreateTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasssignment);

        // ---- Create button on toolbar ----
        btnCreateTop = findViewById(R.id.btnCreate);
        btnCreateTop.setOnClickListener(v ->
                startActivity(new Intent(this, TCreateNewAssignActivity.class)));

        // ---- Toolbar ----
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ---- RecyclerView ----
        rvAssignments = findViewById(R.id.rvAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        // ---- Dummy Data with full details ----
        dataList = new ArrayList<>();

        dataList.add(new TAssignmentModel("1", "Math Homework", "Solve problems 1–10 from chapter 5", "10-12-2025", true, "Chapter5_Math.pdf", 40, 32));

        dataList.add(new TAssignmentModel("2", "Science Project", "Prepare a volcano model & report", "12-12-2025", false, "", 40, 18));
        dataList.add(new TAssignmentModel("3", "English Essay", "Write an essay on environment", "09-12-2025", true, "Essay_Template.docx", 40, 25));

        dataList.add(new TAssignmentModel("4", "History Worksheet", "Answer questions from page 48", "15-12-2025", false, "", 40, 20));

        // ---- Adapter + Card Click ----
        adapter = new TAssignmentAdapter(dataList, (item, pos) -> {
            Intent i = new Intent(TAsssignmentActivity.this, TAssignDetailActivity.class);
            i.putExtra("id", item.getId());
            i.putExtra("title", item.getTitle());
            i.putExtra("description", item.getDescription());
            i.putExtra("dueDate", item.getDueDate());
            i.putExtra("hasAttachment", item.isHasAttachment());
            i.putExtra("attachmentName", item.getAttachmentName());
            i.putExtra("totalStudents", item.getTotalStudents());
            i.putExtra("submittedCount", item.getSubmittedCount());
            startActivity(i);
        });

        rvAssignments.setAdapter(adapter);
    }
}
