package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentAdapter;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentModel;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentRequestAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class TStudentsActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private androidx.appcompat.widget.SearchView searchView;
    private TabLayout tabLayout;

    private TStudentAdapter studentAdapter;
    private final ArrayList<TStudentModel> studentList = new ArrayList<>();

    private TStudentRequestAdapter requestAdapter;
    private final ArrayList<String> requestUidList = new ArrayList<>();

    private String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudents);

        rvStudents = findViewById(R.id.rvStudents);
        searchView = findViewById(R.id.searchViewStudents);
        tabLayout = findViewById(R.id.tabLayoutStudents);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        classId = getIntent().getStringExtra("classId");
        if (classId == null) {
            Toast.makeText(this, "Class ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        // ✅ STUDENT ADAPTER (NO REMOVE)
        studentAdapter = new TStudentAdapter(this, studentList);

        // ✅ REQUEST ADAPTER (ACCEPT + REMOVE REQUEST ONLY)
        requestAdapter = new TStudentRequestAdapter(
                this,
                requestUidList,
                new TStudentRequestAdapter.OnRequestAction() {
                    @Override
                    public void onAccept(String uid) {
                        acceptStudent(uid);
                    }

                    @Override
                    public void onRemove(String uid) {
                        removeRequest(uid);
                    }
                }
        );

        tabLayout.addTab(tabLayout.newTab().setText("Students"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));

        // default
        rvStudents.setAdapter(studentAdapter);
        loadStudents();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    rvStudents.setAdapter(studentAdapter);
                    loadStudents();
                } else {
                    rvStudents.setAdapter(requestAdapter);
                    loadPendingRequests();
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                if (rvStudents.getAdapter() == studentAdapter)
                    studentAdapter.filter(q);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String q) {
                if (rvStudents.getAdapter() == studentAdapter)
                    studentAdapter.filter(q);
                return false;
            }
        });
    }

    // ================= STUDENTS =================
    private void loadStudents() {

        studentList.clear();
        studentAdapter.updateList(studentList);

        Classes_helper.getClassStudents(classId, uids -> {

            if (uids == null || uids.isEmpty()) {
                studentAdapter.updateList(studentList);
                return;
            }

            final int total = uids.size();
            final int[] loaded = {0};

            for (String uid : uids) {
                Classes_helper.Users_Helper.getUserDetails(uid, user -> {
                    if (user != null) {
                        studentList.add(
                                new TStudentModel(
                                        user.firstname + " " + user.lastname,
                                        user.email,
                                        uid,
                                        R.drawable.taklogo
                                )
                        );
                    }
                    loaded[0]++;
                    if (loaded[0] == total) {
                        studentAdapter.updateList(studentList);
                    }
                });
            }
        });
    }

    // ================= PENDING =================
    private void loadPendingRequests() {

        requestUidList.clear();
        requestAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("joinRequests")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String uid = ds.getKey();
                            requestUidList.add(uid);

                            Classes_helper.Users_Helper.getUserDetails(uid, user -> {
                                if (user != null) {
                                    requestAdapter.updateStudentData(
                                            uid,
                                            user.firstname + " " + user.lastname,
                                            user.email
                                    );
                                }
                            });
                        }
                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ================= ACCEPT =================
    private void acceptStudent(String uid) {

        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference classRef = root.child("Classes").child(classId);

        classRef.child("students").child(uid).setValue("approved");

        DatabaseReference studentClass =
                root.child("StudentClasses").child(uid).child(classId);

        studentClass.child("classId").setValue(classId);
        studentClass.child("status").setValue("approved");
        studentClass.child("seenApproved").setValue(false);

        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {

                studentClass.child("className")
                        .setValue(s.child("className").getValue(String.class));
                studentClass.child("teacherId")
                        .setValue(s.child("teacherId").getValue(String.class));

                classRef.child("joinRequests").child(uid).removeValue();
                Classes_helper.updateStudentCount(classId);

                Toast.makeText(
                        TStudentsActivity.this,
                        "Student Approved",
                        Toast.LENGTH_SHORT
                ).show();

                loadPendingRequests();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // ================= REMOVE REQUEST ONLY =================
     private  void removeRequest(String uid) {

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("joinRequests")
                .child(uid)
                .removeValue();

        FirebaseDatabase.getInstance()
                .getReference("StudentClasses")
                .child(uid)
                .child(classId)
                .removeValue();


        Toast.makeText(this,
                "Request removed",
                Toast.LENGTH_SHORT).show();

        loadPendingRequests();
    }
}
