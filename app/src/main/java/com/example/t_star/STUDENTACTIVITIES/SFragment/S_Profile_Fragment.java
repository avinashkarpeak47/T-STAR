package com.example.t_star.STUDENTACTIVITIES.SFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.example.t_star.REAL_TIME_DATABASE.GET_FROM_DATABASE.User_Data_Fetcher;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.Updated_User_Model;
import com.example.t_star.REAL_TIME_DATABASE.Validation.User_Input_Validator;
import com.example.t_star.TSTAR_Login_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class S_Profile_Fragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView ivProfilePic;
    private EditText etFullName, etEmail, etMobile, etRollNo;
    private Button btnEditProfile;

    private LinearLayout rowSettings, rowChangePassword, rowLogout, rowDeleteAccount;

    private FirebaseUser currentUser;
    private String uid;

    private Uri selectedImageUri;
    private boolean inEditMode = false;

    private String profileImageUrl = "";   // ⭐ Cloudinary URL holder

    public S_Profile_Fragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s_profile_, container, false);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        etFullName   = view.findViewById(R.id.etFullName);
        etEmail      = view.findViewById(R.id.etEmail);
        etMobile     = view.findViewById(R.id.etMobile);
        etRollNo     = view.findViewById(R.id.etRollNo);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        rowSettings = view.findViewById(R.id.rowSettings);
        rowChangePassword = view.findViewById(R.id.rowChangePassword);
        rowLogout = view.findViewById(R.id.rowLogout);
        rowDeleteAccount = view.findViewById(R.id.rowDeleteAccount);

        setEditingEnabled(false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return view;

        uid = currentUser.getUid();

        setupAccountActions();
        loadUserData();

        // ---------- PICK IMAGE ----------
        ivProfilePic.setOnClickListener(v -> openImagePicker());

        // ---------- FULL SCREEN PREVIEW ----------
        ivProfilePic.setOnLongClickListener(v -> {
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                showFullImageDialog(profileImageUrl);
            } else {
                Toast.makeText(getContext(),"Profile image not available",Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // ---------- EDIT & SAVE ----------
        btnEditProfile.setOnClickListener(v -> {
            if (!inEditMode) {
                inEditMode = true;
                setEditingEnabled(true);
                btnEditProfile.setText("Save");
            } else {
                attemptSaveProfile();
            }
        });

        return view;
    }

    // ---------------- LOAD USER DATA ----------------
    private void loadUserData() {

        User_Data_Fetcher.getUserData(uid, new User_Data_Fetcher.UserDataCallback() {
            @Override
            public void onSuccess(Updated_User_Model user) {

                profileImageUrl = user.profileImageUrl;   // ⭐ store URL

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(profileImageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .circleCrop()
                            .into(ivProfilePic);
                } else {
                    ivProfilePic.setImageResource(R.drawable.baseline_person_24);
                }

                etFullName.setText(user.getFullName());
                etEmail.setText(user.email);
                etMobile.setText(user.mobile);
                etRollNo.setText(user.rollno != null ? user.rollno : "");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(),
                        "Failed to load profile: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------- ENABLE / DISABLE EDITING ----------------
    private void setEditingEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etRollNo.setEnabled(enabled);

        etEmail.setEnabled(false);
        etMobile.setEnabled(false);
    }

    // ---------------- IMAGE PICKER ----------------
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req,res,data);

        if (req == PICK_IMAGE_REQUEST && res == Activity.RESULT_OK && data != null) {

            selectedImageUri = data.getData();
            ivProfilePic.setImageURI(selectedImageUri);

            uploadProfileImage();
        }
    }

    // ---------------- UPLOAD IMAGE TO CLOUDINARY + DB ----------------
    private void uploadProfileImage() {

        if (selectedImageUri == null) return;

        User_Data_Fetcher.uploadProfileImage(uid, selectedImageUri,
                new User_Data_Fetcher.UploadCallback() {
                    @Override
                    public void onSuccess(String downloadUrl) {

                        profileImageUrl = downloadUrl; // refresh url

                        Map<String,Object> map = new HashMap<>();
                        map.put("profileImageUrl", downloadUrl);

                        User_Data_Fetcher.updateUserFields(uid,map,
                                new User_Data_Fetcher.SimpleCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(getContext(),
                                                "Profile image updated",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(getContext(),
                                                e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(),
                                "Upload failed: "+e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ---------------- FULL SCREEN IMAGE PREVIEW ----------------
    private void showFullImageDialog(String imageUrl) {

        Dialog dialog = new Dialog(requireContext(),
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        dialog.setContentView(R.layout.dialog_full_image);

        ImageView ivFull = dialog.findViewById(R.id.ivFullImage);

        Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(ivFull);

        ivFull.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // ---------------- SAVE PROFILE ----------------
    private void attemptSaveProfile() {

        String fullName = etFullName.getText().toString().trim();
        String roll = etRollNo.getText().toString().trim();

        if (!User_Input_Validator.isValidFullName(fullName)) {
            etFullName.setError("Enter valid name");
            etFullName.requestFocus();
            return;
        }

        if (!User_Input_Validator.isValidRollNo(roll)) {
            etRollNo.setError("Enter roll number");
            etRollNo.requestFocus();
            return;
        }

        String[] parts = User_Input_Validator.splitFullName(fullName);

        Map<String,Object> map = new HashMap<>();
        map.put("firstname", parts[0]);
        map.put("lastname", parts[1]);
        map.put("rollno", roll);

        User_Data_Fetcher.updateUserFields(uid,map,
                new User_Data_Fetcher.SimpleCallback() {
                    @Override
                    public void onSuccess() {

                        inEditMode = false;
                        setEditingEnabled(false);
                        btnEditProfile.setText("Edit Profile");

                        Toast.makeText(getContext(),
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ---------------- ACCOUNT ACTIONS ----------------
    private void setupAccountActions() {

        rowChangePassword.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Reset Password")
                    .setMessage("Send reset link to your email?")
                    .setPositiveButton("Send",(d,w)->{
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(currentUser.getEmail());
                        Toast.makeText(getContext(),"Reset link sent",Toast.LENGTH_SHORT).show();
                    }).setNegativeButton("Cancel",null).show();
        });

        rowLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireContext(), TSTAR_Login_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            requireActivity().finish();
        });

        rowDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("This action is permanent. Continue?")
                    .setPositiveButton("Delete",(d,w)->{

                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid)
                                .removeValue();

                        currentUser.delete();

                        Intent i = new Intent(requireContext(), TSTAR_Login_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel",null).show();
        });
    }
}
