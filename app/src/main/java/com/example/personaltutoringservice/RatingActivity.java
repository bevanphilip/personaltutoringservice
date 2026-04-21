package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etComment;
    private Button btnSubmit;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String tutorId;
    private String bookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rate Tutor");
        }

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitRating);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tutorId = getIntent().getStringExtra("tutorId");
        bookingId = getIntent().getStringExtra("bookingId");

        btnSubmit.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {

        float ratingValue = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();
        String studentId = mAuth.getCurrentUser().getUid();

        if (ratingValue == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Save feedback
        Map<String, Object> feedback = new HashMap<>();
        feedback.put("tutorId", tutorId);
        feedback.put("studentId", studentId);
        feedback.put("rating", ratingValue);
        feedback.put("comment", comment);
        feedback.put("timestamp", FieldValue.serverTimestamp());

        db.collection("TutorFeedback")
                .add(feedback)
                .addOnSuccessListener(doc -> {

                    // Update tutor rating
                    updateTutorRating(tutorId, ratingValue);

                    // Mark booking as rated (IMPORTANT)
                    if (bookingId != null) {
                        db.collection("Bookings")
                                .document(bookingId)
                                .update("rated", true);
                    }

                    Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error submitting rating", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateTutorRating(String tutorId, float newRating) {

        DocumentReference ref = db.collection("Tutors").document(tutorId);

        db.runTransaction(transaction -> {

            Double avgObj = transaction.get(ref).getDouble("ratingAverage");
            Long countObj = transaction.get(ref).getLong("ratingCount");

            double avg = avgObj != null ? avgObj : 0;
            long count = countObj != null ? countObj : 0;

            double newAvg = ((avg * count) + newRating) / (count + 1);

            transaction.update(ref, "ratingAverage", newAvg);
            transaction.update(ref, "ratingCount", count + 1);

            return null;
        });
    }
}