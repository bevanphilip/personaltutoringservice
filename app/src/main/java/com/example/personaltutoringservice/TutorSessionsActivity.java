package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TutorSessionsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LinearLayout layoutSessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        layoutSessions = findViewById(R.id.layoutSessionsContainer);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Session Requests");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        loadBookings();
    }

    private void loadBookings() {

        String tutorId = mAuth.getCurrentUser().getUid();

        db.collection("Bookings")
                .whereEqualTo("tutorId", tutorId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutSessions.removeAllViews();

                    if (querySnapshot.isEmpty()) {
                        TextView tv = new TextView(this);
                        tv.setText("No booking requests");
                        layoutSessions.addView(tv);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : querySnapshot) {

                        String bookingId = doc.getId();
                        String date = doc.getString("date");
                        String time = doc.getString("time");
                        String comment = doc.getString("comment");
                        String studentId = doc.getString("studentId");

                        CardView card = createBookingCard(
                                bookingId, date, time, comment, studentId
                        );

                        layoutSessions.addView(card);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading bookings", Toast.LENGTH_SHORT).show()
                );
    }

    private CardView createBookingCard(String bookingId, String date, String time,
                                       String comment, String studentId) {

        CardView card = new CardView(this);
        card.setRadius(12f);
        card.setCardElevation(6f);
        card.setUseCompatPadding(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        TextView tvInfo = new TextView(this);
        tvInfo.setText("Date: " + safeText(date) +
                "\nTime: " + safeText(time) +
                "\nComment: " + safeText(comment));

        Button btnApprove = new Button(this);
        btnApprove.setText("Approve");

        Button btnDeny = new Button(this);
        btnDeny.setText("Deny");

        btnApprove.setOnClickListener(v ->
                updateStatus(bookingId, "approved", studentId)
        );

        btnDeny.setOnClickListener(v ->
                updateStatus(bookingId, "denied", studentId)
        );

        layout.addView(tvInfo);
        layout.addView(btnApprove);
        layout.addView(btnDeny);

        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);

        card.setLayoutParams(params);

        return card;
    }

    private void updateStatus(String bookingId, String status, String studentId) {

        String tutorId = mAuth.getCurrentUser().getUid();

        if (status.equals("approved")) {

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            Timestamp expireAt = new Timestamp(calendar.getTime());

            Map<String, Object> chat = new HashMap<>();
            chat.put("studentId", studentId);
            chat.put("tutorId", tutorId);
            chat.put("bookingId", bookingId);
            chat.put("timestamp", FieldValue.serverTimestamp());
            chat.put("expireAt", expireAt);

            db.collection("Chats")
                    .add(chat)
                    .addOnSuccessListener(chatRef -> {

                        String chatId = chatRef.getId();

                        db.collection("Bookings")
                                .document(bookingId)
                                .update(
                                        "status", "approved",
                                        "chatId", chatId,
                                        "chatExpireAt", expireAt
                                )
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(this, "Booking approved", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(this, SessionDetailActivity.class);
                                    intent.putExtra("chatId", chatId);
                                    intent.putExtra("bookingId", bookingId);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error updating booking", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error creating chat", Toast.LENGTH_SHORT).show()
                    );

        } else {

            db.collection("Bookings")
                    .document(bookingId)
                    .update("status", "denied")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Booking denied", Toast.LENGTH_SHORT).show();
                        loadBookings();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error updating booking", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}