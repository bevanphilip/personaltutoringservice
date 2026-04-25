package com.example.personaltutoringservice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TutorHomePageActivity extends AppCompatActivity {

    ImageButton btnBack;
    ImageView imgProfilePicture;
    Button btnUploadPicture, buttonActivateTutorServices, buttonTutoringRequests, btnBackToStudentHome, btnLogout;
    TextView profile, studentRequests;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Home");
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgProfilePicture.setImageURI(uri);
                    }
                }
        );

        btnBack = findViewById(R.id.btnBack);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        btnUploadPicture = findViewById(R.id.btnUploadPicture);

        profile = findViewById(R.id.linkMyProfile);
        studentRequests = findViewById(R.id.linkStudentRequests);

        buttonActivateTutorServices = findViewById(R.id.buttonActivateTutorServices);
        buttonTutoringRequests = findViewById(R.id.buttonTutoringRequests);
        btnBackToStudentHome = findViewById(R.id.btnBackToStudentHome);
        btnLogout = findViewById(R.id.btnLogout);

        btnBack.setOnClickListener(v -> finish());

        btnUploadPicture.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*")
        );

        profile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        studentRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyTutoringRequestsActivity.class)));

        buttonActivateTutorServices.setOnClickListener(v ->
                startActivity(new Intent(this, ActivateTutorServicesActivity.class)));

        buttonTutoringRequests.setOnClickListener(v ->
                startActivity(new Intent(this, MyTutoringRequestsActivity.class)));

        btnBackToStudentHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(TutorHomePageActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}