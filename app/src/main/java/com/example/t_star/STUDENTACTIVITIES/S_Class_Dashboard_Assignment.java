package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.AssignmentAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.Assignment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class S_Class_Dashboard_Assignment extends AppCompatActivity {

    private RecyclerView rvAssignments;
    private TextView tvEmpty;
    private TabLayout tabLayout;

    private AssignmentAdapter adapter;
    private final List<Assignment> fullList = new ArrayList<>();

    private String classId;
    private String studentId;

    private DatabaseReference assignmentRef;
    DatabaseReference submissionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_assignment);

        classId = getIntent().getStringExtra("classId");
        studentId = FirebaseAuth.getInstance().getUid();

        if (classId == null || studentId == null) {
            Toast.makeText(this, "Invalid class", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        assignmentRef = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId);

        submissionRef = FirebaseDatabase.getInstance()
                .getReference("Assignments_Submissions")
                .child(classId);


        setupToolbar();
        setupViews();
        setupTabs();
        loadAssignments();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarAssignments);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViews() {
        rvAssignments = findViewById(R.id.rvAssignments);
        tvEmpty = findViewById(R.id.tvEmptyAssignments);
        tabLayout = findViewById(R.id.tabLayoutAssignments);

        rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssignmentAdapter(this, new ArrayList<>(), classId);
        rvAssignments.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Submitted"));
        tabLayout.addTab(tabLayout.newTab().setText("Evaluated"));

        tabLayout.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        filterByStatus(tab.getPosition());
                    }
                    @Override public void onTabUnselected(TabLayout.Tab tab) {}
                    @Override public void onTabReselected(TabLayout.Tab tab) {}
                });
    }

    private void loadAssignments() {

        assignmentRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        fullList.clear();

                        for (DataSnapshot aSnap :
                                snapshot.getChildren()) {

                            String assignmentId = aSnap.getKey();
                            String title = aSnap.child("title")
                                    .getValue(String.class);
                            String subject = aSnap.child("description")
                                    .getValue(String.class);

                            long dueDateMillis = aSnap.child("dueDateMillis")
                                    .getValue(Long.class) != null
                                    ? aSnap.child("dueDateMillis").getValue(Long.class)
                                    : 0L;


                            int totalMarks =
                                    aSnap.child("totalMarks")
                                            .getValue(Integer.class) != null
                                            ? aSnap.child("totalMarks")
                                            .getValue(Integer.class)
                                            : 0;

                            fetchSubmission(
                                    assignmentId,
                                    title,
                                    subject,
                                    dueDateMillis,
                                    totalMarks
                            );
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {
                        Toast.makeText(
                                S_Class_Dashboard_Assignment.this,
                                "Failed to load assignments",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    /*private void fetchSubmission(
            String assignmentId,
            String title,
            String subject,
            long dueDateMillis,
            int totalMarks) {

        assignmentRef.child(assignmentId)
                .child("submissions").child(studentId)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snap) {

                                String status =
                                        Assignment.STATUS_PENDING;
                                int marks = 0;

                                if (snap.exists()) {
                                    status = snap.child("status")
                                            .getValue(String.class);

                                    marks = snap.child("marksObtained")
                                            .getValue(Integer.class) != null
                                            ? snap.child("marksObtained")
                                            .getValue(Integer.class)
                                            : 0;
                                }

                                fullList.add(
                                        new Assignment(
                                                assignmentId,
                                                title,
                                                subject,
                                                dueDateMillis,
                                                status,
                                                marks,
                                                totalMarks
                                        )
                                );

                                filterByStatus(
                                        tabLayout
                                                .getSelectedTabPosition()
                                );
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error) {}
                        });
    }*/



    private void fetchSubmission(
            String assignmentId,
            String title,
            String subject,
            long dueDateMillis,
            int totalMarks) {

        submissionRef.child(assignmentId)
                .child(studentId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                        String status = Assignment.STATUS_PENDING;
                        int marks = 0;

                        if (snap.exists()) {
                            status = snap.child("status")
                                    .getValue(String.class);

                            marks = snap.child("marksObtained")
                                    .getValue(Integer.class) != null
                                    ? snap.child("marksObtained").getValue(Integer.class)
                                    : 0;
                        }

                        Assignment assignment = new Assignment(
                                assignmentId,
                                title,
                                subject,
                                dueDateMillis,
                                status,
                                marks,
                                totalMarks
                        );

                        // Remove old entry if exists (IMPORTANT)
                        for (int i = 0; i < fullList.size(); i++) {
                            if (fullList.get(i).getAssignmentId().equals(assignmentId)) {
                                fullList.remove(i);
                                break;
                            }
                        }

                        fullList.add(assignment);
                        filterByStatus(tabLayout.getSelectedTabPosition());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}

                });
    }

    private void filterByStatus(int tabPosition) {

        List<Assignment> filtered = new ArrayList<>();

        for (Assignment a : fullList) {
            switch (tabPosition) {
                case 1:
                    if (Assignment.STATUS_PENDING
                            .equals(a.getStatus()))
                        filtered.add(a);
                    break;
                case 2:
                    if (Assignment.STATUS_SUBMITTED
                            .equals(a.getStatus()))
                        filtered.add(a);
                    break;
                case 3:
                    if (Assignment.STATUS_EVALUATED
                            .equals(a.getStatus()))
                        filtered.add(a);
                    break;
                default:
                    filtered.add(a);
            }
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvAssignments.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvAssignments.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            //adapter.updateList(filtered);
        }

    }
}