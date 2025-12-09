package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TChatFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.THomeFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TNotificationFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TProfileFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class THomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thome);

        // Drawer + Toolbar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        // Bottom Navigation
        bottomNav = findViewById(R.id.bottomNav);

        // Load Home Fragment by default
        loadFragment(new THomeFragment());

        // ============ DRAWER TOGGLE (Hamburger Icon) ============
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // ============ DRAWER MENU CLICK LISTENER ============
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new THomeFragment());
            }
            else if (id == R.id.nav_share) {
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_setting) {
                Toast.makeText(this, "Setting", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_download) {
                Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_editprofile) {
                Intent intent=new Intent(THomeActivity.this, TProfileActivity.class);
                startActivity(intent);
            }
            else if (id == R.id.nav_contactus) {
                Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_aboutus) {
                Toast.makeText(this, "About Us", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_feedback) {
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show();
            }
            else if (id == R.id.nav_logout) {
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // ============ BOTTOM NAVIGATION CLICK ============

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

    // ============ LOAD FRAGMENTS ============
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
