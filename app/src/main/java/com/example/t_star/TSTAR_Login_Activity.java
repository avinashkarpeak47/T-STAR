package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t_star.REAL_TIME_DATABASE.PUT_ON_DATABASE.HelperClasses.User_Authentication_Helper;
import com.google.firebase.auth.*;

/**
 * Email/password login activity.
 * Also contains link to "Login with mobile (OTP)" and "Signup".
 */
public class TSTAR_Login_Activity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnLoginWithMobile;
    TextView tvSignup, tvForgot;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_activity);

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLoginWithMobile = findViewById(R.id.btn_login_with_mobile);
        tvSignup = findViewById(R.id.tv_register);
        tvForgot = findViewById(R.id.tv_login_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        // If user already signed-in, redirect (Splash handles this too but double-check)
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            User_Authentication_Helper.loadRoleAndRedirect(current.getUid(), this);
            finish();
            return;
        }

       //Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin();
            }
        });

        //Login with Mobile Button
        btnLoginWithMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start OTP Part One with flow=login
                Intent i = new Intent(TSTAR_Login_Activity.this, TSTAR_Login_With_Mobile_Part_One.class);
                i.putExtra("flow", "login");
                startActivity(i);
            }
        });

        //Text View Go to Signup Page
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TSTAR_Login_Activity.this,TSTAR_Signup_Activity.class);
                startActivity(intent);

            }
        });

        //Forgot Password
        tvForgot.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email to reset password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid ->
                    Toast.makeText(this, "Reset email sent", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        });
    }

    private void emailLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email"); etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password"); etPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        // Redirect based on role stored in database
                        User_Authentication_Helper.loadRoleAndRedirect(user.getUid(), TSTAR_Login_Activity.this);
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Account do not exist", Toast.LENGTH_LONG).show();
                });
    }
}

//Below is also important code of proper working of Login... Dont Delete or Uncomment it...
/*import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TSTAR_Login_Activity extends AppCompatActivity {

    EditText Et_Email, Et_Password;
    AppCompatButton Btn_Login, Btn_Login_With_Mobile, Btn_Guest_Login;
    TextView Tv_Signup, Tv_ForgotPassword;
    FirebaseAuth mauth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_activity);

        Et_Email = findViewById(R.id.et_login_email);
        Et_Password = findViewById(R.id.et_login_password);
        Btn_Login = findViewById(R.id.btn_login);
        Btn_Login_With_Mobile = findViewById(R.id.btn_login_with_mobile);
        Btn_Guest_Login = findViewById(R.id.btn_guest_login);
        Tv_Signup = findViewById(R.id.tv_register);
        Tv_ForgotPassword = findViewById(R.id.tv_login_forgot_password);

        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // If user already logged in (app reopened), redirect based on role
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            redirectToRoleMain(currentUser.getUid());
            return; // don't let the rest of onCreate run until redirect finishes
        }

        Btn_Login.setOnClickListener(v -> {
            String useremail = Et_Email.getText().toString().trim();
            String userpassword = Et_Password.getText().toString();

            if (useremail.isEmpty()) {
                Et_Email.setError("Enter email");
                Et_Email.requestFocus();
                return;
            }
            if (userpassword.isEmpty()) {
                Et_Password.setError("Enter password");
                Et_Password.requestFocus();
                return;
            }

            mauth.signInWithEmailAndPassword(useremail, userpassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mauth.getCurrentUser();
                            if (user != null) {
                                Toast.makeText(TSTAR_Login_Activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                // read role and redirect
                                redirectToRoleMain(user.getUid());
                            } else {
                                Toast.makeText(TSTAR_Login_Activity.this, "Login Failed: user is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                           // Toast.makeText(TSTAR_Login_Activity.this, "Login Failed: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "User do not exist", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        Tv_Signup.setOnClickListener(v -> {
            Intent intent = new Intent(TSTAR_Login_Activity.this, TSTAR_Signup_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Tv_ForgotPassword.setOnClickListener(v -> {
            String email = Et_Email.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email in email field to reset password", Toast.LENGTH_SHORT).show();
                return;
            }
            mauth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(TSTAR_Login_Activity.this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TSTAR_Login_Activity.this, "Failed to send reset email: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                }
            });
        });

        // Optional: Guest/Login with Mobile buttons behavior can be added here
        Btn_Login_With_Mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TSTAR_Login_Activity.this, TSTAR_Login_With_Mobile_Part_One.class);
                startActivity(intent);
            }
        });
    }

    private void redirectToRoleMain(String uid) {
        // Read role from Firebase DB and redirect accordingly
        databaseReference.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String role = null;
                if (snapshot != null && snapshot.exists()) {
                    Object val = snapshot.getValue();
                    if (val != null) role = val.toString();
                }
                Intent intent;
                if ("Teacher".equalsIgnoreCase(role)) {
                    // NOTE: Replace below with your actual teacher main activity class if different
                    intent = new Intent(TSTAR_Login_Activity.this, THomeActivity.class);
                } else {
                    // default to student
                    intent = new Intent(TSTAR_Login_Activity.this, S_Mainpage.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // If DB read fails, default to student main page (or handle error)
                Intent intent = new Intent(TSTAR_Login_Activity.this, S_Mainpage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}*/
