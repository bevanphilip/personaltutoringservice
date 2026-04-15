package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity
{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    EditText username, password, email, phone, address;
    Button btnDone, btnBack;
    RadioGroup roleGroup, location;
    LinearLayout studentFields, tutorFields;
    EditText etInterests, etSkills, etHours, etPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Back button on top corner
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Register");
        }
        //Standard user input fields
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        //Button Definitions
        btnDone = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btnBack);
        // Student vs Tutor fields
        etInterests = findViewById(R.id.etInterests);
        etSkills = findViewById(R.id.etSkills);
        location = findViewById(R.id.location);
        etHours = findViewById(R.id.etHours);
        etPrice = findViewById(R.id.etPrice);

        roleGroup = findViewById(R.id.roleGroup);
        studentFields = findViewById(R.id.studentFields);
        tutorFields = findViewById(R.id.tutorFields);

        // Initial visibility check - (radio buttons for student vs tutor)
        updateRoleFields(roleGroup.getCheckedRadioButtonId());

        roleGroup.setOnCheckedChangeListener((group, checkedId) -> updateRoleFields(checkedId));

        // Finish registration
        btnDone.setOnClickListener(v -> validateAndRegister());

        // Back button
        btnBack.setOnClickListener(v -> finish());
    }

    private void updateRoleFields(int checkedId) {
        if (checkedId == R.id.rbStudent) {
            studentFields.setVisibility(View.VISIBLE);
            tutorFields.setVisibility(View.GONE);
        } else if (checkedId == R.id.rbTutor) {
            studentFields.setVisibility(View.GONE);
            tutorFields.setVisibility(View.VISIBLE);
        } else {
            studentFields.setVisibility(View.GONE);
            tutorFields.setVisibility(View.GONE);
        }
    }

    private void validateAndRegister() {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String addr = address.getText().toString().trim();

        StringBuilder errorMessage = new StringBuilder();

        if (user.isEmpty()) errorMessage.append("Username is required\n");
        if (!isValidPassword(pass)) errorMessage.append("Password must be 8+ chars, include upper, lower, special char\n");
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) errorMessage.append("Invalid email format\n");
        if (ph.length() < 10) errorMessage.append("Phone number must be at least 10 digits\n");
        if (addr.isEmpty()) errorMessage.append("Address is required\n");

        int checkedRole = roleGroup.getCheckedRadioButtonId();
        if (checkedRole == -1) {
            errorMessage.append("Please select a role\n");
        } else if (checkedRole == R.id.rbStudent) {
            if (etInterests.getText().toString().trim().isEmpty()) {
                errorMessage.append("Subject interests are required\n");
            }
        } else if (checkedRole == R.id.rbTutor) {
            if (etSkills.getText().toString().trim().isEmpty()) errorMessage.append("Tutoring categories are required\n");
            if (location.getCheckedRadioButtonId() == -1) errorMessage.append("Location is required\n");

            String hoursStr = etHours.getText().toString().trim();
            if (hoursStr.isEmpty()) {
                errorMessage.append("Available hours are required\n");
            } else {
                try {
                    int hours = Integer.parseInt(hoursStr);
                    if (hours < 0 || hours > 168) errorMessage.append("Hours must be between 0 and 168\n");
                } catch (NumberFormatException e) {
                    errorMessage.append("Invalid hours format\n");
                }
            }
            if (etPrice.getText().toString().trim().isEmpty()) errorMessage.append("Price per hour is required\n");
        }

        if (errorMessage.length() > 0) {
            showPopup(errorMessage.toString());
        } else {
            performRegistration(user, pass, mail, ph, addr, checkedRole);
        }
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
            if ("-,><".indexOf(c) != -1) return false;
        }
        return hasUpper && hasLower && hasSpecial;
    }

    private void performRegistration(String user, String pass, String mail, String ph, String addr, int checkedRole) {
        mAuth.createUserWithEmailAndPassword(mail, pass).addOnSuccessListener(authResult ->
                {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null)
                    {
                        firebaseUser.sendEmailVerification().addOnSuccessListener(unused ->
                                Toast.makeText(this, "Verification email sent to " + mail, Toast.LENGTH_LONG).show()
                        );

                        saveUserDataToFirestore(firebaseUser.getUid(), user, mail, ph, addr, checkedRole);
                    }
                })
                .addOnFailureListener(e -> showPopup("Authentication Error: " + e.getMessage()));
    }

    private void saveUserDataToFirestore(String uid, String user, String mail, String ph, String addr, int checkedRole) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", user.toLowerCase());
        userData.put("email", mail);
        userData.put("phone", ph);
        userData.put("address", addr);

        String collectionName;
        if (checkedRole == R.id.rbStudent) {
            userData.put("role", "Student");
            userData.put("interests", etInterests.getText().toString().trim());
            collectionName = "Students"; // or keep as "Users" and use role field
        } else {
            userData.put("role", "Tutor");
            userData.put("skills", etSkills.getText().toString().trim());
            int selectedLocId = location.getCheckedRadioButtonId();
            userData.put("location", (selectedLocId == R.id.on_campus) ? "On-Campus" : "Online");
            userData.put("hours", etHours.getText().toString().trim());
            userData.put("price", etPrice.getText().toString().trim());
            collectionName = "Tutors"; // or keep as "Users" and use role field
        }

        // Option 1: Separate collections (as per "seperate the 2")
        // Option 2: Single "Users" collection with role field (often easier for auth)
        // I will use separate collections as requested, but also a central "Users" record is often helpful.
        // Let's go with separate collections for clarity if that's what was meant.

        db.collection(collectionName).document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    // Redirect to login or home
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Also adding to a central "Users" collection for easier global management
        Map<String, Object> centralUser = new HashMap<>();
        centralUser.put("email", mail);
        centralUser.put("role", userData.get("role"));
        db.collection("Users").document(uid).set(centralUser);
    }

    private void showPopup(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}