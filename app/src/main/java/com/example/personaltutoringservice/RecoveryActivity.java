package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RecoveryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        //return to main screen button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // closes this screen and goes back
        });


    }
}
