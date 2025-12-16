package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.Toast;

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

    private void acceptStudent(String uid) {

        DatabaseReference classRef =
                FirebaseDatabase.getInstance().getReference("Classes").child(classId);

        classRef.child("students").child(uid).setValue(uid)
                .addOnSuccessListener(a -> {

                    classRef.child("joinRequests").child(uid).removeValue();
                    Classes_helper.updateStudentCount(classId);

                    Toast.makeText(this, "Student Added!", Toast.LENGTH_SHORT).show();

                    // 🔥 THIS IS THE KEY
                    setResult(RESULT_OK);
                });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK); // 🔥 ensure refresh
        super.onBackPressed();
    }
}
