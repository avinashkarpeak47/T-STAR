package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TFragment.THomeFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TChatFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TNotificationFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class THomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thome);

        bottomNav = findViewById(R.id.bottomNav);

        // Load Home Fragment by default
        loadFragment(new THomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new THomeFragment();
            }
            else if (id == R.id.nav_chat) {
                selectedFragment = new TChatFragment();
            }
            else if (id == R.id.nav_notifications) {
                selectedFragment = new TNotificationFragment();
            }
            else if (id == R.id.nav_profile) {
                selectedFragment = new TProfileFragment();
            }

            loadFragment(selectedFragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
