package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

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

    RecyclerView rvAssignments;
    TAssignmentAdapter adapter;
    List<TAssignmentModel> list = new ArrayList<>();
    String classId, className;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasssignment);

        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");

        ref = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Assignments • " + className);
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageButton btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, TCreateNewAssignActivity.class);
            i.putExtra("classId", classId);
            startActivity(i);
        });

        rvAssignments = findViewById(R.id.rvAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TAssignmentAdapter(list, model -> {
            Intent i = new Intent(this, TAssignDetailActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("assignmentId", model.getId());
            i.putExtra("title", model.getTitle());
            i.putExtra("description", model.getDescription());
            i.putExtra("dueDate", model.getDueDate());
            i.putExtra("totalStudents", model.getTotalStudents());
            i.putExtra("submittedCount", model.getSubmittedCount());
            startActivity(i);
        });

        rvAssignments.setAdapter(adapter);
        loadAssignments();
    }

    private void loadAssignments() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                list.clear();
                for (DataSnapshot ds : snap.getChildren()) {
                    TAssignmentModel m = ds.getValue(TAssignmentModel.class);
                    if (m != null) list.add(m);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError e) {
                Toast.makeText(TAsssignmentActivity.this,"Error loading",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
