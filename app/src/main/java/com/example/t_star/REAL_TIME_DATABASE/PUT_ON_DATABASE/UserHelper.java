package com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

/**
 * Small helper to centralize Firebase Realtime DB access and role redirection.
 */
public class UserHelper {

    private static DatabaseReference usersRef() {
        return FirebaseDatabase.getInstance().getReference("Users");
    }

    // Save UserModel under Users/{uid}
    public static void saveUserModel(String uid, UserModel user, DatabaseSaveCallback cb) {
        usersRef().child(uid).setValue(user)
                .addOnSuccessListener(aVoid -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    // Load role for a uid and then redirect to correct activity
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
                        // On DB failure, fallback to student main page
                        redirectToRole(ctx, "Student");
                    }
                });
    }

    // Redirect to role main activity
    public static void redirectToRole(Context ctx, String role) {
        Intent intent;
        if ("Teacher".equalsIgnoreCase(role)) {
            intent = new Intent(ctx, THomeActivity.class);
        } else {
            intent = new Intent(ctx, S_Mainpage.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        ctx.startActivity(intent);
    }

    // Callback for save operation
    public interface DatabaseSaveCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
