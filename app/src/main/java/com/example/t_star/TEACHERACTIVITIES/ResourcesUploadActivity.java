package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.t_star.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourcesUploadActivity extends AppCompatActivity {

    private static final int PICK_FILE = 7001;

    private Uri selectedFileUri;
    private TextView tvAttachmentName;
    private ImageView btnViewAttachment;
    private EditText etTitle, etDescription;
    private Button btnSave;

    private String classId;
    private boolean isUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_upload);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        tvAttachmentName = findViewById(R.id.tvAttachmentName);
        btnViewAttachment = findViewById(R.id.btnViewAttachment);
        btnSave = findViewById(R.id.btnSave);

        classId = getIntent().getStringExtra("classId");
        if (classId == null) {
            Toast.makeText(this, "Class not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnViewAttachment.setOnClickListener(v -> openFilePicker());
        btnSave.setOnClickListener(v -> uploadResource());
    }

    private void uploadResource() {

        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Enter title");
            etTitle.requestFocus();
            return;
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "Select attachment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUploading) return;
        isUploading = true;
        btnSave.setEnabled(false);

        MediaManager.get()
                .upload(selectedFileUri)
                .option("resource_type", "auto")
                .unsigned("t_star")
                .option("folder", "app_uploads")
                .callback(new UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String fileUrl =
                                resultData.get("secure_url").toString(); // 🔥 FIX

                        isUploading = false;
                        btnSave.setEnabled(true);

                        saveToFirebase(fileUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {

                        isUploading = false;
                        btnSave.setEnabled(true);

                        Toast.makeText(
                                ResourcesUploadActivity.this,
                                "Upload failed: " + error.getDescription(),
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onStart(String r) {
                        Toast.makeText(ResourcesUploadActivity.this,
                                "Uploading...",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onProgress(String r, long b, long t) {}
                    @Override public void onReschedule(String r, ErrorInfo e) {}
                })
                .dispatch();
    }

    private void saveToFirebase(String fileUrl) {

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .child("StudyMaterial")
                .push();

        String uploadDate = new SimpleDateFormat(
                "dd MMM yyyy", Locale.getDefault()
        ).format(new Date());

        Map<String, Object> map = new HashMap<>();
        map.put("id", ref.getKey());
        map.put("title", etTitle.getText().toString().trim());
        map.put("description", etDescription.getText().toString().trim());
        map.put("date", uploadDate);
        map.put("fileUrl", fileUrl);
        map.put("teacherId", FirebaseAuth.getInstance().getUid());
        map.put("timestamp", System.currentTimeMillis());

        ref.setValue(map);

        Toast.makeText(this,
                "Material uploaded successfully",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES,
                new String[]{"image/*", "application/pdf"});
        startActivityForResult(intent, PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE && resultCode == RESULT_OK && data != null) {

            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                tvAttachmentName.setText(selectedFileUri.getLastPathSegment());

                if (selectedFileUri.toString().contains("image")) {
                    btnViewAttachment.setImageURI(selectedFileUri);
                }
            }
        }
    }
}
