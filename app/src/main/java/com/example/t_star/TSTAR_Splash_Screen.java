package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.User_Authentication_Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TSTAR_Splash_Screen extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1500;
    private boolean hasNavigated = false;  // prevents double navigation

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_splash_screen);
        // NO LOGIC HERE – only layout inflation
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (hasNavigated) return;    // Avoid double redirect if activity restarts
        hasNavigated = true;

        new Handler().postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user == null) {
                startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                finish();
            } else {
                User_Authentication_Helper.loadRoleAndRedirect(user.getUid(), TSTAR_Splash_Screen.this);
                // No finish() here, redirect helper will handle it
            }

        }, SPLASH_DELAY_MS);
    }
}
