package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivateTutorServicesActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etSubject, etPrice, etAvailability, etLocation;
    Button btnSubmitTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_tutor_services);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Activate Tutor Services");
        }

        // Connect UI
        etSubject = findViewById(R.id.etSubject);
        etPrice = findViewById(R.id.etPrice);
        etAvailability = findViewById(R.id.etAvailability);
        etLocation = findViewById(R.id.etLocation);
        btnSubmitTutor = findViewById(R.id.btnSubmitTutor);

        ImageButton homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, TutorHomePageActivity.class));
        });

        // Submit button
        btnSubmitTutor.setOnClickListener(v -> {
            String subject = etSubject.getText().toString().trim();
            String priceText = etPrice.getText().toString().trim();
            String availability = etAvailability.getText().toString().trim();
            String location = etLocation.getText().toString().trim();

            if (subject.isEmpty() || priceText.isEmpty() || availability.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceText);

            // Create tutor object
            Map<String, Object> tutor = new HashMap<>();
            tutor.put("Name", "Tutor"); // later replace with logged-in user
            tutor.put("Subject", subject);
            tutor.put("Price", price);
            tutor.put("Availability", availability);
            tutor.put("Location", location);
            tutor.put("Rating", 5.0); // default

            db.collection("tutors")
                    .add(tutor)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Tutor services activated!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}