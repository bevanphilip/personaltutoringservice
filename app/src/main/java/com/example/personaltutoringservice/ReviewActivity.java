package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    TextView tvTutorName;
    RatingBar ratingBar;
    EditText etReviewComment;
    Button btnSubmitReview, btnBack;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String tutorName, subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Leave Review");
        }

        tvTutorName = findViewById(R.id.tvReviewTutorName);
        ratingBar = findViewById(R.id.ratingBar);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnBack = findViewById(R.id.btnBack);

        tutorName = getIntent().getStringExtra("tutorName");
        subject = getIntent().getStringExtra("subject");

        tvTutorName.setText("Review for " + (tutorName != null ? tutorName : "Tutor"));

        btnBack.setOnClickListener(v -> finish());

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etReviewComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            Toast.makeText(this, "Please enter a review comment", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> review = new HashMap<>();
        review.put("tutorName", tutorName != null ? tutorName : "Tutor");
        review.put("subject", subject != null ? subject : "General");
        review.put("rating", rating);
        review.put("comment", comment);
        review.put("studentName", "User");
        review.put("timestamp", System.currentTimeMillis());

        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error submitting review: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
