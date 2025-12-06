package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Calendar;

public class TCreateNewEventActivity extends AppCompatActivity {

    private EditText etEventTitle, etEventDate, etEventDescription;
    private Button btnSaveEvent;
    private ImageButton btnBackNewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Use the correct layout file
        setContentView(R.layout.activity_tcreate_new_event);

        // ----- Initialize Views -----
        etEventTitle = findViewById(R.id.etEventTitle);
        etEventDate = findViewById(R.id.etEventDate);
        etEventDescription = findViewById(R.id.etEventDescription);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // ----- Date Picker -----
        etEventDate.setOnClickListener(v -> showDatePicker());

        // ----- Save Button -----
        btnSaveEvent.setOnClickListener(v -> validateAndSave());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (DatePicker view, int y, int m, int d) -> {
                    // m is 0-based (Jan = 0)
                    String date = d + " / " + (m + 1) + " / " + y;
                    etEventDate.setText(date);
                },
                year,
                month,
                day
        );
        dp.show();
    }

    // ✅ Check all fields before saving
    private void validateAndSave() {
        String title = etEventTitle.getText() != null
                ? etEventTitle.getText().toString().trim()
                : "";

        String date = etEventDate.getText() != null
                ? etEventDate.getText().toString().trim()
                : "";

        String desc = etEventDescription.getText() != null
                ? etEventDescription.getText().toString().trim()
                : "";

        // ---- Validation ----
        if (title.isEmpty()) {
            etEventTitle.setError("Please enter event title");
            etEventTitle.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etEventDate.setError("Please select event date");
            etEventDate.requestFocus();
            return;
        }

        if (desc.isEmpty()) {
            etEventDescription.setError("Please enter event description");
            etEventDescription.requestFocus();
            return;
        }

        // ✅ All options filled – now you can save
        // TODO: save to DB / Firebase if needed
        Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
