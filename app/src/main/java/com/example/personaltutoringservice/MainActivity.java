package com.example.personaltutoringservice;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword; //user input for these text fields
    Button btnLogin, btnRegister, btnForgot; //current buttons on homepage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

        btnLogin = findViewById(R.id.btnLogin); //login button
        btnRegister = findViewById(R.id.btnRegister); //register button
        btnForgot = findViewById(R.id.btnForgot); //forgot username or password button

        //LOGIN FUNCTIONS
        btnLogin.setOnClickListener(v -> {

            String username = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String errorMessage = "";

            if (username.isEmpty()) {
                errorMessage += "Username is required\n";
            }
            if (pass.isEmpty()) {
                errorMessage += "Password is required\n";
            }
            if (!errorMessage.isEmpty()) {
                showPopup(errorMessage);
            } else {
                loginUser(username, pass);
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

        Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser(String username, String pass) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(username, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        // Not verified yet — sign out and warn user
                        mAuth.signOut();
                        showPopup("Please verify your email before logging in.\nCheck your inbox for a verification link.");
                    }
                })
                .addOnFailureListener(e ->
                        showPopup("Login Failed: " + e.getMessage())
                );
    }

    private void showPopup(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Login Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
