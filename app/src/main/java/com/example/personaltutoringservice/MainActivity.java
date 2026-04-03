package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword; //user input for these text fields
    Button btnLogin, btnRegister, btnForgot; //current buttons on homepage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin); //login button
        btnRegister = findViewById(R.id.btnRegister); //register button
        btnForgot = findViewById(R.id.btnForgot); //forgot username or password button

        //LOGIN FUNCTIONS
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
                //when button is clicked, get what the user type for username and password and returns to a string (trim removes extra spaces at beginning/end)

            if (username.isEmpty() || password.isEmpty()) {
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
