package com.example.t_star.TEACHERACTIVITIES;

import static com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.ChatHelper.addStudentToClassChat;

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
    private ValueEventListener studentsListener;

    private void loadStudents() {
        DatabaseReference studentsRef =
                FirebaseDatabase.getInstance()
                        .getReference("Classes")
                        .child(classId)
                        .child("students");

        if (studentsListener != null) {
            studentsRef.removeEventListener(studentsListener);
        }

        studentsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                studentList.clear();

                if (!snapshot.exists()) {
                    studentAdapter.updateList(studentList);
                    return;
                }

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getKey();
                    if (uid == null) continue;

                    FirebaseDatabase.getInstance()
                            .getReference("Users")
                            .child(uid)
                            .get()
                            .addOnSuccessListener(u -> {

                                String fname = u.child("firstname").getValue(String.class);
                                String lname = u.child("lastname").getValue(String.class);
                                String email = u.child("email").getValue(String.class);
                                String profileUrl = u.child("profileImageUrl").getValue(String.class);

                                studentList.add(
                                        new TStudentModel(
                                                (fname != null ? fname : "") + " " +
                                                        (lname != null ? lname : ""),
                                                email != null ? email : "",
                                                uid,
                                                profileUrl
                                        )
                                );

                                studentAdapter.updateList(studentList);
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        studentsRef.addValueEventListener(studentsListener);
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

                        if (!snapshot.exists()) {
                            requestAdapter.notifyDataSetChanged();
                            return;
                        }

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String uid = ds.getKey();
                            if (uid == null) continue;

                            requestUidList.add(uid);

                            // 🔥 FETCH USER DATA FROM Users TABLE
                            FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(uid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot u) {

                                            String fname = u.child("firstname").getValue(String.class);
                                            String lname = u.child("lastname").getValue(String.class);
                                            String email = u.child("email").getValue(String.class);
                                            String profileUrl =
                                                    u.child("profileImageUrl").getValue(String.class);

                                            String fullName =
                                                    (fname != null ? fname : "") + " " +
                                                            (lname != null ? lname : "");

                                            requestAdapter.updateStudentData(
                                                    uid,
                                                    fullName.trim().isEmpty() ? "Student" : fullName,
                                                    email != null ? email : "",
                                                    profileUrl
                                            );
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                        }

                        requestAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
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

                // ✅ ADD STUDENT TO CHAT TABLE
                addStudentToClassChat(classId, uid);

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
