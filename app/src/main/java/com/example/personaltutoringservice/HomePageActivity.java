package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
        profile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

// Search tutors page
        featured.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchTutorsActivity.class));
        });

        findTutor.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchTutorsActivity.class));
        });

// My Tutors page
        myTutors.setOnClickListener(v -> {
            startActivity(new Intent(this, MyTutorsActivity.class));
        });

// Become tutor page
        becomeTutor.setOnClickListener(v -> {
            startActivity(new Intent(this, BecomeTutorActivity.class));
        });



    }
}
