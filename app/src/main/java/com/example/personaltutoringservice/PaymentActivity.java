package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tvPaymentSummary;
    EditText etCardName, etCardNumber, etExpiration, etCvv;
    Button btnBack, btnSubmitPayment;

    String requestId, tutorName;
    double price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mock Payment");
        }

        // Get data from intent
        requestId = getIntent().getStringExtra("requestId");
        tutorName = getIntent().getStringExtra("tutorName");
        price = getIntent().getDoubleExtra("price", 0.0);

        // Bind views
        tvPaymentSummary = findViewById(R.id.tvPaymentSummary);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiration = findViewById(R.id.etExpiration);
        etCvv = findViewById(R.id.etCvv);
        btnBack = findViewById(R.id.btnBack);
        btnSubmitPayment = findViewById(R.id.btnSubmitPayment);

        // Set summary text
        tvPaymentSummary.setText(
                String.format("Paying %s: $%.0f/hr",
                        tutorName != null ? tutorName : "Tutor",
                        price)
        );

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Submit payment
        btnSubmitPayment.setOnClickListener(v -> submitMockPayment());
    }

    private void submitMockPayment() {

        String cardName = etCardName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().trim();
        String expiration = etExpiration.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        // Basic validation
        if (cardName.isEmpty() || cardNumber.isEmpty() || expiration.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill out all payment fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNumber.length() < 12) {
            Toast.makeText(this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestId == null || requestId.isEmpty()) {
            Toast.makeText(this, "Payment error: missing request ID", Toast.LENGTH_LONG).show();
            return;
        }

        // 🔥 Update Firebase payment status
        db.collection("tutoringRequests")
                .document(requestId)
                .update(
                        "paymentStatus", "Paid",
                        "paymentTimestamp", System.currentTimeMillis()
                )
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "Mock payment successful!", Toast.LENGTH_SHORT).show();

                    // 🚀 GO TO RECEIPT SCREEN
                    Intent intent = new Intent(PaymentActivity.this, ReceiptActivity.class);
                    intent.putExtra("requestId", requestId);
                    intent.putExtra("tutorName", tutorName);
                    intent.putExtra("price", price);
                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
