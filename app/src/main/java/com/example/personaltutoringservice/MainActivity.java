package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister, btnForgot;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnForgot = findViewById(R.id.btnForgot);

        // LOGIN
        btnLogin.setOnClickListener(v -> {
            String username = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("Students")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(studentSnapshot -> {

                        if (!studentSnapshot.isEmpty()) {
                            String email = studentSnapshot.getDocuments().get(0).getString("email");
                            loginWithEmail(email, password, "student");
                        } else {
                            db.collection("Tutors")
                                    .whereEqualTo("username", username)
                                    .get()
                                    .addOnSuccessListener(tutorSnapshot -> {

                                        if (!tutorSnapshot.isEmpty()) {
                                            String email = tutorSnapshot.getDocuments().get(0).getString("email");
                                            loginWithEmail(email, password, "tutor");
                                        } else {
                                            Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
                                        }

                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        // REGISTER
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        // FORGOT USERNAME/PASSWORD
        btnForgot.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RecoveryActivity.class));
        });
    }

    private void loginWithEmail(String email, String password, String userType) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if (userType.equals("tutor")) {
                            intent = new Intent(MainActivity.this, TutorHomePageActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, StudentHomePageActivity.class);
                        }

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(
                                this,
                                "Login Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}