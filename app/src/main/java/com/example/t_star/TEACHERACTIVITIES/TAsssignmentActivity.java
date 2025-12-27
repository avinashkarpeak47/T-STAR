package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    SearchView searchView;
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
        toolbar.setTitle("Assignments" );
        toolbar.setNavigationOnClickListener(v -> finish());

        ImageButton btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> {
            Intent i = new Intent(this, TCreateNewAssignActivity.class);
            i.putExtra("classId", classId);
            startActivity(i);
        });

        rvAssignments = findViewById(R.id.rvAssignments);
        rvAssignments.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TAssignmentAdapter(model -> {
            Intent i = new Intent(this, TAssignDetailActivity.class);
            i.putExtra("classId", classId);
            i.putExtra("assignmentId", model.getId());
            i.putExtra("title", model.getTitle());
            i.putExtra("description", model.getDescription());
            i.putExtra("dueDate", model.getDueDate());
            i.putExtra("totalStudents", model.getTotalStudents());
            i.putExtra("submittedCount", model.getSubmittedCount());
            i.putExtra("dueDateMillis", model.getDueDateMillis());
            startActivity(i);
        });

        rvAssignments.setAdapter(adapter);

        searchView = findViewById(R.id.searchViewGrid);
        setupSearch();

        loadAssignments();
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

    private void loadAssignments() {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {

                list.clear();

                for (DataSnapshot ds : snap.getChildren()) {

                    TAssignmentModel m = ds.getValue(TAssignmentModel.class);
                    if (m == null) continue;

                    Long dueMillis = ds.child("dueDateMillis").getValue(Long.class);
                    m.setDueDateMillis(dueMillis == null ? 0L : dueMillis);

                    DatabaseReference subRef = FirebaseDatabase.getInstance()
                            .getReference("Assignments_Submissions")
                            .child(classId)
                            .child(m.getId());

                    subRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot subSnap) {

                            m.setSubmittedCount((int) subSnap.getChildrenCount());

                            // add / update list item
                            boolean updated = false;

                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getId().equals(m.getId())) {
                                    list.set(i, m);
                                    updated = true;
                                    break;
                                }
                            }

                            if (!updated)
                                list.add(m);

                            // 🔹 update adapter safely
                            adapter.setData(list);

                            // keep active search
                            adapter.filter(searchView.getQuery().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}
