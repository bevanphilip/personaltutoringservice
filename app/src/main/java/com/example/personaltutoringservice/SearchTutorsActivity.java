package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

        // Safe padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        tvNoResults = findViewById(R.id.tvNoResults);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 CLICKABLE TUTOR CARDS
        adapter = new TutorAdapter(filteredTutors, tutor -> {
            Intent intent = new Intent(SearchTutorsActivity.this, TutorDetailActivity.class);

            intent.putExtra("name", tutor.getName());
            intent.putExtra("subject", tutor.getSubject());
            intent.putExtra("rating", tutor.getRating());
            intent.putExtra("price", tutor.getPrice());
            intent.putExtra("availability", tutor.getAvailability());

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // Back button
        Button backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> finish());

        loadTutors();
        setupSearch();
    }

    // 🔥 LOAD FROM FIREBASE
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

    // 🔍 SEARCH
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

    // 🔍 FILTER
    private void filterTutors(String text) {
        filteredTutors.clear();

        String searchText = text == null ? "" : text.trim().toLowerCase();

        if (searchText.isEmpty()) {
            filteredTutors.addAll(allTutors);
        } else {
            for (Tutor tutor : allTutors) {
                String subject = tutor.getSubject() != null ? tutor.getSubject().toLowerCase() : "";
                String name = tutor.getName() != null ? tutor.getName().toLowerCase() : "";

                if (subject.contains(searchText) || name.contains(searchText)) {
                    filteredTutors.add(tutor);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateNoResultsMessage();
    }

    // 📭 EMPTY STATE
    private void updateNoResultsMessage() {
        if (filteredTutors.isEmpty()) {
            tvNoResults.setText("No tutors found");
            tvNoResults.setVisibility(TextView.VISIBLE);
        } else {
            tvNoResults.setVisibility(TextView.GONE);
        }
    }
}