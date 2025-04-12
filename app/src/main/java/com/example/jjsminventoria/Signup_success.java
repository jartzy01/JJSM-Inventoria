package com.example.jjsminventoria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Signup_success extends AppCompatActivity implements View.OnClickListener {

    private String userId;
    private Button btnSReturn;
    TextView tvName;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_success);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        userId = getIntent().getStringExtra("userId");
        tvName = findViewById(R.id.tvName);
        btnSReturn = findViewById(R.id.btnSReturn);

        btnSReturn.setOnClickListener(this);
        userRef = FirebaseConnection.getInstance().getUserDb().child(userId);

        displayName(userRef);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSReturn) goLogin(v);
    }

    private void goLogin(View v) {
        Intent intent = new Intent(Signup_success.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayName(DatabaseReference databaseReference) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("FIREBASE_DEBUG", "Snapshot: " + snapshot.getValue());
                if (snapshot.exists()) {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        Log.d("FIREBASE_DEBUG", child.getKey() + " = " + child.getValue());
                    }

                    String firstName = snapshot.child("firstName").getValue(String.class);
                    String lastName = snapshot.child("lastName").getValue(String.class);

                    if (firstName != null && lastName != null) {
                        String message =
                                "Your account has been created, " + firstName + " " + lastName;
                        tvName.setText(message);
                    } else {
                        tvName.setText("Account Created.");
                    }
                } else {
                    tvName.setText("User data not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvName.setText("Error loading user data.");
            }
        });
    }
}