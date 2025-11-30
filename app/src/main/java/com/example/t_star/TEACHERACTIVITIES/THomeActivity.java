package com.example.t_star.TEACHERACTIVITIES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.SHome_Page;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class THomeActivity extends AppCompatActivity {

    private CardView cardCreateAssignment, cardAttendance, cardCreateEvent, cardChat, cardNotifications;

    private ImageView imgProfile;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNav;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thome);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Optional: set navigation click
        toolbar.setNavigationOnClickListener(v -> finish());

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            // open profile activity
            startActivity(new Intent(this, TProfileActivity.class));
        });

        // Cards
        cardCreateAssignment = findViewById(R.id.cardCreateAssignment);
        cardAttendance = findViewById(R.id.cardAttendance);
        cardCreateEvent = findViewById(R.id.cardCreateEvent);
        cardChat = findViewById(R.id.cardChat);
        cardNotifications = findViewById(R.id.cardNotifications);

        cardCreateAssignment.setOnClickListener(v -> openAssignment());
        cardAttendance.setOnClickListener(v -> openAttendance());
        cardCreateEvent.setOnClickListener(v -> openCreateEvent());
        cardChat.setOnClickListener(v -> openChat());
        cardNotifications.setOnClickListener(v -> openNotifications());


        // Bottom navigation
//        bottomNav = findViewById(R.id.bottomNav);
//        bottomNav.setOnNavigationItemSelectedListener(item -> {
//            switch (item.getItemId()){
//                case R.id.nav_home:
//                    startActivity(new Intent(THomeActivity.this, TProfileActivity.class));
//                    return true;
//                case R.id.nav_chat:
//                    startActivity(new Intent(THomeActivity.this, TProfileActivity.class));
//                    return true;
//                case R.id.nav_notifications:
//                    startActivity(new Intent(THomeActivity.this, TProfileActivity.class));
//                    return true;
//                case R.id.nav_profile:
//                    startActivity(new Intent(THomeActivity.this, TProfileActivity.class));
//                    return true;
//            }
//            return false;
//        });
    }

    private void openAssignment(){
        // Launch AssignmentActivity
        startActivity(new Intent(this, TAsssignmentActivity.class));
    }

    private void openAttendance(){
        startActivity(new Intent(this, TAttendanceActivity.class));
    }

    private void openCreateEvent(){
        startActivity(new Intent(this, TEventActivity.class));
    }

    private void openChat(){
        startActivity(new Intent(this, TChatActivity.class));
    }

    private void openNotifications(){
        startActivity(new Intent(this, TNotificationActivity.class));
    }

    }

