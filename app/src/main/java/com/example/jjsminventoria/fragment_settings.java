package com.example.jjsminventoria;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public fragment_settings() {
        // Required empty public constructor
    }

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

        TextView nameTextView = view.findViewById(R.id.userNameTextView);
        TextView emailTextView = view.findViewById(R.id.userEmailTextView);
        TextView passwordTextView = view.findViewById(R.id.userPasswordTextView);
        TextView positionTextView = view.findViewById(R.id.userPositionTextView);
        TextView privilegesTextView = view.findViewById(R.id.userPrivilegesTextView);

        String uid = FirebaseConnection.getInstance().getAuth().getCurrentUser().getUid();

        FirebaseConnection.getInstance().getUserDb().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class); // Optional

                        nameTextView.setText((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));
                        emailTextView.setText(email != null ? email : "N/A");
                        passwordTextView.setText("••••••••");
                        positionTextView.setText(role != null ? capitalize(role) : "Unknown");
                        privilegesTextView.setText("Viewer");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load user settings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 🔠 Helper to capitalize first letter
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
