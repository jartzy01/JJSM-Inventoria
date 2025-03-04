package com.example.jjsminventoria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import model.Users;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvCAUserId;
    private EditText etCAPassword, etCAConfirmPassword, etCAName, etCARole, etCAUsername, etCAEmail;
    private Button btnCACreateAccount, btnCAReturn;

    DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvCAUserId = findViewById(R.id.tvCAUserId);
        etCAPassword = findViewById(R.id.etCAPassword);
        etCAConfirmPassword = findViewById(R.id.etCAConfirmPassword);
        etCAName = findViewById(R.id.etCAName);
        etCAEmail = findViewById(R.id.etCAEmail);
        etCARole = findViewById(R.id.etCARole);
        etCAUsername = findViewById(R.id.etCAUsername);
        btnCACreateAccount = findViewById(R.id.btnCACreateAccount);
        btnCAReturn = findViewById(R.id.btnCAReturn);

        btnCACreateAccount.setOnClickListener(this);
        btnCAReturn.setOnClickListener(this);

        userDB = FirebaseConnection.getInstance().getUserDb();

        displayId();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnCAReturn) goToLoginActivity();
        if (id == R.id.btnCACreateAccount) createAccount();
    }

    private void createAccount() {
        String password = etCAPassword.getText().toString().trim();
        String passwordConfirm = etCAConfirmPassword.getText().toString().trim();
        int userId = Integer.parseInt(tvCAUserId.getText().toString().trim());
        String name = etCAName.getText().toString().trim();
        String username = etCAUsername.getText().toString().trim();
        String role = etCARole.getText().toString().trim();
        String email = etCAEmail.getText().toString().trim();

        if (passwordConfirm.equals(password)){
            if (isValidPassword(password)) {
                Toast.makeText(this, "✅ Password respect the constraints",
                        Toast.LENGTH_SHORT).show();
                Users users = new Users(userId, password, name, username, role, email);

                userDB.child(String.valueOf(userId)).setValue(users).addOnCompleteListener(userTask -> {
                    if (userTask.isSuccessful()) {
                        Toast.makeText(this, "✅ Account created successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "❌ Failed to create Users", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                clearWidget();
                if (password.length() < 8){
                    Toast.makeText(this, "❌ Password needs to have at least 8 char.",
                            Toast.LENGTH_SHORT).show();
                } if (!password.matches(".*[A-Z].*")) {
                    Toast.makeText(this, "❌ Password needs to have a minimum of 1 uppercase letter", Toast.LENGTH_SHORT).show();
                } if (!password.matches(".*[a-z].*")){
                    Toast.makeText(this, "Password needs to have a minimum of 1 lowercase letter", Toast.LENGTH_SHORT).show();
                } if (!password.matches(".*\\d*.")) {
                    Toast.makeText(this, "❌ Password needs to have at least 1 digit",
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "❌ Password is not similar. Password must be similar",
                    Toast.LENGTH_SHORT).show();
            clearWidget();
        }
    }

    private boolean isValidPassword(String password){
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Gets the latest user Id and increment by 100
    private void displayId() {
        userDB.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        int userId = Integer.parseInt(userSnapshot.getKey()) + 100;

                        tvCAUserId.setText(String.valueOf(userId));
                    }
                } else {
                    tvCAUserId.setText("No users found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DisplayId", "Error retrieving user ID: " + error.getMessage());
                tvCAUserId.setText("Error fetching User Id.");
            }
        });
    }

    private void clearWidget(){
        etCAName.setText(null);
        etCAUsername.setText(null);
        etCARole.setText(null);
        etCAConfirmPassword.setText(null);
        etCAPassword.setText(null);
        etCAPassword.setFocusable(true);
    }
}