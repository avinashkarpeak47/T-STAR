package com.example.t_star.STUDENTACTIVITIES;



import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.STUDENTACTIVITIES.SFragment.S_Ai_Assistance_Fragment;
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
                .replace(R.id.s_framelayout_mainpage, new com.example.t_star.STUDENTACTIVITIES.SFragment.S_Classes_Fragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selected = null;

            if (item.getItemId() == R.id.nav_s_classes) {
                selected = new com.example.t_star.STUDENTACTIVITIES.SFragment.S_Classes_Fragment();
            } else if (item.getItemId() == R.id.nav_s_ai_assistant) {
                selected = new S_Ai_Assistance_Fragment();
            }   else if (item.getItemId() == R.id.nav_s_profile) {
                selected = new S_Profile_Fragment();
            }

            if (selected != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.s_framelayout_mainpage, selected)
                        .commit();
            }

            return true;
        });
    }
}
