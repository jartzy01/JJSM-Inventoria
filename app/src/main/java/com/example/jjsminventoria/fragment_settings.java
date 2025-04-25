package com.example.jjsminventoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class fragment_settings extends Fragment {

    public fragment_settings() {}

    public static fragment_settings newInstance() {
        return new fragment_settings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔷 Basic Info TextViews
        TextView nameTextView = view.findViewById(R.id.userNameTextView);
        TextView emailTextView = view.findViewById(R.id.userEmailTextView);
        TextView passwordTextView = view.findViewById(R.id.userPasswordTextView);
        TextView positionTextView = view.findViewById(R.id.userPositionTextView);
        TextView privilegesTextView = view.findViewById(R.id.userPrivilegesTextView);

        // 🔷 Notification Checkboxes
        CheckBox cbItemCreated = view.findViewById(R.id.cbItemCreated);
        CheckBox cbItemDeleted = view.findViewById(R.id.cbItemDeleted);
        CheckBox cbOutOfStock = view.findViewById(R.id.cbOutOfStock);
        CheckBox cbRequestChange = view.findViewById(R.id.cbRequestChange);

        String uid = FirebaseConnection.getInstance().getAuth().getCurrentUser().getUid();

        FirebaseConnection.getInstance().getUserDb().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);

                        // 🔸 Notification Preferences from Firebase
                        Boolean notifyCreated = snapshot.child("notifyItemCreated").getValue(Boolean.class);
                        Boolean notifyDeleted = snapshot.child("notifyItemDeleted").getValue(Boolean.class);
                        Boolean notifyOutOfStock = snapshot.child("notifyOutOfStock").getValue(Boolean.class);
                        Boolean notifyRequestChange = snapshot.child("notifyRequestChange").getValue(Boolean.class);

                        // 🔸 Set basic user data
                        nameTextView.setText((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                        emailTextView.setText(email != null ? email : "N/A");
                        passwordTextView.setText("••••••••");
                        positionTextView.setText(role != null ? capitalize(role) : "Unknown");
                        privilegesTextView.setText("Viewer");

                        // ✅ Set checkbox states (default to true if null)
                        cbItemCreated.setChecked(notifyCreated == null || notifyCreated);
                        cbItemDeleted.setChecked(notifyDeleted == null || notifyDeleted);
                        cbOutOfStock.setChecked(notifyOutOfStock == null || notifyOutOfStock);
                        cbRequestChange.setChecked(notifyRequestChange == null || notifyRequestChange);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "❌ Failed to load user settings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 🔠 Helper to capitalize first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
