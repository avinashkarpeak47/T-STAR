package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

public class TAssignDetailActivity extends AppCompatActivity {

    private TextView tvTitleDetail, tvDescDetail, tvDueDateDetail;
    private TextView tvAttachmentName, tvTotalStudents, tvSubmitted, tvUnsubmitted;
    private ImageView btnViewAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tassign_detail);  // <-- your XML file name

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);
        tvDueDateDetail = findViewById(R.id.tvDueDateDetail);
        tvAttachmentName = findViewById(R.id.tvAttachmentName);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvSubmitted = findViewById(R.id.tvSubmitted);
        tvUnsubmitted = findViewById(R.id.tvUnsubmitted);
        btnViewAttachment = findViewById(R.id.btnViewAttachment);

        // ----- Get data from Intent -----
        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String dueDate = getIntent().getStringExtra("dueDate");
        boolean hasAttachment = getIntent().getBooleanExtra("hasAttachment", false);
        String attachmentName = getIntent().getStringExtra("attachmentName");
        int totalStudents = getIntent().getIntExtra("totalStudents", 0);
        int submittedCount = getIntent().getIntExtra("submittedCount", 0);
        int unsubmittedCount = totalStudents - submittedCount;

        // ----- Set UI -----
        tvTitleDetail.setText(title);
        tvDescDetail.setText(desc);
        tvDueDateDetail.setText("Due: " + dueDate);

        tvTotalStudents.setText("Total Students: " + totalStudents);
        tvSubmitted.setText("Submitted: " + submittedCount);
        tvUnsubmitted.setText("Unsubmitted: " + unsubmittedCount);

        if (hasAttachment && attachmentName != null && !attachmentName.isEmpty()) {
            tvAttachmentName.setText(attachmentName);
            btnViewAttachment.setEnabled(true);
        } else {
            tvAttachmentName.setText("No file attached");
            btnViewAttachment.setEnabled(false);
        }

        btnViewAttachment.setOnClickListener(v ->
                Toast.makeText(this, "Open attachment: " + attachmentName, Toast.LENGTH_SHORT).show());
    }
}
