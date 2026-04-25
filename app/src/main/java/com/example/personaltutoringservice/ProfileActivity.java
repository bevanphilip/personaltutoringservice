package com.example.personaltutoringservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText etProfileName, etProfileEmail, etProfileRole, etProfilePhone, etProfileLocation;
    TextView tvTutorServiceStatus, tvTutorServiceSubject, tvTutorServicePrice,
            tvTutorServiceAvailability, tvTutorServiceLocation;

    Button btnSaveProfile, btnEditTutorServices, btnMyTutors, btnTutorRequests;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        etProfileName = findViewById(R.id.etProfileName);
        etProfileEmail = findViewById(R.id.etProfileEmail);
        etProfileRole = findViewById(R.id.etProfileRole);
        etProfilePhone = findViewById(R.id.etProfilePhone);
        etProfileLocation = findViewById(R.id.etProfileLocation);

        tvTutorServiceStatus = findViewById(R.id.tvTutorServiceStatus);
        tvTutorServiceSubject = findViewById(R.id.tvTutorServiceSubject);
        tvTutorServicePrice = findViewById(R.id.tvTutorServicePrice);
        tvTutorServiceAvailability = findViewById(R.id.tvTutorServiceAvailability);
        tvTutorServiceLocation = findViewById(R.id.tvTutorServiceLocation);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnEditTutorServices = findViewById(R.id.btnEditTutorServices);
        btnMyTutors = findViewById(R.id.btnMyTutors);
        btnTutorRequests = findViewById(R.id.btnTutorRequests);

        ImageButton homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnSaveProfile.setOnClickListener(v -> saveProfileInfo());

        btnEditTutorServices.setOnClickListener(v ->
                startActivity(new Intent(this, ActivateTutorServicesActivity.class)));

        btnMyTutors.setOnClickListener(v ->
                startActivity(new Intent(this, MyTutorsActivity.class)));

        btnTutorRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyTutoringRequestsActivity.class)));

        loadProfileInfo();
        loadTutorServiceInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTutorServiceInfo();
    }

    private void loadProfileInfo() {
        etProfileName.setText(sharedPreferences.getString("name", "User"));
        etProfileEmail.setText(sharedPreferences.getString("email", "Not connected yet"));
        etProfileRole.setText(sharedPreferences.getString("role", "Student / Tutor"));
        etProfilePhone.setText(sharedPreferences.getString("phone", ""));
        etProfileLocation.setText(sharedPreferences.getString("location", ""));
    }

    private void saveProfileInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", etProfileName.getText().toString().trim());
        editor.putString("email", etProfileEmail.getText().toString().trim());
        editor.putString("role", etProfileRole.getText().toString().trim());
        editor.putString("phone", etProfilePhone.getText().toString().trim());
        editor.putString("location", etProfileLocation.getText().toString().trim());
        editor.apply();

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
    }

    private void loadTutorServiceInfo() {
        db.collection("tutors")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        showNoTutorService();
                        return;
                    }

                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);

                    String subject = doc.getString("Subject");
                    String availability = doc.getString("Availability");
                    String location = doc.getString("Location");
                    Double price = doc.getDouble("Price");

                    tvTutorServiceStatus.setText("Tutor services activated");
                    tvTutorServiceSubject.setText("Subject: " + (subject != null ? subject : "--"));
                    tvTutorServicePrice.setText(price != null ? String.format("Price: $%.0f/hr", price) : "Price: --");
                    tvTutorServiceAvailability.setText("Availability: " + (availability != null ? availability : "--"));
                    tvTutorServiceLocation.setText("Location: " + (location != null ? location : "--"));
                })
                .addOnFailureListener(e -> showNoTutorService());
    }

    private void showNoTutorService() {
        tvTutorServiceStatus.setText("Tutor services not activated yet.");
        tvTutorServiceSubject.setText("Subject: --");
        tvTutorServicePrice.setText("Price: --");
        tvTutorServiceAvailability.setText("Availability: --");
        tvTutorServiceLocation.setText("Location: --");
    }
}