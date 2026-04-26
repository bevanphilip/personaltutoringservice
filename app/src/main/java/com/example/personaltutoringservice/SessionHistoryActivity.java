package com.example.personaltutoringservice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        layoutHistory = findViewById(R.id.layoutHistoryContainer);

        userType = getIntent().getStringExtra("userType");

        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        tv.setTextColor(Color.parseColor("#666666"));
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
                        String paymentStatus = doc.getString("paymentStatus");

                        CardView card = createHistoryCard(
                                bookingId,
                                chatId,
                                date,
                                time,
                                status,
                                paymentStatus
                        );

                        layoutHistory.addView(card);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show()
                );
    }

    private CardView createHistoryCard(String bookingId, String chatId, String date,
                                       String time, String status, String paymentStatus) {

        CardView card = new CardView(this);
        card.setRadius(12f);
        card.setCardElevation(6f);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        boolean isPaid = paymentStatus != null && paymentStatus.trim().equalsIgnoreCase("Paid");

        TextView tv = new TextView(this);
        tv.setText("Date: " + safeText(date) +
                "\nTime: " + safeText(time) +
                "\nStatus: " + capitalize(status) +
                "\nPayment Status: " + capitalize(paymentStatus) +
                "\nTap card to view archived session");
        tv.setTextSize(16f);
        tv.setTextColor(Color.parseColor("#666666"));

        layout.addView(tv);

        if (!"tutor".equals(userType)) {
            Button btnPaymentOrReceipt = new Button(this);
            btnPaymentOrReceipt.setTransformationMethod(null);
            btnPaymentOrReceipt.setTextSize(18f);
            btnPaymentOrReceipt.setTextColor(Color.WHITE);

            if (isPaid) {
                btnPaymentOrReceipt.setText("View Receipt");
                btnPaymentOrReceipt.setBackgroundColor(Color.parseColor("#388E3C"));
            } else {
                btnPaymentOrReceipt.setText("Pay Now");
                btnPaymentOrReceipt.setBackgroundColor(Color.parseColor("#6A00F4"));
            }

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams.setMargins(0, 18, 0, 0);
            btnPaymentOrReceipt.setLayoutParams(btnParams);

            btnPaymentOrReceipt.setOnClickListener(v -> {
                Intent intent;

                if (isPaid) {
                    intent = new Intent(this, ReceiptActivity.class);
                } else {
                    intent = new Intent(this, PaymentActivity.class);
                }

                intent.putExtra("bookingId", bookingId);
                startActivity(intent);
            });

            layout.addView(btnPaymentOrReceipt);
        }

        card.addView(layout);

        card.setOnClickListener(v -> {
            if (chatId == null || chatId.trim().isEmpty()) {
                Toast.makeText(this, "No chat found for this session", Toast.LENGTH_SHORT).show();
                return;
            }

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

    private String capitalize(String value) {
        if (value == null || value.trim().isEmpty()) return "Not set";

        value = value.trim();

        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}