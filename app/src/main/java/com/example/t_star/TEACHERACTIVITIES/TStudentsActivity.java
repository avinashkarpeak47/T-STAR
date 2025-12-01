package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentAdapter;
import com.example.t_star.TEACHERACTIVITIES.STUDENTS.TStudentModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TStudentsActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private SearchView searchView;
    private TStudentAdapter adapter;
    private List<TStudentModel> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tstudents);

        rvStudents = findViewById(R.id.rvStudents);
        searchView = findViewById(R.id.searchViewStudents);

        rvStudents.setLayoutManager(new LinearLayoutManager(this));


        // ----- Toolbar -----
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // handle back arrow
        toolbar.setNavigationOnClickListener(v -> finish());

        // Sample Data
        studentList = new ArrayList<>();
        studentList.add(new TStudentModel("Avinash", "01", R.drawable.taklogo));
        studentList.add(new TStudentModel("Ankita", "02", R.drawable.taklogo));
        studentList.add(new TStudentModel("Rohan", "03", R.drawable.taklogo));
        studentList.add(new TStudentModel("Priya", "04", R.drawable.taklogo));

        adapter = new TStudentAdapter(this, studentList);
        rvStudents.setAdapter(adapter);

        // 🔍 Search Feature
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.filter(s);
                return false;
            }
        });
    }
}
