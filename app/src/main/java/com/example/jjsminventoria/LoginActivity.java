package com.example.jjsminventoria;

import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.credentials.Credential;
import android.credentials.CredentialManager;
import android.credentials.GetCredentialRequest;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jjsminventoria.database.FirebaseConnection;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String PREFS_NAME = "MyAppPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_LAST_ACTIVITY = "lastActivity";

    private SignInButton btnGoogleSignIn;
    private ImageButton btnLogin;
    private EditText etEmail, etPassword;
    private CheckBox cbHide;
    private TextView tvForgotPassword, tvSignUp;

    //private DatabaseReference userDb;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSingInClient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginGoogle(account);
            } catch (ApiException e) {
                Snackbar.make(findViewById(android.R.id.content), "Google Sign-In Failed",
                        Snackbar.LENGTH_LONG).show();
                navigateToLoginActivity();
            }
        }
    }

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
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        tvSignUp = findViewById(R.id.tvSignUp);

        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);

        cbHide = findViewById(R.id.cbHide);

        btnGoogleSignIn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        cbHide.setOnCheckedChangeListener(((buttonView, isChecked) -> showPassword(isChecked)));

        //userDb = FirebaseConnection.getInstance().getUserDb();

        auth = FirebaseConnection.getInstance().getAuth();

        disablePaste(etEmail);
        disablePaste(etPassword);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnLogin) login(v);
        if (id == R.id.btnGoogleSignIn) googleSignIn(v);
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

    private void login(View v) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(v, "Please enter email and password", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (!isValidEmail(email)) {
            Snackbar.make(v, "Please enter a valid email address", Snackbar.LENGTH_LONG).show();
            return;
        }

        //showLoading(true);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserSession(user.getUid());
                            navigateToMainMenu();
                        }
                    } else {
                        Snackbar.make(v, "Login failed. Check credentials.", Snackbar.LENGTH_LONG).show();
                        Log.e("AuthError", "Sign-in failed", task.getException());
                    }
                });
    }

    private void loginGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Log.d("GoogleSignIn", "Signed in successfully with Google: " + user.getEmail());
                            saveUserSession(user.getUid());
                            navigateToMainMenu();
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.",
                                Snackbar.LENGTH_LONG).show();
                        navigateToLoginActivity();
                        Log.e("GoogleSignIn", "Authentication failed", task.getException());
                    }
                });
    }


    private void googleSignIn(View v) {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, options);

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void saveUserSession(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();
    }

    private void navigateToMainMenu(){
        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }


    // Old Login Code Using Firebase RealTime Database
//    private void login(View v){
//        try {
//            int userId = Integer.parseInt(etUserId.getText().toString());
//            String password = etPassword.getText().toString();
//
//            DatabaseReference userRef = userDb.child(String.valueOf(userId));
//
//            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        String storedPassword = snapshot.child("password").getValue(String.class);
//
//                        if (storedPassword != null && storedPassword.equals(password)){
//                            Users user = new Users();
//
//                            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME
//                                    , MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putInt(KEY_USER_ID,userId);
//                            editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
//                            editor.apply();
//
//                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Snackbar.make(v, "Error. Password Invalid", Snackbar.LENGTH_LONG).show();
//                            Log.d("FirebaseDebug", "Checking User: " + userId);
//                        }
//                    } else {
//                        Snackbar.make(v, "Error. User Id not found.", Snackbar.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(LoginActivity.this, "Database Error: "+ error.getMessage(),
//                            Toast.LENGTH_LONG).show();
//                }
//            });
//        } catch (NumberFormatException e) {
//            Toast.makeText(this, "Invalid User ID", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    private void forgotPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(intent);
    }

    private void createAccount(View v) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //loading screen when login is done if needed in the future
//    private void showLoading(boolean show){
//        ProgressBar progressBar = findViewById(R.id.progressBar);
//        if (show) {
//            progressBar.setVisibility(View.VISIBLE);
//        } else {
//            progressBar.setVisibility(View.GONE);
//        }
//    }

    private boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}