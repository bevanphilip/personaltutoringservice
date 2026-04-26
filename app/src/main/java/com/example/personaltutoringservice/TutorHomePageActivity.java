package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TutorHomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView welcomeText;
    private TextView tvQuickName;
    private TextView tvQuickSkills;
    private LinearLayout layoutTutorSessions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Home");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        welcomeText = findViewById(R.id.textWelcome);
        tvQuickName = findViewById(R.id.tvQuickName);
        tvQuickSkills = findViewById(R.id.tvQuickSkills);
        layoutTutorSessions = findViewById(R.id.layoutTutorSessions);

        TextView profile = findViewById(R.id.linkMyProfile);
        TextView sessions = findViewById(R.id.linkSessions);
        Button btnTutorHistory = findViewById(R.id.btnTutorHistory);
        Button btnLogout = findViewById(R.id.btnLogout);

        welcomeText.setText("Welcome, User");

        loadTutorQuickProfile();
        loadTutorBookings();

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(TutorHomePageActivity.this, ProfileActivity.class);
            intent.putExtra("userType", "tutor");
            startActivity(intent);
        });

        sessions.setOnClickListener(v -> {
            Intent intent = new Intent(TutorHomePageActivity.this, TutorSessionsActivity.class);
            startActivity(intent);
        });

        btnTutorHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, SessionHistoryActivity.class);
            intent.putExtra("userType", "tutor");
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(TutorHomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTutorQuickProfile();
        loadTutorBookings();
    }

    private void loadTutorQuickProfile() {
        if (currentUser == null) {
            return;
        }

        db.collection("Tutors")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("username");
                        String skills = doc.getString("skills");

                        if (username == null || username.trim().isEmpty()) {
                            username = "Not set";
                        }

                        if (skills == null || skills.trim().isEmpty()) {
                            skills = "Not set";
                        }

                        welcomeText.setText("Welcome, " + username);
                        tvQuickName.setText("Name: " + username);
                        tvQuickSkills.setText("Skills: " + skills);
                    }
                });
    }

    private void loadTutorBookings() {

        if (currentUser == null) return;

        db.collection("Bookings")
                .whereEqualTo("tutorId", currentUser.getUid())
                .get()
                .addOnSuccessListener(query -> {

                    layoutTutorSessions.removeAllViews();

                    boolean hasVisibleSession = false;
                    int pendingCount = 0;

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String status = doc.getString("status");
                        String date = doc.getString("date");
                        String time = doc.getString("time");
                        String comment = doc.getString("comment");
                        String chatId = doc.getString("chatId");

                        if ("completed".equals(status) || "denied".equals(status)) {
                            continue;
                        }

                        hasVisibleSession = true;

                        if ("pending".equals(status)) {
                            pendingCount++;
                        }

                        TextView tv = new TextView(this);

                        tv.setText("Date: " + safeText(date) + " | Time: " + safeText(time) +
                                "\nStatus: " + safeText(status) +
                                "\nComment: " + safeText(comment));

                        if ("pending".equals(status)) {
                            tv.setText(tv.getText() + "\nTap to approve or deny");

                            tv.setOnClickListener(v -> {
                                Intent intent = new Intent(this, TutorSessionsActivity.class);
                                startActivity(intent);
                            });
                        }

                        if ("approved".equals(status) && chatId != null) {

                            tv.setText(tv.getText() + "\nTap to view session");

                            db.collection("Chats")
                                    .document(chatId)
                                    .get()
                                    .addOnSuccessListener(chatDoc -> {
                                        if (chatDoc.exists()) {
                                            java.util.List<String> unreadFor =
                                                    (java.util.List<String>) chatDoc.get("unreadFor");

                                            if (unreadFor != null && unreadFor.contains(currentUser.getUid())) {
                                                tv.setText(tv.getText() + "\nNEW MESSAGE");
                                            }
                                        }
                                    });

                            tv.setOnClickListener(v -> {
                                Intent intent = new Intent(this, SessionDetailActivity.class);
                                intent.putExtra("chatId", chatId);
                                intent.putExtra("bookingId", doc.getId());
                                startActivity(intent);
                            });
                        }

                        tv.setTextSize(15f);
                        tv.setTextColor(0xFF666666);
                        tv.setPadding(0, 12, 0, 12);

                        layoutTutorSessions.addView(tv);
                    }

                    TextView sessions = findViewById(R.id.linkSessions);
                    sessions.setText("View Requests (" + pendingCount + ")");

                    if (!hasVisibleSession) {
                        TextView tv = new TextView(this);
                        tv.setText("No active sessions or pending requests yet");
                        tv.setTextSize(15f);
                        tv.setTextColor(0xFF666666);
                        layoutTutorSessions.addView(tv);
                    }
                });
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
}