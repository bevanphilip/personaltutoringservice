package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

        EditText etUsername, etPassword;
        Button btnLogin, btnRegister, btnForgot;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                // Connect XML IDs to Java
                etUsername = findViewById(R.id.username);
                etPassword = findViewById(R.id.password);

                btnLogin = findViewById(R.id.btnLogin);
                btnRegister = findViewById(R.id.btnRegister);
                btnForgot = findViewById(R.id.btnForgot);

                // LOGIN BUTTON
                btnLogin.setOnClickListener(v -> {
                        String username = etUsername.getText().toString().trim();
                        String password = etPassword.getText().toString().trim();

                        // Check if fields are empty
                        if (username.isEmpty() || password.isEmpty()) {
                                Toast.makeText(MainActivity.this,
                                        "Please enter username and password",
                                        Toast.LENGTH_SHORT).show();
                        } else {
                                // TEMP LOGIC (for now)
                                if (username.equalsIgnoreCase("Tutor")) {
                                        // Go to tutor home
                                        startActivity(new Intent(MainActivity.this, TutorHomePageActivity.class));
                                } else {
                                        // Go to student home
                                        startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                                }
                        }
                });

                // REGISTER BUTTON
                btnRegister.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, RegisterActivity.class))
                );

                // FORGOT PASSWORD BUTTON
                btnForgot.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, RecoveryActivity.class))
                );
        }
}