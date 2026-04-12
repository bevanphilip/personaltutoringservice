package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Home");
        }

        // update the username
        TextView welcomeText = findViewById(R.id.textWelcome);

        String username = "User"; // temporary (later from login)
        welcomeText.setText("Welcome, " + username);

        TextView profile = findViewById(R.id.linkMyProfile);
        TextView featured = findViewById(R.id.linkFeaturedTutors);
        TextView myTutors = findViewById(R.id.linkMyTutors);

        Button findTutor = findViewById(R.id.buttonFindTutor);
        Button becomeTutor = findViewById(R.id.buttonBecomeTutor);

// Profile page
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

// Search tutors page
        featured.setOnClickListener(v -> startActivity(new Intent(this, SearchTutorsActivity.class)));

        findTutor.setOnClickListener(v -> startActivity(new Intent(this, SearchTutorsActivity.class)));

// My Tutors page
        myTutors.setOnClickListener(v -> startActivity(new Intent(this, MyTutorsActivity.class)));

// Become tutor page
        becomeTutor.setOnClickListener(v -> startActivity(new Intent(this, BecomeTutorActivity.class)));

//Logout - go to the main start page
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }
}
