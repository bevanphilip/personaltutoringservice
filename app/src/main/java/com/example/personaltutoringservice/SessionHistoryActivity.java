package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SessionHistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout layoutHistory;

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My History");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        layoutHistory = findViewById(R.id.layoutHistoryContainer);

        userType = getIntent().getStringExtra("userType");

        loadHistory();
    }

    private void loadHistory() {
        if (mAuth.getCurrentUser() == null) return;

        String currentUserId = mAuth.getCurrentUser().getUid();

        String fieldName;
        if ("tutor".equals(userType)) {
            fieldName = "tutorId";
        } else {
            fieldName = "studentId";
        }

        db.collection("Bookings")
                .whereEqualTo(fieldName, currentUserId)
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(query -> {
                    layoutHistory.removeAllViews();

                    if (query.isEmpty()) {
                        TextView tv = new TextView(this);
                        tv.setText("No completed sessions yet");
                        tv.setTextSize(16f);
                        tv.setPadding(12, 12, 12, 12);
                        layoutHistory.addView(tv);
                        return;
                    }

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String bookingId = doc.getId();
                        String date = doc.getString("date");
                        String time = doc.getString("time");
                        String status = doc.getString("status");
                        String chatId = doc.getString("chatId");

                        CardView card = createHistoryCard(bookingId, chatId, date, time, status);
                        layoutHistory.addView(card);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show()
                );
    }

    private CardView createHistoryCard(String bookingId, String chatId, String date, String time, String status) {
        CardView card = new CardView(this);
        card.setRadius(12f);
        card.setCardElevation(6f);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView tv = new TextView(this);
        tv.setText("Date: " + safeText(date) +
                "\nTime: " + safeText(time) +
                "\nStatus: " + safeText(status) +
                "\nTap to view session history");
        tv.setTextSize(16f);

        layout.addView(tv);
        card.addView(layout);

        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, SessionDetailActivity.class);
            intent.putExtra("bookingId", bookingId);
            intent.putExtra("chatId", chatId);
            intent.putExtra("readOnly", true);
            startActivity(intent);
        });

        return card;
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
}