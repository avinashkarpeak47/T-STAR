package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentAdapter;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class TStudentsActivity extends AppCompatActivity {

    private static final int REQ_REQUESTS = 101;

    ImageView btnNotifications;
    RecyclerView rvStudents;
    SearchView searchView;

    TStudentAdapter adapter;
    ArrayList<TStudentModel> studentList = new ArrayList<>();
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudents);

        rvStudents = findViewById(R.id.rvStudents);
        searchView = findViewById(R.id.searchViewStudents);
        btnNotifications = findViewById(R.id.btnNotifications);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        classId = getIntent().getStringExtra("classId");

        btnNotifications.setOnClickListener(v -> {
            Intent i = new Intent(this, TStudentRequestActivity.class);
            i.putExtra("classId", classId);
            startActivityForResult(i, REQ_REQUESTS); // 🔥 IMPORTANT
        });

        adapter = new TStudentAdapter(this, studentList);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));
        rvStudents.setAdapter(adapter);

        loadStudents();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) { adapter.filter(q); return false; }
            @Override public boolean onQueryTextChange(String q) { adapter.filter(q); return false; }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }

    // 🚀 STEP 1 — fetch student UIDs
    private void loadStudents() {
        Classes_helper.getClassStudents(classId, uids -> {

            if (uids == null || uids.isEmpty()) {
                studentList.clear();
                adapter.updateList(studentList);
                return;
            }

            loadStudentDetails(uids);
        });
    }

    // 🚀 STEP 2 — fetch full user details
    private void loadStudentDetails(ArrayList<String> uids) {

        studentList.clear();
        final int total = uids.size();
        final int[] loaded = {0};

        for (String uid : uids) {

            Classes_helper.Users_Helper.getUserDetails(uid, user -> {

                if (user != null) {
                    String name = user.firstname + " " + user.lastname;
                    studentList.add(new TStudentModel(name, uid, R.drawable.taklogo));
                }

                loaded[0]++;

                if (loaded[0] == total) {
                    adapter.updateList(studentList); // 🔥 update once
                }
            });
        }
    }

    // 🔥 THIS MAKES THE MAGIC HAPPEN
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_REQUESTS && resultCode == RESULT_OK) {
            loadStudents(); // 🔥 FORCE REFRESH
        }
    }
}
