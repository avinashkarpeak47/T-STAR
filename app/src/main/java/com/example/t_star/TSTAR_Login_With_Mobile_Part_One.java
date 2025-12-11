package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.*;

import java.util.concurrent.TimeUnit;

/**
 * Part One: Collect phone (either from Signup extras or local input).
 * Sends OTP using Firebase PhoneAuth and then opens Part Two with verificationId and original extras.
 *
 * Behavior changes:
 * - flow="signup": always send OTP (signup path)
 * - flow="login" : first check if mobile exists in Realtime DB under Users/
 *                  only send OTP if mobile exists; otherwise show Toast instructing signup.
 */
public class TSTAR_Login_With_Mobile_Part_One extends AppCompatActivity {

    EditText etPhone;
    Button btnSendOtp;
    FirebaseAuth mAuth;

    String flow; // "signup" or "login"
    // Signup extras (may be null when flow=login)
    String firstname, lastname, email, password, mobile, role;

    // Realtime DB reference to Users node
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_one_activity);

        etPhone = findViewById(R.id.et_login_mobile);
        btnSendOtp = findViewById(R.id.btn_login_send_otp);
        mAuth = FirebaseAuth.getInstance();

        // Initialize DB reference
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Read extras if present
        Intent in = getIntent();
        flow = in.getStringExtra("flow");
        if ("signup".equals(flow)) {
            firstname = in.getStringExtra("firstname");
            lastname = in.getStringExtra("lastname");
            email = in.getStringExtra("email");
            password = in.getStringExtra("password");
            mobile = in.getStringExtra("mobile");
            role = in.getStringExtra("role");

            // If mobile provided from signup, prefill field
            if (mobile != null) etPhone.setText(mobile);
        }

        btnSendOtp.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.length() != 10) {
                etPhone.setError("Enter 10-digit mobile");
                return;
            }

            // If flow is "signup" -> always send OTP
            if ("signup".equals(flow)) {
                sendVerificationCode("+91" + phone);
                return;
            }

            // If flow is "login" -> verify mobile exists in DB before sending OTP
            checkMobileExistsAndSendOtp(phone);
        });
    }

    /**
     * For login flow: check if Users node contains a record with mobile == phone.
     * If exists -> send OTP; else -> show Toast asking user to signup first.
     */
    private void checkMobileExistsAndSendOtp(String phoneWithoutCountryCode) {
        String phoneToCheck = phoneWithoutCountryCode; // 10-digit number as stored in your DB
        // Query Users where mobile equals phone
        usersRef.orderByChild("mobile").equalTo(phoneToCheck)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Mobile exists — send OTP (with +91 prefix)
                            sendVerificationCode("+91" + phoneToCheck);
                        } else {
                            // Mobile does not exist — inform user to sign up first
                            Toast.makeText(TSTAR_Login_With_Mobile_Part_One.this,
                                    "Account with this mobile does not exist. Please sign up first.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // DB read error — notify and do not send OTP
                        Toast.makeText(TSTAR_Login_With_Mobile_Part_One.this,
                                "Database error: " + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        // Configure PhoneAuthOptions and send OTP
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callbacks for OTP send / auto-retrieval
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull com.google.firebase.auth.PhoneAuthCredential credential) {
                    /*
                     * Auto-retrieval or instant verification — in some cases the credential will be
                     * available without user entering SMS code. We still navigate to Part Two and
                     * let Part Two handle sign-in/linking using the available information.
                     */
                    Intent i = new Intent(TSTAR_Login_With_Mobile_Part_One.this, TSTAR_Login_With_Mobile_Part_Two.class);
                    i.putExtra("flow", flow);
                    i.putExtra("credentialFromAuto", true); // indicate auto credential present

                    // Pass signup fields if any
                    if ("signup".equals(flow)) {
                        i.putExtra("firstname", firstname);
                        i.putExtra("lastname", lastname);
                        i.putExtra("email", email);
                        i.putExtra("password", password);
                        i.putExtra("mobile", etPhone.getText().toString().trim());
                        i.putExtra("role", role);
                    } else {
                        // flow=login: pass phone number to prefill
                        i.putExtra("mobile", etPhone.getText().toString().trim());
                    }

                    startActivity(i);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(TSTAR_Login_With_Mobile_Part_One.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // Code sent — move to Part Two with verificationId and the original extras
                    Intent intent = new Intent(TSTAR_Login_With_Mobile_Part_One.this, TSTAR_Login_With_Mobile_Part_Two.class);
                    intent.putExtra("verificationId", verificationId);
                    intent.putExtra("flow", flow);

                    if ("signup".equals(flow)) {
                        intent.putExtra("firstname", firstname);
                        intent.putExtra("lastname", lastname);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("mobile", etPhone.getText().toString().trim());
                        intent.putExtra("role", role);
                    } else {
                        // flow=login: pass phone number to prefill
                        intent.putExtra("mobile", etPhone.getText().toString().trim());
                    }

                    startActivity(intent);
                }
            };
}
