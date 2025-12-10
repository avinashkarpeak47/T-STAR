package com.example.t_star;



import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.UserHelper;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.UserModel;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import com.google.firebase.database.DatabaseError;

/**
 * Part Two: verify OTP code. Handles two flows:
 * - flow="signup": after successful phone verification, create email account,
 *   then link phone credential to that same user and write UserModel to DB.
 * - flow="login": verify phone credential and sign in (user must already have phone linked at signup).
 *
 * Note: This class contains key linking logic to ensure SAME UID for phone and email.
 */
public class TSTAR_Login_With_Mobile_Part_Two extends AppCompatActivity {

    EditText etOtp;
    Button btnVerify;
    FirebaseAuth mAuth;

    String verificationId;
    String flow;
    // Signup fields (if flow == "signup")
    String firstname, lastname, email, password, mobile, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_two_activity);

        etOtp = findViewById(R.id.et_login_mobile); // reuse same id; it's OTP input here
        btnVerify = findViewById(R.id.btn_login_verify_otp);
        mAuth = FirebaseAuth.getInstance();

        Intent in = getIntent();
        flow = in.getStringExtra("flow");
        verificationId = in.getStringExtra("verificationId");
        mobile = in.getStringExtra("mobile");

        if ("signup".equals(flow)) {
            firstname = in.getStringExtra("firstname");
            lastname = in.getStringExtra("lastname");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            role = in.getStringExtra("role");
        }

        btnVerify.setOnClickListener(v -> {
            String code = etOtp.getText().toString().trim();
            if (TextUtils.isEmpty(code) || code.length() < 6) {
                etOtp.setError("Enter valid 6-digit OTP");
                etOtp.requestFocus();
                return;
            }
            verifyCode(code);
        });
    }

    // Construct credential from verificationId and code then handle flows
    private void verifyCode(String code) {
        PhoneAuthCredential phoneCredential;
        try {
            phoneCredential = PhoneAuthProvider.getCredential(verificationId, code);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid verification data", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("signup".equals(flow)) {
            handleSignupWithPhoneCredential(phoneCredential);
        } else {
            handleLoginWithPhoneCredential(phoneCredential);
        }
    }

    // Signup flow:
    // 1) Create user with email/password
    // 2) Link phone credential to the newly created user (so phone & email share same UID)
    // 3) Save user model under Users/{uid}
    private void handleSignupWithPhoneCredential(PhoneAuthCredential phoneCredential) {
        // Create user with email/password first
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser createdUser = authResult.getUser();
                    if (createdUser == null) {
                        Toast.makeText(this, "Signup failed: no user", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Link phone credential to the same created user
                    createdUser.linkWithCredential(phoneCredential)
                            .addOnSuccessListener(linkResult -> {
                                FirebaseUser linkedUser = linkResult.getUser();
                                if (linkedUser == null) {
                                    Toast.makeText(this, "Account created but linking failed", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // Save user data under Users/{uid}
                                String uid = linkedUser.getUid();
                                UserModel um = new UserModel(uid, firstname, lastname, email, mobile, role);
                                UserHelper.saveUserModel(uid, um, new UserHelper.DatabaseSaveCallback() {
                                    @Override
                                    public void onSuccess() {
                                        // Success -> redirect
                                        UserHelper.redirectToRole(TSTAR_Login_With_Mobile_Part_Two.this, role);
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this,
                                                "Failed to save profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Linking failed. This can happen if phone number already associated with other account.
                                if (e instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(this, "Phone already linked to another account. Contact support.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(this, "Failed to link phone: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    // If email already exists, try a recovery strategy:
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        // Email already exists: sign in with email and then link phone to that account
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(signInResult -> {
                                    FirebaseUser user = signInResult.getUser();
                                    if (user == null) {
                                        Toast.makeText(this, "Cannot sign in to existing email.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    // Now link phone credential to the existing email account
                                    user.linkWithCredential(phoneCredential)
                                            .addOnSuccessListener(linkResult -> {
                                                // Save/overwrite user model data (update mobile/role etc)
                                                String uid = user.getUid();
                                                UserModel um = new UserModel(uid, firstname, lastname, email, mobile, role);
                                                UserHelper.saveUserModel(uid, um, new UserHelper.DatabaseSaveCallback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        UserHelper.redirectToRole(TSTAR_Login_With_Mobile_Part_Two.this, role);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(Exception ex) {
                                                        Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this,
                                                                "Failed to save profile after linking: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            })
                                            .addOnFailureListener(linkEx -> {
                                                Toast.makeText(this, "Failed to link phone to existing email: " + linkEx.getMessage(), Toast.LENGTH_LONG).show();
                                            });
                                })
                                .addOnFailureListener(signInEx -> {
                                    Toast.makeText(this, "Email already exists and sign-in failed: " + signInEx.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Login flow: sign in with phone credential.
    // The phone should already be linked to an existing account (from signup).
    private void handleLoginWithPhoneCredential(PhoneAuthCredential phoneCredential) {
        mAuth.signInWithCredential(phoneCredential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        Toast.makeText(this, "Phone login failed: no user", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // User is signed in with phone credential; redirect based on stored role
                    UserHelper.loadRoleAndRedirect(user.getUid(), TSTAR_Login_With_Mobile_Part_Two.this);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Phone login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}


/*import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class TSTAR_Login_With_Mobile_Part_Two extends AppCompatActivity {

    EditText otpInput;
    AppCompatButton verifyOtp;
    FirebaseAuth mauth;
    String verificationId;
    String phoneNumber;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_two_activity);

        otpInput = findViewById(R.id.et_login_mobile);
        verifyOtp = findViewById(R.id.btn_login_verify_otp);
        mauth = FirebaseAuth.getInstance();

        verificationId = getIntent().getStringExtra("verificationId");
        phoneNumber = getIntent().getStringExtra("phone number");

        userRef = FirebaseDatabase.getInstance().getReference("Users");

        verifyOtp.setOnClickListener(v -> {
            String code = otpInput.getText().toString().trim();
            if (code.isEmpty() || code.length() < 6) {
                otpInput.setError("Enter a valid OTP");
                return;
            }
            verifyCode(code);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        mauth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mauth.getCurrentUser();
                        if (firebaseUser == null) {
                            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = firebaseUser.getUid();

                        // Attach this UID to the existing user record
                        attachUidToUser(phoneNumber, uid);

                    } else {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void attachUidToUser(String phoneNumber, String uid) {

        userRef.orderByChild("mobile").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this,
                                    "No user found with this mobile number. Please sign up.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Get the first matched record
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Map<String, Object> userData = new HashMap<>();

                            if (ds.getValue() instanceof Map) {
                                //noinspection unchecked
                                userData.putAll((Map<String, Object>) ds.getValue());
                            }

                            // Set correct UID
                            userData.put("uid", uid);

                            // Save at Users/<uid>
                            userRef.child(uid).setValue(userData)
                                    .addOnCompleteListener(writeTask -> {
                                        if (writeTask.isSuccessful()) {

                                            // Optionally delete the old record
                                            // ds.getRef().removeValue();

                                            // Redirect based on role
                                            String role = "";

                                            if (userData.containsKey("role")) {
                                                role = String.valueOf(userData.get("role"));
                                            }

                                            if (role.equalsIgnoreCase("Teacher")) {
                                                Intent intent = new Intent(TSTAR_Login_With_Mobile_Part_Two.this,
                                                        THomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(TSTAR_Login_With_Mobile_Part_Two.this,
                                                        S_Mainpage.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this,
                                                    "Login failed. Try again.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            break; // we only need one
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}*/
