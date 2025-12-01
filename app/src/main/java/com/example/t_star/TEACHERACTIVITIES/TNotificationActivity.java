package com.example.t_star.TEACHERACTIVITIES;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.HashMap;
import java.util.Map;

public class TNotificationActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private Spinner spinnerType;
    private Button btnSelectTarget;
    private EditText etTitle, etMessage;
    private Button btnSend;
    private ProgressDialog progressDialog;

    private final String[] targets = {
            "All Students","Class 5","Class 6",
            "Class 7","Class 8","Class 9","Class 10"
    };

    private int selectedTargetIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnotification);

        toolbar = findViewById(R.id.toolbar);
        spinnerType = findViewById(R.id.spinnerType);
        btnSelectTarget = findViewById(R.id.btnSelectTarget);
        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Toolbar setup
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Spinner setup
        String[] types = {
                "Assignment", "Class Update", "Marks",
                "Circular", "Announcement", "Reminder"
        };

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        types);

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerType.setAdapter(spinnerAdapter);

        // Progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending...");

        // Listeners
        btnSelectTarget.setOnClickListener(v -> showSelectTargetDialog());
        btnSend.setOnClickListener(v -> attemptSend());
    }

    private void showSelectTargetDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Select Target");

        b.setSingleChoiceItems(targets, selectedTargetIndex, (dialog, which) -> {
            selectedTargetIndex = which;
        });

        b.setPositiveButton("OK", (dialog, which) -> {
            if (selectedTargetIndex >= 0)
                btnSelectTarget.setText(targets[selectedTargetIndex]);
            else
                btnSelectTarget.setText("Choose Students / Class / All");
        });

        b.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void attemptSend() {
        String type = spinnerType.getSelectedItem().toString();
        String target = (selectedTargetIndex >= 0) ? targets[selectedTargetIndex] : "";
        String title = etTitle.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        // Validation
        if (title.isEmpty()) {
            etTitle.setError("Enter title");
            etTitle.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            etMessage.setError("Enter message");
            etMessage.requestFocus();
            return;
        }

        if (target.isEmpty()) {
            Toast.makeText(this, "Select target", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Prepare data
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("title", title);
        payload.put("message", message);
        payload.put("target", target);
        payload.put("timestamp", System.currentTimeMillis());

        // Simulate Sending
        btnSend.postDelayed(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Notification sent to " + target, Toast.LENGTH_LONG).show();
            finish();
        }, 1000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
