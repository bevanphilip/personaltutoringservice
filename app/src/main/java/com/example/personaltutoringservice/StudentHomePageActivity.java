package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

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
        TextView profileNameText = findViewById(R.id.textProfileName);
        TextView interestsText = findViewById(R.id.textInterests);
        TextView tutor1Name = findViewById(R.id.textTutor1Name);
        TextView tutor1Rating = findViewById(R.id.textTutor1Rating);
        TextView tutor2Name = findViewById(R.id.textTutor2Name);
        TextView tutor2Rating = findViewById(R.id.textTutor2Rating);

        String name = getIntent().getStringExtra("username");
        if (name != null) {
            welcomeText.setText("Welcome, " + name.toUpperCase());
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            db.collection("Students").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists()) {

                            String profileName = documentSnapshot.getString("username");
                            String interests = documentSnapshot.getString("interests");

                            if (profileName != null) {
                                profileNameText.setText("Name: " + profileName);
                            }

                            if (interests != null) {
                                interestsText.setText("Interests: " + interests.toLowerCase());
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            welcomeText.setText("Error loading profile")
                    );
        }

        db.collection("Tutors")
                .orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(2) // get top 2 tutors
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int index = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String tutorName = doc.getString("name");
                        String subject = doc.getString("subject");
                        Double rating = doc.getDouble("rating");

                        if (tutorName != null && rating != null) {

                            if (index == 0) {
                                tutor1Name.setText(tutorName + " (" + subject + ")");
                                tutor1Rating.setText("Rating: " + String.format("%.1f", rating));
                            } else if (index == 1) {
                                tutor2Name.setText(tutorName + " (" + subject + ")");
                                tutor2Rating.setText("Rating: " + String.format("%.1f", rating));
                            }

                            index++;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    tutor1Name.setText("Error loading tutors");
                });

        Button findTutor = findViewById(R.id.buttonFindTutor);

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
