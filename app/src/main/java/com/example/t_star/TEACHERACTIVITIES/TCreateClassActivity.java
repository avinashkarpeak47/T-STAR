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

    private TextInputEditText edtClassName, edtDescription, edtTeacherName, edtClassId;
    private Button btnSend;
    private TextView txtCheck;

    private boolean isIdAvailable = false; // Track availability

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcreate_class);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtClassName = findViewById(R.id.showClassName);
        edtDescription = findViewById(R.id.showDescription);
        edtTeacherName = findViewById(R.id.showTeacherName);
        edtClassId = findViewById(R.id.edtClassId);
        btnSend = findViewById(R.id.btnSend);
        txtCheck = findViewById(R.id.txtCheck);

        txtCheck.setVisibility(TextView.GONE);
        btnSend.setEnabled(false);

        // 🔥 Add TextWatcher to check class ID live
        edtClassId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkClassIdAvailability(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSend.setOnClickListener(v -> createClassInDatabase());
    }

    private void checkClassIdAvailability(String classId) {

        if (classId.isEmpty()) {
            txtCheck.setText("Enter Class ID");
            txtCheck.setTextColor(getColor(R.color.red));
            txtCheck.setVisibility(TextView.VISIBLE);
            btnSend.setEnabled(false);
            return;
        }

        FirebaseDatabase.getInstance().getReference("Classes")
                .child(classId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {
                        // ❌ ID already used
                        txtCheck.setText("❌ Not Available");
                        txtCheck.setTextColor(getColor(R.color.red));
                        txtCheck.setVisibility(TextView.VISIBLE);
                        isIdAvailable = false;
                        btnSend.setEnabled(false);
                    } else {
                        // ✅ ID available
                        txtCheck.setText("✔ Available");
                        txtCheck.setTextColor(getColor(R.color.green));
                        txtCheck.setVisibility(TextView.VISIBLE);
                        isIdAvailable = true;
                        btnSend.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    txtCheck.setText("Error checking ID");
                    txtCheck.setTextColor(getColor(R.color.red));
                    txtCheck.setVisibility(TextView.VISIBLE);
                    btnSend.setEnabled(false);
                });
    }

    private void createClassInDatabase() {

        String className = edtClassName.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String teacherName = edtTeacherName.getText().toString().trim();
        String classId = edtClassId.getText().toString().trim();
        String teacherId = FirebaseAuth.getInstance().getUid();

        if (!isIdAvailable) {
            Toast.makeText(this, "Class ID not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (className.isEmpty() || teacherName.isEmpty() || classId.isEmpty()) {
            Toast.makeText(this, "Please enter all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        TClassesModel model = new TClassesModel(
                className,
                classId,
                description,
                teacherId,
                teacherName,
                0
        );

        Classes_helper.createClass(model, new Classes_helper.DatabaseCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TCreateClassActivity.this, "Class Created Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TCreateClassActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
