package com.example.personaltutoringservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StudentHomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private TextView welcomeText;
    private TextView tvQuickName;
    private TextView tvQuickInterests;
    private LinearLayout layoutMyRequests;

    private ViewFlipper featuredTutorFlipper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Home");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        welcomeText = findViewById(R.id.textWelcome);
        tvQuickName = findViewById(R.id.tvQuickName);
        tvQuickInterests = findViewById(R.id.tvQuickInterests);
        layoutMyRequests = findViewById(R.id.layoutMyRequests);
        featuredTutorFlipper = findViewById(R.id.featuredTutorFlipper);

        TextView profile = findViewById(R.id.linkMyProfile);
        Button findTutor = findViewById(R.id.buttonFindTutor);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnStudentHistory = findViewById(R.id.btnStudentHistory);

        welcomeText.setText("Welcome, User");

        loadStudentQuickProfile();
        loadTopTutors();

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("userType", "student");
            startActivity(intent);
        });

        findTutor.setOnClickListener(v ->
                startActivity(new Intent(this, SearchTutorsActivity.class))
        );

        btnStudentHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, SessionHistoryActivity.class);
            intent.putExtra("userType", "student");
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(StudentHomePageActivity.this, MainActivity.class);
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
        loadStudentQuickProfile();
        loadStudentBookings();
        loadTopTutors();
    }

    private void loadStudentQuickProfile() {
        if (currentUser == null) {
            return;
        }

        db.collection("Students")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String username = doc.getString("username");
                        String interests = doc.getString("interests");

                        if (username == null || username.trim().isEmpty()) {
                            username = "Not set";
                        }

                        if (interests == null || interests.trim().isEmpty()) {
                            interests = "Not set";
                        }

                        welcomeText.setText("Welcome, " + username);
                        tvQuickName.setText("Name: " + username);
                        tvQuickInterests.setText("Interests: " + interests);
                    }
                });
    }

    private void loadTopTutors() {
        db.collection("Tutors")
                .orderBy("ratingAverage", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    featuredTutorFlipper.removeAllViews();

                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String username = doc.getString("username");
                        String skills = doc.getString("skills");
                        String location = doc.getString("location");

                        Double ratingAverage = doc.getDouble("ratingAverage");
                        Long ratingCount = doc.getLong("ratingCount");

                        Object priceObject = doc.get("price");
                        String price = priceObject != null ? priceObject.toString() : null;

                        View cardView = inflater.inflate(
                                R.layout.item_featured_tutor,
                                featuredTutorFlipper,
                                false
                        );

                        TextView tvName = cardView.findViewById(R.id.tvFeaturedName);
                        TextView tvSubject = cardView.findViewById(R.id.tvFeaturedSubject);
                        TextView tvRating = cardView.findViewById(R.id.tvFeaturedRating);
                        TextView tvPrice = cardView.findViewById(R.id.tvFeaturedPrice);

                        tvName.setText(safeText(username));
                        tvSubject.setText("Skills: " + safeText(skills));

                        if (ratingAverage != null && ratingCount != null && ratingCount > 0) {
                            tvRating.setText("⭐ " + String.format("%.1f", ratingAverage)
                                    + " (" + ratingCount + " ratings)");
                        } else {
                            tvRating.setText("⭐ No rating yet");
                        }

                        if (price != null && !price.trim().isEmpty()) {
                            tvPrice.setText("$" + price + "/hr");
                        } else {
                            tvPrice.setText("$--/hr");
                        }

                        cardView.setOnClickListener(v -> {
                            Intent intent = new Intent(this, TutorSearchDetailActivity.class);
                            intent.putExtra("tutorId", doc.getId());
                            startActivity(intent);
                        });

                        featuredTutorFlipper.addView(cardView);
                    }

                    if (featuredTutorFlipper.getChildCount() == 0) {
                        TextView tv = new TextView(this);
                        tv.setText("No featured tutors yet");
                        tv.setTextSize(15f);
                        tv.setPadding(16, 16, 16, 16);
                        featuredTutorFlipper.addView(tv);
                    }

                    if (featuredTutorFlipper.getChildCount() > 1) {
                        featuredTutorFlipper.setFlipInterval(3000);
                        featuredTutorFlipper.startFlipping();
                    }
                });
    }

    private void loadStudentBookings() {

        if (currentUser == null) return;

        db.collection("Bookings")
                .whereEqualTo("studentId", currentUser.getUid())
                .get()
                .addOnSuccessListener(query -> {

                    layoutMyRequests.removeAllViews();

                    boolean hasVisibleRequest = false;

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String status = doc.getString("status");
                        String date = doc.getString("date");
                        String time = doc.getString("time");
                        String chatId = doc.getString("chatId");

                        if ("completed".equals(status) || "denied".equals(status)) {
                            continue;
                        }

                        hasVisibleRequest = true;

                        TextView tv = new TextView(this);

                        tv.setText("Date: " + safeText(date) + " | Time: " + safeText(time) +
                                "\nStatus: " + safeText(status));

                        if ("pending".equals(status)) {
                            tv.setText(tv.getText() + "\nWaiting for tutor approval");
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
                        tv.setPadding(0, 12, 0, 12);

                        layoutMyRequests.addView(tv);
                    }

                    if (!hasVisibleRequest) {
                        TextView tv = new TextView(this);
                        tv.setText("No active requests yet");
                        layoutMyRequests.addView(tv);
                    }
                });
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "Not set" : value;
    }
}