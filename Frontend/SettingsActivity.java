package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
//import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    private static final String PRIVACY_POLICY_URL = "https://example.com/privacy";

    private SwitchCompat darkModeSwitch;
    private SwitchCompat notificationsSwitch;
    private SwitchCompat highAccuracySwitch;

    private SharedPreferences sharedPreferences;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        highAccuracySwitch = findViewById(R.id.highAccuracySwitch);

        // Set up click listeners
        findViewById(R.id.privacyPolicyItem).setOnClickListener(v -> openPrivacyPolicy());
        findViewById(R.id.termsItem).setOnClickListener(v -> openTermsOfService());

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        // Load settings
        loadSettings();

        // Set up switch listeners
        setupSwitchListeners();
    }

    private void loadSettings() {
        isLoading = true;

        boolean darkMode = sharedPreferences.getBoolean("darkMode", false);
        boolean notifications = sharedPreferences.getBoolean("notifications", true);
        boolean highAccuracy = sharedPreferences.getBoolean("highAccuracy", false);

        darkModeSwitch.setChecked(darkMode);
        notificationsSwitch.setChecked(notifications);
        highAccuracySwitch.setChecked(highAccuracy);

        isLoading = false;
    }

    private void setupSwitchListeners() {
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoading) {
                saveSetting("darkMode", isChecked);
                applyDarkMode(isChecked);
            }
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoading) {
                saveSetting("notifications", isChecked);
            }
        });

        highAccuracySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isLoading) {
                saveSetting("highAccuracy", isChecked);
            }
        });
    }

    private void saveSetting(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private void applyDarkMode(boolean enabled) {
        // Implement your dark mode logic here
        // This might require recreating the activity
        recreate();
    }

    private void openPrivacyPolicy() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Could not open privacy policy", Toast.LENGTH_SHORT).show();
        }
    }

    private void openTermsOfService() {
        // Similar implementation as privacy policy
        Toast.makeText(this, "Terms of service", Toast.LENGTH_SHORT).show();
    }
}