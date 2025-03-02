package com.example.jjsminventoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import model.Users;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFPReturn, btnFPConfirm, btnFPFindUser;
    private TextView tvFPNewPassword, tvFPPasswordConf;
    private EditText etFPUserID, etFPNewPassword, etFPConfirmPass;

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
        tvFPNewPassword = findViewById(R.id.tvFPNewPassword);
        tvFPPasswordConf = findViewById(R.id.tvFPPasswordConf);
        etFPUserID = findViewById(R.id.etFPUserId);
        etFPNewPassword = findViewById(R.id.etFPNewPassword);
        etFPConfirmPass = findViewById(R.id.etFPConfirmPass);
        btnFPReturn = findViewById(R.id.btnFPReturn);
        btnFPConfirm = findViewById(R.id.btnFPConfirm);
        btnFPFindUser = findViewById(R.id.btnFPFindUser);

        btnFPReturn.setOnClickListener(this);
        btnFPFindUser.setOnClickListener(this);
        btnFPConfirm.setOnClickListener(this);

        userDB = FirebaseConnection.getInstance().getUserDb();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnFPReturn) goToLoginActivity(view);
        if (id == R.id.btnFPFindUser) findUser(view);
        if (id == R.id.btnFPConfirm) changePassword(view);
    }

    private void changePassword(View view) {
        String userIdField = etFPUserID.getText().toString().trim();
        String newPassword = etFPNewPassword.getText().toString().trim();
        String confirmPassword = etFPConfirmPass.getText().toString().trim();

        if (userIdField.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId;

        try {
            userId = Integer.parseInt(userIdField);
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
            // Gets UserId
            String userId = etFPUserID.getText().toString().trim();

            if (userId.isEmpty()) {
                Snackbar.make(view, "User Id field must not be empty.", Snackbar.LENGTH_LONG).show();
                return;
            }

            // Check if userId exist in firebase
            userDB.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Snackbar.make(view, "Input ur new Password", Snackbar.LENGTH_LONG).show();

                        // Change visibility of EditText, TextView, Button in order to change
                        // password
                        tvFPNewPassword.setVisibility(View.VISIBLE);
                        tvFPPasswordConf.setVisibility(View.VISIBLE);
                        etFPConfirmPass.setVisibility(View.VISIBLE);
                        etFPNewPassword.setVisibility(View.VISIBLE);
                        btnFPConfirm.setVisibility(View.VISIBLE);
                    } else {
                        Snackbar.make(view, "UserId does not exist", Snackbar.LENGTH_LONG).show();
                    }
                }

                // Gives a message if there's a database error
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Snackbar.make(view, "Error : " + error.getMessage(), Snackbar.LENGTH_LONG).show();
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
        etFPUserID.setText(null);
        etFPNewPassword.setText(null);
        etFPConfirmPass.setText(null);
        etFPConfirmPass.setVisibility(View.GONE);
        etFPNewPassword.setVisibility(View.GONE);
        tvFPPasswordConf.setVisibility(View.GONE);
        tvFPNewPassword.setVisibility(View.GONE);
        btnFPConfirm.setVisibility(View.GONE);
        etFPUserID.requestFocus();
    }
}