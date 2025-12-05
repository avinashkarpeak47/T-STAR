package com.example.t_star.TEACHERACTIVITIES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class TProfileActivity extends AppCompatActivity {

    ImageView imgProfile;
    TextInputEditText etName, etEmail, etPhone;
    AppCompatButton btprofileSave, btprofileEDP, btprofileDEP;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tprofile);

        imgProfile = findViewById(R.id.imgProfile);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);

        btprofileSave = findViewById(R.id.btprofileSave);
        btprofileEDP = findViewById(R.id.btprofileEDP);
        btprofileDEP = findViewById(R.id.btprofileDEP);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Back button
        toolbar.setNavigationOnClickListener(v -> finish());

        // Menu handling (logout)
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                Intent intent = new Intent(TProfileActivity.this, THomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
