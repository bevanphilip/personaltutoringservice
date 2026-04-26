package com.example.personaltutoringservice;

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

public class ChatActivity extends AppCompatActivity {

    EditText messageInput;
    Button sendBtn, btnBackChat;
    LinearLayout chatContainer;
    ScrollView scrollChat;
    TextView tvChatTitle;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String chatId;
    String currentUserId;
    String studentId;
    String tutorId;
    String studentName = "Student";
    String tutorName = "Tutor";
    Timestamp chatExpireAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        chatId = getIntent().getStringExtra("chatId");

        if (chatId == null || chatId.trim().isEmpty()) {
            Toast.makeText(this, "Chat not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = mAuth.getCurrentUser().getUid();

        messageInput = findViewById(R.id.etMessage);
        sendBtn = findViewById(R.id.btnSend);
        btnBackChat = findViewById(R.id.btnBackChat);
        chatContainer = findViewById(R.id.chatContainer);
        scrollChat = findViewById(R.id.scrollChat);
        tvChatTitle = findViewById(R.id.tvChatTitle);

        btnBackChat.setOnClickListener(v -> finish());

        loadChatInfo();

        sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void loadChatInfo() {
        db.collection("Chats")
                .document(chatId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Chat no longer exists", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    studentId = doc.getString("studentId");
                    tutorId = doc.getString("tutorId");
                    chatExpireAt = doc.getTimestamp("expireAt");

                    markChatAsRead();
                    loadUserNames();
                    loadMessages();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading chat", Toast.LENGTH_SHORT).show()
                );
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
                            updateChatTitle();
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
                            updateChatTitle();
                        }
                    });
        }
    }

    private void updateChatTitle() {
        if (currentUserId.equals(studentId)) {
            tvChatTitle.setText("Chat with " + tutorName);
        } else if (currentUserId.equals(tutorId)) {
            tvChatTitle.setText("Chat with " + studentName);
        } else {
            tvChatTitle.setText("Chat");
        }
    }

    private void markChatAsRead() {
        db.collection("Chats")
                .document(chatId)
                .update("unreadFor", FieldValue.arrayRemove(currentUserId));
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();

        if (text.isEmpty()) return;

        String otherUserId = getOtherUserId();

        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", currentUserId);
        msg.put("senderName", getCurrentUserDisplayName());
        msg.put("text", text);
        msg.put("timestamp", FieldValue.serverTimestamp());

        if (chatExpireAt != null) {
            msg.put("expireAt", chatExpireAt);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 14);
            msg.put("expireAt", new Timestamp(calendar.getTime()));
        }

        db.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .add(msg)
                .addOnSuccessListener(doc -> {
                    messageInput.setText("");

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
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error sending message", Toast.LENGTH_SHORT).show()
                );
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

        boolean isMe = currentUserId.equals(senderId);

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
        bubble.setText(safeText(text));
        bubble.setTextSize(16f);
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
        timeView.setText(formatTimestamp(timestamp));
        timeView.setTextSize(11f);
        timeView.setTextColor(Color.GRAY);
        timeView.setGravity(isMe ? Gravity.END : Gravity.START);

        row.addView(nameView);
        row.addView(bubble);
        row.addView(timeView);

        chatContainer.addView(row);
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

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "" : value;
    }
}