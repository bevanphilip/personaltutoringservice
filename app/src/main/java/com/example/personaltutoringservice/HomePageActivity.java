package com.example.personaltutoringservice;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search submit
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle live search
                return false;
            }
        });
    }
}
