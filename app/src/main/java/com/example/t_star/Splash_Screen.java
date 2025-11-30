package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t_star.STUDENTACTIVITIES.SHome_Page;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Intent intent= new Intent(Splash_Screen.this, SHome_Page.class);
        startActivity(intent);
        finish();

    }
}