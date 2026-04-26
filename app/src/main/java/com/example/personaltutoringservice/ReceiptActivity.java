package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView tvReceiptStatus, tvReceiptDetails;
    private Button btnBackToMyHistory;

    private String bookingId;
    private String tutorId;
    private String tutorName = "Tutor";
    private String price = "0";
    private String paymentStatus = "Paid";
    private Timestamp paymentTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Receipt");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvReceiptDetails = findViewById(R.id.tvReceiptDetails);
        btnBackToMyHistory = findViewById(R.id.btnBackToMyTutors);

        bookingId = getIntent().getStringExtra("bookingId");

        String passedTutorName = getIntent().getStringExtra("tutorName");
        String passedPrice = getIntent().getStringExtra("price");

        if (passedTutorName != null && !passedTutorName.trim().isEmpty()) {
            tutorName = passedTutorName;
        }

        if (passedPrice != null && !passedPrice.trim().isEmpty()) {
            price = passedPrice;
        }

        btnBackToMyHistory.setText("Back to My History");

        btnBackToMyHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, SessionHistoryActivity.class);
            intent.putExtra("userType", "student");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        loadReceipt();
    }

    private void loadReceipt() {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            showReceipt();
            return;
        }

        db.collection("Bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(bookingDoc -> {
                    if (!bookingDoc.exists()) {
                        Toast.makeText(this, "Receipt not found", Toast.LENGTH_SHORT).show();
                        showReceipt();
                        return;
                    }

                    tutorId = bookingDoc.getString("tutorId");

                    String status = bookingDoc.getString("paymentStatus");
                    if (status != null && !status.trim().isEmpty()) {
                        paymentStatus = status;
                    }

                    paymentTimestamp = bookingDoc.getTimestamp("paymentTimestamp");

                    if (tutorId == null || tutorId.trim().isEmpty()) {
                        showReceipt();
                        return;
                    }

                    db.collection("Tutors")
                            .document(tutorId)
                            .get()
                            .addOnSuccessListener(tutorDoc -> {
                                String name = tutorDoc.getString("username");
                                String tutorPrice = tutorDoc.getString("price");

                                if (name != null && !name.trim().isEmpty()) {
                                    tutorName = name;
                                }

                                if (tutorPrice != null && !tutorPrice.trim().isEmpty()) {
                                    price = tutorPrice;
                                }

                                showReceipt();
                            })
                            .addOnFailureListener(e -> showReceipt());
                })
                .addOnFailureListener(e -> showReceipt());
    }

    private void showReceipt() {
        tvReceiptStatus.setText("Payment Successful");

        tvReceiptDetails.setText(
                "Tutor: " + safeText(tutorName) +
                        "\nAmount Paid: $" + safeText(price) + "/hr" +
                        "\nPayment Type: Credit Card" +
                        "\nStatus: " + safeText(paymentStatus) +
                        "\nPaid On: " + formatTimestamp(paymentTimestamp) +
                        "\nConfirmation ID: " + safeText(bookingId)
        );
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "N/A";

        return new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
                .format(timestamp.toDate());
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}