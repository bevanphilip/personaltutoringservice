package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SessionFeedbackActivity extends AppCompatActivity {

    private TextView tvFeedbackTitle, tvFeedbackDetails;
    private Button btnBackFeedback;

    private FirebaseFirestore db;

    private String bookingId;
    private String studentId;
    private String tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_feedback);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Session Feedback");
        }

        db = FirebaseFirestore.getInstance();

        tvFeedbackTitle = findViewById(R.id.tvFeedbackTitle);
        tvFeedbackDetails = findViewById(R.id.tvFeedbackDetails);
        btnBackFeedback = findViewById(R.id.btnBackFeedback);

        bookingId = getIntent().getStringExtra("bookingId");
        studentId = getIntent().getStringExtra("studentId");
        tutorId = getIntent().getStringExtra("tutorId");

        btnBackFeedback.setOnClickListener(v -> finish());

        loadFeedback();
    }

    private void loadFeedback() {
        if (bookingId == null || studentId == null || tutorId == null) {
            Toast.makeText(this, "Feedback not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackDocId = bookingId + "_" + studentId;

        db.collection("Tutors")
                .document(tutorId)
                .collection("TutorFeedback")
                .document(feedbackDocId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        tvFeedbackDetails.setText("No feedback has been submitted for this session yet.");
                        return;
                    }

                    Double ratingDouble = doc.getDouble("rating");
                    double rating = ratingDouble != null ? ratingDouble : 0.0;

                    String comment = doc.getString("feedback");
                    if (comment == null || comment.trim().isEmpty()) {
                        comment = doc.getString("comment");
                    }

                    Timestamp timestamp = doc.getTimestamp("timestamp");

                    tvFeedbackDetails.setText(
                            "Rating: " + String.format(Locale.getDefault(), "%.1f", rating) + "/5" +
                                    "\nDate: " + formatTimestamp(timestamp) +
                                    "\n\nFeedback:\n" + safeText(comment)
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading feedback", Toast.LENGTH_SHORT).show()
                );
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "No date";

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "No written feedback" : value;
    }
}