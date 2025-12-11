package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.User_Authentication_Helper;
import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.ModelClasses.User_Authentication_Model;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

public class TSTAR_Login_With_Mobile_Part_Two extends AppCompatActivity {

    EditText etOtp;
    Button btnVerify;
    FirebaseAuth mAuth;

    // Data passed from Part One
    String verificationId;
    String flow;
    String firstname, lastname, email, password, mobile, role;

    boolean credentialFromAuto = false; // auto-verification flag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_two_activity);

        etOtp = findViewById(R.id.et_otp); // IMPORTANT: must be OTP EditText
        btnVerify = findViewById(R.id.btn_login_verify_otp);
        mAuth = FirebaseAuth.getInstance();

        Intent in = getIntent();
        flow = in.getStringExtra("flow");
        verificationId = in.getStringExtra("verificationId");
        credentialFromAuto = in.getBooleanExtra("credentialFromAuto", false);
        mobile = in.getStringExtra("mobile");

        if ("signup".equals(flow)) {
            firstname = in.getStringExtra("firstname");
            lastname = in.getStringExtra("lastname");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            role = in.getStringExtra("role");
        }

        btnVerify.setOnClickListener(v -> {
            if (credentialFromAuto) {
                Toast.makeText(this, "Auto verification detected, completing login...", Toast.LENGTH_SHORT).show();
                handleAutoLoginOrSignup();
                return;
            }

            String code = etOtp.getText().toString().trim();
            if (TextUtils.isEmpty(code) || code.length() < 6) {
                etOtp.setError("Enter valid OTP");
                return;
            }

            verifyCode(code);
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential;

        try {
            credential = PhoneAuthProvider.getCredential(verificationId, code);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("signup".equals(flow)) handleSignup(credential);
        else handleLogin(credential);
    }

    //-------------------------------------------------------
    //        AUTO VERIFICATION HANDLING (Part One)
    //-------------------------------------------------------
    private void handleAutoLoginOrSignup() {

        // Auto verification only provides the phone credential on Part One
        // So we reattempt sign-in with the auto credential
        // (Firebase automatically stores the credential behind the scenes)

        // There is a chance verificationId is null in auto mode.
        // So for auto verification we simply call signInWithCredential from part one.
        // But since we cannot pass the credential object via Intent,
        // we force user to enter OTP if auto failed.

        Toast.makeText(this, "Auto verification – please enter OTP if not logged in", Toast.LENGTH_LONG).show();
    }

    //-------------------------------------------------------
    //                  SIGNUP FLOW
    //-------------------------------------------------------
    private void handleSignup(PhoneAuthCredential phoneCred) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {

                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Link phone to the same UID
                    user.linkWithCredential(phoneCred)
                            .addOnSuccessListener(linkRes -> {
                                FirebaseUser finalUser = linkRes.getUser();
                                if (finalUser == null) {
                                    Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String uid = finalUser.getUid();

                                User_Authentication_Model model =
                                        new User_Authentication_Model(uid, firstname, lastname, email, mobile, role);

                                User_Authentication_Helper.saveUserModel(uid, model, new User_Authentication_Helper.DatabaseSaveCallback() {
                                    @Override
                                    public void onSuccess() {
                                        User_Authentication_Helper.redirectToRole(TSTAR_Login_With_Mobile_Part_Two.this, role);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this, "DB Error: " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Phone linking error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );

                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        handleExistingEmailSignup(phoneCred);
                    } else {
                        Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    //-------------------------------------------------------
    //    EXISTING EMAIL → SIGN-IN → THEN LINK PHONE
    //-------------------------------------------------------
    private void handleExistingEmailSignup(PhoneAuthCredential phoneCred) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {

                    FirebaseUser user = result.getUser();
                    if (user == null) return;

                    user.linkWithCredential(phoneCred)
                            .addOnSuccessListener(link -> {

                                String uid = user.getUid();

                                User_Authentication_Model model =
                                        new User_Authentication_Model(uid, firstname, lastname, email, mobile, role);

                                User_Authentication_Helper.saveUserModel(uid, model,
                                        new User_Authentication_Helper.DatabaseSaveCallback() {
                                            @Override
                                            public void onSuccess() {
                                                User_Authentication_Helper.redirectToRole(TSTAR_Login_With_Mobile_Part_Two.this, role);
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                Toast.makeText(TSTAR_Login_With_Mobile_Part_Two.this, "DB Error: " + e, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Phone linking failed", Toast.LENGTH_SHORT).show()
                            );

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Email exists but login failed: " + e, Toast.LENGTH_SHORT).show()
                );
    }

    //-------------------------------------------------------
    //                    LOGIN FLOW
    //-------------------------------------------------------
    private void handleLogin(PhoneAuthCredential phoneCred) {

        mAuth.signInWithCredential(phoneCred)
                .addOnSuccessListener(result -> {

                    FirebaseUser user = result.getUser();
                    if (user == null) return;

                    User_Authentication_Helper.loadRoleAndRedirect(user.getUid(), this);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Phone login failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
