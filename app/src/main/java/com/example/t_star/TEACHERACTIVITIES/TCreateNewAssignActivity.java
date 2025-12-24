package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class TCreateNewAssignActivity extends AppCompatActivity {

    private static final int REQ_PICK = 1010;

    TextInputEditText etTitle, etDescription, etDueDate;
    AppCompatButton btnSave;
    ImageView btnViewAttachment;
    TextView tvAttachmentName;

    String classId;

    ArrayList<Uri> selectedUris = new ArrayList<>();
    ArrayList<String> attachmentUrls = new ArrayList<>();
    ArrayList<String> attachmentNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tcreate_new_assign);

        classId = getIntent().getStringExtra("classId");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDueDate = findViewById(R.id.etDueDate);
        btnSave = findViewById(R.id.btnSave);
        btnViewAttachment = findViewById(R.id.btnViewAttachment);
        tvAttachmentName = findViewById(R.id.tvAttachmentName);

        etDueDate.setOnClickListener(v -> pickDate());
        btnViewAttachment.setOnClickListener(v -> pickFiles());
        btnSave.setOnClickListener(v -> saveAssignment());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, y, m, d) -> etDueDate.setText(d + "-" + (m + 1) + "-" + y),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // UPDATED: ACTION_GET_CONTENT (BEST FOR FILE UPLOAD)
    private void pickFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Files"), REQ_PICK);
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);

        if (req == REQ_PICK && res == RESULT_OK && data != null) {

            selectedUris.clear();
            attachmentNames.clear();

            ClipData cd = data.getClipData();

            if (cd != null) {
                for (int i = 0; i < cd.getItemCount(); i++) {
                    Uri uri = cd.getItemAt(i).getUri();
                    selectedUris.add(uri);
                    attachmentNames.add(getFileName(uri));
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                selectedUris.add(uri);
                attachmentNames.add(getFileName(uri));
            }

            if (attachmentNames.size() == 1) {
                tvAttachmentName.setText(attachmentNames.get(0));
            } else if (attachmentNames.size() > 1) {
                tvAttachmentName.setText(attachmentNames.size() + " files selected");
            } else {
                tvAttachmentName.setText("No file selected");
            }

            Toast.makeText(this, attachmentNames.size() + " file(s) selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String name = "file";
        Cursor c = getContentResolver().query(uri, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            name = c.getString(idx);
            c.close();
        }
        return name;
    }

    private void saveAssignment() {

        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String due = etDueDate.getText().toString().trim();

        if (title.isEmpty()) { etTitle.setError("Required"); return; }
        if (desc.isEmpty()) { etDescription.setError("Required"); return; }
        if (due.isEmpty()) { etDueDate.setError("Required"); return; }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Assignments")
                .child(classId);

        String assignmentId = ref.push().getKey();

        FirebaseDatabase.getInstance().getReference("Classes")
                .child(classId)
                .child("studentsCount")
                .get()
                .addOnSuccessListener(snap -> {

                    int totalStudents = snap.exists() ? snap.getValue(Integer.class) : 0;

                    if (selectedUris.isEmpty()) {
                        saveWithoutAttachments(assignmentId, ref, title, desc, due, totalStudents);
                    } else {
                        attachmentUrls.clear();
                        uploadFiles(0, assignmentId, ref, title, desc, due, totalStudents);
                    }
                });
    }

    private void saveWithoutAttachments(
            String assignmentId,
            DatabaseReference ref,
            String title,
            String desc,
            String due,
            int totalStudents
    ) {

        TAssignmentModel model = new TAssignmentModel(
                assignmentId,
                classId,
                title,
                desc,
                due,
                false,
                "",
                totalStudents,
                0,
                new ArrayList<>()
        );

        ref.child(assignmentId).setValue(model)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Assignment Created!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // ⭐ UPDATED: Stream close + cleaner upload logic
    private void uploadFiles(int index, String assignmentId,
                             DatabaseReference ref, String title,
                             String desc, String due, int totalStudents) {

        if (index >= selectedUris.size()) {

            TAssignmentModel model = new TAssignmentModel(
                    assignmentId,
                    classId,
                    title,
                    desc,
                    due,
                    true,
                    attachmentNames.get(0),
                    totalStudents,
                    0,
                    new ArrayList<>(attachmentUrls)
            );

            ref.child(assignmentId).setValue(model)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(this, "Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });

            return;
        }

        Uri uri = selectedUris.get(index);
        String cleanName = attachmentNames.get(index).replaceAll("[^A-Za-z0-9._-]", "_");

        StorageReference fileRef = FirebaseStorage.getInstance()
                .getReference("assignments/" + classId + "/" + assignmentId + "/" + cleanName);

        InputStream stream;

        try {
            stream = getContentResolver().openInputStream(uri);
            if (stream == null) {
                Toast.makeText(this, "File read error!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        InputStream finalStream = stream;

        fileRef.putStream(finalStream)
                .addOnSuccessListener(task -> {

                    try { finalStream.close(); } catch (Exception ignored) {}

                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(url -> {
                                attachmentUrls.add(url.toString());

                                uploadFiles(index + 1, assignmentId, ref,
                                        title, desc, due, totalStudents);
                            });
                })
                .addOnFailureListener(e -> {
                    try { finalStream.close(); } catch (Exception ignored) {}
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
