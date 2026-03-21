package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.Classes_helper;
import com.example.t_star.TEACHERACTIVITIES.CLASSES.TClassesModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class TCreateClassActivity extends AppCompatActivity {

    private TextInputEditText edtClassName, edtSubject, edtClassId;
    private Button btnSend;
    private TextView txtCheck;

    private boolean isIdAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcreate_class);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bind views
        edtClassName = findViewById(R.id.showClassName);
        edtSubject   = findViewById(R.id.showTeacherName);
        edtClassId   = findViewById(R.id.edtClassId);
        btnSend      = findViewById(R.id.btnSend);
        txtCheck     = findViewById(R.id.txtCheck);

        txtCheck.setVisibility(TextView.GONE);
        btnSend.setEnabled(false);

        // --------------------------------------------------
        // CLASS ID TEXT WATCHER
        // --------------------------------------------------
        edtClassId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Auto uppercase
                String input = s.toString();
                String classId = input.toUpperCase();

                if (!input.equals(classId)) {
                    edtClassId.setText(classId);
                    edtClassId.setSelection(classId.length());
                    return;
                }

                // Empty
                if (classId.isEmpty()) {
                    txtCheck.setVisibility(TextView.GONE);
                    btnSend.setEnabled(false);
                    isIdAvailable = false;
                    return;
                }

                // 🔴 FORMAT VALIDATION (4 letters + 2 digits)
                if (!isValidClassId(classId)) {
                    txtCheck.setText(
                            "ID must have EXACTLY 4 letters & 2 digits"
                    );
                    txtCheck.setTextColor(getColor(R.color.red));
                    txtCheck.setVisibility(TextView.VISIBLE);
                    btnSend.setEnabled(false);
                    isIdAvailable = false;
                    return;
                }

                // ✅ Format correct → Firebase availability check
                checkClassIdAvailability(classId);
            }
        });

        btnSend.setOnClickListener(v -> createClassInDatabase());
    }

    // --------------------------------------------------
    // CHECK CLASS ID AVAILABILITY
    // --------------------------------------------------
    private void checkClassIdAvailability(String classId) {

        FirebaseDatabase.getInstance()
                .getReference("Classes")
                .child(classId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {
                        txtCheck.setText("❌ Not Available");
                        txtCheck.setTextColor(getColor(R.color.red));
                        isIdAvailable = false;
                        btnSend.setEnabled(false);
                    } else {
                        txtCheck.setText("✔ Available");
                        txtCheck.setTextColor(getColor(R.color.green));
                        isIdAvailable = true;
                        btnSend.setEnabled(true);
                    }
                    txtCheck.setVisibility(TextView.VISIBLE);
                })
                .addOnFailureListener(e -> {
                    txtCheck.setText("❌ Error checking ID");
                    txtCheck.setTextColor(getColor(R.color.red));
                    txtCheck.setVisibility(TextView.VISIBLE);
                    btnSend.setEnabled(false);
                    isIdAvailable = false;
                });
    }

    // --------------------------------------------------
    // CREATE CLASS
    // --------------------------------------------------
    private void createClassInDatabase() {

        String className = edtClassName.getText().toString().trim();
        String subject   = edtSubject.getText().toString().trim();
        String classId   = edtClassId.getText().toString().trim();

        String teacherId = FirebaseAuth.getInstance().getUid();

        if (teacherId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (className.isEmpty() || subject.isEmpty() || classId.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔴 FINAL SAFETY CHECK
        if (!isValidClassId(classId)) {
            Toast.makeText(this,
                    "Class ID must have exactly 4 letters & 2 digits",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isIdAvailable) {
            Toast.makeText(this, "Class ID not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create model
        TClassesModel model = new TClassesModel();
        model.setClassName(className);
        model.setClassId(classId);
        model.setTeacherId(teacherId);
        model.setSubject(subject);
        model.setStudentsCount(0);

        Classes_helper.createClass(model, new Classes_helper.DatabaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TCreateClassActivity.this,
                        "Class Created Successfully!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TCreateClassActivity.this,
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------------------------------------------------
    // EXACT VALIDATION METHOD
    // --------------------------------------------------
    private boolean isValidClassId(String classId) {

        // Only A-Z and 0-9, exactly 6 chars
        if (!classId.matches("^[A-Z0-9]{6}$"))
            return false;

        int letterCount = 0;
        int digitCount = 0;

        for (char c : classId.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCount++;
            } else if (Character.isDigit(c)) {
                digitCount++;
            }
        }

        // EXACT: 4 letters + 2 digits
        return letterCount == 4 && digitCount == 2;
    }
}
