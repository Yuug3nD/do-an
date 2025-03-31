package com.example.do_an;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private EditText emailLogin, passwordLogin;
    private Button btnLogin, btn_Register;
    private ImageButton btnBack;
    private FirebaseAuth firebase_Auth;
    private TextView tvForgotPassword;
    private SignInClient oneTapClient;
    private GoogleSignInClient googleSignInClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 100;
    private static final String TAG = "GoogleLogin";
    private CallbackManager callbackManager;
    private LinearLayout btnFacebook;
    private final ActivityResultLauncher<Intent> facebookLoginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> callbackManager.onActivityResult(result.getResultCode(), result.getResultCode(), result.getData())
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Khởi tạo Facebook SDK
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.setAutoLogAppEventsEnabled(true);
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

        setContentView(R.layout.activity_login);
        setupGoogleSignIn();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tv_name_category), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.imbBack);
        emailLogin = findViewById(R.id.Email_User);
        passwordLogin = findViewById(R.id.Password_User);
        btnLogin = findViewById(R.id.btn_Login);
        btn_Register = findViewById(R.id.btn_Register);
        tvForgotPassword = findViewById(R.id.QuenMatKhau);
        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnFacebook = findViewById(R.id.btnFacebook);

        callbackManager = CallbackManager.Factory.create();
        firebase_Auth = FirebaseAuth.getInstance();



        btnBack.setOnClickListener(v -> finish());
        btnLogin.setOnClickListener(v -> LoginUser());
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        btn_Register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id)) // Lấy từ google-services.json
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
        LinearLayout btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        btnFacebook.setOnClickListener(v -> loginWithFacebook());

    }

    private void LoginUser() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        if (email.isEmpty()) {
            emailLogin.setError("Vui lòng nhập email");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.setError("Email không đúng định dạng!");
            return;
        }
        if (password.isEmpty()) {
            passwordLogin.setError("Vui lòng nhập password");
            return;
        }

        firebase_Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase_Auth.getCurrentUser();
//                        if (user != null && !user.isEmailVerified()) {
//                            Toast.makeText(LoginActivity.this, "Vui lòng xác thực email trước khi đăng nhập!", Toast.LENGTH_SHORT).show();
//                            firebase_Auth.signOut();
//                            return;
//                        }

                        // Lưu thông tin đăng nhập
                        saveUserInfo(user);

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_ONE_TAP);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//         Facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google login callback
        if (requestCode == REQ_ONE_TAP) {
            if (data != null) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    firebase_Auth.signInWithCredential(credential)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    FirebaseUser user = firebase_Auth.getCurrentUser();
                                    if (user != null) {
                                        // Lưu thông tin user vào SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("userId", user.getUid());
                                        editor.putString("userName", user.getDisplayName());
                                        editor.putString("userEmail", user.getEmail());
                                        editor.apply();

                                        // Hiển thị thông báo đăng nhập thành công
                                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                        // Chuyển về MainActivity
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // Thông báo lỗi khi đăng nhập không thành công
                                    Toast.makeText(this, "Đăng nhập thất bại: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (ApiException e) {
                    // Lỗi khi không lấy được thông tin từ Google
                    Toast.makeText(this, "Đăng nhập bằng Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Dữ liệu bị null, có thể do người dùng hủy đăng nhập
                Toast.makeText(this, "Không có dữ liệu đăng nhập từ Google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList("email", "public_profile")
        );

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Đăng nhập bị hủy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebase_Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebase_Auth.getCurrentUser();
                        if (user != null) {
                            saveUserInfo(user);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Xác thực thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserInfo(FirebaseUser user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userId", user.getUid());
        editor.putString("userName", user.getDisplayName());
        editor.putString("userEmail", user.getEmail());

        if (user.getPhotoUrl() != null) {
            editor.putString("userPhoto", user.getPhotoUrl().toString());
        }
        editor.apply();
    }

}