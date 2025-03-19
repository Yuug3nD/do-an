package com.example.do_an;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
private EditText edEmailForgot;
private Button btnResetPassword;
private ImageButton Back_ForgotPassword;
private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        edEmailForgot = findViewById(R.id.edEmailForgot);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        Back_ForgotPassword = findViewById(R.id.Back_ForgotPassword);
        fbAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> resetPassword());
        Back_ForgotPassword.setOnClickListener(v -> finish());
    }
    private void resetPassword(){
        String email = edEmailForgot.getText().toString().trim();
        if (email.isEmpty()){
            edEmailForgot.setError("Vui lòng nhập email");
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmailForgot.setError("Email không đúng định dạng!");
            return;
        }
        fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(ForgotPasswordActivity.this, "Email đăt lại mật khẩu đã được gửi.", Toast.LENGTH_SHORT).show();
                finish();
            } else  Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}