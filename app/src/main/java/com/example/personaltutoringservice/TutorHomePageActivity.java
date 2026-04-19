package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TutorHomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView welcomeText;
    private TextView tvQuickName;
    private TextView tvQuickSkills;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Home");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        welcomeText = findViewById(R.id.textWelcome);
        tvQuickName = findViewById(R.id.tvQuickName);
        tvQuickSkills = findViewById(R.id.tvQuickSkills);

        TextView profile = findViewById(R.id.linkMyProfile);
        TextView sessions = findViewById(R.id.linkMySessions);
        Button advertiseBtn = findViewById(R.id.buttonAdvertiseServices);
        Button btnLogout = findViewById(R.id.btnLogout);

        welcomeText.setText("Welcome, User");

        loadTutorQuickProfile();

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(TutorHomePageActivity.this, ProfileActivity.class);
            intent.putExtra("userType", "tutor");
            startActivity(intent);
        });

        sessions.setOnClickListener(v ->
                startActivity(new Intent(TutorHomePageActivity.this, TutorSessionsActivity.class))
        );

        advertiseBtn.setOnClickListener(v ->
                startActivity(new Intent(TutorHomePageActivity.this, AdvertiseServicesActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(TutorHomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTutorQuickProfile();
    }

    private void loadTutorQuickProfile() {
        if (currentUser == null) {
            return;
        }

        db.collection("Tutors")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("username");
                        String skills = doc.getString("skills");

                        if (username == null || username.trim().isEmpty()) {
                            username = "Not set";
                        }

                        if (skills == null || skills.trim().isEmpty()) {
                            skills = "Not set";
                        }

                        welcomeText.setText("Welcome, " + username);
                        tvQuickName.setText("Name: " + username);
                        tvQuickSkills.setText("Skills: " + skills);
                    }
                });
    }
}