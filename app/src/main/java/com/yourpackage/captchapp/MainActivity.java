package com.yourpackage.captchapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.yourpackage.captchapp.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * MainActivity: The entry point of the app with CAPTCHA validation before navigation.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String captchaId;

    // Replace with your actual backend URLs
    private static final String CAPTCHA_URL = "http://192.168.1.9:3000/api/captcha";
    private static final String VALIDATE_URL = "http://192.168.1.9:3000/api/validate-captcha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the initial CAPTCHA
        loadCaptcha();

        // Set up refresh button to reload CAPTCHA
        binding.buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCaptcha();
            }
        });

        // Set up submit button to validate CAPTCHA
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = binding.captchaInput.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    validateCaptcha(userInput);
                } else {
                    showError("Please enter the CAPTCHA text.");
                }
            }
        });

        // Set up navigate button (disabled until CAPTCHA is validated)
        binding.buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });

        // Initially disable the navigate button
        binding.buttonNavigate.setEnabled(false);
        binding.buttonNavigate.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDisabled));
    }

    /**
     * Fetches the CAPTCHA from the backend API.
     */
    private void loadCaptcha() {
        showLoading(true); // Show loading state

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(CAPTCHA_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Failed to load CAPTCHA. Please try again.");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        // Parse the response
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String imageUrl = jsonResponse.getString("image_url");
                        captchaId = String.valueOf(jsonResponse.getInt("id"));

                        runOnUiThread(() -> {
                            // Load the CAPTCHA image using Glide
                            Glide.with(MainActivity.this).load(imageUrl).into(binding.captchaImage);
                            showLoading(false); // Hide loading spinner
                            binding.captchaInput.setText(""); // Clear input field
                            binding.buttonNavigate.setEnabled(false); // Disable navigation button
                            binding.buttonNavigate.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDisabled));
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showError("Error parsing CAPTCHA. Please try again.");
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError("Failed to load CAPTCHA. Please try again.");
                    });
                }
            }
        });
    }

    /**
     * Validates the user's CAPTCHA input by sending it to the backend.
     *
     * @param userAnswer The CAPTCHA answer entered by the user.
     */
    private void validateCaptcha(String userAnswer) {
        showLoading(true); // Show loading spinner

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", captchaId);
            jsonObject.put("answer", userAnswer);
        } catch (JSONException e) {
            showError("Error creating validation request.");
            showLoading(false);
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder().url(VALIDATE_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Failed to validate CAPTCHA. Please try again.");
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        boolean success = jsonResponse.getBoolean("success");

                        runOnUiThread(() -> {
                            showLoading(false);
                            if (success) {
                                binding.buttonNavigate.setEnabled(true); // Enable the button if CAPTCHA is correct
                                binding.buttonNavigate.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                                Toast.makeText(MainActivity.this, "CAPTCHA validated!", Toast.LENGTH_SHORT).show();
                            } else {
                                showError("Incorrect CAPTCHA. Please try again.");
                                binding.buttonNavigate.setEnabled(false);
                                binding.buttonNavigate.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDisabled));
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            showLoading(false);
                            showError("Failed to validate CAPTCHA. Please try again.");
                        });
                    }
                } catch (JSONException e) {
                    runOnUiThread(() -> {
                        showLoading(false);
                        showError("Error parsing validation response.");
                    });
                }
            }
        });
    }

    /**
     * Navigates to the HomeActivity after successful CAPTCHA validation.
     */
    private void navigateToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent); // Start HomeActivity
    }

    /**
     * Shows or hides the loading spinner and manages button states.
     *
     * @param isLoading Whether to show the loading spinner.
     */
    private void showLoading(boolean isLoading) {
        binding.loadingSpinner.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.buttonSubmit.setEnabled(!isLoading);
        binding.buttonRefresh.setEnabled(!isLoading);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    private void showError(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}