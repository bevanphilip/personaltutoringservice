package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
public class ChatActivity extends AppCompatActivity {

    EditText messageInput;
    Button sendBtn;
    LinearLayout chatContainer;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatId = getIntent().getStringExtra("chatId");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        messageInput = findViewById(R.id.etMessage);
        sendBtn = findViewById(R.id.btnSend);
        chatContainer = findViewById(R.id.chatContainer);

        loadMessages();

        sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String text = messageInput.getText().toString();

        if (text.isEmpty()) return;

        Map<String, Object> msg = new HashMap<>();
        msg.put("senderId", mAuth.getCurrentUser().getUid());
        msg.put("text", text);
        msg.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .add(msg);

        messageInput.setText("");
    }

    private void loadMessages() {
        db.collection("Chats")
                .document(chatId)
                .collection("Messages")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {

                    chatContainer.removeAllViews();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        String text = doc.getString("text");

                        TextView tv = new TextView(this);
                        tv.setText(text);

                        chatContainer.addView(tv);
                    }
                });
    }
}