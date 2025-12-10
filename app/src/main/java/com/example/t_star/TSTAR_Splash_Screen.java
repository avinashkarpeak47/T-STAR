package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.UserHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Simple splash that waits briefly, then:
 * - If no authenticated user → open Entry screen
 * - If authenticated → read role and redirect appropriately
 */
public class TSTAR_Splash_Screen extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 1200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_splash_screen);

        new Handler().postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // Not signed in -> go to entry screen
                startActivity(new Intent(TSTAR_Splash_Screen.this, TSTAR_Entry_Activity.class));
                finish();
            } else {
                // User signed in -> load role from DB and redirect
                UserHelper.loadRoleAndRedirect(user.getUid(), TSTAR_Splash_Screen.this);
                finish();
            }
        }, SPLASH_DELAY_MS);
    }
}
