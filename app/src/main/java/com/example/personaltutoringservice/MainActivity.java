package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword; //user input for these text fields
    Button btnLogin, btnRegister, btnForgot; //current buttons on homepage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnLogin = findViewById(R.id.btnLogin); //login button
        btnRegister = findViewById(R.id.btnRegister); //register button
        btnForgot = findViewById(R.id.btnForgot); //forgot username or password button

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //LOGIN FUNCTIONS
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
                //when button is clicked, get what the user type for username and password and returns to a string (trim removes extra spaces at beginning/end)

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user.isEmailVerified()) {

                                Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(this, MainActivity.class));

                            } else {
                                Toast.makeText(this,"Please verify your email",Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(this,"Login Failed",Toast.LENGTH_LONG).show();
                        }

                    });

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                Intent loginIntent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(loginIntent);
                //if else is used to return errors if user leave fields empty during login attempt, otherwise
                // login should be successful and take us further into the app

            }
        });

        //REGISTER FUNCTIONS
        btnRegister.setOnClickListener(v -> {

//            String email = etEmail.getText().toString();
//            String password = etPassword.getText().toString();
//
//            mAuth.createUserWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(task -> {
//
//                        if(task.isSuccessful()) {
//
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            user.sendEmailVerification();
//
//                            Toast.makeText(this,"Verification Email Sent",Toast.LENGTH_LONG).show();
//                        }
//                        else {
//                            Toast.makeText(this,"Registration Failed",Toast.LENGTH_LONG).show();
//                        }
//
//                    });
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });
                //send user to register page

        //FORGOT USERNAME/PASSWORD FUNCTIONS
        btnForgot.setOnClickListener(v -> {
            Intent recoveryIntent = new Intent(MainActivity.this, RecoveryActivity.class);
            startActivity(recoveryIntent);
                //send user to recovery page
        });
    }
}
