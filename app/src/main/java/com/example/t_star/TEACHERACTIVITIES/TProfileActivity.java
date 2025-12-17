package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.R;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class TProfileActivity extends AppCompatActivity {

    CircleImageView ivProfilePic;
    TextInputEditText etFullName, etEmail, etMobile;
    AppCompatButton btnEditProfile;
    TextView tvLogout;

    boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tprofile);

        // ---- Bind views (MATCH XML IDs) ----
        ivProfilePic = findViewById(R.id.ivProfilePic);
        etFullName   = findViewById(R.id.etFullName);
        etEmail      = findViewById(R.id.etEmail);
        etMobile     = findViewById(R.id.etMobile);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        // Logout TextView (add id in XML)
        tvLogout = findViewById(R.id.tvlogout);

        // ---- Edit Profile Button ----
        btnEditProfile.setOnClickListener(v -> {
            isEditMode = !isEditMode;

            etFullName.setEnabled(isEditMode);
            etMobile.setEnabled(isEditMode);

            btnEditProfile.setText(
                    isEditMode ? "Save Profile" : "Edit Profile"
            );
        });

        // ---- Logout ----
        tvLogout.setOnClickListener(v -> {
            finish(); // later add FirebaseAuth.getInstance().signOut();
        });
    }
}
