package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TSTAR_Entry_Activity extends AppCompatActivity {

    Button btn_Login , btn_Signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_entry_activity);

        btn_Login = findViewById(R.id.btnLogin);
        btn_Signup = findViewById(R.id.btnSignup);

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TSTAR_Entry_Activity.this,TSTAR_Login_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TSTAR_Entry_Activity.this,TSTAR_Signup_Activity.class);
            }
        });

    }
}