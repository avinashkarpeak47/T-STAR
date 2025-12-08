package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t_star.R;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.AssignmentAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.Assignment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class S_class_dashboard_assignment extends AppCompatActivity {

    private RecyclerView rvAssignments;
    private TabLayout tabLayoutStatus;
    private AssignmentAdapter adapter;
    private final List<Assignment> fullList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_assignment);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAssignments);
        setSupportActionBar(toolbar);

        rvAssignments = findViewById(R.id.rvAssignments);
        tabLayoutStatus = findViewById(R.id.tabLayoutStatus);

        rvAssignments.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssignmentAdapter(this, new ArrayList<>());
        rvAssignments.setAdapter(adapter);

        setupTabs();
        loadDummyData();   // later replace with Firebase
        filterList("ALL");
    }

    private void setupTabs() {
        tabLayoutStatus.addTab(tabLayoutStatus.newTab().setText("All"));
        tabLayoutStatus.addTab(tabLayoutStatus.newTab().setText("Pending"));
        tabLayoutStatus.addTab(tabLayoutStatus.newTab().setText("Submit"));
        tabLayoutStatus.addTab(tabLayoutStatus.newTab().setText("Checked"));

        tabLayoutStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: filterList("ALL"); break;
                    case 1: filterList(Assignment.STATUS_PENDING); break;
                    case 2: filterList(Assignment.STATUS_SUBMITTED); break;
                    case 3: filterList(Assignment.STATUS_CHECKED); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadDummyData() {
        fullList.clear();

        fullList.add(new Assignment(
                "DSA Assignment 1",
                "Solve stack, queue and linked list problems",
                "Due: 10 Dec",
                Assignment.STATUS_PENDING
        ));

        fullList.add(new Assignment(
                "Java Mini Project",
                "RecyclerView and Adapter practice",
                "Submitted on: 5 Dec",
                Assignment.STATUS_SUBMITTED
        ));

        fullList.add(new Assignment(
                "Computer Networks",
                "OSI & TCP/IP Model",
                "Checked",
                Assignment.STATUS_CHECKED
        ));
    }

    private void filterList(String status) {
        if ("ALL".equals(status)) {
            adapter.updateList(new ArrayList<>(fullList));
            return;
        }

        List<Assignment> filtered = new ArrayList<>();
        for (Assignment a : fullList) {
            if (status.equals(a.getStatus())) {
                filtered.add(a);
            }
        }
        adapter.updateList(filtered);
    }
}
