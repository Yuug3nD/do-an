package com.example.do_an;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Patterns;

public class RegisterActivity extends AppCompatActivity {
    private EditText edName, edEmail, edPassword, edConfirmPassword;
    private Button btnRegister;
    ImageButton btnBack;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tv_name_category), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edName = findViewById(R.id.Name_Register);
        edEmail = findViewById(R.id.Email_Register);
        edPassword = findViewById(R.id.Password_Register);
        edConfirmPassword = findViewById(R.id.Check_Password);
        btnRegister = findViewById(R.id.Register);
        btnBack = findViewById(R.id.Back_Register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance("https://doanmobile-t13-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("User");

        btnRegister.setOnClickListener(v -> registerUser());
        btnBack.setOnClickListener(v -> finish());
    }
    private void registerUser() {
    String name = edName.getText().toString().trim();
    String email = edEmail.getText().toString().trim();
    String password = edPassword.getText().toString().trim();
    String confirmPassword = edConfirmPassword.getText().toString().trim();

    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        return;
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        edEmail.setError("Email không đúng định dạng!");
        return;
    }
    if (password.length() < 6) {
        Toast.makeText(RegisterActivity.this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
        return;
    }
    if (!password.equals(confirmPassword)) {
        Toast.makeText(RegisterActivity.this, "Mật khẩu xác nhận không khớp! Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
        return;
    }

    firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        String userName = name;  // Sử dụng tên người dùng đã nhập
                        String userEmail = user.getEmail();

                        // Lưu thông tin người dùng vào Realtime Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                        User newUser = new User(userId, userName, userEmail);  // Tạo đối tượng User

                        // Lưu vào database
                        databaseReference.child(userId).setValue(newUser)
                                .addOnCompleteListener(databaseTask -> {
                                    if (databaseTask.isSuccessful()) {
                                        // Lưu thông tin người dùng vào SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putBoolean("isLoggedIn", true);
                                        editor.putString("userId", userId);
                                        editor.putString("userName", userName);
                                        editor.putString("userEmail", userEmail);
                                        editor.apply();

                                        // Chuyển về MainActivity
                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Lưu thông tin vào Database thất bại!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}
