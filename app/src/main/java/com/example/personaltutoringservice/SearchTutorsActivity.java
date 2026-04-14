package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
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
    TutorAdapter adapter;

    List<Tutor> allTutors = new ArrayList<>();    // full list from Firestore
    List<Tutor> filteredTutors = new ArrayList<>(); // filtered list shown to user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tutors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Find a Tutor");
        }

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorAdapter(filteredTutors);
        recyclerView.setAdapter(adapter);

        // Load all tutors from Firestore
        loadTutors();

        // Search bar — filter as user types
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

    private void loadTutors() {
        db.collection("tutors")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    allTutors.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Tutor tutor = new Tutor(
                                doc.getString("Name"),
                                doc.getDouble("Price"),
                                doc.getDouble("Rating"),
                                doc.getString("Subject")
                        );
                        allTutors.add(tutor);
                    }
                    // Show all tutors initially
                    filteredTutors.clear();
                    filteredTutors.addAll(allTutors);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load tutors: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void filterTutors(String query) {
        filteredTutors.clear();
        if (query.isEmpty()) {
            // Show all tutors if search is empty
            filteredTutors.addAll(allTutors);
        } else {
            String lower = query.toLowerCase();
            for (Tutor tutor : allTutors) {
                // Search by name OR subject/skills
                if ((tutor.getName() != null && tutor.getName().toLowerCase().contains(lower)) ||
                        (tutor.getSubject() != null && tutor.getSubject().toLowerCase().contains(lower))) {
                    filteredTutors.add(tutor);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}