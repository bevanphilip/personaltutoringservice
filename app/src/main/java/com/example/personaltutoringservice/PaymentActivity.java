package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView tvPaymentSummary;
    private EditText etCardName, etCardNumber, etExpiration, etCvv;
    private Button btnBack, btnSubmitPayment;

    private String bookingId;
    private String tutorId;
    private String tutorName = "Tutor";
    private String price = "0";
    private String paymentStatus = "Unpaid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        bookingId = getIntent().getStringExtra("bookingId");

        tvPaymentSummary = findViewById(R.id.tvPaymentSummary);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        setupCardNumberFormatting();
        etExpiration = findViewById(R.id.etExpiration);
        etCvv = findViewById(R.id.etCvv);
        btnBack = findViewById(R.id.btnBack);
        btnSubmitPayment = findViewById(R.id.btnSubmitPayment);

        btnBack.setOnClickListener(v -> finish());
        btnSubmitPayment.setOnClickListener(v -> submitPayment());

        loadPaymentInfo();
    }

    private void setupCardNumberFormatting() {
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                String digitsOnly = s.toString().replaceAll("\\D", "");

                if (digitsOnly.length() > 16) {
                    digitsOnly = digitsOnly.substring(0, 16);
                }

                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(digitsOnly.charAt(i));
                }

                etCardNumber.setText(formatted.toString());
                etCardNumber.setSelection(etCardNumber.getText().length());

                isFormatting = false;
            }
        });
    }
    private void loadPaymentInfo() {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            Toast.makeText(this, "Missing booking information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("Bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(bookingDoc -> {
                    if (!bookingDoc.exists()) {
                        Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    tutorId = bookingDoc.getString("tutorId");

                    String currentPaymentStatus = bookingDoc.getString("paymentStatus");
                    if (currentPaymentStatus != null && !currentPaymentStatus.trim().isEmpty()) {
                        paymentStatus = currentPaymentStatus;
                    }

                    if ("Paid".equals(paymentStatus)) {
                        Toast.makeText(this, "This session is already paid", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PaymentActivity.this, ReceiptActivity.class);
                        intent.putExtra("bookingId", bookingId);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if (tutorId == null || tutorId.trim().isEmpty()) {
                        updateSummary();
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

                                updateSummary();
                            })
                            .addOnFailureListener(e -> updateSummary());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading payment info", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateSummary() {
        tvPaymentSummary.setText(
                "Payment Summary\n" +
                        "Tutor: " + tutorName + "\n" +
                        "Amount: $" + price + "/hr\n" +
                        "Payment Type: Credit Card"
        );
    }

    private void submitPayment() {
        String cardName = etCardName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().replaceAll("\\D", "");
        String expiration = etExpiration.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (cardName.isEmpty() || cardNumber.isEmpty() || expiration.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please enter a 16-digit card number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNumber.length() != 16) {
            Toast.makeText(this, "Please enter a valid card number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bookingId == null || bookingId.trim().isEmpty()) {
            Toast.makeText(this, "Payment error: missing booking ID", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("Bookings")
                .document(bookingId)
                .update(
                        "paymentStatus", "Paid",
                        "paymentTimestamp", FieldValue.serverTimestamp()
                )
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PaymentActivity.this, ReceiptActivity.class);
                    intent.putExtra("bookingId", bookingId);
                    intent.putExtra("tutorName", tutorName);
                    intent.putExtra("price", price);
                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error processing payment: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}