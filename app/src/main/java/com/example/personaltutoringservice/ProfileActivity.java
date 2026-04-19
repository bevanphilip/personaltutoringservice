package com.example.personaltutoringservice;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private String userType = "student";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String collectionName;
    private String documentId;

    private EditText etUsername, etEmail, etPhone, etAddress, etNewItem, etHours, etPrice;
    private TextView tvDynamicLabel, tvTutorExtrasLabel, tvProfileTitle;
    private Button btnAddItem, btnSave;
    private ImageButton btnHome;
    private ChipGroup chipGroupItems;
    private CheckBox cbOnline, cbCampus;

    private final ArrayList<String> itemList = new ArrayList<>();

    private boolean hasUnsavedChanges = false;
    private boolean isLoadingProfile = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (getIntent() != null && getIntent().hasExtra("userType")) {
            String passedUserType = getIntent().getStringExtra("userType");
            if (passedUserType != null && !passedUserType.isEmpty()) {
                userType = passedUserType.toLowerCase();
            }
        }

        collectionName = userType.equals("tutor") ? "Tutors" : "Students";

        setupViews();
        setupHeader();
        setupInsets();
        setupButtons();
        configureFieldsForUserType();
        setupChangeTracking();
        setupBackHandling();
        loadProfile();
    }

    private void setupViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etNewItem = findViewById(R.id.etNewItem);
        etHours = findViewById(R.id.etHours);
        etPrice = findViewById(R.id.etPrice);

        tvDynamicLabel = findViewById(R.id.tvDynamicLabel);
        tvTutorExtrasLabel = findViewById(R.id.tvTutorExtrasLabel);
        tvProfileTitle = findViewById(R.id.tvProfileTitle);

        btnAddItem = findViewById(R.id.btnAddItem);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnHome = findViewById(R.id.btnHome);

        chipGroupItems = findViewById(R.id.chipGroupItems);

        cbOnline = findViewById(R.id.cbOnline);
        cbCampus = findViewById(R.id.cbCampus);
    }

    private void setupHeader() {
        String title = userType.equals("tutor") ? "Tutor Profile" : "My Profile";
        tvProfileTitle.setText(title);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupButtons() {
        btnHome.setOnClickListener(v -> attemptLeavePage());
        btnAddItem.setOnClickListener(v -> addItem());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void setupBackHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                attemptLeavePage();
            }
        });
    }

    private void configureFieldsForUserType() {
        etEmail.setEnabled(false);

        if (userType.equals("tutor")) {
            tvDynamicLabel.setText("Skills");
            etNewItem.setHint("Add a skill (ex: Math)");

            tvTutorExtrasLabel.setVisibility(View.VISIBLE);
            cbOnline.setVisibility(View.VISIBLE);
            cbCampus.setVisibility(View.VISIBLE);
            etHours.setVisibility(View.VISIBLE);
            etPrice.setVisibility(View.VISIBLE);
        } else {
            tvDynamicLabel.setText("Interests");
            etNewItem.setHint("Add an interest (ex: Science)");

            tvTutorExtrasLabel.setVisibility(View.GONE);
            findViewById(R.id.tvAvailabilityLabel).setVisibility(View.GONE);
            cbOnline.setVisibility(View.GONE);
            cbCampus.setVisibility(View.GONE);
            findViewById(R.id.tvHoursLabel).setVisibility(View.GONE);
            etHours.setVisibility(View.GONE);
            findViewById(R.id.tvRateLabel).setVisibility(View.GONE);
            etPrice.setVisibility(View.GONE);
        }
    }

    private void setupChangeTracking() {
        TextWatcher watcher = new SimpleTextWatcher();

        etUsername.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etAddress.addTextChangedListener(watcher);

        if (userType.equals("tutor")) {
            etHours.addTextChangedListener(watcher);
            etPrice.addTextChangedListener(watcher);

            cbOnline.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isLoadingProfile) {
                    hasUnsavedChanges = true;
                }
            });

            cbCampus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isLoadingProfile) {
                    hasUnsavedChanges = true;
                }
            });
        }
    }

    private class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isLoadingProfile) {
                hasUnsavedChanges = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }

    private void loadProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_LONG).show();
            return;
        }

        isLoadingProfile = true;
        documentId = currentUser.getUid();

        db.collection(collectionName)
                .document(documentId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Profile not found in " + collectionName, Toast.LENGTH_LONG).show();
                        isLoadingProfile = false;
                        return;
                    }

                    etUsername.setText(getSafeString(doc.getString("username")));
                    etEmail.setText(getSafeString(doc.getString("email")));
                    etPhone.setText(getSafeString(doc.getString("phone")));
                    etAddress.setText(getSafeString(doc.getString("address")));

                    if (userType.equals("tutor")) {
                        etHours.setText(getSafeString(doc.getString("hours")));
                        etPrice.setText(getSafeString(doc.getString("price")));

                        String location = getSafeString(doc.getString("location")).toLowerCase();
                        cbOnline.setChecked(location.contains("online"));
                        cbCampus.setChecked(location.contains("campus"));

                        loadItemsFromString(doc.getString("skills"));
                    } else {
                        loadItemsFromString(doc.getString("interests"));
                    }

                    hasUnsavedChanges = false;
                    isLoadingProfile = false;
                })
                .addOnFailureListener(e -> {
                    isLoadingProfile = false;
                    Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadItemsFromString(String raw) {
        itemList.clear();

        if (raw != null && !raw.trim().isEmpty()) {
            String[] parts = raw.split(",");
            for (String part : parts) {
                String cleaned = part.trim();
                if (!cleaned.isEmpty() && !containsIgnoreCase(cleaned)) {
                    itemList.add(cleaned);
                }
            }
        }

        refreshChips();
    }

    private void addItem() {
        String newItem = etNewItem.getText().toString().trim();

        if (newItem.isEmpty()) {
            Toast.makeText(this, "Enter a value first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (containsIgnoreCase(newItem)) {
            Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
            return;
        }

        itemList.add(newItem);
        etNewItem.setText("");
        hasUnsavedChanges = true;
        refreshChips();
    }

    private void refreshChips() {
        chipGroupItems.removeAllViews();

        for (String item : new ArrayList<>(itemList)) {
            Chip chip = new Chip(this);
            chip.setText(item);
            chip.setCloseIconVisible(true);
            chip.setClickable(false);
            chip.setCheckable(false);

            chip.setOnCloseIconClickListener(v -> {
                itemList.remove(item);
                hasUnsavedChanges = true;
                refreshChips();
            });

            chipGroupItems.addView(chip);
        }
    }

    private boolean containsIgnoreCase(String value) {
        for (String item : itemList) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private void saveProfile() {
        if (documentId == null) {
            Toast.makeText(this, "Profile document not loaded yet", Toast.LENGTH_LONG).show();
            return;
        }

        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String joinedItems = TextUtils.join(", ", itemList);

        if (userType.equals("tutor")) {
            String hours = etHours.getText().toString().trim();
            String price = etPrice.getText().toString().trim();

            ArrayList<String> availabilityList = new ArrayList<>();
            if (cbOnline.isChecked()) availabilityList.add("Online");
            if (cbCampus.isChecked()) availabilityList.add("On Campus");

            String location = TextUtils.join(", ", availabilityList);

            db.collection(collectionName).document(documentId)
                    .update(
                            "username", username,
                            "phone", phone,
                            "address", address,
                            "skills", joinedItems,
                            "location", location,
                            "hours", hours,
                            "price", price
                    )
                    .addOnSuccessListener(unused -> {
                        hasUnsavedChanges = false;
                        Toast.makeText(this, "Tutor profile updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        } else {
            db.collection(collectionName).document(documentId)
                    .update(
                            "username", username,
                            "phone", phone,
                            "address", address,
                            "interests", joinedItems
                    )
                    .addOnSuccessListener(unused -> {
                        hasUnsavedChanges = false;
                        Toast.makeText(this, "Student profile updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }
    }

    private void attemptLeavePage() {
        if (hasUnsavedChanges) {
            new AlertDialog.Builder(this)
                    .setTitle("Unsaved Changes")
                    .setMessage("You have unsaved changes. Are you sure you want to leave without saving?")
                    .setPositiveButton("Leave", (dialog, which) -> goHome())
                    .setNegativeButton("Stay", null)
                    .show();
        } else {
            goHome();
        }
    }

    private void goHome() {
        Intent intent;
        if (userType.equals("tutor")) {
            intent = new Intent(ProfileActivity.this, TutorHomePageActivity.class);
        } else {
            intent = new Intent(ProfileActivity.this, StudentHomePageActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private String getSafeString(String value) {
        return value == null ? "" : value;
    }
}