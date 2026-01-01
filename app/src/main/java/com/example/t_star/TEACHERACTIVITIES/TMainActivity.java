package com.example.t_star.TEACHERACTIVITIES;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TAiFragment;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TClassesFragment;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TMainActivity extends AppCompatActivity {

    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmain);

        bottomNav = findViewById(R.id.bottomNav);

        // ================= DEFAULT FRAGMENT =================
        if (savedInstanceState == null) {
            loadFragment(new TClassesFragment());
            bottomNav.setSelectedItemId(R.id.nav_t_classes);
        }

        // ================= BOTTOM NAV =================
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_t_classes) {
                selectedFragment = new TClassesFragment();
            }
            else if (item.getItemId() == R.id.nav_t_ai_assistant) {
                selectedFragment = new TAiFragment();
            }
            else if (item.getItemId() == R.id.nav_tprofile) {
                selectedFragment = new TProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    // ================= LOAD FRAGMENT =================
    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
