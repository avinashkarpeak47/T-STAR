package com.example.t_star.STUDENTACTIVITIES;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.Models.Assignment;
import com.example.t_star.utils.FileUtils;
import com.example.t_star.utils.CloudinaryConfig;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class S_Assignment_Submit_Activity extends AppCompatActivity {

    private static final int RC_PICK_FILE = 2001;

    private TextView txtStatus, txtSubmittedFile, txtDueDate, txtLateBadge;
    private ImageView imgFileType;
    private Button btnChooseFile, btnSubmit, btnViewSubmission;

    private Uri selectedFileUri;
    private String selectedFileName;
    private String submittedFileUrl;

    private String classId, assignmentId, studentId;
    private long dueDateMillis;
    private boolean allowResubmission;

    private DatabaseReference assignmentRef;
    private DatabaseReference submissionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_assignment_submit);

        setupToolbar();
        initViews();

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");
        studentId = FirebaseAuth.getInstance().getUid();

        if (classId == null || assignmentId == null || studentId == null) {
            Toast.makeText(this, "Invalid assignment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        assignmentRef = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId)
                .child(assignmentId);

        submissionRef = FirebaseDatabase.getInstance()
                .getReference("Assignments_Submissions")
                .child(classId)
                .child(assignmentId)
                .child(studentId);

        btnChooseFile.setOnClickListener(v -> openFilePicker());
        btnSubmit.setOnClickListener(v -> submitAssignment());
        btnViewSubmission.setOnClickListener(v -> openSubmittedFile());

        loadAssignmentInfo();
        loadSubmissionState();
    }

    /* ---------------- ASSIGNMENT INFO ---------------- */

    private void loadAssignmentInfo() {
        assignmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                dueDateMillis = snap.child("dueDateMillis").getValue(Long.class) != null
                        ? snap.child("dueDateMillis").getValue(Long.class) : 0;

                allowResubmission = snap.child("allowResubmission").getValue(Boolean.class) != null
                        && snap.child("allowResubmission").getValue(Boolean.class);

                showDueDate();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showDueDate() {
        if (dueDateMillis > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            txtDueDate.setText("Due Date: " + sdf.format(new Date(dueDateMillis)));
        } else {
            txtDueDate.setText("Due Date: Not specified");
        }
    }

    /* ---------------- SUBMISSION STATE ---------------- */

    private void loadSubmissionState() {
        submissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.exists()) showSubmittedUI(snap);
                else showNotSubmittedUI();
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showSubmittedUI(DataSnapshot snap) {
        String fileName = snap.child("fileName").getValue(String.class);
        submittedFileUrl = snap.child("fileUrl").getValue(String.class);
        long submittedAt = snap.child("submittedAt").getValue(Long.class) != null
                ? snap.child("submittedAt").getValue(Long.class) : 0;

        txtStatus.setText("Submitted");
        txtSubmittedFile.setText(fileName);
        setFileIcon(fileName);

        txtLateBadge.setVisibility(
                dueDateMillis > 0 && submittedAt > dueDateMillis
                        ? View.VISIBLE : View.GONE
        );

        btnChooseFile.setEnabled(allowResubmission);
        btnSubmit.setVisibility(allowResubmission ? View.VISIBLE : View.GONE);
        btnViewSubmission.setVisibility(View.VISIBLE);
    }

    private void showNotSubmittedUI() {
        boolean isPastDue = dueDateMillis > 0 && System.currentTimeMillis() > dueDateMillis;

        txtStatus.setText(isPastDue ? "Submission Closed" : "Not Submitted");
        txtSubmittedFile.setText("No file selected");
        imgFileType.setImageResource(R.drawable.ic_file_generic);

        btnChooseFile.setEnabled(!isPastDue || allowResubmission);
        btnSubmit.setEnabled(!isPastDue || allowResubmission);
        btnViewSubmission.setVisibility(View.GONE);
        txtLateBadge.setVisibility(View.GONE);
    }

    /* ---------------- FILE PICK ---------------- */

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, RC_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PICK_FILE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            try{
                getContentResolver().takePersistableUriPermission(selectedFileUri,Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }catch(SecurityException e)
            {
                e.printStackTrace();
            }
            selectedFileName = FileUtils.getFileName(this, selectedFileUri);
            txtSubmittedFile.setText(selectedFileName);
            setFileIcon(selectedFileName);
        }
    }

    /* ---------------- SUBMIT ---------------- */

    private void submitAssignment() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        uploadToCloudinary();
    }

    private void uploadToCloudinary() {
        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);

                Map<String , Object>options = new HashMap<>();
                options.put("resources_type","raw");
                options.put("folder","assignment");

                Map uploadResult = CloudinaryConfig.getInstance()
                        .uploader()
                        .upload(inputStream, options);

                String fileUrl = uploadResult.get("secure_url").toString();
                runOnUiThread(() -> saveSubmission(fileUrl));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(this, "Upload failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveSubmission(String fileUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("fileName", selectedFileName);
        map.put("fileUrl", fileUrl);
        map.put("submittedAt", System.currentTimeMillis());
        map.put("status", Assignment.STATUS_SUBMITTED);
        map.put("marksObtained", 0);

        submissionRef.setValue(map)
                .addOnSuccessListener(v ->
                        Toast.makeText(this, "Assignment submitted", Toast.LENGTH_SHORT).show()
                );
    }

    private void openSubmittedFile() {
        if (submittedFileUrl != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(submittedFileUrl)));
    }

    private void setFileIcon(String name) {
        if (name == null) return;
        name = name.toLowerCase();
        if (name.endsWith(".pdf")) imgFileType.setImageResource(R.drawable.ic_pdf);
        else if (name.endsWith(".jpg") || name.endsWith(".png"))
            imgFileType.setImageResource(R.drawable.ic_image);
        else if (name.endsWith(".doc") || name.endsWith(".docx"))
            imgFileType.setImageResource(R.drawable.ic_doc);
        else imgFileType.setImageResource(R.drawable.ic_file_generic);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        txtStatus = findViewById(R.id.txtStatus);
        txtSubmittedFile = findViewById(R.id.txtFileName);
        txtDueDate = findViewById(R.id.txtDueDate);
        txtLateBadge = findViewById(R.id.txtLateBadge);
        imgFileType = findViewById(R.id.imgFileType);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmit = findViewById(R.id.btnSubmitAssignment);
        btnViewSubmission = findViewById(R.id.btnViewSubmission);
    }
}
