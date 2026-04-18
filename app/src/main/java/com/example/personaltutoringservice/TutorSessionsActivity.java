package com.example.personaltutoringservice;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class TutorSessionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Sessions");
        }
    }
}