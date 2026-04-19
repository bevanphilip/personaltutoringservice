package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TutorSearchDetailActivity extends AppCompatActivity {

    TextView tvTutorName, tvTutorSubject, tvTutorPrice, tvTutorLocation, tvTutorRating, tvNoFeedback;
    LinearLayout layoutTutorFeedbackContainer;
    Button btnBookSession;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_search_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTutorName = findViewById(R.id.tvDetailTutorName);
        tvTutorSubject = findViewById(R.id.tvDetailTutorSubject);
        tvTutorPrice = findViewById(R.id.tvDetailTutorPrice);
        tvTutorLocation = findViewById(R.id.tvDetailTutorLocation);
        tvTutorRating = findViewById(R.id.tvDetailTutorRating);
        tvNoFeedback = findViewById(R.id.tvNoFeedback);
        layoutTutorFeedbackContainer = findViewById(R.id.layoutTutorFeedbackContainer);
        btnBookSession = findViewById(R.id.btnBookSession);

        tutorId = getIntent().getStringExtra("tutorId");

        if (tutorId == null || tutorId.isEmpty()) {
            Toast.makeText(this, "Tutor not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTutorDetails();
        loadTutorFeedback();

        btnBookSession.setOnClickListener(v -> {
            Intent intent = new Intent(TutorSearchDetailActivity.this, BookingActivity.class);
            intent.putExtra("tutorId", tutorId);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTutorDetails() {
        db.collection("Tutors")
                .document(tutorId)
                .get()
                .addOnSuccessListener(doc -> showTutor(doc))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading tutor details", Toast.LENGTH_SHORT).show()
                );
    }

    private void showTutor(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Toast.makeText(this, "Tutor details not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = doc.getString("username");
        String subject = doc.getString("skills");
        String price = doc.getString("price");
        String location = doc.getString("location");

        Double ratingAverageValue = doc.getDouble("ratingAverage");
        double ratingAverage = ratingAverageValue != null ? ratingAverageValue : 0.0;

        Long ratingCountValue = doc.getLong("ratingCount");
        long ratingCount = ratingCountValue != null ? ratingCountValue : 0;

        tvTutorName.setText("Tutor Name: " + safeText(name));
        tvTutorSubject.setText("Subject: " + safeText(subject));
        tvTutorPrice.setText("Price: $" + safeText(price) + "/hr");
        tvTutorLocation.setText("Location: " + safeText(location));

        if (ratingCount > 0) {
            tvTutorRating.setText("Rating: " +
                    String.format(Locale.getDefault(), "%.1f", ratingAverage) +
                    " (" + ratingCount + " reviews)");
        } else {
            tvTutorRating.setText("Rating: No ratings yet");
        }
    }

    private void loadTutorFeedback() {
        db.collection("Tutors")
                .document(tutorId)
                .collection("TutorFeedback")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    layoutTutorFeedbackContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {
                        tvNoFeedback.setVisibility(TextView.VISIBLE);
                        layoutTutorFeedbackContainer.addView(tvNoFeedback);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String feedback = doc.getString("feedback");
                        Long ratingLong = doc.getLong("rating");
                        Timestamp timestamp = doc.getTimestamp("timestamp");

                        int rating = ratingLong != null ? ratingLong.intValue() : 0;
                        String dateText = formatTimestamp(timestamp);

                        CardView feedbackCard = createFeedbackCard(dateText, rating, safeText(feedback));
                        layoutTutorFeedbackContainer.addView(feedbackCard);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading feedback", Toast.LENGTH_SHORT).show()
                );
    }

    private CardView createFeedbackCard(String dateText, int rating, String feedbackText) {
        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12f);
        cardView.setCardElevation(6f);
        cardView.setUseCompatPadding(true);

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(24, 24, 24, 24);

        TextView tvDate = new TextView(this);
        tvDate.setText("Date: " + dateText);
        tvDate.setTextSize(14f);
        tvDate.setTextColor(0xFF666666);

        TextView tvRating = new TextView(this);
        tvRating.setText("Rating: " + rating + "/5");
        tvRating.setTextSize(16f);
        tvRating.setTextColor(0xFF666666);
        tvRating.setPadding(0, 12, 0, 12);

        TextView tvFeedback = new TextView(this);
        tvFeedback.setText("Feedback: " + feedbackText);
        tvFeedback.setTextSize(16f);
        tvFeedback.setTextColor(0xFF666666);
        tvFeedback.setGravity(Gravity.START);

        innerLayout.addView(tvDate);
        innerLayout.addView(tvRating);
        innerLayout.addView(tvFeedback);

        cardView.addView(innerLayout);

        return cardView;
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "No date";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
}