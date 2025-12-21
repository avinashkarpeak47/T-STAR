package com.example.t_star.STUDENTACTIVITIES;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.api.client.http.FileContent;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class S_Assignment_Submit_Activity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private static final int RC_PICK_FILE = 2001;

    private TextView  txtStatus, txtSubmittedFile, txtDueDate, txtLateBadge;
    private ImageView imgFileType;
    private Button btnChooseFile, btnSubmit, btnViewSubmission;

    private Uri selectedFileUri;
    private String selectedFileName;

    private String classId, assignmentId, studentId;
    private String submittedDriveUrl;

    private long dueDateMillis;
    private boolean allowResubmission;

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount driveAccount;

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
                .child(assignmentId)
                .child(studentId);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope("https://www.googleapis.com/auth/drive.file"))
                        .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnChooseFile.setOnClickListener(v -> openFilePicker());
        btnSubmit.setOnClickListener(v -> submitAssignment());
        btnViewSubmission.setOnClickListener(v -> openSubmittedFile());

        loadAssignmentInfo();
        loadSubmissionState();



    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Back navigation action
        toolbar.setNavigationOnClickListener(v->finish());
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

    /* ---------------- LOAD ASSIGNMENT ---------------- */

    private void loadAssignmentInfo() {
        assignmentRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        dueDateMillis =
                                snap.child("dueDateMillis").getValue(Long.class) != null
                                        ? snap.child("dueDateMillis").getValue(Long.class)
                                        : 0;

                        allowResubmission =
                                snap.child("allowResubmission").getValue(Boolean.class) != null
                                        && snap.child("allowResubmission").getValue(Boolean.class);

                        showDueDate();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /* ---------------- LOAD SUBMISSION ---------------- */

    private void loadSubmissionState() {
        submissionRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        if (snap.exists()) {
                            showSubmittedUI(snap);
                        } else {
                            showNotSubmittedUI();
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void showDueDate() {
        if (dueDateMillis > 0) {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            txtDueDate.setText("Due Date: " + sdf.format(new Date(dueDateMillis)));
        } else {
            txtDueDate.setText("Due Date: Not specified");
        }
    }

    private void showSubmittedUI(DataSnapshot snap) {

        String fileName = snap.child("fileName").getValue(String.class);
        long submittedAt = snap.child("submittedAt").getValue(Long.class) != null
                ? snap.child("submittedAt").getValue(Long.class)
                : 0;

        submittedDriveUrl = snap.child("driveFileUrl").getValue(String.class);

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

        boolean isPastDue =
                dueDateMillis > 0 &&
                        System.currentTimeMillis() > dueDateMillis;

        if (isPastDue && !allowResubmission) {

            txtStatus.setText("Submission Closed");
            txtSubmittedFile.setText("Due date crossed");
            imgFileType.setImageResource(R.drawable.ic_file_generic);

            btnChooseFile.setEnabled(false);
            btnSubmit.setEnabled(false);
            btnViewSubmission.setVisibility(View.GONE);

            txtLateBadge.setVisibility(View.GONE);

        } else {

            txtStatus.setText("Not Submitted");
            txtSubmittedFile.setText("No file selected");
            imgFileType.setImageResource(R.drawable.ic_file_generic);

            btnChooseFile.setEnabled(true);
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(true);
            btnViewSubmission.setVisibility(View.GONE);

            txtLateBadge.setVisibility(View.GONE);
        }
    }


    /* ---------------- FILE PICKER ---------------- */

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

       // startActivityForResult(intent, RC_PICK_FILE);

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent,RC_PICK_FILE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // FILE PICK
        if (requestCode == RC_PICK_FILE && resultCode == RESULT_OK && data != null) {

            selectedFileUri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                        selectedFileUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {}

            selectedFileName = FileUtils.getFileName(this, selectedFileUri);
            txtSubmittedFile.setText(selectedFileName);
            setFileIcon(selectedFileName);
        }

        // GOOGLE SIGN-IN RESULT (🔥 FIXED)
        if (requestCode == RC_SIGN_IN) {

            try {
                driveAccount =
                        GoogleSignIn
                                .getSignedInAccountFromIntent(data)
                                .getResult(ApiException.class);

                uploadToDrive();

            } catch (ApiException e) {

                Toast.makeText(
                        this,
                        "Google sign-in failed. Please try again.",
                        Toast.LENGTH_LONG
                ).show();

                Log.e("DRIVE_SIGN_IN", "Error code = " + e.getStatusCode());
            }
        }
    }

    /* ---------------- SUBMIT ---------------- */

    private void submitAssignment() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "Select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            driveAccount = account;
            uploadToDrive();
        } else {
            startActivityForResult(
                    googleSignInClient.getSignInIntent(),
                    RC_SIGN_IN
            );
        }
    }

    /* ---------------- DRIVE UPLOAD ---------------- */

    private void uploadToDrive() {

        GoogleAccountCredential credential =
                GoogleAccountCredential.usingOAuth2(
                        this,
                        Collections.singleton("https://www.googleapis.com/auth/drive.file"));

        credential.setSelectedAccount(driveAccount.getAccount());

        Drive drive = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("T-STAR")
                .build();

        new Thread(() -> {
            try {
                java.io.File local = getFileFromUri(selectedFileUri);

                File meta = new File();
                meta.setName(selectedFileName);

                FileContent content =
                        new FileContent(getContentResolver().getType(selectedFileUri), local);

                File uploaded =
                        drive.files().create(meta, content).setFields("id").execute();

                Permission p =
                        new Permission().setType("anyone").setRole("reader");
                drive.permissions().create(uploaded.getId(), p).execute();

                String link =
                        "https://drive.google.com/file/d/" + uploaded.getId() + "/view";

                runOnUiThread(() ->
                        saveSubmission(uploaded.getId(), link)
                );

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private java.io.File getFileFromUri(Uri uri) throws Exception {
        InputStream in = getContentResolver().openInputStream(uri);
        java.io.File temp =
                java.io.File.createTempFile("assign_", selectedFileName, getCacheDir());

        OutputStream out = new FileOutputStream(temp);
        byte[] buf = new byte[1024];
        int r;
        while ((r = in.read(buf)) != -1) out.write(buf, 0, r);

        in.close();
        out.close();
        return temp;
    }

    /* ---------------- SAVE ---------------- */

    private void saveSubmission(String driveId, String link) {

        Map<String, Object> map = new HashMap<>();
        map.put("fileName", selectedFileName);
        map.put("driveFileId", driveId);
        map.put("driveFileUrl", link);
        map.put("submittedAt", System.currentTimeMillis());
        map.put("status", Assignment.STATUS_SUBMITTED);
        map.put("marksObtained", 0);

        submissionRef.setValue(map)
                .addOnSuccessListener(v ->
                        Toast.makeText(this,
                                "Assignment submitted successfully",
                                Toast.LENGTH_SHORT).show()
                );
    }

    /* ---------------- VIEW ---------------- */

    private void openSubmittedFile() {
        if (submittedDriveUrl == null) return;

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(submittedDriveUrl)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found", Toast.LENGTH_SHORT).show();
        }
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
}