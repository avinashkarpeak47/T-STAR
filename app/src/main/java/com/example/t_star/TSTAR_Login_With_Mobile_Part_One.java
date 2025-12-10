package com.example.t_star;

import android.content.Intent;
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
}