package com.example.t_star.STUDENTACTIVITIES.SFragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.t_star.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import de.hdodenhof.circleimageview.CircleImageView;

public class S_Profile_Fragment extends Fragment {

    private CircleImageView ivProfilePic;
    private View headerBg;
    private NestedScrollView profileScroll;

    private TextInputEditText etFirstName, etLastName, etEmail, etMobile;
    private AppCompatButton btnEdit;

    // Animation fields
    private float startX, startY;
    private float deltaX, deltaY;
    private boolean positionsReady = false;

    // Firebase
    private FirebaseUser user;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    private Uri selectedImageUri = null;

    // Image picker launcher
    private final androidx.activity.result.ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(requireContext()).load(selectedImageUri).into(ivProfilePic);
                }
            });

    public S_Profile_Fragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s_profile_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);

        // Bind views
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        headerBg = view.findViewById(R.id.header_bg);
        profileScroll = view.findViewById(R.id.profileScroll);

        etFirstName = view.findViewById(R.id.etFullName);
        etLastName = view.findViewById(R.id.etUserName);
        etEmail = view.findViewById(R.id.etEmail);
        etMobile = view.findViewById(R.id.etMobile);
        btnEdit = view.findViewById(R.id.btnEditProfile);

        // Firebase init
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("ProfileImages");

        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Load firebase data
        loadUserRealtime();

        // Edit/Save button logic
        btnEdit.setOnClickListener(v -> {
            if (btnEdit.getText().toString().equals("Edit Profile")) {
                enableEditing(true);
            } else {
                saveProfile();
            }
        });

        // Click to change profile photo
        ivProfilePic.setOnClickListener(v -> {
            if (btnEdit.getText().toString().equals("Save Profile")) openGallery();
        });

        // Prepare animation after layout drawn
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                prepareAnimationPositions();
            }
        });

        // Scroll listener (supports all Android versions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            profileScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    applyAnimation(scrollY);
                }
            });
        } else {
            profileScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override public void onScrollChanged() {
                    applyAnimation(profileScroll.getScrollY());
                }
            });
        }
    }

    /* ------------------------- FIREBASE PROFILE FUNCTIONS ------------------------- */

    private void loadUserRealtime() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {

                etFirstName.setText(snap.child("firstname").getValue(String.class));
                etLastName.setText(snap.child("lastname").getValue(String.class));
                etEmail.setText(snap.child("email").getValue(String.class));
                etMobile.setText(snap.child("mobile").getValue(String.class));

                String img = snap.child("profilePic").getValue(String.class);
                if (img != null)
                    Glide.with(requireContext()).load(img).into(ivProfilePic);

                enableEditing(false);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableEditing(boolean enable) {
        etFirstName.setEnabled(enable);
        etLastName.setEnabled(enable);
        etMobile.setEnabled(enable);

        etEmail.setEnabled(false); // email not editable

        btnEdit.setText(enable ? "Save Profile" : "Edit Profile");
    }

    private void saveProfile() {
        String fn = etFirstName.getText().toString().trim();
        String ln = etLastName.getText().toString().trim();
        String mob = etMobile.getText().toString().trim();

        if (fn.isEmpty()) { etFirstName.setError("Required"); return; }
        if (ln.isEmpty()) { etLastName.setError("Required"); return; }
        if (mob.length() != 10) { etMobile.setError("Invalid"); return; }

        if (selectedImageUri != null)
            uploadImageAndSave(fn, ln, mob);
        else
            saveData(fn, ln, mob, null);
    }

    private void uploadImageAndSave(String fn, String ln, String mob) {
        // Always save JPG to avoid extension issues
        StorageReference fileRef = storageRef.child(user.getUid() + ".jpg");

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(task -> fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                        saveData(fn, ln, mob, uri.toString())
                ))
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void saveData(String fn, String ln, String mob, @Nullable String img) {

        userRef.child("firstname").setValue(fn);
        userRef.child("lastname").setValue(ln);
        userRef.child("mobile").setValue(mob);

        if (img != null)
            userRef.child("profilePic").setValue(img);

        Toast.makeText(requireContext(), "Profile Updated", Toast.LENGTH_SHORT).show();

        selectedImageUri = null;
        enableEditing(false);
        btnEdit.setText("Edit Profile");
    }

    private void openGallery() {
        Intent pick = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pick.setType("image/*");
        pickImageLauncher.launch(pick);
    }

    /* ----------------------------- ANIMATION LOGIC ----------------------------- */

    private void prepareAnimationPositions() {
        startX = ivProfilePic.getX();
        startY = ivProfilePic.getY();

        int margin = dpToPx(16);
        float endX = margin;
        float endY = margin;

        deltaX = endX - startX;
        deltaY = endY - startY;

        positionsReady = true;
    }

    private void applyAnimation(int scrollY) {
        if (!positionsReady) return;

        int maxScroll = dpToPx(140);
        float progress = Math.min(1f, scrollY / (float) maxScroll);

        ivProfilePic.setTranslationX(deltaX * progress);
        ivProfilePic.setTranslationY(deltaY * progress);

        float scale = 1f - 0.4f * progress;
        ivProfilePic.setScaleX(scale);
        ivProfilePic.setScaleY(scale);

        headerBg.setTranslationY(-dpToPx(40) * progress);
        headerBg.setAlpha(1f - 0.3f * progress);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
