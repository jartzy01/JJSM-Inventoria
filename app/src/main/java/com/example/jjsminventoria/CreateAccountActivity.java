package com.example.jjsminventoria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import model.Users;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvCAUserId;
    private EditText etCAFirstName, etCALastName, etCAEmail, etCACompanyName, etCAAddress, etCACountry, etCAPassword, etCAConfirm;
    private ImageButton btnSignup, btnCAReturn;

    DatabaseReference userDB;
    FirebaseAuth auth;

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
        etCAPassword = findViewById(R.id.etCAPassword);
        etCAConfirm = findViewById(R.id.etCAConfirm);
        etCAFirstName = findViewById(R.id.etCAFirstName);
        etCALastName = findViewById(R.id.etCALastName);
        etCAEmail = findViewById(R.id.etCAEmail);

        etCACompanyName = findViewById(R.id.etCACompanyName);
        etCAAddress = findViewById(R.id.etCAAddress);
        etCACountry = findViewById(R.id.etCACountry);

        btnSignup = findViewById(R.id.btnSignup);
        btnCAReturn = findViewById(R.id.btnCAReturn);

        btnSignup.setOnClickListener(this);
        btnCAReturn.setOnClickListener(this);

        userDB = FirebaseConnection.getInstance().getUserDb();
        auth = FirebaseConnection.getInstance().getAuth();

        //displayId();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btnCAReturn) goToLoginActivity();
        if (id == R.id.btnSignup) createAccount();
    }

    private void createAccount() {
        String password = etCAPassword.getText().toString().trim();
        String passwordConfirm = etCAConfirm.getText().toString().trim();
        String firstName = etCAFirstName.getText().toString().trim();
        String lastName = etCALastName.getText().toString().trim();
        String role = "employee";
        String email = etCAEmail.getText().toString().trim();

        if (!passwordConfirm.equals(password)) {
            Toast.makeText(this, "❌ Passwords do not match", Toast.LENGTH_SHORT).show();
            clearPassword();
            return;
        }

        if (!isValidPassword(password)) {
            clearWidget();
            if (password.length() < 8) {
                Toast.makeText(this, "❌ Passwords must be at least 8 characters",
                        Toast.LENGTH_SHORT).show();
            } if (!password.matches(".*[A-Z]].*")) {
                Toast.makeText(this, "❌ Passwords must have at least 1 uppercase letter",
                        Toast.LENGTH_SHORT).show();
            } if (!password.matches(".*[a-z].*]")) {
                Toast.makeText(this, "❌ Passwords must have at least 1 lowercase letter",
                        Toast.LENGTH_SHORT).show();
            } if (!password.matches(".*\\d.*")) {
                Toast.makeText(this, "❌ Passwords must have at least 1 digit",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }

        FirebaseConnection firebaseConnection = FirebaseConnection.getInstance();
        FirebaseAuth mAuth = firebaseConnection.getAuth();
        DatabaseReference userDb = firebaseConnection.getUserDb();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               FirebaseUser firebaseUser = mAuth.getCurrentUser();
               if (firebaseUser != null) {
                   String userId = firebaseUser.getUid();
                   Users users = new Users(userId, firstName, lastName, email, role);
                   userDb.child(userId).setValue(users).addOnCompleteListener(dbTask -> {
                      if (dbTask.isSuccessful()) {
                          Toast.makeText(this, "✅ Account created successfully", Toast.LENGTH_SHORT).show();
                          goToSuccessActivity();
                      } else {
                          Toast.makeText(this, "❌ Failed to store user data.", Toast.LENGTH_SHORT).show();
                      }
                   });
               } else {
                   Toast.makeText(this,
                           "❌ Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
               }
           } else {
               Exception e = task.getException();
               if (e instanceof FirebaseAuthUserCollisionException) {
                   Toast.makeText(this, "❌ This email is already registered.", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(this, "❌ Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
               }
           }
        });

//        if (passwordConfirm.equals(password)){
//            if (isValidPassword(password)) {
//                Toast.makeText(this, "✅ Password respect the constraints",
//                        Toast.LENGTH_SHORT).show();
//                Users users = new Users(userId, password, firstName, lastName, email);
//
//                userDB.child(String.valueOf(userId)).setValue(users).addOnCompleteListener(userTask -> {
//                    if (userTask.isSuccessful()) {
//                        Toast.makeText(this, "✅ Account created successfully", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(this, "❌ Failed to create Users", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                clearWidget();
//                if (password.length() < 8){
//                    Toast.makeText(this, "❌ Password needs to have at least 8 char.",
//                            Toast.LENGTH_SHORT).show();
//                } if (!password.matches(".*[A-Z].*")) {
//                    Toast.makeText(this, "❌ Password needs to have a minimum of 1 uppercase letter", Toast.LENGTH_SHORT).show();
//                } if (!password.matches(".*[a-z].*")){
//                    Toast.makeText(this, "Password needs to have a minimum of 1 lowercase letter", Toast.LENGTH_SHORT).show();
//                } if (!password.matches(".*\\d*.")) {
//                    Toast.makeText(this, "❌ Password needs to have at least 1 digit",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        } else {
//            Toast.makeText(this, "❌ Password is not similar. Password must be similar",
//                    Toast.LENGTH_SHORT).show();
//            clearWidget();
//        }
    }

    private boolean isValidPassword(String password){
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    private void goToSuccessActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, Signup_success.class);
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Gets the latest user Id and increment by 100
//    private void displayId() {
//        userDB.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
//                        int userId = Integer.parseInt(userSnapshot.getKey()) + 100;
//
//                        tvCAUserId.setText(String.valueOf(userId));
//                    }
//                } else {
//                    tvCAUserId.setText("No users found.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("DisplayId", "Error retrieving user ID: " + error.getMessage());
//                tvCAUserId.setText("Error fetching User Id.");
//            }
//        });
//    }

    private void clearWidget(){
        etCAFirstName.setText(null);
        etCALastName.setText(null);
        etCAEmail.setText(null);
        etCACompanyName.setText(null);
        etCAAddress.setText(null);
        etCACountry.setText(null);
        etCAConfirm.setText(null);
        etCAPassword.setText(null);
        etCAFirstName.setFocusable(true);
    }

    private void clearPassword(){
        etCAPassword.setText(null);
        etCAConfirm.setText(null);
        etCAPassword.setFocusable(true);
    }
}