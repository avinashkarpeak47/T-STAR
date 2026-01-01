package com.example.t_star.STUDENTACTIVITIES.Drawer_Activites;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t_star.R;
import com.google.android.material.appbar.MaterialToolbar;

public class S_Help_FAQ_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_help_faq);

        setupToolbar();

    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.Help_FAQ_topBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> {finish();});
    }
}