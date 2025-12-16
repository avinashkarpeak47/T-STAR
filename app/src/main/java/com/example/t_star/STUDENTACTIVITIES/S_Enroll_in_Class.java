package com.example.t_star.STUDENTACTIVITIES;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.t_star.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

public class S_Enroll_in_Class extends AppCompatActivity {

    private EditText etClassId;
    private AppCompatButton btnSubmitEnroll;
    private TextView tvEnrollStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_enroll_in_class);

        etClassId = findViewById(R.id.etClassId);
        btnSubmitEnroll = findViewById(R.id.btnSubmitEnroll);
        tvEnrollStatus = findViewById(R.id.tvEnrollStatus);

        btnSubmitEnroll.setOnClickListener(v -> enrollClass());
    }

    private void enrollClass() {

        String classId = etClassId.getText().toString().trim();
        String uid = FirebaseAuth.getInstance().getUid();

        if (classId.isEmpty()) {
            tvEnrollStatus.setText("Please enter Class ID");
            return;
        }

        DatabaseReference classRef = FirebaseDatabase.getInstance()
                .getReference("Classes").child(classId);

        classRef.get().addOnSuccessListener(snap -> {

            if (!snap.exists()) {
                tvEnrollStatus.setText("Invalid Class ID!");
                return;
            }

            // add join request
            classRef.child("joinRequests").child(uid).setValue(uid)
                    .addOnSuccessListener(a -> {
                        tvEnrollStatus.setText("Request sent to teacher!");
                        etClassId.setText("");
                    });

        }).addOnFailureListener(e ->
                tvEnrollStatus.setText("Error: " + e.getMessage())
        );
    }
}
