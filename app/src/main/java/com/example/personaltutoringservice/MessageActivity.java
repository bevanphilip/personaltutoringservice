package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MessageActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tvChatTitle;
    RecyclerView recyclerMessages;
    EditText etMessage;
    Button btnSendMessage, btnBack;

    MessageAdapter adapter;
    List<ChatMessage> messageList = new ArrayList<>();

    String tutorName;
    String studentName = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        tutorName = getIntent().getStringExtra("tutorName");
        if (tutorName == null) {
            tutorName = "Tutor";
        }

        tvChatTitle = findViewById(R.id.tvChatTitle);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnBack = findViewById(R.id.btnBack);

        tvChatTitle.setText("Message " + tutorName);

        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList);
        recyclerMessages.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> sendMessage());

        loadMessages();
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> message = new HashMap<>();
        message.put("sender", studentName);
        message.put("receiver", tutorName);
        message.put("tutorName", tutorName);
        message.put("studentName", studentName);
        message.put("message", text);
        message.put("timestamp", System.currentTimeMillis());

        db.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    etMessage.setText("");
                    loadMessages();
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error sending message: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loadMessages() {
        db.collection("messages")
                .whereEqualTo("tutorName", tutorName)
                .whereEqualTo("studentName", studentName)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    messageList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String sender = doc.getString("sender");
                        String receiver = doc.getString("receiver");
                        String messageText = doc.getString("message");
                        Long timestamp = doc.getLong("timestamp");

                        messageList.add(new ChatMessage(
                                sender != null ? sender : "User",
                                receiver != null ? receiver : "Tutor",
                                messageText != null ? messageText : "",
                                timestamp != null ? timestamp : 0L
                        ));
                    }

                    adapter.notifyDataSetChanged();
                    recyclerMessages.scrollToPosition(Math.max(messageList.size() - 1, 0));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading messages: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}