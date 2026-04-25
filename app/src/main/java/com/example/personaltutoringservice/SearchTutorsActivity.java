package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchTutorsActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;
    TutorAdapter adapter;
    List<Tutor> allTutors = new ArrayList<>();
    List<Tutor> filteredTutors = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tutors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Find a Tutor");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorAdapter(filteredTutors, tutor -> {
            Intent intent = new Intent(SearchTutorsActivity.this, TutorSearchDetailActivity.class);
            intent.putExtra("tutorId", tutor.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        loadTutors();

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
        db.collection("Tutors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    allTutors.clear();
                    filteredTutors.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String id = doc.getId();
                        String name = doc.getString("username");
                        String subject = doc.getString("skills");
                        String price = doc.getString("price");
                        String location = doc.getString("location");

                        Double ratingValue = doc.getDouble("ratingAverage");
                        double rating = ratingValue != null ? ratingValue : 0.0;

                        List<String> feedback = new ArrayList<>();

                        Tutor tutor = new Tutor(id, name, subject, price, location, rating, feedback);
                        allTutors.add(tutor);
                    }

                    filteredTutors.addAll(allTutors);
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Error: " + e.getMessage()));
    }

    private void filterTutors(String query) {
        filteredTutors.clear();

        if (query == null || query.isEmpty()) {
            filteredTutors.addAll(allTutors);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (Tutor tutor : allTutors) {
                String name = tutor.getName() != null ? tutor.getName().toLowerCase() : "";
                String subject = tutor.getSubject() != null ? tutor.getSubject().toLowerCase() : "";
                String location = tutor.getLocation() != null ? tutor.getLocation().toLowerCase() : "";

                if (name.contains(lowerCaseQuery) ||
                        subject.contains(lowerCaseQuery) ||
                        location.contains(lowerCaseQuery)) {
                    filteredTutors.add(tutor);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}