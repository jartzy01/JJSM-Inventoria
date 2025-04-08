package com.example.jjsminventoria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.example.jjsminventoria.ui.inventory.InventoryHomeViewFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.jjsminventoria.ui.dashboard.DashboardFragment;
import com.example.jjsminventoria.ui.profile.ProfileFragment;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "MyAppPrefs";

    private TextView tvLogout;
    private LinearLayout dashboardTab, inventoryTab, profileTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu); // DO NOT use binding anymore

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

        if (id == R.id.tvLogout) {
            logout();
        } else if (id == R.id.dashboardTab) {
            loadFragment(new DashboardFragment());
        } else if (id == R.id.inventoryTab) {
            loadFragment(new fragment_categories());
        } else if (id == R.id.profileTab) {
            loadFragment(new ProfileFragment());
        }
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main_menu_bottom_tabs, fragment)
                .commit();
    }
}
