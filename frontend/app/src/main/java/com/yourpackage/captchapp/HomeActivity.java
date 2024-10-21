package com.yourpackage.captchapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * HomeActivity: Contains BottomNavigationView to switch between different fragments.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.nav_activity1);

        // Load the default fragment
        loadFragment(new Fragment1());

        // Set a listener for navigation item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_activity1) {
                selectedFragment = new Fragment1();
            } else if (itemId == R.id.nav_activity2) {
                selectedFragment = new Fragment2();
            } else if (itemId == R.id.nav_activity3) {
                selectedFragment = new Fragment3();
            } else if (itemId == R.id.nav_activity4) {
                selectedFragment = new Fragment4();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }

    /**
     * Helper method to load a fragment into the container.
     *
     * @param fragment The fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}