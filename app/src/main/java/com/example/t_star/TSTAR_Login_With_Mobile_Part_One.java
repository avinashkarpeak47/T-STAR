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

import java.util.concurrent.TimeUnit;

/**
 * Part One: Collect phone (either from Signup extras or local input).
 * Sends OTP using Firebase PhoneAuth and then opens Part Two with verificationId and original extras.
 *
 * This activity handles both flows:
 * - flow="signup" : extra fields firstname, lastname, email, password, mobile, role
 * - flow="login"  : no extra fields required (or mobile may be typed)
 */
public class TSTAR_Login_With_Mobile_Part_One extends AppCompatActivity {

    EditText etPhone;
    Button btnSendOtp;
    FirebaseAuth mAuth;

    String flow; // "signup" or "login"
    // Signup extras (may be null when flow=login)
    String firstname, lastname, email, password, mobile, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_one_activity);

        etPhone = findViewById(R.id.et_login_mobile);
        btnSendOtp = findViewById(R.id.btn_login_send_otp);
        mAuth = FirebaseAuth.getInstance();

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
            sendVerificationCode("+91" + phone);
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
                    // Auto-retrieval or instant verification — in many cases the credential can be used directly.
                    // We still navigate to Part Two and let Part Two handle the credential.
                    Intent i = new Intent(TSTAR_Login_With_Mobile_Part_One.this, TSTAR_Login_With_Mobile_Part_Two.class);
                    i.putExtra("flow", flow);
                    i.putExtra("credentialFromAuto", true); // indicate auto credential present
                    // pass signup fields if any
                    if ("signup".equals(flow)) {
                        i.putExtra("firstname", firstname);
                        i.putExtra("lastname", lastname);
                        i.putExtra("email", email);
                        i.putExtra("password", password);
                        i.putExtra("mobile", etPhone.getText().toString().trim());
                        i.putExtra("role", role);
                    }
                    // We cannot directly put Credential object via Intent, PartTwo will call verify again or use signInWithCredential approach.
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


/*import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class TSTAR_Login_With_Mobile_Part_One extends AppCompatActivity {

    EditText phoneInput;
    AppCompatButton sendOtpBtn;
    FirebaseAuth mauth;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_one_activity);

        mauth = FirebaseAuth.getInstance();
        phoneInput = findViewById(R.id.et_login_mobile);
        sendOtpBtn = findViewById(R.id.btn_login_send_otp);

        sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = phoneInput.getText().toString().trim();
                if((phone.isEmpty()) || (phone.length()!=10)){
                    phoneInput.setError("Enter valid phone number");
                    return;
                }
                sendVerificationCode("+91"+phone);
            }
        });
    }
    private void sendVerificationCode(String phoneNumber){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mauth).
                setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(TSTAR_Login_With_Mobile_Part_One.this, "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        public void onCodeSent(@NonNull String vId,@NonNull PhoneAuthProvider.ForceResendingToken token){
            verificationId = vId;
            Intent intent = new Intent(TSTAR_Login_With_Mobile_Part_One.this, TSTAR_Login_With_Mobile_Part_Two.class);
            intent.putExtra("verificationId",verificationId);
            intent.putExtra("phone number",phoneInput.getText().toString().trim());
            startActivity(intent);
        }
    };
}*/