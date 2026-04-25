package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTutoringRequestsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    RecyclerView recyclerIncomingRequests;
    TextView tvNoIncomingRequests;
    TutorRequestAdapter adapter;
    List<TutoringRequest> requestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_my_tutoring_requests);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Tutoring Requests");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerIncomingRequests = findViewById(R.id.recyclerIncomingRequests);
        tvNoIncomingRequests = findViewById(R.id.tvNoIncomingRequests);

        recyclerIncomingRequests.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorRequestAdapter(requestList, new TutorRequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(TutoringRequest request) {
                updateRequestStatus(request, "Accepted");
            }

            @Override
            public void onDecline(TutoringRequest request) {
                updateRequestStatus(request, "Declined");
            }
        });

        recyclerIncomingRequests.setAdapter(adapter);

        ImageButton btnBackToProfile = findViewById(R.id.btnBackToProfile);
        btnBackToProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        ImageButton homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, TutorHomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        loadTutoringRequests();
    }

    private void loadTutoringRequests() {
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

                        TutoringRequest request = new TutoringRequest(
                                id,
                                tutorName != null ? tutorName : "Tutor",
                                subject != null ? subject : "General",
                                availability != null ? availability : "Not provided",
                                status != null ? status : "Pending",
                                price,
                                doc.getString("requestedDate"),
                                doc.getString("requestedTime"),
                                doc.getString("sessionType"),
                                doc.getString("paymentStatus") != null ? doc.getString("paymentStatus") : "Unpaid"
                        );

                        requestList.add(request);
                    }

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading requests: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void updateRequestStatus(TutoringRequest request, String status) {
        db.collection("tutoringRequests")
                .document(request.getId())
                .update("status", status)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();
                    loadTutoringRequests();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error updating request: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void updateEmptyState() {
        if (requestList.isEmpty()) {
            tvNoIncomingRequests.setVisibility(View.VISIBLE);
            recyclerIncomingRequests.setVisibility(View.GONE);
        } else {
            tvNoIncomingRequests.setVisibility(View.GONE);
            recyclerIncomingRequests.setVisibility(View.VISIBLE);
        }
    }
}