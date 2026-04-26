package com.example.personaltutoringservice;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RecoveryActivity extends AppCompatActivity {

    private EditText etRecoveryEmail;
    private Button btnResetPassword;
    private Button btnBack;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Password Recovery");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etRecoveryEmail = findViewById(R.id.etRecoveryEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnResetPassword.setOnClickListener(v -> checkEmailAndSendReset());

        btnBack.setOnClickListener(v -> finish());
    }

    private void checkEmailAndSendReset() {
        String email = etRecoveryEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        checkStudentsCollection(email);
    }

    private void checkStudentsCollection(String email) {
        db.collection("Students")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(studentSnapshot -> {
                    if (!studentSnapshot.isEmpty()) {
                        sendResetEmail(email);
                    } else {
                        checkTutorsCollection(email);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking student records", Toast.LENGTH_SHORT).show()
                );
    }

    private void checkTutorsCollection(String email) {
        db.collection("Tutors")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(tutorSnapshot -> {
                    if (!tutorSnapshot.isEmpty()) {
                        sendResetEmail(email);
                    } else {
                        Toast.makeText(this, "No account found with that email", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking tutor records", Toast.LENGTH_SHORT).show()
                );
    }

    private void sendResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(
                            this,
                            "Password reset email sent. Check your inbox.",
                            Toast.LENGTH_LONG
                    ).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Error sending reset email: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}