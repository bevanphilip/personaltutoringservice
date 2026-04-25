package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReceiptActivity extends AppCompatActivity {

    TextView tvReceiptStatus, tvReceiptDetails;
    Button btnBackToMyTutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Receipt");
        }

        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvReceiptDetails = findViewById(R.id.tvReceiptDetails);
        btnBackToMyTutors = findViewById(R.id.btnBackToMyTutors);

        String tutorName = getIntent().getStringExtra("tutorName");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String requestId = getIntent().getStringExtra("requestId");

        tvReceiptStatus.setText("✅ Payment Successful");

        tvReceiptDetails.setText(
                "Tutor: " + (tutorName != null ? tutorName : "Tutor") +
                        "\nAmount Paid: $" + String.format("%.0f", price) + "/hr" +
                        "\nPayment Type: Mock Credit Card" +
                        "\nStatus: Paid" +
                        "\nConfirmation ID: " + (requestId != null ? requestId : "N/A")
        );

        btnBackToMyTutors.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, MyTutorsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
