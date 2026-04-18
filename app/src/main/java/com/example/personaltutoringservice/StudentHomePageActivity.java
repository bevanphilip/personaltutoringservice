package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class StudentHomePageActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Home");
        }

        // update the username
        TextView welcomeText = findViewById(R.id.textWelcome);

        String username = "User";
        welcomeText.setText("Welcome, " + username);

        TextView profile = findViewById(R.id.linkMyProfile);
        Button findTutor = findViewById(R.id.buttonFindTutor);

        // Profile page
        profile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        // Search tutors
        findTutor.setOnClickListener(v -> startActivity(new Intent(this, SearchTutorsActivity.class)));

        //Logout - go to the main start page
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(StudentHomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


    }
}
