package com.example.do_an;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeActivity extends AppCompatActivity {
    DatabaseReference postRef;
    String postId, userId, currentUserId;
    ImageView imgRecipe;
    ImageButton btnBack, btnEdit, btnDelete;
    TextView tvTitle, tvTime, tvIngredients, tvSteps;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // Ánh xạ View
        tvTitle = findViewById(R.id.title);
        tvTime = findViewById(R.id.time);
        tvIngredients = findViewById(R.id.NguyenLieu);
        tvSteps = findViewById(R.id.CachLam);
        imgRecipe = findViewById(R.id.img_chitiet);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.delete);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("postId");
        }

        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy công thức!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy dữ liệu từ Firebase
        postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(RecipeActivity.this, "Công thức không tồn tại!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Lấy thông tin từ Firebase
                String title = snapshot.child("title").getValue(String.class);
                String time = snapshot.child("cookingTime").getValue(String.class);
                String ingredients = snapshot.child("ingredients").getValue(String.class);
                String steps = snapshot.child("steps").getValue(String.class);
                String imageUrl = snapshot.child("image").getValue(String.class);

                // Cập nhật giao diện
                tvTitle.setText(title != null ? title : "Không có tiêu đề");
                tvTime.setText(time != null ? time : "Không có thời gian");
                tvIngredients.setText(ingredients != null ? ingredients : "Không có nguyên liệu");
                tvSteps.setText(steps != null ? steps : "Không có hướng dẫn");

                // Load ảnh từ Firebase
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(RecipeActivity.this)
                            .load(imageUrl)
                            .into(imgRecipe);
                }
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    btnEdit.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                }
                if (currentUser != null) {
                    String currentUserId = currentUser.getUid();
                    userId = snapshot.child("userId").getValue(String.class);

                    if (userId != null && userId.equals(currentUserId)) {
                        btnEdit.setVisibility(View.VISIBLE);
                        btnDelete.setVisibility(View.VISIBLE);
                    } else {
                        btnEdit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                    }
                } else {
                    Log.e("FirebaseAuth", "Người dùng chưa đăng nhập!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> {
            Intent intent1 = new Intent(RecipeActivity.this, Share_RepiceActivity.class);
            intent1.putExtra("postId", postId); // Gửi postId sang activity chỉnh sửa
            startActivity(intent1);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(RecipeActivity.this)
                    .setTitle("Xóa bài viết")
                    .setMessage("Bạn có chắc chắn muốn xóa bài viết này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        postRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RecipeActivity.this, "Đã xóa bài viết!", Toast.LENGTH_SHORT).show();
                                finish(); // Quay lại màn hình trước
                            } else {
                                Toast.makeText(RecipeActivity.this, "Lỗi khi xóa!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadPostData(postId);
    }
    private void loadPostData(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(RecipeActivity.this, "Công thức không tồn tại!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Lấy thông tin từ Firebase
                String title = snapshot.child("title").getValue(String.class);
                String time = snapshot.child("cookingTime").getValue(String.class);
                String ingredients = snapshot.child("ingredients").getValue(String.class);
                String steps = snapshot.child("steps").getValue(String.class);
                String imageUrl = snapshot.child("image").getValue(String.class);

                // Cập nhật giao diện
                tvTitle.setText(title != null ? title : "Không có tiêu đề");
                tvTime.setText(time != null ? time : "Không có thời gian");
                tvIngredients.setText(ingredients != null ? ingredients : "Không có nguyên liệu");
                tvSteps.setText(steps != null ? steps : "Không có hướng dẫn");

                // Load ảnh từ Firebase
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(RecipeActivity.this)
                            .load(imageUrl)
                            .into(imgRecipe);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}
