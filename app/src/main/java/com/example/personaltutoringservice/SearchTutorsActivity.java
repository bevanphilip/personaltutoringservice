package com.example.personaltutoringservice;

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
        }

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TutorAdapter(filteredTutors);
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

                        String name = doc.getString("username");
                        String subject = doc.getString("skills");
                        String price = doc.getString("price");
                        String location = doc.getString("location");

                        Tutor tutor = new Tutor(
                                name,
                                subject,
                                price,
                                location
                        );

                        allTutors.add(tutor);
                    }
                    filteredTutors.addAll(allTutors);
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Error: " + e.getMessage());
                });
    }

    private void filterTutors(String query) {
        filteredTutors.clear();

        if (query == null || query.isEmpty()) {
            filteredTutors.addAll(allTutors);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (Tutor tutor : allTutors) {

                if (tutor.getName().toLowerCase().contains(lowerCaseQuery) ||
                        tutor.getSubject().toLowerCase().contains(lowerCaseQuery)) {

                    filteredTutors.add(tutor);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}