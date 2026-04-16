package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchTutorsActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SearchView searchView;
    RecyclerView recyclerView;
    TextView tvNoResults;
    TutorAdapter adapter;

    List<Tutor> allTutors = new ArrayList<>();
    List<Tutor> filteredTutors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_search_tutors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Tutor Search Page");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoResults = findViewById(R.id.tvNoResults);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TutorAdapter(filteredTutors);
        recyclerView.setAdapter(adapter);

        ImageButton homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        loadTutors();
        setupSearch();
    }

    private void loadTutors() {
        db.collection("tutors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allTutors.clear();
                    filteredTutors.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Tutor tutor = doc.toObject(Tutor.class);
                        if (tutor != null) {
                            allTutors.add(tutor);
                        }
                    }

                    filteredTutors.addAll(allTutors);
                    adapter.notifyDataSetChanged();
                    updateNoResultsMessage();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error loading tutors: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTutors(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTutors(newText);
                return true;
            }
        });
    }

    private void filterTutors(String text) {
        filteredTutors.clear();

        String searchText = text == null ? "" : text.trim().toLowerCase();

        if (searchText.isEmpty()) {
            filteredTutors.addAll(allTutors);
        } else {
            for (Tutor tutor : allTutors) {
                if (tutor.getSubject() != null &&
                        tutor.getSubject().toLowerCase().contains(searchText)) {
                    filteredTutors.add(tutor);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateNoResultsMessage();
    }

    private void updateNoResultsMessage() {
        if (filteredTutors.isEmpty()) {
            tvNoResults.setText("No tutors found");
            tvNoResults.setVisibility(View.VISIBLE);
        } else {
            tvNoResults.setVisibility(View.GONE);
        }
    }
}