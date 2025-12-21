package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Assignment_Helper;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusAdapter;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.STUSTATUS.TAssignStudentStatusModel;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.SubmissionModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class TAssignDetailActivity extends AppCompatActivity {

    private TextInputEditText tvTitleDetail, tvDescDetail, tvDueDateDetail;
    private TextView tvAttachmentName, tvTotalStudents, tvSubmitted, tvUnsubmitted;

    private RecyclerView rvStudentStatus;
    private ArrayList<TAssignStudentStatusModel> statusList = new ArrayList<>();
    private TAssignStudentStatusAdapter adapter;

    private LinearLayout attachmentContainer;

    private String classId, assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tassign_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Intent data
        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");

        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String dueDate = getIntent().getStringExtra("dueDate");
        boolean hasAttachment = getIntent().getBooleanExtra("hasAttachment", false);
        String attachmentName = getIntent().getStringExtra("attachmentName");

        ArrayList<String> attachmentUrls =
                getIntent().getStringArrayListExtra("attachmentUrls");

        int total = getIntent().getIntExtra("totalStudents", 0);
        int submitted = getIntent().getIntExtra("submittedCount", 0);

        // Bind UI
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);
        tvDueDateDetail = findViewById(R.id.tvDueDateDetail);
        tvAttachmentName = findViewById(R.id.tvAttachmentName);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvSubmitted = findViewById(R.id.tvSubmitted);
        tvUnsubmitted = findViewById(R.id.tvUnsubmitted);

        attachmentContainer = findViewById(R.id.attachmentContainer);

        // Set data
        tvTitleDetail.setText(title);
        tvDescDetail.setText(desc);
        tvDueDateDetail.setText("Due: " + dueDate);
        tvAttachmentName.setText(hasAttachment ? attachmentName : "No File Attached");

        tvTotalStudents.setText("Total Students: " + total);
        tvSubmitted.setText("Submitted: " + submitted);
        tvUnsubmitted.setText("Unsubmitted: " + (total - submitted));


        // Show attachment list
        if (attachmentUrls != null && !attachmentUrls.isEmpty()) {
            attachmentContainer.removeAllViews();
            for (String url : attachmentUrls) {

                TextView t = new TextView(this);
                t.setPadding(10, 12, 10, 12);
                t.setTextColor(getColor(R.color.white));

                // Extract filename
                String fname = url.substring(url.lastIndexOf('/') + 1);

                t.setText("📎 " + fname);
                t.setOnClickListener(v -> {
                    try {
                        Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(open);
                    } catch (Exception e) {
                        Toast.makeText(this, "Cannot open attachment", Toast.LENGTH_SHORT).show();
                    }
                });
                attachmentContainer.addView(t);
            }
        }

        // Recycler view
        rvStudentStatus = findViewById(R.id.rvStudentStatus);
        rvStudentStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TAssignStudentStatusAdapter(statusList);
        rvStudentStatus.setAdapter(adapter);

        loadStudentStatus();
    }

    private void loadStudentStatus() {

        Classes_helper.getClassStudents(classId, studentUids -> {

            Assignment_Helper.getAssignmentStudentStatus(
                    classId,
                    assignmentId,
                    submittedUids -> {

                        statusList.clear();

                        for (String uid : studentUids) {

                            Classes_helper.Users_Helper.getUserDetails(uid, user -> {

                                if (user == null) return;
                                String driveUrl=null;
                                boolean isSubmitted =
                                        submittedUids.contains(uid);

                                statusList.add(
                                        new TAssignStudentStatusModel(
                                                uid,
                                                user.firstname + " " + user.lastname,
                                                isSubmitted,
                                                driveUrl
                                        )
                                );

                                adapter.notifyDataSetChanged();
                            });
                        }
                    }
            );


        });
    }
}

