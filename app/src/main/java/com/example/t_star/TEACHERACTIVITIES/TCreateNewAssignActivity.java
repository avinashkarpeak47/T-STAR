package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class TCreateNewAssignActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etDueDate;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tcreate_new_assign);

        // ----- Toolbar -----
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // handle back arrow
        toolbar.setNavigationOnClickListener(v -> finish());

        // ----- Views -----
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDueDate = findViewById(R.id.etDueDate);
        btnSave = findViewById(R.id.btnSave);

        // Open calendar when clicking due date
        etDueDate.setOnClickListener(v -> openDatePicker());

        // Save button
        btnSave.setOnClickListener(v -> saveAssignment());
    }

    private void openDatePicker() {
        Calendar c = Calendar.getInstance();
        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    // m is 0-based
                    String date = d + "-" + (m + 1) + "-" + y;
                    etDueDate.setText(date);
                },
                year, month, day
        );
        dialog.show();
    }

    private void saveAssignment() {
        String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        String desc  = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
        String date  = etDueDate.getText() != null ? etDueDate.getText().toString().trim() : "";

        if (title.isEmpty()) {
            etTitle.setError("Enter title");
            etTitle.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            etDescription.setError("Enter description");
            etDescription.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etDueDate.setError("Select due date");
            etDueDate.requestFocus();
            return;
        }

        // TODO: save to DB / Firebase here
        Toast.makeText(this, "Assignment created successfully", Toast.LENGTH_SHORT).show();

        finish(); // go back to previous screen
    }
}
