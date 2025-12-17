package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.StudentAttendanceAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.StudentAttendance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class S_Class_Dashboard_Attendance extends AppCompatActivity {

    private RecyclerView rvAttendance;
    private TextView tvEmpty;
    private StudentAttendanceAdapter adapter;
    private String classId;


    private final List<StudentAttendance> fullList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_attendance);

        String studentUid = FirebaseAuth.getInstance().getUid();

        classId = getIntent().getStringExtra("classId");

        if (classId == null) {
            Toast.makeText(this, "Invalid class", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        MaterialToolbar toolbar = findViewById(R.id.toolbarAttendance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAttendance = findViewById(R.id.rvAttendance);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAttendanceAdapter(this, new ArrayList<>());
        rvAttendance.setAdapter(adapter);

        setupTabs();
        loadDummyData();
        filter("ALL");
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabLayoutAttendance);

        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Present"));
        tabLayout.addTab(tabLayout.newTab().setText("Absent"));
        tabLayout.addTab(tabLayout.newTab().setText("Leave"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filter(tab.getText().toString().toUpperCase());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadDummyData() {
        fullList.add(new StudentAttendance("09 Dec 2025", StudentAttendance.STATUS_PRESENT));
        fullList.add(new StudentAttendance("10 Dec 2025", StudentAttendance.STATUS_ABSENT));
        fullList.add(new StudentAttendance("11 Dec 2025", StudentAttendance.STATUS_LEAVE));
    }

    private void filter(String status) {
        List<StudentAttendance> filtered = new ArrayList<>();

        if ("ALL".equals(status)) {
            filtered.addAll(fullList);
        } else {
            for (StudentAttendance a : fullList) {
                if (a.getStatus().equals(status.toLowerCase())) {
                    filtered.add(a);
                }
            }
        }

        adapter.updateList(filtered);

        if (filtered.isEmpty()) {
            rvAttendance.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            rvAttendance.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
