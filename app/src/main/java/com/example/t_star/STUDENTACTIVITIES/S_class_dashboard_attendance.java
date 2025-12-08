package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.AttendanceAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.Attendance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class S_class_dashboard_attendance extends AppCompatActivity {

    private RecyclerView rvAttendance;
    private TabLayout tabLayoutAttendanceStatus;
    private AttendanceAdapter adapter;

    // full list from DB
    private final List<Attendance> fullList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_attendance);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAttendance);
        setSupportActionBar(toolbar);

        rvAttendance = findViewById(R.id.rvAttendance);
        tabLayoutAttendanceStatus = findViewById(R.id.tabLayoutAttendanceStatus);

        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(this, new ArrayList<>());
        rvAttendance.setAdapter(adapter);

        setupTabs();

        // OPTION 1 (now): local dummy data
        loadDummyData();
        filterList("ALL");

        // OPTION 2 (later): Firebase
        // loadFromFirebase();
    }

    private void setupTabs() {
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("All"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Present"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Absent"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Leave"));

        tabLayoutAttendanceStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: filterList("ALL"); break;
                    case 1: filterList(Attendance.STATUS_PRESENT); break;
                    case 2: filterList(Attendance.STATUS_ABSENT); break;
                    case 3: filterList(Attendance.STATUS_LEAVE); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // TEMP: Dummy data
    private void loadDummyData() {
        fullList.clear();

        fullList.add(new Attendance(
                "Archit Kulkarni",
                "01",
                "09 Dec 2025",
                "Java",
                Attendance.STATUS_PRESENT
        ));

        fullList.add(new Attendance(
                "Rahul Sharma",
                "02",
                "09 Dec 2025",
                "Java",
                Attendance.STATUS_ABSENT
        ));

        fullList.add(new Attendance(
                "Priya Patil",
                "03",
                "09 Dec 2025",
                "Java",
                Attendance.STATUS_LEAVE
        ));

        fullList.add(new Attendance(
                "Sneha More",
                "04",
                "09 Dec 2025",
                "DSA",
                Attendance.STATUS_PRESENT
        ));
    }

    private void filterList(String status) {
        if ("ALL".equals(status)) {
            adapter.updateList(new ArrayList<>(fullList));
            return;
        }

        List<Attendance> filtered = new ArrayList<>();
        for (Attendance a : fullList) {
            if (status.equals(a.getStatus())) {
                filtered.add(a);
            }
        }
        adapter.updateList(filtered);
    }
}
