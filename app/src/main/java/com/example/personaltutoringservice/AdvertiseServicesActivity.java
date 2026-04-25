package com.example.personaltutoringservice;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AdvertiseServicesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertise_services);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Advertise My Services");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}