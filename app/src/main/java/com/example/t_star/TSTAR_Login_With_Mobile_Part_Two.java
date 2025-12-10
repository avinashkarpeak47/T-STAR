package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.t_star.STUDENTACTIVITIES.S_Mainpage;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class TSTAR_Login_With_Mobile_Part_Two extends AppCompatActivity {

    EditText otpInput;
    AppCompatButton verifyOtp;
    FirebaseAuth mauth;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_with_mobile_part_two_activity);

        otpInput = findViewById(R.id.et_login_mobile);
        verifyOtp = findViewById(R.id.btn_login_verify_otp);
        mauth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId");

        verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = otpInput.getText().toString().trim();
                if(code.isEmpty() || code.length()<6)
                {
                    otpInput.setError("Enter a valid OTP");
                    return;
                }
                verifyCode(code);
            }
        });
    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Login Successfull", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TSTAR_Login_With_Mobile_Part_Two.this, S_Mainpage.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}