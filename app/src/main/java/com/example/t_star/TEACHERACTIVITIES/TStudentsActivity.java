package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
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

    private RecyclerView rvStudents;
    private SearchView searchView;
    private TStudentAdapter adapter;
    private ArrayList<TStudentModel> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudents);

        rvStudents = findViewById(R.id.rvStudents);
        searchView = findViewById(R.id.searchViewStudents);

        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String classId = getIntent().getStringExtra("classId");

        studentList = new ArrayList<>();
        adapter = new TStudentAdapter(this, studentList);
        rvStudents.setAdapter(adapter);

        loadStudents(classId);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { adapter.filter(query); return false; }
            @Override public boolean onQueryTextChange(String text) { adapter.filter(text); return false; }
        });
    }

    private void loadStudents(String classId) {

        Classes_helper.getClassStudents(classId, uids -> {

            if (uids == null || uids.isEmpty()) return;

            studentList.clear();

            for (String uid : uids) {

                Classes_helper.Users_Helper.getUserDetails(uid, user -> {

                    if (user != null) {

                        String name = user.firstname + " " + user.lastname;

                        studentList.add(new TStudentModel(
                                name,
                                uid,
                                R.drawable.taklogo
                        ));

                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
