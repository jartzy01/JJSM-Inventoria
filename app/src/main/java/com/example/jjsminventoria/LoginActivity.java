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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

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

    private FirebaseAuth auth;
    private GoogleSignInClient googleSingInClient;

    private View logo, illustration, loginCard;

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

    private void initialize() {
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        cbHide = findViewById(R.id.cbHide);

        logo = findViewById(R.id.logoImage);
        illustration = findViewById(R.id.illustrationImage);
        loginCard = findViewById(R.id.loginCard);

        btnGoogleSignIn.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgotPassword.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        cbHide.setOnCheckedChangeListener((buttonView, isChecked) -> showPassword(isChecked));

        auth = FirebaseConnection.getInstance().getAuth();

        disablePaste(etEmail);
        disablePaste(etPassword);

        // 👇 Focus change: hide logo & illustration
        View.OnFocusChangeListener hideOnFocus = (v, hasFocus) -> {
            if (hasFocus) {
                illustration.setVisibility(View.GONE);
            } else if (!etEmail.hasFocus() && !etPassword.hasFocus()) {
                illustration.setVisibility(View.VISIBLE);
            }
        };

        etEmail.setOnFocusChangeListener(hideOnFocus);
        etPassword.setOnFocusChangeListener(hideOnFocus);


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) login(v);
        if (id == R.id.btnGoogleSignIn) googleSignIn(v);
        if (id == R.id.tvSignUp) createAccount(v);
        if (id == R.id.tvForgotPassword) forgotPassword(v);
    }

    private void showPassword(boolean isChecked) {
        if (isChecked) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void disablePaste(EditText et) {
        et.setLongClickable(false);
        et.setOnTouchListener((v, event) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
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

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
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
                            saveUserSession(user.getUid());
                            navigateToMainMenu();
                        }
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Authentication Failed.",
                                Snackbar.LENGTH_LONG).show();
                        Log.e("GoogleSignIn", "Authentication failed", task.getException());
                        navigateToLoginActivity();
                    }
                });
    }

    private void googleSignIn(View v) {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        googleSingInClient = GoogleSignIn.getClient(this, options);
        Intent signInIntent = googleSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private void saveUserSession(String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis());
        editor.apply();
    }

    private void navigateToMainMenu() {
        Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void forgotPassword(View v) {
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        startActivity(intent);
        finish();
    }

    private void createAccount(View v) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
