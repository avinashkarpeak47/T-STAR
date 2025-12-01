package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
       Intent intent = new Intent(Splash_Screen.this, S_Mainpage.class);
        startActivity(intent);
        finish();

    }
}