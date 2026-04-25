package com.example.personaltutoringservice;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorDetailActivity extends AppCompatActivity {

    TextView tvName, tvSubject, tvRating, tvPrice, tvAvailability, tvNoReviews, tvSessionType, tvSelectedDate;
    Button btnBack, btnRequestSession, btnLeaveReview, btnSelectDate, btnMessageTutor;
    RadioGroup rgSessionType;
    Spinner spinnerTimeSlot;
    RecyclerView recyclerReviews;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String name, subject, availability, sessionType;
    String selectedDate = "";
    double rating, price;

    ReviewAdapter reviewAdapter;
    List<Review> reviewList = new ArrayList<>();

    String[] timeSlots = {
            "Select a time",
            "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM",
            "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM",
            "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Profile");
        }

        tvName = findViewById(R.id.tvDetailName);
        tvSubject = findViewById(R.id.tvDetailSubject);
        tvRating = findViewById(R.id.tvDetailRating);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvAvailability = findViewById(R.id.tvDetailAvailability);
        tvSessionType = findViewById(R.id.tvDetailSessionType);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvNoReviews = findViewById(R.id.tvNoReviews);

        btnBack = findViewById(R.id.btnBack);
        btnRequestSession = findViewById(R.id.btnRequestSession);
        btnLeaveReview = findViewById(R.id.btnLeaveReview);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnMessageTutor = findViewById(R.id.btnMessageTutor);

        rgSessionType = findViewById(R.id.rgSessionType);
        spinnerTimeSlot = findViewById(R.id.spinnerTimeSlot);

        recyclerReviews = findViewById(R.id.recyclerReviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerReviews.setAdapter(reviewAdapter);

        setupTimeSpinner();

        name = getIntent().getStringExtra("name");
        subject = getIntent().getStringExtra("subject");
        availability = getIntent().getStringExtra("availability");
        sessionType = getIntent().getStringExtra("sessionType");

        rating = getIntent().getDoubleExtra("rating", 0.0);
        price = getIntent().getDoubleExtra("price", 0.0);

        tvName.setText(name != null ? name : "Tutor");
        tvSubject.setText("Subject: " + (subject != null ? subject : "General"));
        tvRating.setText("Rating: ⭐ " + String.format("%.1f", rating));
        tvPrice.setText("Price: $" + String.format("%.0f", price) + "/hr");
        tvAvailability.setText("Availability: " + (availability != null ? availability : "Not provided"));
        tvSessionType.setText("Session Type Offered: " + (sessionType != null ? sessionType : "Not specified"));

        btnBack.setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnRequestSession.setOnClickListener(v -> saveTutoringRequest());

        // 🔥 NEW MESSAGE BUTTON
        btnMessageTutor.setOnClickListener(v -> {
            Intent intent = new Intent(TutorDetailActivity.this, MessageActivity.class);
            intent.putExtra("tutorName", name);
            startActivity(intent);
        });

        btnLeaveReview.setVisibility(View.GONE);
        btnLeaveReview.setOnClickListener(v -> {
            Intent intent = new Intent(TutorDetailActivity.this, ReviewActivity.class);
            intent.putExtra("tutorName", name);
            intent.putExtra("subject", subject);
            startActivity(intent);
        });

        loadReviews();
        checkIfReviewAllowed();
    }

    private void setupTimeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                timeSlots
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeSlot.setAdapter(adapter);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    selectedDate = (month + 1) + "/" + day + "/" + year;
                    tvSelectedDate.setText("Selected Date: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveTutoringRequest() {
        String selectedTime = spinnerTimeSlot.getSelectedItem().toString();

        int selectedId = rgSessionType.getCheckedRadioButtonId();
        String selectedSessionType = "";

        if (selectedId == R.id.rbOnline) {
            selectedSessionType = "Online";
        } else if (selectedId == R.id.rbInPerson) {
            selectedSessionType = "In Person";
        }

        if (selectedDate.isEmpty() || selectedTime.equals("Select a time") || selectedSessionType.isEmpty()) {
            Toast.makeText(this, "Please complete scheduling fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("tutorName", name);
        request.put("subject", subject);
        request.put("price", price);
        request.put("rating", rating);
        request.put("availability", availability);
        request.put("status", "Pending");
        request.put("studentName", "User");

        request.put("requestedDate", selectedDate);
        request.put("requestedTime", selectedTime);
        request.put("sessionType", selectedSessionType);

        request.put("timestamp", System.currentTimeMillis());

        db.collection("tutoringRequests")
                .add(request)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Session requested!", Toast.LENGTH_SHORT).show();

                    selectedDate = "";
                    tvSelectedDate.setText("Selected Date: Not selected");
                    spinnerTimeSlot.setSelection(0);
                    rgSessionType.clearCheck();

                    checkIfReviewAllowed();
                });
    }

    private void checkIfReviewAllowed() {
        db.collection("tutoringRequests")
                .whereEqualTo("tutorName", name)
                .whereEqualTo("studentName", "User")
                .whereEqualTo("status", "Accepted")
                .get()
                .addOnSuccessListener(query -> {
                    btnLeaveReview.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }

    private void loadReviews() {
        db.collection("reviews")
                .whereEqualTo("tutorName", name)
                .get()
                .addOnSuccessListener(query -> {

                    reviewList.clear();

                    for (DocumentSnapshot doc : query) {
                        reviewList.add(new Review(
                                doc.getString("comment"),
                                doc.getString("studentName"),
                                doc.getDouble("rating")
                        ));
                    }

                    reviewAdapter.notifyDataSetChanged();

                    tvNoReviews.setVisibility(reviewList.isEmpty() ? View.VISIBLE : View.GONE);
                    recyclerReviews.setVisibility(reviewList.isEmpty() ? View.GONE : View.VISIBLE);
                });
    }
}
