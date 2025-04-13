package com.example.jjsminventoria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.jjsminventoria.ui.dashboard.DashboardFragment;
import com.example.jjsminventoria.ui.profile.ProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "MyAppPrefs";
    private int currentTabIndex = 0;

    private TextView tvLogout;
    private LinearLayout dashboardTab, inventoryTab, profileTab, bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize logout button
        tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(this);

        // Set up your custom bottom tab buttons
        dashboardTab = findViewById(R.id.dashboardTab);
        inventoryTab = findViewById(R.id.inventoryTab);
        profileTab = findViewById(R.id.profileTab);

        dashboardTab.setOnClickListener(this);
        inventoryTab.setOnClickListener(this);
        profileTab.setOnClickListener(this);

        // Load the default fragment (Dashboard)
        loadFragment(new DashboardFragment());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Fragment selectedFragment = null;
        int newTabIndex = currentTabIndex;

        resetTabBackgrounds();

        if (id == R.id.tvLogout) {
            logout();
        } else if (id == R.id.dashboardTab) {
            selectedFragment = new DashboardFragment();
            newTabIndex = 0;
            dashboardTab.setSelected(true);
        } else if (id == R.id.inventoryTab) {
            selectedFragment = new fragment_categories();
            newTabIndex = 1;
            profileTab.setSelected(true);
        } else if (id == R.id.profileTab) {
            selectedFragment = new ProfileFragment();
            newTabIndex = 2;
            inventoryTab.setSelected(true);
        }

        if (selectedFragment != null && newTabIndex != currentTabIndex) {
            currentTabIndex = newTabIndex;
            loadFragment(selectedFragment);
        }
    }

    private void resetTabBackgrounds() {
        // Use ContextCompat to get the color in a more compatible way
        dashboardTab.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray)); // Set unselected background
        inventoryTab.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray)); // Set unselected background
        profileTab.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray)); // Set unselected background
    }

    private void logout() {
        FirebaseConnection.getInstance().logout();
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadFragment(Fragment fragment) {
        // Get the FragmentManager and check if the current fragment is the same as the new one
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_menu_bottom_tabs);

        // If the fragment is the same as the current one, no need to replace it again
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        // Directly replace the fragment without animations
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, fragment) // Replace the fragment instead of adding
                .commit(); // No animation
    }
}
