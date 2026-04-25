package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BecomeTutorActivity extends AppCompatActivity {

    ImageButton homeBtn;
    Button btnStartTutorSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_become_tutor);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Become a Tutor");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeBtn = findViewById(R.id.btnHome);
        btnStartTutorSetup = findViewById(R.id.btnStartTutorSetup);

        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnStartTutorSetup.setOnClickListener(v ->
                startActivity(new Intent(this, ActivateTutorServicesActivity.class))
        );
    }
}