package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    }
    private void loadTutors() {
        db.collection("tutors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    TextView tvTutors = findViewById(R.id.tvTutors);
                    StringBuilder data = new StringBuilder();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String tutorName = doc.getString("Name");
                        Double tutorPrice = doc.getDouble("Price");
                        Double tutorRating = doc.getDouble("Rating");
                        String tutorSubject = doc.getString("Subject");

                        data.append(tutorName)
                                .append(" - $")
                                .append(tutorPrice)
                                .append(" - *")
                                .append(tutorRating)
                                .append(" - ")
                                .append(tutorSubject)
                                .append("\n\n");

                        tvTutors.setText(data.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE", "Error: " + e.getMessage());
                });
    }
}