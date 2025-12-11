package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.Locale;

/**
 * SIGNUP ACTIVITY
 * ----------------
 * Signup Flow:
 *
 * 1. User enters:
 *      - First Name
 *      - Last Name
 *      - Email
 *      - Password
 *      - Mobile
 *      - Role (Student/Teacher)
 *
 * 2. After validation → we check in Firebase Realtime Database:
 *      If email OR mobile already exist → Stop + toast.
 *
 * 3. If user does NOT exist → we open OTP Activity (Part One)
 *      with flow = "signup".
 */

public class TSTAR_Signup_Activity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etMobile;
    RadioGroup rgRole;
    RadioButton rbStudent, rbTeacher;
    Button btnRegister;
    TextView tvGoToLOginPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_signup_activity);

        // Initialize Views
        etFirstName = findViewById(R.id.et_signup_firstname);
        etLastName = findViewById(R.id.et_signup_lastname);
        etEmail = findViewById(R.id.et_signup_email);
        etPassword = findViewById(R.id.et_signup_password);
        etMobile = findViewById(R.id.et_signup_mobile);
        rgRole = findViewById(R.id.rg_signup_select_role);
        rbStudent = findViewById(R.id.rb_signup_studentrole);
        rbTeacher = findViewById(R.id.rb_signup_teacherrole);
        btnRegister = findViewById(R.id.btn_signup);
        tvGoToLOginPage = findViewById(R.id.tv_login_on_signup_page);

        // Register button click
        btnRegister.setOnClickListener(v -> attemptSignup());

        // Go to Login Page
        tvGoToLOginPage.setOnClickListener(v -> {
            Intent intent = new Intent(TSTAR_Signup_Activity.this, TSTAR_Login_Activity.class);
            startActivity(intent);
        });
    }

    /**
     * Validates input and then checks if user already exists before starting OTP flow.
     */
    private void attemptSignup() {

        // Collect input
        String firstname = etFirstName.getText().toString().trim();
        String lastname = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        String password = etPassword.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();

        // Validate fields
        if (TextUtils.isEmpty(firstname)) {
            etFirstName.setError("Enter first name");
            etFirstName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(lastname)) {
            etLastName.setError("Enter last name");
            etLastName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
            etMobile.setError("Enter 10-digit number");
            etMobile.requestFocus();
            return;
        } else if (TextUtils.isEmpty(email) || !email.contains("@") || !email.contains(".")) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return;
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            etPassword.setError("Min 8 characters");
            etPassword.requestFocus();
            return;
        }

        // Role selection validation
        int selectedId = rgRole.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = rbStudent.isChecked() ? "Student" : "Teacher";

        // ***************************************************************
        // NEW UPDATE:
        // Before opening OTP Activity, first check if email or mobile exists.
        // ***************************************************************
        checkIfUserExists(firstname, lastname, email, password, mobile, role);
    }

    /**
     * NEW METHOD ADDED
     * --------------------------------------------------------------------
     * Checks Realtime Database for existing user with same email or mobile.
     * If user exists → show toast and DO NOT continue to OTP screen.
     * If user does NOT exist → proceed to OTP Activity.
     * --------------------------------------------------------------------
     */
    private void checkIfUserExists(String firstname, String lastname, String email,
                                   String password, String mobile, String role) {

        DatabaseReference usersRef = FirebaseDatabase.getInstance()
                .getReference("Users");  // Your DB path

        usersRef.get().addOnCompleteListener(task -> {

            if (!task.isSuccessful()) {
                Toast.makeText(this, "Error checking user. Try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean exists = false;

            // Loop through existing users
            for (DataSnapshot snap : task.getResult().getChildren()) {

                String dbEmail = snap.child("email").getValue(String.class);
                String dbMobile = snap.child("mobile").getValue(String.class);

                if (email.equalsIgnoreCase(dbEmail) || mobile.equals(dbMobile)) {
                    exists = true;
                    break;
                }
            }

            if (exists) {
                // STOP SIGNUP → user already exists
                Toast.makeText(this,
                        "Account already exists. Use different email or mobile.",
                        Toast.LENGTH_LONG).show();
                return; // STOP HERE
            }

            // If no user exists → continue original flow
            Intent otpIntent = new Intent(TSTAR_Signup_Activity.this,
                    TSTAR_Login_With_Mobile_Part_One.class);

            otpIntent.putExtra("flow", "signup");
            otpIntent.putExtra("firstname", firstname);
            otpIntent.putExtra("lastname", lastname);
            otpIntent.putExtra("email", email);
            otpIntent.putExtra("password", password);
            otpIntent.putExtra("mobile", mobile);
            otpIntent.putExtra("role", role);

            startActivity(otpIntent);
        });
    }
}


//Below is also important code of proper working of Signup... Dont Delete or Uncomment it...

/*import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.example.t_star.TEACHERACTIVITIES.THomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TSTAR_Signup_Activity extends AppCompatActivity {

    EditText Et_FirstName, Et_LastName, Et_Mobile, Et_Email, Et_Password;
    AppCompatButton Btn_Register;
    TextView Tv_Login;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;
    private RadioGroup rgRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_signup_activity);

        Et_FirstName = findViewById(R.id.et_signup_firstname);
        Et_LastName = findViewById(R.id.et_signup_lastname);
        Et_Mobile = findViewById(R.id.et_signup_mobile);
        Et_Email = findViewById(R.id.et_signup_email);
        Et_Password = findViewById(R.id.et_signup_password);
        Btn_Register = findViewById(R.id.btn_signup);
        Tv_Login = findViewById(R.id.tv_login_on_signup_page);
        rgRole = findViewById(R.id.rg_signup_select_role);

        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Btn_Register.setOnClickListener(v -> registeruser1());

        Tv_Login.setOnClickListener(v -> {
            Intent intent = new Intent(TSTAR_Signup_Activity.this, TSTAR_Login_Activity.class);
            // clear this activity from back stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }




    private void registeruser1() {
        String userfirstname = Et_FirstName.getText().toString().trim();
        String userlastname = Et_LastName.getText().toString().trim();
        String usermobile = Et_Mobile.getText().toString().trim();
        String useremail = Et_Email.getText().toString().trim();
        String userpassword = Et_Password.getText().toString();

        // Validate role selection
        int checkedId = rgRole.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Please select role (Student or Teacher)", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rb = findViewById(checkedId);
        String role = rb.getText().toString().trim(); // "Student" or "Teacher"

        // Field validations (kept as you had, but using trimmed strings)
        if (TextUtils.isEmpty(userfirstname)) {
            Et_FirstName.setError("Please Enter First name");
            Et_FirstName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(userlastname)) {
            Et_LastName.setError("Please Enter Last name");
            Et_LastName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(usermobile)) {
            Et_Mobile.setError("Please Enter Mobile Number");
            Et_Mobile.requestFocus();
            return;
        } else if (usermobile.length() != 10) {
            Et_Mobile.setError("Mobile Number must contain 10 digits");
            Et_Mobile.requestFocus();
            return;
        } else if (TextUtils.isEmpty(useremail)) {
            Et_Email.setError("Please Enter Email Address");
            Et_Email.requestFocus();
            return;
        } else if (!useremail.contains("@") || !(useremail.contains(".") || useremail.endsWith(".com"))) {
            Et_Email.setError("Invalid Email: Email must contain @ and domain");
            Et_Email.requestFocus();
            return;
        } else if (TextUtils.isEmpty(userpassword)) {
            Et_Password.setError("Please Enter Password");
            Et_Password.requestFocus();
            return;
        } else if (userpassword.length() < 8) {
            Et_Password.setError("Password should not be less than 8");
            Et_Password.requestFocus();
            return;
        }

        // Password composition checks
        boolean up = false, low = false, dig = false, special = false;
        for (char c : userpassword.toCharArray()) {
            if (Character.isUpperCase(c)) up = true;
            else if (Character.isLowerCase(c)) low = true;
            else if (Character.isDigit(c)) dig = true;
            else if (!Character.isLetterOrDigit(c)) special = true;
        }
        if (!up) {
            Et_Password.setError("Password must contain at least one Uppercase letter");
            Et_Password.requestFocus();
            return;
        } else if (!low) {
            Et_Password.setError("Password must contain at least one Lowercase letter");
            Et_Password.requestFocus();
            return;
        } else if (!dig) {
            Et_Password.setError("Password must contain at least one Digit");
            Et_Password.requestFocus();
            return;
        } else if (!special) {
            Et_Password.setError("Password must contain at least one Special Symbol");
            Et_Password.requestFocus();
            return;
        }

        // All validations passed — proceed to create user
        registeruser2(userfirstname, userlastname, usermobile, useremail, userpassword, role);
    }

    private void registeruser2(String userfirstname, String userlastname, String usermobile, String useremail, String userpassword, String role) {

        mauth.createUserWithEmailAndPassword(useremail, userpassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mauth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            HashMap<String, String> userData = new HashMap<>();
                            userData.put("firstname", userfirstname);
                            userData.put("lastname", userlastname);
                            userData.put("mobile", usermobile);
                            userData.put("email", useremail);
                            userData.put("password", userpassword); // consider not storing raw password in production
                            userData.put("role", role); // save role here
                            userData.put("userId",userId);// save uid

                            databaseReference.child(userId).setValue(userData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            // Redirect to appropriate main page based on role, clearing backstack
                                            Intent intent;
                                            if (role.equalsIgnoreCase("Teacher")) {
                                                // NOTE: Replace T_Mainpage.class below with your actual teacher main activity if different
                                                intent = new Intent(TSTAR_Signup_Activity.this, THomeActivity.class);
                                            } else {
                                                intent = new Intent(TSTAR_Signup_Activity.this, S_Mainpage.class);
                                            }
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(this, "Registration failed (db). Try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // handle user already exists case
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "User already exists with this email.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Registration Failed: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}*/
