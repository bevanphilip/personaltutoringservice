package com.example.personaltutoringservice;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    EditText etDate, etTime, etComment;
    Button btnSubmit;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String tutorId;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmitBooking);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tutorId = getIntent().getStringExtra("tutorId");

        setupDatePicker();
        setupTimePicker();

        btnSubmit.setOnClickListener(v -> saveBooking());
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();

            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = day;

                        etDate.setText(day + "/" + (month + 1) + "/" + year);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();

            new TimePickerDialog(this,
                    (view, hour, minute) -> {
                        selectedHour = hour;
                        selectedMinute = minute;

                        etTime.setText(hour + ":" + minute);
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true).show();
        });
    }

    private void saveBooking() {

        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        String comment = etComment.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Select date & time", Toast.LENGTH_SHORT).show();
            return;
        }

        String studentId = mAuth.getCurrentUser().getUid();

        Map<String, Object> booking = new HashMap<>();
        booking.put("studentId", studentId);
        booking.put("tutorId", tutorId);
        booking.put("date", date);
        booking.put("time", time);
        booking.put("sessionTimestamp", getSessionTimestamp());
        booking.put("comment", comment);
        booking.put("status", "pending");
        booking.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Bookings")
                .add(booking)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Booking request sent!", Toast.LENGTH_SHORT).show();

                    // Redirect to Home
                    Intent intent = new Intent(this, StudentHomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving booking", Toast.LENGTH_SHORT).show()
                );
    }

    private Timestamp getSessionTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime());
    }
}