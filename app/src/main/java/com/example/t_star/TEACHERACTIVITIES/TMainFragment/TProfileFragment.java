package com.example.t_star.TEACHERACTIVITIES.TMainFragment;

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
import com.example.t_star.TEACHERACTIVITIES.TMainActivity;
import com.example.t_star.TSTAR_Login_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 2001;

    private ImageView ivProfilePic;
    private EditText etFullName, etEmail, etMobile;
    private TextView tvHeaderTeacherId;
    private Button btnEditProfile;

    private String profileImageUrl;   // 🔥 long click साठी

    private LinearLayout rowChangePassword, rowSettings, rowLogout, rowDeleteAccount;

    private FirebaseUser currentUser;
    private String uid;
    private Uri selectedImageUri;
    private boolean inEditMode = false;

    public TProfileFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_profile, container, false);

        // ---------------- BIND VIEWS ----------------
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvHeaderTeacherId = view.findViewById(R.id.tvHeaderStudentId);

        rowChangePassword = view.findViewById(R.id.rowChangePassword);
        rowSettings = view.findViewById(R.id.rowSettings);
        rowLogout = view.findViewById(R.id.rowLogout);
        rowDeleteAccount = view.findViewById(R.id.rowDeleteAccount);

        // Disable edit initially
        setEditingEnabled(false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "No authenticated user", Toast.LENGTH_SHORT).show();
            return view;
        }
        uid = currentUser.getUid();

        setupAccountActions();
        loadTeacherData();

        ivProfilePic.setOnClickListener(v -> openImagePicker());

        btnEditProfile.setOnClickListener(v -> {
            if (!inEditMode) {
                inEditMode = true;
                setEditingEnabled(true);
                btnEditProfile.setText("Save");
            } else {
                attemptSaveProfile();
            }
        });

        ivProfilePic.setOnLongClickListener(v -> {

            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                showFullImageDialog(profileImageUrl);
            } else {
                Toast.makeText(getContext(),
                        "Profile image not available",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        });



        // ---------- KEYBOARD FIX (KEEP BOTTOM NAV VISIBLE) ----------
        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            if (getActivity() == null) return;

            int heightDiff = view.getRootView().getHeight() - view.getHeight();

            // keyboard visible condition
            boolean isKeyboardOpen = heightDiff > 200;

            if (TMainActivity.bottomNav != null) {
                TMainActivity.bottomNav.setTranslationY(0);
            }
        });


        return view;
    }

    private void showFullImageDialog(String imageUrl) {

        Dialog dialog = new Dialog(requireContext(),
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        dialog.setContentView(R.layout.dialog_full_image);

        ImageView ivFull = dialog.findViewById(R.id.ivFullImage);

        Glide.with(requireContext())
                .load(imageUrl)
                .into(ivFull);

        // tap anywhere to close
        ivFull.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    // ---------------- ENABLE / DISABLE ----------------
    private void setEditingEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etEmail.setEnabled(false);
        etMobile.setEnabled(false);
    }

    // ---------------- LOAD TEACHER DATA ----------------
    private void loadTeacherData() {

        User_Data_Fetcher.getUserData(uid, new User_Data_Fetcher.UserDataCallback() {
            @Override
            public void onSuccess(Updated_User_Model user) {

                etFullName.setText(user.getFullName());
                etEmail.setText(user.email != null ? user.email : "");
                etMobile.setText(user.mobile != null ? user.mobile : "");

                tvHeaderTeacherId.setText("Teacher ID: " + uid.substring(0, 28));

                profileImageUrl = user.profileImageUrl;   // 🔥 ADD THIS

                if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .centerCrop()
                            .circleCrop()
                            .into(ivProfilePic);
                }

                if (user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(user.profileImageUrl)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .centerCrop()
                            .circleCrop()
                            .into(ivProfilePic);

                } else {
                    ivProfilePic.setImageResource(R.drawable.baseline_person_24);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(),
                        "Failed to load profile: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------- IMAGE PICKER ----------------
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {

            selectedImageUri = data.getData();
            ivProfilePic.setImageURI(selectedImageUri);
            uploadProfileImage();
        }
    }

    private void uploadProfileImage() {
        if (selectedImageUri == null) return;

        User_Data_Fetcher.uploadProfileImage(uid, selectedImageUri,
                new User_Data_Fetcher.UploadCallback() {
                    @Override
                    public void onSuccess(String downloadUrl) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("profileImageUrl", downloadUrl);


                        User_Data_Fetcher.updateUserFields(uid, map,
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
                                "Upload failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ---------------- SAVE PROFILE ----------------
    private void attemptSaveProfile() {

        String fullName = etFullName.getText().toString().trim();

        if (!User_Input_Validator.isValidFullName(fullName)) {
            etFullName.setError("Enter valid full name");
            etFullName.requestFocus();
            return;
        }

        String[] parts = User_Input_Validator.splitFullName(fullName);

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstname", parts[0]);
        updates.put("lastname", parts[1]);

        User_Data_Fetcher.updateUserFields(uid, updates,
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
                    .setMessage("Send reset link to registered email?")
                    .setPositiveButton("Send", (d, w) -> {
                        FirebaseAuth.getInstance()
                                .sendPasswordResetEmail(currentUser.getEmail())
                                .addOnSuccessListener(a ->
                                        Toast.makeText(getContext(),
                                                "Reset email sent",
                                                Toast.LENGTH_LONG).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        rowLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you really sure you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> {

                        FirebaseAuth.getInstance().signOut();

                        Intent i = new Intent(requireContext(), TSTAR_Login_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });


        rowDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("This action is permanent. Continue?")
                    .setPositiveButton("Delete", (d, w) -> {
                        FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(uid)
                                .removeValue();

                        currentUser.delete();
                        Toast.makeText(getContext(),
                                "Account deleted",
                                Toast.LENGTH_LONG).show();

                        Intent i = new Intent(requireContext(), TSTAR_Login_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
