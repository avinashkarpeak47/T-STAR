package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class TSTAR_Splash_Screen extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    private static final int SPLASH_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_splash_screen);

        new Handler().postDelayed(this::checkUserStatus, SPLASH_DELAY);
    }

    private void checkUserStatus() {

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // CASE 1: USER NOT LOGGED IN → Open Entry Page
        if (currentUser == null) {
            startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
            finish();
            return;
        }

        // CASE 2: USER LOGGED IN → CHECK ROLE IN DATABASE
        String uid = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    mAuth.signOut();
                    startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                    finish();
                    return;
                }

                String role = snapshot.child("role").getValue(String.class);

                if (role == null) {
                    mAuth.signOut();
                    startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                    finish();
                    return;
                }

                // Redirect based on Role
                if (role.equalsIgnoreCase("Student")) {
                    startActivity(new Intent(TSTAR_Splash_Screen.this, S_Mainpage.class));
                } else if (role.equalsIgnoreCase("Teacher")) {
                    startActivity(new Intent(TSTAR_Splash_Screen.this, THomeActivity.class));
                } else {
                    // Unknown role → force logout
                    mAuth.signOut();
                    startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                }

                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mAuth.signOut();
                startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                finish();
            }
        });
    }
}
