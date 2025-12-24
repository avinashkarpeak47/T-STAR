package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Adaptar.AttendanceAdapter;
import com.example.t_star.STUDENTACTIVITIES.Models.Attendance;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class S_Class_Dashboard_Attendance extends AppCompatActivity {

    private RecyclerView rvAttendance;
    private TabLayout tabLayoutAttendanceStatus;

    private TextView tvEmptyAttendance;
    private TextView tvAttendancePercentage;
    private TextView tvAttendanceSummary;
    private TextView tvSelectedMonth;

    private AttendanceAdapter adapter;

    private final List<Attendance> fullList = new ArrayList<>();
    private final List<Attendance> monthFilteredList = new ArrayList<>();

    private String classId;
    private String studentId;

    private int selectedMonth;
    private int selectedYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_class_dashboard_attendance);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupMonthAndYear();
        setupTabs();
        loadAttendanceFromFirebase();
    }

    private void initViews() {
        rvAttendance = findViewById(R.id.rvAttendance);
        tabLayoutAttendanceStatus = findViewById(R.id.tabLayoutAttendanceStatus);
        tvEmptyAttendance = findViewById(R.id.tvEmptyAttendance);
        tvAttendancePercentage = findViewById(R.id.tvAttendancePercentage);
        tvAttendanceSummary = findViewById(R.id.tvAttendanceSummary);
        tvSelectedMonth = findViewById(R.id.tvSelectedMonth);

        classId = getIntent().getStringExtra("classId");
        studentId = FirebaseAuth.getInstance().getUid();

        if (classId == null || studentId == null) {
            Toast.makeText(this, "Invalid class data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarAttendance);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvAttendance.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttendanceAdapter(this, new ArrayList<>());
        rvAttendance.setAdapter(adapter);
    }

    private void setupMonthAndYear() {
        Calendar cal = Calendar.getInstance();
        selectedMonth = cal.get(Calendar.MONTH);
        selectedYear = cal.get(Calendar.YEAR);

        updateMonthText();
        tvSelectedMonth.setOnClickListener(v -> showMonthYearPicker());
    }

    private void setupTabs() {
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("All"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Present"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Absent"));
        tabLayoutAttendanceStatus.addTab(tabLayoutAttendanceStatus.newTab().setText("Leave"));

        tabLayoutAttendanceStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterByTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadAttendanceFromFirebase() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(classId)
                .child(studentId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fullList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Attendance a = snap.getValue(Attendance.class);
                    if (a != null) {
                        fullList.add(a);
                    }
                }

                applyMonthFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(
                        S_Class_Dashboard_Attendance.this,
                        "Attendance load failed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

}

    private void applyMonthFilter() {

        monthFilteredList.clear();

        for (Attendance a : fullList) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(a.getTimestamp());

            if (cal.get(Calendar.MONTH) == selectedMonth &&
                    cal.get(Calendar.YEAR) == selectedYear) {
                monthFilteredList.add(a);
            }
        }

        if (monthFilteredList.isEmpty()) {
            rvAttendance.setVisibility(View.GONE);
            tvEmptyAttendance.setVisibility(View.VISIBLE);
            tvAttendancePercentage.setText("0%");
            tvAttendanceSummary.setText("No attendance data");
        } else {
            rvAttendance.setVisibility(View.VISIBLE);
            tvEmptyAttendance.setVisibility(View.GONE);
            calculateMonthlyPercentage();
            filterByTab(tabLayoutAttendanceStatus.getSelectedTabPosition());
        }
    }

    private void filterByTab(int tabPosition) {

        List<Attendance> filtered = new ArrayList<>();

        for (Attendance a : monthFilteredList) {
            switch (tabPosition) {
                case 0:
                    filtered.add(a);
                    break;
                case 1:
                    if (Attendance.STATUS_PRESENT.equals(a.getStatus()))
                        filtered.add(a);
                    break;
                case 2:
                    if (Attendance.STATUS_ABSENT.equals(a.getStatus()))
                        filtered.add(a);
                    break;
                case 3:
                    if (Attendance.STATUS_LEAVE.equals(a.getStatus()))
                        filtered.add(a);
                    break;
            }
        }

        if (filtered.isEmpty()) {
            rvAttendance.setVisibility(View.GONE);
            tvEmptyAttendance.setVisibility(View.VISIBLE);
        } else {
            rvAttendance.setVisibility(View.VISIBLE);
            tvEmptyAttendance.setVisibility(View.GONE);
            adapter.updateList(filtered);
        }
    }

    private void calculateMonthlyPercentage() {

        int present = 0;
        int total = 0;

        for (Attendance a : monthFilteredList) {
            if (Attendance.STATUS_PRESENT.equals(a.getStatus())) {
                present++;
                total++;
            } else if (Attendance.STATUS_ABSENT.equals(a.getStatus())) {
                total++;
            }
        }

        if (total == 0) {
            tvAttendancePercentage.setText("0%");
            tvAttendanceSummary.setText("No attendance data");
        } else {
            int percent = (present * 100) / total;
            tvAttendancePercentage.setText(percent + "%");
            tvAttendanceSummary.setText(present + " / " + total + " Days Present");
        }
    }

    private void showMonthYearPicker() {

        final String[] months = {
                "January","February","March","April","May","June",
                "July","August","September","October","November","December"
        };

        final int startYear = 2023;
        final int endYear = Calendar.getInstance().get(Calendar.YEAR) + 5;

        List<String> years = new ArrayList<>();
        for (int y = startYear; y <= endYear; y++) {
            years.add(String.valueOf(y));
        }

        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_month_year_picker, null);

        NumberPicker monthPicker = dialogView.findViewById(R.id.pickerMonth);
        NumberPicker yearPicker = dialogView.findViewById(R.id.pickerYear);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setDisplayedValues(months);
        monthPicker.setValue(selectedMonth);

        yearPicker.setMinValue(0);
        yearPicker.setMaxValue(years.size() - 1);
        yearPicker.setDisplayedValues(years.toArray(new String[0]));
        yearPicker.setValue(years.indexOf(String.valueOf(selectedYear)));

        new AlertDialog.Builder(this)
                .setTitle("Select Month & Year")
                .setView(dialogView)
                .setPositiveButton("Apply", (d, w) -> {
                    selectedMonth = monthPicker.getValue();
                    selectedYear = Integer.parseInt(years.get(yearPicker.getValue()));
                    updateMonthText();
                    applyMonthFilter();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateMonthText() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, selectedMonth);
        cal.set(Calendar.YEAR, selectedYear);

        tvSelectedMonth.setText(
                android.text.format.DateFormat.format("MMMM yyyy", cal)
        );
    }
}
