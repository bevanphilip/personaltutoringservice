package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ViewFlipper featuredTutorFlipper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Home");
        }

        TextView welcomeText = findViewById(R.id.textWelcome);
        String username = "User";
        welcomeText.setText("Welcome, " + username);

        TextView profile = findViewById(R.id.linkMyProfile);
        TextView myTutors = findViewById(R.id.linkMyTutors);

        Button findTutor = findViewById(R.id.buttonFindTutor);
        Button becomeTutor = findViewById(R.id.buttonBecomeTutor);
        Button btnLogout = findViewById(R.id.btnLogout);

        featuredTutorFlipper = findViewById(R.id.featuredTutorFlipper);

        loadTopTutors();

        profile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findTutor.setOnClickListener(v ->
                startActivity(new Intent(this, SearchTutorsActivity.class)));

        myTutors.setOnClickListener(v ->
                startActivity(new Intent(this, MyTutorsActivity.class)));

        becomeTutor.setOnClickListener(v ->
                startActivity(new Intent(this, BecomeTutorActivity.class)));

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

    private void loadTopTutors() {
        db.collection("tutors")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    featuredTutorFlipper.removeAllViews();

                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("Name");
                        String subject = doc.getString("Subject");
                        Double rating = doc.getDouble("Rating");
                        Double price = doc.getDouble("Price");

                        View cardView = inflater.inflate(R.layout.item_featured_tutor, featuredTutorFlipper, false);

                        TextView tvName = cardView.findViewById(R.id.tvFeaturedName);
                        TextView tvSubject = cardView.findViewById(R.id.tvFeaturedSubject);
                        TextView tvRating = cardView.findViewById(R.id.tvFeaturedRating);
                        TextView tvPrice = cardView.findViewById(R.id.tvFeaturedPrice);

                        tvName.setText(name != null ? name : "Tutor");
                        tvSubject.setText("Subject: " + (subject != null ? subject : "General"));
                        tvRating.setText("⭐ " + (rating != null ? String.format("%.1f", rating) : "N/A"));
                        tvPrice.setText(price != null ? String.format("$%.0f/hr", price) : "$--/hr");

                        featuredTutorFlipper.addView(cardView);
                    }

                    if (featuredTutorFlipper.getChildCount() > 1) {
                        featuredTutorFlipper.startFlipping();
                    }
                });
    }
}