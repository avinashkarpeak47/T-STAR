package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TSTAR_Login_Activity extends AppCompatActivity {

    EditText Et_Email , Et_Password;

    AppCompatButton Btn_Login , Btn_Login_With_Mobile , Btn_Guest_Login;

    TextView Tv_Signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_login_activity);

        Et_Email= findViewById(R.id.et_login_email);
        Et_Password = findViewById(R.id.et_login_password);
        Btn_Login = findViewById(R.id.btn_login);
        Btn_Login_With_Mobile = findViewById(R.id.btn_login_with_mobile);
        Btn_Guest_Login = findViewById(R.id.btn_guest_login);
        Tv_Signup = findViewById(R.id.tv_register);


        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String useremail = Et_Email.getText().toString();        //toString() is used to convert view to String
                String userpassword = Et_Password.getText().toString();


               // Toast.makeText(TSTAR_Login_Activity.this, "email:"+useremail+"pass:"+userpassword,
                        //Toast.LENGTH_SHORT).show();
            }
        });


        Tv_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TSTAR_Login_Activity.this, TSTAR_Signup_Activity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}