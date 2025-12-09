package com.example.t_star;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class TSTAR_Signup_Activity extends AppCompatActivity {

    EditText Et_FirstName , Et_LastName ,Et_Mobile, Et_Email , Et_Password;

    AppCompatButton Btn_Register;

    TextView Tv_Login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tstar_signup_activity);

        Et_FirstName = findViewById(R.id.et_signup_firstname);
        Et_LastName = findViewById(R.id.et_signup_lastname);
        Et_Mobile = findViewById(R.id.et_signup_mobile);
        Et_Email= findViewById(R.id.et_signup_email);
        Et_Password = findViewById(R.id.et_signup_password);
        Btn_Register = findViewById(R.id.btn_signup);
        Tv_Login = findViewById(R.id.tv_login_on_signup_page);


        Btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registeruser1();

            }
        });

        Tv_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TSTAR_Signup_Activity.this, TSTAR_Signup_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registeruser1(){

        String userfirstname = Et_FirstName.getText().toString();
        String userlastname = Et_LastName.getText().toString();
        String userMobile = Et_Mobile.getText().toString();
        String useremail = Et_Email.getText().toString();
        String userpassword = Et_Password.getText().toString();

        // Toast.makeText(TSTAR_Signup_Activity.this, "First Name:"+userfirstname+"
        // Last:"+userlastname+"email:"+useremail+"pass:"+userpassword, Toast.LENGTH_SHORT).show();


        if(TextUtils.isEmpty(userfirstname)){

            Et_FirstName.setError("Please Enter First name");

        } else if (TextUtils.isEmpty(userlastname)) {

            Et_LastName.setError("Please Enter Last name");

        } else if (Et_Mobile.getText().toString().isEmpty() ) {

            Et_Mobile.setError("Please Enter Mobile Number");

        } else if (Et_Mobile.getText().toString().length() != 10) {

            Et_Mobile.setError("Mobile Number must contain 10 digits ");

        } else if (Et_Email.getText().toString().isEmpty()) {

            Et_Mobile.setError("Please Enter Email Address");

        } else if ((!Et_Email.getText().toString().contains("@")) && (!Et_Email.getText().toString().contains("@"))) {

            Et_Email.setError("Invalid Email: Email must contain @ and .com");

        } else if (Et_Password.getText().toString().isEmpty()) {

            Et_Password.setError("Please Enter Password");

        } else if (Et_Password.getText().toString().length() < 8) {

            Et_Password.setError("Password should not be less than 8");
        } else {

            boolean up = false, low = false, dig = false, special = false;
            String pass = Et_Password.getText().toString();

            for (char c:pass.toCharArray()) {

                if (Character.isUpperCase(c)) {
                    up = true;

                } else if (Character.isLowerCase(c)) {
                    low = true;
                } else if (Character.isDigit(c)) {
                    dig = true;
                } else if (!Character.isLetterOrDigit(c)) {
                    special = true;
                } else {

                }
            }

            if (!up) {
                Et_Password.setError("Password must contain atleast one Uppercase letter");
            } else if (!low) {
                Et_Password.setError("Password must contain atleast one Lowercase letter");
            } else if (!dig) {
                Et_Password.setError("Password must contain atleast one Digit");
            } else if (!dig) {
                Et_Password.setError("Password must contain atleast one Digit");
            } else if (!special) {
                Et_Password.setError("Password must contain atleast one Special Symbol");
            } else {

                registeruser2(userfirstname,userlastname,userMobile,useremail,userpassword);

            }
        }
    }

    private void registeruser2(String userfirstname,String userlastname,String userMobile,String useremail,String userpassword){


    }



}