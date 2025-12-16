package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.User_Authentication_Model;
import com.example.t_star.TEACHERACTIVITIES.TMainActivity;
import com.google.firebase.database.*;

/**
 * Centralized helper class for:
 * 1. Saving user model in Realtime Database
 * 2. Fetching roles
 * 3. Redirecting based on role
 */
public class User_Authentication_Helper {

    // Reference: Users/
    private static DatabaseReference usersRef() {
        return FirebaseDatabase.getInstance().getReference("Users");
    }

    // Save UserModel under Users/{uid}
    public static void saveUserModel(String uid, User_Authentication_Model user, DatabaseSaveCallback cb) {
        usersRef().child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    // Load role and redirect
    public static void loadRoleAndRedirect(String uid, Context ctx) {
        usersRef().child(uid).child("role")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.getValue(String.class);
                        if (role == null) role = "Student";
                        redirectToRole(ctx, role);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        redirectToRole(ctx, "Student");
                    }
                });
    }

    // Decide destination activity
    public static void redirectToRole(Context ctx, String role) {
        Intent intent;
        if ("Teacher".equalsIgnoreCase(role)) {
            intent = new Intent(ctx, TMainActivity.class);
        } else {
            intent = new Intent(ctx, S_Mainpage.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(intent);
    }

    // Callback interface
    public interface DatabaseSaveCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
