package com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE;


import android.net.Uri;

import androidx.annotation.NonNull;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.Updated_User_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;


import java.util.HashMap;
import java.util.Map;

/**
 * Centralized helper for reading and updating user data in Realtime Database
 * and uploading profile images to Firebase Storage.
 *
 * Methods are asynchronous and use callback interfaces defined below.
 */
public class User_Data_Fetcher {

    private static final DatabaseReference USERS_REF = FirebaseDatabase.getInstance().getReference("Users");


    // Callback for fetching full user model
    public interface UserDataCallback {
        void onSuccess(Updated_User_Model user);
        void onFailure(Exception e);
    }

    // Callback for simple success/failure operations
    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Callback for image upload - returns download URL
    public interface UploadCallback {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }

    /**
     * Read user model from Users/{uid}
     */
    public static void getUserData(String uid, UserDataCallback cb) {
        if (uid == null || uid.isEmpty()) {
            cb.onFailure(new IllegalArgumentException("UID is null/empty"));
            return;
        }

        USERS_REF.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    cb.onFailure(new Exception("User data not found"));
                    return;
                }
                try {
                    Updated_User_Model user = snapshot.getValue(Updated_User_Model.class);
                    // If rollno or profileImageUrl missing, fill defaults to avoid nulls
                    if (user != null) {
                        if (user.rollno == null) user.rollno = "Not Assigned";
                        if (user.profileImageUrl == null) user.profileImageUrl = "";
                        cb.onSuccess(user);
                    } else {
                        cb.onFailure(new Exception("Failed to parse user model"));
                    }
                } catch (Exception e) {
                    cb.onFailure(e);
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                cb.onFailure(error.toException());
            }
        });
    }

    /**
     * Update specific fields on Users/{uid}. Use a Map<String,Object> of fields to update.
     */
    public static void updateUserFields(String uid, Map<String, Object> updates, SimpleCallback cb) {
        if (uid == null || uid.isEmpty()) {
            cb.onFailure(new IllegalArgumentException("UID is null/empty"));
            return;
        }
        if (updates == null || updates.isEmpty()) {
            cb.onFailure(new IllegalArgumentException("No updates provided"));
            return;
        }

        USERS_REF.child(uid).updateChildren(updates)
                .addOnSuccessListener(aVoid -> cb.onSuccess())
                .addOnFailureListener(cb::onFailure);
    }

    /**
     * Upload profile image to Storage at "profile_images/{uid}.jpg" and return download URL via callback.
     */
    public static void uploadProfileImage(
            String uid,
            Uri imageUri,
            UploadCallback callback) {

        MediaManager.get().upload(imageUri)
                .unsigned("t_star")
                .option("folder", "app uploads")
                .callback(new com.cloudinary.android.callback.UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl =
                                resultData.get("secure_url").toString();
                        callback.onSuccess(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onFailure(
                                new Exception(error.getDescription()));
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String r, long b, long t) {}
                    @Override public void onReschedule(String r, ErrorInfo e) {}
                })
                .dispatch();

    }
    public static void uploadChatImage(
            String uid,
            Uri imageUri,
            UploadCallback callback) {

        MediaManager.get().upload(imageUri)
                .unsigned("t_star")
                .option("folder", "chat data")
                .callback(new com.cloudinary.android.callback.UploadCallback() {

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl =
                                resultData.get("secure_url").toString();
                        callback.onSuccess(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onFailure(
                                new Exception(error.getDescription()));
                    }

                    @Override public void onStart(String requestId) {}
                    @Override public void onProgress(String r, long b, long t) {}
                    @Override public void onReschedule(String r, ErrorInfo e) {}
                })
                .dispatch();

    }
}

