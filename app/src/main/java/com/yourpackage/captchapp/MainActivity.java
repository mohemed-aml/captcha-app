package com.yourpackage.captchapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity: The entry point of the app with a button to navigate to HomeActivity.
 */
public class MainActivity extends AppCompatActivity {

    private Button buttonNavigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout defined above

        // Initialize the button by finding it by its ID
        buttonNavigate = findViewById(R.id.buttonNavigate);

        // Set an OnClickListener to handle button clicks
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome(); // Method to navigate to HomeActivity
            }
        });
    }

    /**
     * Navigates to the HomeActivity.
     */
    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent); // Start HomeActivity
    }
}