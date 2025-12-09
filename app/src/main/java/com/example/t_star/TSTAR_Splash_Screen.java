package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.TEACHERACTIVITIES.THomeActivity;

public class TSTAR_Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_splash_screen);
        //  Intent intent = new Intent(Splash_Screen.this, S_Mainpage.class);
       // Intent intent = new Intent(TSTAR_Splash_Screen.this, THomeActivity.class);
        Intent intent = new Intent(TSTAR_Splash_Screen.this, TSTAR_Login_Activity.class);
        startActivity(intent);
        finish();

    }
}