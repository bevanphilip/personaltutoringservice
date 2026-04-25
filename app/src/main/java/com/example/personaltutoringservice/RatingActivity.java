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
    private String studentId;

    private boolean feedbackAlreadySubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rate Tutor");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitRating);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tutorId = getIntent().getStringExtra("tutorId");
        bookingId = getIntent().getStringExtra("bookingId");

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        studentId = mAuth.getCurrentUser().getUid();

        loadExistingFeedback();

        btnSubmit.setOnClickListener(v -> submitRating());
    }

    private void loadExistingFeedback() {

        if (bookingId == null || studentId == null) {
            return;
        }

        String feedbackDocId = bookingId + "_" + studentId;

        db.collection("TutorFeedback")
                .document(feedbackDocId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        feedbackAlreadySubmitted = true;

                        Double ratingDouble = doc.getDouble("rating");
                        String comment = doc.getString("comment");

                        if (ratingDouble != null) {
                            ratingBar.setRating(ratingDouble.floatValue());
                        }

                        if (comment != null) {
                            etComment.setText(comment);
                        }

                        ratingBar.setIsIndicator(true);
                        etComment.setEnabled(false);
                        btnSubmit.setEnabled(false);
                        btnSubmit.setText("Feedback Already Submitted");
                    }
                });
    }

    private void submitRating() {

        if (feedbackAlreadySubmitted) {
            Toast.makeText(this, "Feedback already submitted", Toast.LENGTH_SHORT).show();
            return;
        }

        float ratingValue = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (ratingValue == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tutorId == null || bookingId == null) {
            Toast.makeText(this, "Missing session information", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackDocId = bookingId + "_" + studentId;

        Map<String, Object> feedback = new HashMap<>();
        feedback.put("tutorId", tutorId);
        feedback.put("studentId", studentId);
        feedback.put("bookingId", bookingId);
        feedback.put("rating", ratingValue);
        feedback.put("comment", comment);
        feedback.put("feedback", comment);
        feedback.put("timestamp", FieldValue.serverTimestamp());

        db.collection("TutorFeedback")
                .document(feedbackDocId)
                .set(feedback)
                .addOnSuccessListener(doc -> {

                    db.collection("Tutors")
                            .document(tutorId)
                            .collection("TutorFeedback")
                            .document(feedbackDocId)
                            .set(feedback);

                    updateTutorRating(tutorId, ratingValue);

                    db.collection("Bookings")
                            .document(bookingId)
                            .update("rated", true);

                    feedbackAlreadySubmitted = true;

                    ratingBar.setIsIndicator(true);
                    etComment.setEnabled(false);
                    btnSubmit.setEnabled(false);
                    btnSubmit.setText("Feedback Already Submitted");

                    Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
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
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}