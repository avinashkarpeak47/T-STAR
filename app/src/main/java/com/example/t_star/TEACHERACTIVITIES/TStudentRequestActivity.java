package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentRequestAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class TStudentRequestActivity extends AppCompatActivity {

    RecyclerView rvStudents;
    ArrayList<String> requestList = new ArrayList<>();
    TStudentRequestAdapter adapter;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudent_request);

        classId = getIntent().getStringExtra("classId");
        if (classId == null) {
            Toast.makeText(this, "Class ID missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            setResult(RESULT_OK); // 🔥 IMPORTANT
            finish();
        });

        rvStudents = findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TStudentRequestAdapter(this, requestList, this::acceptStudent);
        rvStudents.setAdapter(adapter);

        loadRequests();
    }

    private void loadRequests() {
        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("joinRequests")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snap) {

                        requestList.clear();
                        adapter.notifyDataSetChanged();

                        for (DataSnapshot ds : snap.getChildren()) {

                            String uid = ds.getKey();
                            requestList.add(uid);

                            Classes_helper.Users_Helper.getUserDetails(uid, user -> {
                                if (user != null) {
                                    String fullName = user.firstname + " " + user.lastname;
                                    adapter.updateStudentName(uid, fullName);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) { }
                });
    }

    private void acceptStudent(String studentUid) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference classRef = rootRef.child("Classes").child(classId);

        // 1️⃣ Teacher side
        classRef.child("students")
                .child(studentUid)
                .setValue("approved");

        // 2️⃣ Student side (MOST IMPORTANT)
        DatabaseReference studentClassRef =
                rootRef.child("StudentClasses")
                        .child(studentUid)
                        .child(classId);

        studentClassRef.child("classId").setValue(classId);
        studentClassRef.child("status").setValue("approved");
        studentClassRef.child("seenApproved").setValue(false);

        // Class info fetch
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String className = snapshot.child("className").getValue(String.class);
                String teacherId = snapshot.child("teacherId").getValue(String.class);

                studentClassRef.child("className").setValue(className);
                studentClassRef.child("teacherId").setValue(teacherId);

                // 3️⃣ remove request
                classRef.child("joinRequests").child(studentUid).removeValue();

                // 4️⃣ update count
                Classes_helper.updateStudentCount(classId);

                Toast.makeText(TStudentRequestActivity.this,
                        "Student Approved Successfully",
                        Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TStudentRequestActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK); // 🔥 ensure refresh
        super.onBackPressed();
    }
}
