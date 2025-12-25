package com.example.t_star.TEACHERACTIVITIES;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.t_star.CHATDATA.MessageDeliveryObserver;
import com.example.t_star.R;
import com.example.t_star.TEACHERACTIVITIES.TFragment.THomeFragment;
import com.example.t_star.TEACHERACTIVITIES.TFragment.TNotificationFragment;
import com.example.t_star.TSTAR_Entry_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class THomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private FirebaseUser currentUser;
    private String myUid;


    // 🔥 CLASS SESSION DATA
    public String classId;
    public String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thome);

        // user id fetch
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(THomeActivity.this, "No authenticated user", Toast.LENGTH_SHORT).show();
            return ;
        }
        myUid = currentUser.getUid();



        // ================= GET CLASS DATA =================
        classId = getIntent().getStringExtra("classId");
        className = getIntent().getStringExtra("className");

        if (classId == null || className == null) {
            Toast.makeText(this, "Class data missing!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MessageDeliveryObserver.start(classId, myUid); //msg delevered tick


        bottomNav = findViewById(R.id.bottomNav);

//        setSupportActionBar(toolbar);
//        toolbar.setTitle(className); // 🔥 show class name

        // ================= LOAD HOME FRAGMENT =================
        loadFragment(THomeFragment.newInstance(classId));



        // ================= BOTTOM NAVIGATION =================
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = THomeFragment.newInstance(classId);
            }
            else if (id == R.id.nav_notifications) {
                selectedFragment = TNotificationFragment.newInstance(classId);
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }
    protected void onStart() {
        super.onStart();
        MessageDeliveryObserver.start(classId, myUid); //msg delevered tick
    }

    // ================= LOAD FRAGMENT =================
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}