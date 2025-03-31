package com.example.do_an;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.do_an.database.CloudinaryHelper;
import com.example.do_an.database.CloudinaryManager;
import com.example.do_an.database.ImgurAPI;
import com.example.do_an.database.ImgurResponse;
import com.example.do_an.database.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Share_RepiceActivity extends AppCompatActivity {
    private boolean isEditing = false;
    private String postId;
    private EditText edtTenMon, edtThoiGian, edtNguyenLieu, edtCachLam;
    private Spinner spinnerCategory;
    private ImageView img;
    private String existingImageUrl;
    private MaterialButton btnLenSong;
    private Uri imageUri;
    private static final int PICK_IMAGE = 1;

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    ImageButton btnBack;
    ArrayList<String> categoryList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_repice);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ Spinner
        edtTenMon = findViewById(R.id.edtTex_TenMon);
        edtThoiGian = findViewById(R.id.ThoiGianNau);
        edtNguyenLieu = findViewById(R.id.Tex_NguyenLieu);
        edtCachLam = findViewById(R.id.Tex_CachLam);
        spinnerCategory = findViewById(R.id.spinner_Category);
        img = findViewById(R.id.anh_mon_an);
        btnLenSong = findViewById(R.id.btn_LenSong);
        btnBack = findViewById(R.id.btn_Back);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Posts");

        img.setOnClickListener(v -> openGallery());
        btnLenSong.setOnClickListener(v -> uploadPost());
        btnBack.setOnClickListener(v -> finish());

        // Tạo danh sách loại món ăn
        categoryList = new ArrayList<>();
        categoryList.add("Chọn loại món ăn");
        categoryList.add("Món Chính");
        categoryList.add("Salad");
        categoryList.add("Đồ uống");
        categoryList.add("Tráng Miệng");

        // Tạo Adapter để hiển thị danh sách lên Spinner
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerCategory.setAdapter(adapter);

        // Xử lý sự kiện chọn Spinner
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categoryList.get(position);
                if (!selectedCategory.equals("Chọn loại món ăn")) {
                    Toast.makeText(Share_RepiceActivity.this, "Bạn chọn: " + selectedCategory, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần làm gì
            }
        });
        postId = getIntent().getStringExtra("postId");
        if (postId != null) {
            isEditing = true;
            loadPostData(postId); // Gọi hàm để tải dữ liệu bài viết
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            img.setImageURI(imageUri);  // Hiển thị ảnh đã chọn
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void uploadPost() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để đăng bài!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String title = edtTenMon.getText().toString().trim();
        String cookingTime = edtThoiGian.getText().toString().trim();
        String ingredients = edtNguyenLieu.getText().toString().trim();
        String steps = edtCachLam.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(cookingTime) ||
                TextUtils.isEmpty(ingredients) || TextUtils.isEmpty(steps) ||
                category.equals("Chọn loại món ăn")) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu đang chỉnh sửa bài viết
        if (postId != null && !postId.isEmpty()) {
            if (imageUri != null) {
                // Nếu có ảnh mới -> Upload lên Cloudinary
                uploadImageToCloudinary(userId, postId, title, cookingTime, ingredients, steps, category);
            } else {
                // Nếu không có ảnh mới -> Dùng ảnh cũ
                savePostToDatabase(userId, postId, title, cookingTime, ingredients, steps, category, existingImageUrl);
            }
        } else {
            // Nếu là bài viết mới
            postId = UUID.randomUUID().toString();
            if (imageUri == null) {
                Toast.makeText(this, "Vui lòng chọn ảnh món ăn!", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImageToCloudinary(userId, postId, title, cookingTime, ingredients, steps, category);
        }
    }
private void savePostToDatabase(String userId, String postId, String title, String cookingTime,
                                String ingredients, String steps, String category, String imageUrl) {
    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Posts");

    HashMap<String, Object> postMap = new HashMap<>();
    postMap.put("postId", postId);
    postMap.put("userId", userId);
    postMap.put("title", title);
    postMap.put("cookingTime", cookingTime);
    postMap.put("ingredients", ingredients);
    postMap.put("steps", steps);
    postMap.put("category", category);
    postMap.put("timestamp", System.currentTimeMillis());
    // Nếu không có ảnh mới -> Giữ ảnh cũ
    if (!TextUtils.isEmpty(imageUrl)) {
        postMap.put("image", imageUrl);
    }

    databaseRef.child(postId).setValue(postMap)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, isEditing ? "Bài viết đã được cập nhật!" : "Bài viết đã được đăng!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Lỗi khi đăng bài: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

    // Gửi kết quả về Intent để cập nhật dữ liệu trên Activity khác
    Intent resultIntent = new Intent();
    resultIntent.putExtra("postId", postId);
    setResult(RESULT_OK, resultIntent);
    finish();
}
    private void loadPostData(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(Share_RepiceActivity.this, "Bài viết không tồn tại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy dữ liệu từ Firebase và đổ vào EditText
                edtTenMon.setText(snapshot.child("title").getValue(String.class));
                edtThoiGian.setText(snapshot.child("cookingTime").getValue(String.class));
                edtNguyenLieu.setText(snapshot.child("ingredients").getValue(String.class));
                edtCachLam.setText(snapshot.child("steps").getValue(String.class));

                existingImageUrl = snapshot.child("image").getValue(String.class);
                if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                    Glide.with(Share_RepiceActivity.this).load(existingImageUrl).into(img);
                }

                // Chọn lại danh mục trong Spinner
                String category = snapshot.child("category").getValue(String.class);
                if (category != null) {
                    int position = categoryList.indexOf(category);
                    if (position != -1) {
                        spinnerCategory.setSelection(position);
                    }
                }

//                // Load ảnh lên ImageView bằng Glide
                String imageUrl = snapshot.child("image").getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(Share_RepiceActivity.this).load(imageUrl).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Share_RepiceActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImageToCloudinary(String userId, String postId, String title, String cookingTime,
                                         String ingredients, String steps, String category) {
        File file = new File(getRealPathFromURI(imageUri)); // Chuyển URI sang File
        CloudinaryHelper cloudinaryHelper = new CloudinaryHelper();

        new Thread(() -> {
            String imageUrl = cloudinaryHelper.uploadImage(file);
            runOnUiThread(() -> {
                if (imageUrl != null) {
                    savePostToDatabase(userId, postId, title, cookingTime, ingredients, steps, category, imageUrl);
                } else {
                    Toast.makeText(this, "Lỗi khi tải ảnh lên Cloudinary!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
