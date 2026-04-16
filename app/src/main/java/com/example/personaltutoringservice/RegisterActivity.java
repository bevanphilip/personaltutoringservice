package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText username, password, email, phone, address;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Back button on top corner
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Register");
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        btnDone = findViewById(R.id.btnDone);

        btnDone.setOnClickListener(v -> validateAndRegister());
        setupBackButton();
    }

    private void validateAndRegister() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String addr = address.getText().toString().trim();

        String errorMessage = "";

        // Username check
        if (user.isEmpty()) {
            errorMessage += "Username is required\n";
        }

        // Password validation
        if (!isValidPassword(pass)) {
            errorMessage += "Password must be 8+ chars, include upper, lower, special char\n";
        }

        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            errorMessage += "Invalid email format\n";
        }

        // Phone validation
        if (ph.length() < 10) {
            errorMessage += "Phone number must be at least 10 digits\n";
        }

        // Address validation
        if (addr.isEmpty()) {
            errorMessage += "Address is required\n";
        }

        if (!errorMessage.isEmpty()) {
            showPopup(errorMessage);
        } else {
            saveToFirebase(user, pass, mail, ph, addr);
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;

        boolean hasUpper = false, hasLower = false, hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }

            // Disallowed characters
            if (c == '-' || c == ',' || c == '>' || c == '<') {
                return false;
            }
        }

        return hasUpper && hasLower && hasSpecial;
    }

    private void saveToFirebase(String user, String pass, String mail, String ph, String addr) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", user);
        map.put("email", mail);
        map.put("phone", ph);
        map.put("address", addr);

        db.collection("Users")
                .add(map)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void showPopup(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    // Return to main screen button
    private void setupBackButton() {
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}