package com.example.t_star.TEACHERACTIVITIES;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.ASSIGNMENTS.TAssignmentModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class TCreateNewAssignActivity extends AppCompatActivity {

    TextInputEditText etTitle, etDescription, etDueDate;
    AppCompatButton btnSave;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcreate_new_assign);

        classId = getIntent().getStringExtra("classId");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDueDate = findViewById(R.id.etDueDate);
        btnSave = findViewById(R.id.btnSave);

        etDueDate.setOnClickListener(v -> pickDate());
        btnSave.setOnClickListener(v -> saveAssignment());
    }

    private void pickDate() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                this,
                (view, y, m, d) -> etDueDate.setText(d+"-"+(m+1)+"-"+y),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
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

        String id = ref.push().getKey();

        FirebaseDatabase.getInstance().getReference("Classes")
                .child(classId).child("studentsCount")
                .get()
                .addOnSuccessListener(s -> {

                    int total = s.exists() ? s.getValue(Integer.class) : 0;

                    TAssignmentModel m = new TAssignmentModel(
                            id, classId, title, desc, due, total, 0
                    );

                    ref.child(id).setValue(m)
                            .addOnSuccessListener(a -> {
                                Toast.makeText(this,"Assignment Created",Toast.LENGTH_SHORT).show();
                                finish();
                            });
                });
    }
}
