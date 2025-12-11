package com.example.t_star.STUDENTACTIVITIES.SFragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Profile Fragment (uses centralized fetcher/validator).
 *
 * Behaviour:
 * - Loads user data via User_Data_Fetcher.getUserData(uid, callback)
 * - Populates UI fields (etFullName, etUserName, etEmail, etMobile, etRollNo)
 * - Edit Profile button toggles between "Edit" and "Save"
 * - On Save -> validates and updates firstname/lastname (split from full name) and rollno
 * - Tap profile image -> pick image from gallery (uploads via User_Data_Fetcher.uploadProfileImage)
 *
 * IMPORTANT:
 * - This fragment assumes your layout IDs match the provided XML:
 *   ivProfilePic, etFullName, etUserName, etEmail, etMobile, etRollNo, btnEditProfile
 */
public class S_Profile_Fragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView ivProfilePic;
    private EditText etFullName, etUserName, etEmail, etMobile, etRollNo;
    private Button btnEditProfile;
    private ProgressBar progressBar; // optional progress (we'll create one programmatically if missing)

    private FirebaseUser currentUser;
    private String uid;

    private Uri selectedImageUri;
    private boolean inEditMode = false;

    public S_Profile_Fragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_s__profile_, container, false);

        // Bind views (IDs from your XML)
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        etFullName = view.findViewById(R.id.etFullName);
        etUserName = view.findViewById(R.id.etUserName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        etRollNo = view.findViewById(R.id.etRollNo);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Optional: if your layout doesn't include a ProgressBar, we add a simple one programmatically
        progressBar = new ProgressBar(requireContext());
        progressBar.setVisibility(View.GONE);

        // Disable editing by default
        setEditingEnabled(false);

        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Not signed in - nothing to load
            Toast.makeText(getContext(), "No authenticated user", Toast.LENGTH_SHORT).show();
            return view;
        }
        uid = currentUser.getUid();

        // Load user data
        loadUserData();

        // Image click -> pick from gallery
        ivProfilePic.setOnClickListener(v -> openImagePicker());

        // Edit button toggles edit/save
        btnEditProfile.setOnClickListener(v -> {
            if (!inEditMode) {
                // Enter edit mode
                inEditMode = true;
                setEditingEnabled(true);
                btnEditProfile.setText("Save");
            } else {
                // Save changes
                attemptSaveProfile();
            }
        });

        return view;
    }

    private void setEditingEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        // etUserName not used for primary save (we populate with firstname), but allow editing if desired
        etUserName.setEnabled(enabled);
        // Email and mobile are usually not editable; keep them disabled for safety
        etEmail.setEnabled(false);
        etMobile.setEnabled(false);
        etRollNo.setEnabled(enabled);
    }

    private void showLoading(boolean show) {
        // Show a Toast-based quick feedback or implement a better Progress UI
        if (show) {
            // simple feedback
            Toast.makeText(getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        showLoading(true);

        User_Data_Fetcher.getUserData(uid, new User_Data_Fetcher.UserDataCallback() {
            @Override
            public void onSuccess(Updated_User_Model user) {
                showLoading(false);
                // Populate fields
                String fullName = user.getFullName();
                etFullName.setText(fullName);
                etUserName.setText(user.firstname != null ? user.firstname : "");
                etEmail.setText(user.email != null ? user.email : "");
                etMobile.setText(user.mobile != null ? user.mobile : "");
                etRollNo.setText(user.rollno != null ? user.rollno : "Not Assigned");

                // Load profile image using Glide if available
                String imgUrl = user.profileImageUrl;
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    Glide.with(requireContext())
                            .load(imgUrl)
                            .placeholder(R.drawable.baseline_person_24) // fallback drawable - ensure this exists
                            .error(R.drawable.baseline_person_24)
                            .into(ivProfilePic);
                } else {
                    // placeholder
                    ivProfilePic.setImageResource(R.drawable.baseline_person_24);
                }
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivProfilePic.setImageURI(selectedImageUri); // show immediately
            // upload to storage and save URL in DB
            uploadProfileImageToStorage();
        }
    }

    private void uploadProfileImageToStorage() {
        if (selectedImageUri == null) return;
        showLoading(true);

        User_Data_Fetcher.uploadProfileImage(uid, selectedImageUri, new User_Data_Fetcher.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                // Save downloadUrl to DB under profileImageUrl
                Map<String, Object> updates = new HashMap<>();
                updates.put("profileImageUrl", downloadUrl);

                User_Data_Fetcher.updateUserFields(uid, updates, new User_Data_Fetcher.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        showLoading(false);
                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        showLoading(false);
                        Toast.makeText(getContext(), "Failed to save image URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Validate and save profile edits (fullname and rollno)
    private void attemptSaveProfile() {

        String fullName = etFullName.getText().toString().trim();
        String rollNo = etRollNo.getText().toString().trim();

        if (!User_Input_Validator.isValidFullName(fullName)) {
            etFullName.setError("Enter valid name");
            etFullName.requestFocus();
            return;
        }

        if (!User_Input_Validator.isValidRollNo(rollNo)) {
            etRollNo.setError("Enter roll number");
            etRollNo.requestFocus();
            return;
        }

        showLoading(true);

        // Split full name into firstname and lastname
        String[] parts = User_Input_Validator.splitFullName(fullName);
        String first = parts[0];
        String last = parts[1];

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstname", first);
        updates.put("lastname", last);
        updates.put("rollno", rollNo);

        User_Data_Fetcher.updateUserFields(uid, updates, new User_Data_Fetcher.SimpleCallback() {
            @Override
            public void onSuccess() {
                showLoading(false);
                inEditMode = false;
                setEditingEnabled(false);
                btnEditProfile.setText("Edit Profile");
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}


/*import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.t_star.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class S_Profile_Fragment extends Fragment {

    private CircleImageView ivProfilePic;
    private View headerBg;
    private NestedScrollView profileScroll;

    // For animation calculations
    private float startX, startY;
    private float deltaX, deltaY;
    private boolean positionsReady = false;

    public S_Profile_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s__profile_, container, false); // use your layout name
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        headerBg = view.findViewById(R.id.header_bg);
        profileScroll = view.findViewById(R.id.profileScroll);

        // 1) Wait until layout is done so we know start position of profile image
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener(){
                    @Override
                    public void onGlobalLayout() {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        prepareAnimationPositions(view);
                    }
                }
        );

        // 2) Listen for scroll and animate according to scrollY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            profileScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY,
                                           int oldScrollX, int oldScrollY) {
                    applyAnimation(scrollY);
                }
            });
        } else {
            profileScroll.getViewTreeObserver().addOnScrollChangedListener(
                    () -> applyAnimation(profileScroll.getScrollY())
            );
        }
    }

    private void prepareAnimationPositions(View root) {
        // Starting position: where the view is initially (centered)
        startX = ivProfilePic.getX();
        startY = ivProfilePic.getY();

        // Target (end) position: top-left corner inside the header
        int margin = dpToPx(16);
        float endX = margin;
        float endY = margin;

        // We will move the image using translation, not changing layout params
        deltaX = endX - startX;
        deltaY = endY - startY;

        positionsReady = true;
    }

    private void applyAnimation(int scrollY) {
        if (!positionsReady) return;

        // How many pixels of scroll we use to finish the animation (adjust if you want)
        int maxScrollForAnim = dpToPx(140);

        float progress = scrollY / (float) maxScrollForAnim;
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        // 1) Move profile picture from center to top-left
        ivProfilePic.setTranslationX(deltaX * progress);
        ivProfilePic.setTranslationY(deltaY * progress);

        // 2) Scale profile picture (make it smaller)
        float startScale = 1.0f;
        float endScale = 0.6f; // 60% of original size
        float currentScale = startScale + (endScale - startScale) * progress;
        ivProfilePic.setScaleX(currentScale);
        ivProfilePic.setScaleY(currentScale);

        // 3) Slightly move header up and fade it a bit (only visual, no layout changes)
        float headerTranslationY = -dpToPx(40) * progress;
        headerBg.setTranslationY(headerTranslationY);

        float startAlpha = 1f;
        float endAlpha = 0.7f;
        float currentAlpha = startAlpha + (endAlpha - startAlpha) * progress;
        headerBg.setAlpha(currentAlpha);
    }

    private int dpToPx(int dp) {
        if (getContext() == null) return dp;
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}*/
