package com.example.do_an;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.adapter.FoodAdapter;
import com.example.do_an.fragments.Category_ViewFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity{
    Toolbar toolbar;

    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood;

    RecyclerView recyclerView;
    TextView tvCategory;
    ImageButton imgBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.Toolbar_category);

        imgBack = findViewById(R.id.back_category);
        tvCategory = findViewById(R.id.title_category);
        recyclerView = findViewById(R.id.rcv_categories);

        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        tvCategory.setText(category);

        LoadData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodAdapter = new FoodAdapter(ListFood);
        recyclerView.setAdapter(foodAdapter);

       loadCategoryFragment();
    }

    void LoadData() {
        ListFood = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListFood.clear(); // Xóa danh sách cũ để cập nhật danh sách mới

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Tạo một đối tượng FoodItem từ dữ liệu Firebase
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);

                    // Kiểm tra nếu dữ liệu hợp lệ thì thêm vào danh sách
                    if (foodItem != null) {
                        // Kiểm tra nếu category bị null thì gán giá trị mặc định
                        if (foodItem.getCategory() == null) {
                            foodItem.setCategory("Chưa phân loại"); // ✅ Gán category mặc định
                        }
                        ListFood.add(foodItem);
                    }
                }

                // Cập nhật RecyclerView sau khi lấy dữ liệu từ Firebase
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi khi lấy dữ liệu
                System.out.println("Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
            }
        });
    }

    private void loadCategoryFragment() {
        Fragment fragment = new Category_ViewFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.Frame_Category, fragment);
        transaction.commit();
    }


}