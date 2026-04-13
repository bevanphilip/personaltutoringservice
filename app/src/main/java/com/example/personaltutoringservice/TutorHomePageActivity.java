package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TutorHomePageActivity extends AppCompatActivity {

    Button buttonActivateTutorServices, buttonTutoringRequests, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Home");
        }

        TextView profile = findViewById(R.id.linkMyProfile);
        TextView studentRequests = findViewById(R.id.linkStudentRequests);

        buttonActivateTutorServices = findViewById(R.id.buttonActivateTutorServices);
        buttonTutoringRequests = findViewById(R.id.buttonTutoringRequests);
        btnLogout = findViewById(R.id.btnLogout);

        // My Profile page
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // Student Requests page
        studentRequests.setOnClickListener(v -> startActivity(new Intent(this, MyTutoringRequestsActivity.class)));

        // Activate Tutor Services page
        buttonActivateTutorServices.setOnClickListener(v -> startActivity(new Intent(this, ActivateTutorServicesActivity.class)));

        // Tutoring Requests page
        buttonTutoringRequests.setOnClickListener(v -> startActivity(new Intent(this, MyTutoringRequestsActivity.class)));

        // Logout
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(TutorHomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}