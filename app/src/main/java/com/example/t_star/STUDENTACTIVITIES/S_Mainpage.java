package com.example.t_star.STUDENTACTIVITIES;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Assignment_Fragment;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Chat_Fragment;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Event_Fragment;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Home_Fragment;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Profile_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class S_Mainpage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_mainpage);

        bottomNavigationView = findViewById(R.id.s_bottom_nav);

        // Default fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.s_framelayout_homepage, new S_Home_Fragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selected = null;

            if (item.getItemId() == R.id.nav_s_home) {
                selected = new S_Home_Fragment();
            } else if (item.getItemId() == R.id.nav_s_assignment) {
                selected = new S_Assignment_Fragment();
            } else if (item.getItemId() == R.id.nav_s_event) {
                selected = new S_Event_Fragment();
            } else if (item.getItemId() == R.id.nav_s_chat) {
                selected = new S_Chat_Fragment();
            } else if (item.getItemId() == R.id.nav_s_profile) {
                selected = new S_Profile_Fragment();
            }

            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.s_framelayout_homepage, selected)
                        .commit();
            }

            return true;
        });
    }
}
