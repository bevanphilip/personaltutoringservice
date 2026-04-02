package com.example.personaltutoringservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register button - testing screen setup and a return when you click on button
        Button registerButton = findViewById(R.id.register);

        registerButton.setOnClickListener(v -> {
                    Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        });

        //login button - testing screen setup and a return when you click on button
        Button loginButton = findViewById(R.id.login);

        loginButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Button Clicked!", Toast.LENGTH_SHORT).show();
        });

    }
}
