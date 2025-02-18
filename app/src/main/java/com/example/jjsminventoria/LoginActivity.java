package com.example.jjsminventoria;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import model.Users;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LAST_ACTIVITY = "lastActivity";

    private Button btnLogin;
    private EditText etUserId, etPassword;
    private CheckBox cbHide;
    private TextView tvForgotPassword, tvSignUp;

    private DatabaseReference userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initialize();
    }

    private void initialize(){
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        etPassword = findViewById(R.id.etPassword);
        etUserId = findViewById(R.id.etUserId);

        cbHide = findViewById(R.id.cbHide);

        btnLogin.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        cbHide.setOnCheckedChangeListener(((buttonView, isChecked) -> showPassword(isChecked)));

        userDb = FirebaseDatabase.getInstance().getReference("Users");

        disablePaste(etUserId);
        disablePaste(etPassword);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnLogin) login(v);
        if (id == R.id.tvSignUp) createAccount(v);
        if (id == R.id.tvForgotPassword) forgotPassword(v);
    }

    private void showPassword(boolean isChecked){
        if (isChecked){
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void disablePaste(EditText et) {
        et.setLongClickable(false);
        et.setOnTouchListener((v, event) ->{
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()){
                clipboard.clearPrimaryClip();
            }
            return false;
        });
    }

    private void login(View v){
        try {
            int userId = Integer.parseInt(etUserId.getText().toString());
            String password = etPassword.getText().toString();

            DatabaseReference userRef = userDb.child(String.valueOf(userId));

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String storedPassword = snapshot.child("password").getValue(String.class);

                        if (storedPassword != null && storedPassword.equals(password)){
                            Users user = new Users();

                            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME
                                    , MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt(KEY_USER_ID,userId);
                            editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Snackbar.make(v, "Error. Password Invalid", Snackbar.LENGTH_LONG).show();
                            Log.d("FirebaseDebug", "Checking User: " + userId);
                        }
                    } else {
                        Snackbar.make(v, "Error. User Id not found.", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Database Error: "+ error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void forgotPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(intent);
    }

    private void createAccount(View v) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }
}