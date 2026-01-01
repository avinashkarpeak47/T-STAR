package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusAdapter;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TAssignDetailActivity extends AppCompatActivity {

    TextView tvTitleDetail, tvDescDetail, tvDueDateDetail,
            tvTotalStudents, tvSubmitted, tvUnsubmitted;

    RecyclerView rvStudentStatus;
    ArrayList<TAssignStudentStatusModel> list = new ArrayList<>();
    TAssignStudentStatusAdapter adapter;

    String classId, assignmentId;
    long dueDateMillis = 0;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_tassign_detail);

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");

        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);
        tvDueDateDetail = findViewById(R.id.tvDueDateDetail);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvSubmitted = findViewById(R.id.tvSubmitted);
        tvUnsubmitted = findViewById(R.id.tvUnsubmitted);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvTitleDetail.setText(getIntent().getStringExtra("title"));
        tvDescDetail.setText(getIntent().getStringExtra("description"));

        String dueText = getIntent().getStringExtra("dueDate");
        dueDateMillis = getIntent().getLongExtra("dueDateMillis", 0);

        if (dueDateMillis > 0) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            tvDueDateDetail.setText("Due: " + sdf.format(new Date(dueDateMillis)));
        } else {
            tvDueDateDetail.setText("Due: " + dueText);
        }

        // make clickable (even if expired)
        tvDueDateDetail.setEnabled(true);
        tvDueDateDetail.setFocusable(false);
        tvDueDateDetail.setClickable(true);
        tvDueDateDetail.setOnClickListener(v -> showExtendPicker());

        int total = getIntent().getIntExtra("totalStudents", 0);
        int submitted = getIntent().getIntExtra("submittedCount", 0);

        tvTotalStudents.setText("Total: " + total);
        tvSubmitted.setText("Submitted: " + submitted);
        tvUnsubmitted.setText("Pending: " + (total - submitted));

        rvStudentStatus = findViewById(R.id.rvStudentStatus);
        rvStudentStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TAssignStudentStatusAdapter(list);
        rvStudentStatus.setAdapter(adapter);

        loadStatus();
    }

    private void showExtendPicker() {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {

                    Calendar cal = Calendar.getInstance();
                    cal.set(y, m, d, 23, 59, 59);

                    long newMillis = cal.getTimeInMillis();

                    if (newMillis < System.currentTimeMillis()) {
                        Toast.makeText(this,
                                "Cannot select past date",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String newText = d + "-" + (m + 1) + "-" + y;

                    updateDueDate(newMillis, newText);

                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateDueDate(long newMillis, String newText) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId)
                .child(assignmentId);

        ref.child("dueDateMillis").setValue(newMillis);
        ref.child("dueDate").setValue(newText);

        tvDueDateDetail.setText("Due: " + newText);
        dueDateMillis = newMillis;

        Toast.makeText(this,
                "Due date extended",
                Toast.LENGTH_SHORT).show();
    }

    private void loadStatus() {

        DatabaseReference classRef = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("students");

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Users");

        DatabaseReference subRef = FirebaseDatabase.getInstance()
                .getReference("Assignments_Submissions")
                .child(classId)
                .child(assignmentId);

        subRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot subSnap) {

                classRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot studentSnap) {

                        list.clear();

                        int totalStudents = (int) studentSnap.getChildrenCount();
                        tvTotalStudents.setText("Total: " + totalStudents);

                        for (DataSnapshot s : studentSnap.getChildren()) {

                            String uid = s.getKey();

                            userRef.child(uid)
                                    .addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot uSnap) {

                                                    String first =
                                                            uSnap.child("firstname")
                                                                    .getValue(String.class);

                                                    String last =
                                                            uSnap.child("lastname")
                                                                    .getValue(String.class);

                                                    String name =
                                                            ((first == null ? "" : first)
                                                                    + " " +
                                                                    (last == null ? "" : last))
                                                                    .trim();

                                                    boolean submitted =
                                                            subSnap.hasChild(uid);

                                                    String fileUrl = submitted
                                                            ? subSnap.child(uid)
                                                            .child("fileUrl")
                                                            .getValue(String.class)
                                                            : null;

                                                    list.add(new TAssignStudentStatusModel(
                                                            uid, name, submitted, fileUrl
                                                    ));

                                                    if (list.size() == totalStudents) {

                                                        int submittedCount = 0;
                                                        for (TAssignStudentStatusModel m : list)
                                                            if (m.isSubmitted())
                                                                submittedCount++;

                                                        tvSubmitted.setText("Submitted: " + submittedCount);
                                                        tvUnsubmitted.setText("Pending: " + (totalStudents - submittedCount));

                                                        // 👉 Sort list: Submitted first, Pending later
                                                        Collections.sort(list, (a, b) -> {
                                                            if (a.isSubmitted() == b.isSubmitted())
                                                                return 0;
                                                            return a.isSubmitted() ? -1 : 1;
                                                        });

                                                        adapter.notifyDataSetChanged();
                                                    }

                                                }

                                                @Override public void onCancelled(DatabaseError e) {}
                                            });
                        }
                    }

                    @Override public void onCancelled(DatabaseError e) {}
                });
            }

            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}
