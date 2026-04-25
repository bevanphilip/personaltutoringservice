package com.example.personaltutoringservice;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SessionDetailActivity extends AppCompatActivity {

    TextView tvDetails, tvTitle;
    LinearLayout chatContainer;
    EditText etMessage;
    Button btnSend, btnFeedback, btnBack;
    ScrollView scrollChat;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    String bookingId, chatId, tutorId, studentId;
    String currentUserId;
    String studentName = "Student";
    String tutorName = "Tutor";

    Timestamp sessionTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        bookingId = getIntent().getStringExtra("bookingId");
        chatId = getIntent().getStringExtra("chatId");

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        currentUserId = mAuth.getCurrentUser().getUid();

        tvDetails = findViewById(R.id.tvSessionDetails);
        tvTitle = findViewById(R.id.tvSessionTitle);
        chatContainer = findViewById(R.id.chatContainer);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnFeedback = findViewById(R.id.btnFeedback);
        btnBack = findViewById(R.id.btnBack);
        scrollChat = findViewById(R.id.scrollChat);

        btnBack.setOnClickListener(v -> finish());

        loadSessionDetails();

        btnSend.setOnClickListener(v -> sendMessage());
        btnFeedback.setOnClickListener(v -> handleFeedback());
    }

    private void loadSessionDetails() {

        if (bookingId == null || chatId == null) {
            Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("Bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this, "Session not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    String date = doc.getString("date");
                    String time = doc.getString("time");
                    String comment = doc.getString("comment");
                    String location = doc.getString("location");
                    String status = doc.getString("status");

                    tutorId = doc.getString("tutorId");
                    studentId = doc.getString("studentId");

                    sessionTimestamp = doc.getTimestamp("sessionTimestamp");

                    tvDetails.setText(
                            "Date: " + safe(date) +
                                    "\nTime: " + safe(time) +
                                    "\nLocation: " + safe(location) +
                                    "\nStatus: " + safe(status) +
                                    "\nComment: " + safe(comment)
                    );

                    loadUserNames();
                    loadMessages();
                    setupFeedbackButton();
                    markChatAsRead();
                });
    }

    private void loadUserNames() {
        if (studentId != null) {
            db.collection("Students")
                    .document(studentId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String name = doc.getString("username");
                        if (name != null && !name.trim().isEmpty()) {
                            studentName = name;
                        }
                    });
        }

        if (tutorId != null) {
            db.collection("Tutors")
                    .document(tutorId)
                    .get()
                    .addOnSuccessListener(doc -> {
                        String name = doc.getString("username");
                        if (name != null && !name.trim().isEmpty()) {
                            tutorName = name;
                        }
                    });
        }
    }

    private void setupFeedbackButton() {
        if (currentUserId.equals(studentId)) {
            btnFeedback.setText("Leave Feedback");
        } else if (currentUserId.equals(tutorId)) {
            btnFeedback.setText("View Received Feedback");
        } else {
            btnFeedback.setText("Feedback");
        }
    }

    private void sendMessage() {

        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String otherUserId = getOtherUserId();

        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", currentUserId);
        msg.put("senderName", getCurrentUserDisplayName());
        msg.put("text", text);
        msg.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .add(msg)
                .addOnSuccessListener(d -> {
                    etMessage.setText("");

                    Map<String, Object> chatUpdate = new HashMap<>();
                    chatUpdate.put("lastMessage", text);
                    chatUpdate.put("lastMessageSenderId", currentUserId);
                    chatUpdate.put("lastMessageTimestamp", FieldValue.serverTimestamp());

                    if (otherUserId != null) {
                        chatUpdate.put("unreadFor", FieldValue.arrayUnion(otherUserId));
                    }

                    db.collection("Chats")
                            .document(chatId)
                            .update(chatUpdate);
                });
    }

    private void loadMessages() {

        db.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return;

                    chatContainer.removeAllViews();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        String text = doc.getString("text");
                        String senderId = doc.getString("senderId");
                        Timestamp timestamp = doc.getTimestamp("timestamp");

                        addMessageBubble(senderId, text, timestamp);
                    }

                    scrollChat.post(() -> scrollChat.fullScroll(ScrollView.FOCUS_DOWN));
                    markChatAsRead();
                });
    }

    private void addMessageBubble(String senderId, String text, Timestamp timestamp) {

        boolean isMe = senderId != null && senderId.equals(currentUserId);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setGravity(isMe ? Gravity.END : Gravity.START);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 6, 0, 6);
        row.setLayoutParams(rowParams);

        TextView nameView = new TextView(this);
        nameView.setText(isMe ? getCurrentUserDisplayName() : getOtherUserDisplayName());
        nameView.setTextSize(12f);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setTextColor(Color.DKGRAY);
        nameView.setGravity(isMe ? Gravity.END : Gravity.START);

        TextView bubble = new TextView(this);
        bubble.setText(safe(text));
        bubble.setTextSize(15f);
        bubble.setTextColor(isMe ? Color.WHITE : Color.BLACK);
        bubble.setPadding(18, 12, 18, 12);

        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(28f);
        background.setColor(isMe ? Color.parseColor("#6A00F4") : Color.parseColor("#E0E0E0"));
        bubble.setBackground(background);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        bubbleParams.setMargins(0, 4, 0, 2);
        bubbleParams.width = getResources().getDisplayMetrics().widthPixels * 3 / 4;
        bubble.setLayoutParams(bubbleParams);

        TextView timeView = new TextView(this);
        timeView.setText(format(timestamp));
        timeView.setTextSize(11f);
        timeView.setTextColor(Color.GRAY);
        timeView.setGravity(isMe ? Gravity.END : Gravity.START);

        row.addView(nameView);
        row.addView(bubble);
        row.addView(timeView);

        chatContainer.addView(row);
    }

    private void handleFeedback() {

        if (currentUserId.equals(tutorId)) {
            Toast.makeText(this, "Received feedback page coming next", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sessionTimestamp == null) {
            Toast.makeText(this, "Session time not set", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(sessionTimestamp.toDate());

        Calendar end = Calendar.getInstance();
        end.setTime(sessionTimestamp.toDate());
        end.add(Calendar.DAY_OF_YEAR, 7);

        if (now.before(start)) {
            Toast.makeText(this, "Feedback not available yet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (now.after(end)) {
            Toast.makeText(this, "Feedback period ended", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, RatingActivity.class);
        i.putExtra("tutorId", tutorId);
        i.putExtra("bookingId", bookingId);
        startActivity(i);
    }

    private void markChatAsRead() {
        db.collection("Chats")
                .document(chatId)
                .update("unreadFor", FieldValue.arrayRemove(currentUserId));
    }

    private String getOtherUserId() {
        if (currentUserId.equals(studentId)) {
            return tutorId;
        } else if (currentUserId.equals(tutorId)) {
            return studentId;
        }

        return null;
    }

    private String getCurrentUserDisplayName() {
        if (currentUserId.equals(studentId)) {
            return studentName;
        } else if (currentUserId.equals(tutorId)) {
            return tutorName;
        }

        return "Me";
    }

    private String getOtherUserDisplayName() {
        if (currentUserId.equals(studentId)) {
            return tutorName;
        } else if (currentUserId.equals(tutorId)) {
            return studentName;
        }

        return "Them";
    }

    private String format(Timestamp ts) {
        if (ts == null) return "";

        return new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
                .format(ts.toDate());
    }

    private String safe(String s) {
        return s == null || s.trim().isEmpty() ? "N/A" : s;
    }
}