package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TAiFragment;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TClassesFragment;
import com.example.t_star.TEACHERACTIVITIES.TMainFragment.TProfileFragment;
import com.example.t_star.TSTAR_Entry_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class TMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static BottomNavigationView bottomNav;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tmain);

        // ================= FIND VIEWS =================
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNav = findViewById(R.id.bottomNav);

        // ================= DRAWER MENU LISTENER =================
        navigationView.setNavigationItemSelectedListener(this);

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
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    // ================= DRAWER ITEM CLICK =================
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_editprofile) {
           loadFragment(new TProfileFragment());
        }
        else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, TSTAR_Entry_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // ================= BACK PRESS =================
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // ================= OPEN DRAWER (FROM FRAGMENT) =================
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}
