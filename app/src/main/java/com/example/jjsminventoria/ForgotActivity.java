package com.example.jjsminventoria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import model.Users;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFPChangePassword;
    private TextView tvFPPasswordLabel, tvFPNext;
    private ImageView ivFPNext;
    private ImageButton ibFPReturn;
    private EditText etFPEmail, etFPPassword, etFPConPassword;

    public DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvFPPasswordLabel = findViewById(R.id.tvFPPasswordLabel);
        tvFPNext = findViewById(R.id.tvFPNext);
        etFPEmail = findViewById(R.id.etFPEmail);
        etFPPassword = findViewById(R.id.etFPPassword);
        etFPConPassword = findViewById(R.id.etFPConPassword);
        tvFPNext = findViewById(R.id.tvFPNext);
        ivFPNext = findViewById(R.id.ivFPNext);
        btnFPChangePassword = findViewById(R.id.btnFPChangePassword);
        ibFPReturn = findViewById(R.id.ibFPReturn);

        ibFPReturn.setOnClickListener(this);
        ivFPNext.setOnClickListener(this); tvFPNext.setOnClickListener(this);
        btnFPChangePassword.setOnClickListener(this);

        userDB = FirebaseConnection.getInstance().getUserDb();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.ibFPReturn) goToLoginActivity(view);
        if (id == R.id.ivFPNext || id == R.id.tvFPNext) findUser(view);
        if (id == R.id.btnFPChangePassword) changePassword(view);
    }

    private void changePassword(View view) {
        String email = etFPEmail.getText().toString().trim();
        String newPassword = etFPPassword.getText().toString().trim();
        String confirmPassword = etFPConPassword.getText().toString().trim();

        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(email);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Error : " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        userDB.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Snackbar.make(view, "User Id: " + userId + " does not exist.",
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                Users currentUser = snapshot.getValue(Users.class);
                if (currentUser != null) {
                    currentUser.setPassword(newPassword);

                    userDB.child(String.valueOf(userId)).setValue(currentUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar.make(view, "The User with ID " + userId + " has successfully " +
                                            "changed password. Password: " + currentUser.getPassword(),
                                    Snackbar.LENGTH_LONG).show();
                            clearWidgets();
                            Intent intent = new Intent(ForgotActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(view, "Failed to changed password. Please try again.",
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void findUser(View view) {
        try {
            // Gets Email
            String email = etFPEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Snackbar.make(view, "User Id field must not be empty.", Snackbar.LENGTH_LONG).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Snackbar.make(view, "Please enter a valid email address.", Snackbar.LENGTH_LONG).show();
                return;
            }

            FirebaseAuth auth = FirebaseConnection.getInstance().getAuth();

            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    boolean emailExists = !task.getResult().getSignInMethods().isEmpty();

                    if (emailExists) {
                        Snackbar.make(view, "Input your new Password", Snackbar.LENGTH_LONG).show();

                        tvFPPasswordLabel.setVisibility(View.VISIBLE);
                        etFPPassword.setVisibility(View.VISIBLE);
                        etFPConPassword.setVisibility(View.VISIBLE);
                        btnFPChangePassword.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email);
                    } else {
                        Snackbar.make(view, "Email does not exist", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    // Detailed error handling
                    Exception exception = task.getException();
                    String errorMessage = (exception != null) ? exception.getMessage() : "Unknown error";
                    Log.e("FindUserError", errorMessage); // Log the error for debugging
                    Snackbar.make(view, "Error: " + errorMessage, Snackbar.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Snackbar.make(view, "Error : " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void goToLoginActivity(View view) {
        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearWidgets(){
        etFPEmail.setText(null);
        etFPPassword.setText(null);
        etFPConPassword.setText(null);
        tvFPPasswordLabel.setText(null);
        etFPPassword.setVisibility(View.GONE);
        etFPConPassword.setVisibility(View.GONE);
        btnFPChangePassword.setVisibility(View.GONE);
        etFPEmail.requestFocus();
    }
}