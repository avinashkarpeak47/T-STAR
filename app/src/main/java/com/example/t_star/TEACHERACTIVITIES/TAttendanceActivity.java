package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.ATTENDANCE.Attendacemodel;
import com.example.t_star.TEACHERACTIVITIES.ATTENDANCE.AttendanceAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TAttendanceActivity extends AppCompatActivity {

    RecyclerView rvAttendance;
    AttendanceAdapter adapter;
    ArrayList<Attendacemodel> studentList = new ArrayList<>();

    TextView tvclass, tvsubject, tvdate, tvSummary;
    CheckBox cbHoliday;
    AppCompatButton btnSave;


    String classId, subjectId, selectedDate;
    boolean isHoliday = false;
    boolean isDateLocked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattendance);

        // ---------------- INIT ----------------
        rvAttendance = findViewById(R.id.rvAttendance);
        tvclass = findViewById(R.id.tvclass);
        tvsubject = findViewById(R.id.tvsubject);
        tvdate = findViewById(R.id.tvdate);
        tvSummary = findViewById(R.id.tvSummary);
        cbHoliday = findViewById(R.id.cbHoliday);
        btnSave = findViewById(R.id.btnSave);
        Log.d("ATT_DEBUG", "classId = " + classId);
        Log.d("ATT_DEBUG", "subjectId = " + subjectId);


        rvAttendance.setLayoutManager(new LinearLayoutManager(this));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ---------------- FETCH CLASS & SUBJECT ----------------
        classId = getIntent().getStringExtra("classId");
        subjectId = getIntent().getStringExtra("subjectId");

        if (classId == null) classId = "class";
        if (subjectId == null) subjectId = "";
        fetchClassAndSubject();

        tvclass.setText("Class: " + classId);
        tvsubject.setText("Subject:" + subjectId);
        // ---------------- DEFAULT TODAY DATE ----------------
        Calendar c = Calendar.getInstance();
        selectedDate = c.get(Calendar.YEAR) + "-" +
                (c.get(Calendar.MONTH) + 1) + "-" +
                c.get(Calendar.DAY_OF_MONTH);

        tvdate.setText("Date: " + selectedDate);

        // ---------------- DATE PICKER ON CLICK ----------------
        tvdate.setOnClickListener(v -> openDatePicker());

        // ---------------- FETCH STUDENTS ----------------
        fetchStudents();

        // ---------------- HOLIDAY TOGGLE ----------------
        cbHoliday.setOnCheckedChangeListener((b, checked) -> {
            isHoliday = checked;

            if (adapter != null) {
                adapter.setHoliday(checked);
            }

            updateSummary();
        });

        // ---------------- SAVE ----------------
        btnSave.setOnClickListener(v -> saveAttendance());
    }

    // =====================================================
    // DATE PICKER
    // =====================================================
    private void openDatePicker() {

        Calendar c = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {

                    selectedDate = year + "-" + (month + 1) + "-" + day;
                    tvdate.setText("Date: " + selectedDate);

                    checkAttendanceLocked(); // 🔒 IMPORTANT
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void checkAttendanceLocked() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(classId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                boolean alreadySaved = false;

                // 🔍 Check ANY student has this date
                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    if (studentSnap.child(selectedDate).exists()) {
                        alreadySaved = true;
                        break;
                    }
                }
                if (alreadySaved) {
                    isDateLocked = true;   // ⭐ ADD THIS
                    loadSavedAttendanceFromStudentNodes(snapshot);
                    lockUI();
                    calculatePercentage();
                } else {
                    isDateLocked = false;  // ⭐ ADD THIS
                    resetDefaultAttendance();
                    unlockUI();
                    calculatePercentage();
                }

            }

            private void loadSavedAttendanceFromStudentNodes(DataSnapshot classSnapshot) {

                int present = 0;
                int absent = 0;

                for (Attendacemodel s : studentList) {

                    DataSnapshot studentSnap = classSnapshot.child(s.studentId);
                    if (!studentSnap.exists()) continue;

                    DataSnapshot daySnap = studentSnap.child(selectedDate);
                    if (!daySnap.exists()) continue;

                    String status = daySnap.child("status").getValue(String.class);

                    if ("present".equals(status)) {
                        s.status = "present";
                        present++;
                    } else if ("absent".equals(status)) {
                        s.status = "absent";
                        absent++;
                    } else if ("leave".equals(status)) {
                        s.status = "leave";
                    }
                }

                adapter.notifyDataSetChanged();

                if (isHoliday) {
                    tvSummary.setText("Today is Holiday");
                } else {
                    tvSummary.setText(
                            "Total Present: " + present + "    Absent: " + absent
                    );
                }
            }


            private void unlockUI() {
                btnSave.setEnabled(true);
                cbHoliday.setEnabled(true);
                if (adapter != null) adapter.setLocked(false);
            }


            private void resetDefaultAttendance() {
                isHoliday = false;
                cbHoliday.setChecked(false);

                for (Attendacemodel s : studentList) {
                    s.status = "present"; // default
                }

                if (adapter != null) adapter.notifyDataSetChanged();
                updateSummary();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void loadSavedAttendance(DataSnapshot snapshot) {

        String dayStatus = snapshot.child("dayStatus").getValue(String.class);

        int present = 0;
        int absent = 0;

        if ("HOLIDAY".equals(dayStatus)) {
            isHoliday = true;
            cbHoliday.setChecked(true);
            tvSummary.setText("Today is Holiday");

            for (Attendacemodel s : studentList) {
                s.status = "leave";
            }

            adapter.notifyDataSetChanged();
            return;
        }

        isHoliday = false;
        cbHoliday.setChecked(false);

        DataSnapshot records = snapshot.child("records");

        for (Attendacemodel s : studentList) {

            String st = records.child(s.studentId).getValue(String.class);

            if ("present".equals(st)) {
                s.status = "present";
                present++;
            } else if ("absent".equals(st)) {
                s.status = "absent";
                absent++;
            } else if ("leave".equals(st)) {
                s.status = "leave";
            }
        }

        adapter.notifyDataSetChanged();

        tvSummary.setText(
                "Total Present: " + present + "    Absent: " + absent
        );
    }


    // =====================================================
    // FETCH STUDENTS
    // =====================================================
    private void fetchStudents() {

        Classes_helper.getClassStudents(classId, uids -> {

            studentList.clear();
            final int[] roll = {1};
            final int total = uids.size();
            final int[] loaded = {0};

            for (String uid : uids) {

                Classes_helper.Users_Helper.getUserDetails(uid, user -> {

                    if (user != null) {
                        String name = user.firstname + " " + user.lastname;
                        studentList.add(new Attendacemodel(roll[0]++, uid, name));
                    }

                    loaded[0]++;

                    if (loaded[0] == total) {

                        adapter = new AttendanceAdapter(studentList);
                        rvAttendance.setAdapter(adapter);

                        updateSummary();
                        checkAttendanceLocked(); // 🔥 VERY IMPORTANT
                    }
                });
            }
        });
    }

    // =====================================================
    // TOTAL PRESENT / ABSENT
    // =====================================================
    private void updateSummary() {

        if (isDateLocked) return;   // ⭐ MOST IMPORTANT LINE
        int present = 0;
        int absent = 0;

        for (Attendacemodel s : studentList) {
            if ("present".equals(s.status)) {
                present++;
            } else if ("absent".equals(s.status)) {
                absent++;
            }
            // leave ignore
        }

        if (isHoliday) {
            tvSummary.setText("Today is Holiday");
        } else {
            tvSummary.setText(
                    "Total Present: " + present + "    Absent: " + absent
            );
        }
    }



    // =====================================================
    // CALCULATE MONTHLY PERCENTAGE
    // =====================================================
    private void calculatePercentage() {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(classId);

        // selectedDate → month & year
        Calendar selectedCal = Calendar.getInstance();
        try {
            selectedCal.setTime(
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(selectedDate)
            );
        } catch (Exception e) {
            return;
        }

        int selectedMonth = selectedCal.get(Calendar.MONTH);
        int selectedYear  = selectedCal.get(Calendar.YEAR);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                for (Attendacemodel s : studentList) {

                    int workingDays = 0;
                    int presentDays = 0;

                    DataSnapshot studentSnap = snapshot.child(s.studentId);
                    if (!studentSnap.exists()) continue;

                    for (DataSnapshot day : studentSnap.getChildren()) {

                        String status = day.child("status").getValue(String.class);
                        Long ts = day.child("timestamp").getValue(Long.class);

                        if (status == null || ts == null) continue;

                        Calendar dayCal = Calendar.getInstance();
                        dayCal.setTimeInMillis(ts);

                        // ✅ SAME month/year filter as Student
                        if (dayCal.get(Calendar.MONTH) != selectedMonth ||
                                dayCal.get(Calendar.YEAR)  != selectedYear) {
                            continue;
                        }

                        // ❌ ignore holiday
                        if ("leave".equals(status)) continue;

                        workingDays++;

                        if ("present".equals(status)) {
                            presentDays++;
                        }
                    }

                    if (workingDays > 0) {
                        s.monthlyPercentage = (presentDays * 100) / workingDays;

                    }else {
                        s.monthlyPercentage = 0;
                    }

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void fetchClassAndSubject() {

        if (classId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                String className = snapshot.child("className").getValue(String.class);
                String subject   = snapshot.child("subject").getValue(String.class);

                tvclass.setText("Class: " + (className != null ? className : "-"));
                tvsubject.setText("Subject: " + (subject != null ? subject : "-"));

                // 🔥 VERY IMPORTANT
                subjectId = subject;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TAttendanceActivity.this,
                        "Failed to load class data",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // =====================================================
    // SAVE ATTENDANCE
    // =====================================================
    private void saveAttendance() {

        DatabaseReference classRef = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(classId);

        // 🔒 STEP 1: Check – this date already saved?
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot studentSnap : snapshot.getChildren()) {
                    if (studentSnap.child(selectedDate).exists()) {
                        Toast.makeText(
                                TAttendanceActivity.this,
                                "Attendance already saved for this date",
                                Toast.LENGTH_SHORT
                        ).show();
                        lockUI();
                        return; // 🔥 STOP – already saved
                    }
                }

                // ✅ STEP 2: Not saved → SAVE NOW
                long timestamp = System.currentTimeMillis();

                for (Attendacemodel s : studentList) {

                    String status;
                    if (isHoliday) {
                        status = "leave";
                    } else {
                        status = s.status; // present / absent
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("date", selectedDate);
                    map.put("subject", subjectId);
                    map.put("status", status);
                    map.put("timestamp", timestamp);

                    classRef.child(s.studentId)
                            .child(selectedDate)
                            .setValue(map);
                }

                Toast.makeText(
                        TAttendanceActivity.this,
                        "Attendance saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                lockUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void lockUI() {
        btnSave.setEnabled(false);
        cbHoliday.setEnabled(false);
        if (adapter != null) adapter.setLocked(true);
    }
}