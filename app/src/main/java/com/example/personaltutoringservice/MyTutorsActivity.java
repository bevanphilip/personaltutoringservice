package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTutorsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerView;
    TextView tvNoRequests;

    List<TutoringRequest> requestList = new ArrayList<>();
    TutoringRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tutors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Tutors");
        }

        recyclerView = findViewById(R.id.recyclerRequests);
        tvNoRequests = findViewById(R.id.tvNoRequests);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutoringRequestAdapter(requestList, request -> confirmCancelRequest(request));
        recyclerView.setAdapter(adapter);

        // 🔙 BACK BUTTON
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 🏠 HOME BUTTON
        ImageButton btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(MyTutorsActivity.this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        loadRequests();
    }

    private void loadRequests() {
        db.collection("tutoringRequests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    requestList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String id = doc.getId();
                        String tutorName = doc.getString("tutorName");
                        String subject = doc.getString("subject");
                        String availability = doc.getString("availability");
                        String status = doc.getString("status");
                        Double price = doc.getDouble("price");

                        String requestedDate = doc.getString("requestedDate");
                        String requestedTime = doc.getString("requestedTime");
                        String sessionType = doc.getString("sessionType");

                        String paymentStatus = doc.getString("paymentStatus");
                        if (paymentStatus == null) paymentStatus = "Unpaid";

                        requestList.add(new TutoringRequest(
                                id,
                                tutorName != null ? tutorName : "Tutor",
                                subject != null ? subject : "General",
                                availability != null ? availability : "Not provided",
                                status != null ? status : "Pending",
                                price,
                                requestedDate,
                                requestedTime,
                                sessionType,
                                paymentStatus
                        ));
                    }

                    adapter.notifyDataSetChanged();

                    if (requestList.isEmpty()) {
                        tvNoRequests.setVisibility(android.view.View.VISIBLE);
                        recyclerView.setVisibility(android.view.View.GONE);
                    } else {
                        tvNoRequests.setVisibility(android.view.View.GONE);
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                    }
                });
    }

    private void confirmCancelRequest(TutoringRequest request) {
        db.collection("tutoringRequests")
                .document(request.getId())
                .delete()
                .addOnSuccessListener(unused -> loadRequests());
    }
}