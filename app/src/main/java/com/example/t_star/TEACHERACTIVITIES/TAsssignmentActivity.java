package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentAdapter;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class TAsssignmentActivity extends AppCompatActivity {

    private RecyclerView rvAssignments;
    private TAssignmentAdapter adapter;
    private final List<TAssignmentModel> list = new ArrayList<>();

    private String classId, className;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasssignment);

        // ================= GET INTENT DATA =================
        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");

        // 🔥 SAFETY CHECK
        if (classId == null || classId.isEmpty()) {
            Toast.makeText(this, "Class ID missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (className == null) {
            className = "Class"; // fallback (avoid null title)
        }

        // ================= FIREBASE REF =================
        ref = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId);

        // ================= TOOLBAR =================
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Assignments • " + className);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ================= CREATE ASSIGNMENT BUTTON =================
        ImageButton btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, TCreateNewAssignActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("className", className);
            startActivity(i);
        });

        // ================= RECYCLER VIEW =================
        rvAssignments = findViewById(R.id.rvAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TAssignmentAdapter(list, model -> {
            Intent i = new Intent(this, TAssignDetailActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("className", className);
            i.putExtra("id", model.getId());
            i.putExtra("title", model.getTitle());
            i.putExtra("description", model.getDescription());
            i.putExtra("dueDate", model.getDueDate());
            i.putExtra("attachmentName", model.getAttachmentName());
            i.putExtra("hasAttachment", model.isHasAttachment());
            i.putExtra("totalStudents", model.getTotalStudents());
            i.putExtra("submittedCount", model.getSubmittedCount());
            startActivity(i);
        });

        rvAssignments.setAdapter(adapter);

        // ================= LOAD DATA =================
        loadAssignmentsLive();
    }

    // ================= REALTIME ASSIGNMENTS =================
    private void loadAssignmentsLive() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {

                list.clear();

                for (DataSnapshot ds : snap.getChildren()) {
                    TAssignmentModel m = ds.getValue(TAssignmentModel.class);
                    if (m != null) list.add(m);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        TAsssignmentActivity.this,
                        "Failed to load assignments",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
