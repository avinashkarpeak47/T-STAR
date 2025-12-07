package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.t_star.R;

public class S_Enroll_in_Class extends AppCompatActivity {

    private EditText etClassId;
    private AppCompatButton btnSubmitEnroll;
    private TextView tvEnrollStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_enroll_in_class);

        // Initialize views
        etClassId = findViewById(R.id.etClassId);
        btnSubmitEnroll = findViewById(R.id.btnSubmitEnroll);
        tvEnrollStatus = findViewById(R.id.tvEnrollStatus);

        // Set button click listener
        btnSubmitEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollClass();
            }
        });
    }

    // Method to enroll student
    private void enrollClass() {
        String classId = etClassId.getText().toString().trim();

        // Validate input
        if (classId.isEmpty()) {
            tvEnrollStatus.setText("Please enter a Class ID!");
            return;
        }
// Simulate enrollment process (You can replace with real Firebase/API logic)
        if (isValidClassId(classId)) {
            tvEnrollStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvEnrollStatus.setText("Successfully enrolled in class: " + classId);
            etClassId.setText(""); // Clear input
        } else {
            tvEnrollStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvEnrollStatus.setText("Invalid Class ID. Please try again.");
        }
    }

    // Dummy validation for example
    private boolean isValidClassId(String classId) {
        // In real app, check with database or API
        return classId.startsWith("CL") && classId.length() == 6;
    }
}