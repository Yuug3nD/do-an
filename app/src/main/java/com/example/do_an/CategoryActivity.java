package com.example.do_an;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity{
    Toolbar toolbar;
    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood = new ArrayList<>();;
    RecyclerView recyclerView;
    TextView tvCategory;
    ImageButton imgBack;
    private Category mCategory;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_category);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tv_name_category), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.Toolbar_category);

        imgBack = findViewById(R.id.back_category);
        tvCategory = findViewById(R.id.title_category);
        recyclerView = findViewById(R.id.rcv_categories);

        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodAdapter = new FoodAdapter(ListFood);
        recyclerView.setAdapter(foodAdapter);

        Intent intent = getIntent();
        mCategory = (Category) intent.getSerializableExtra("category");

        if (mCategory != null) {
            tvCategory.setText(mCategory.getName());
            LoadData();
        }

    }

    void LoadData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListFood.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);

                    if (foodItem != null && foodItem.getCategory() != null &&
                            foodItem.getCategory().equals(mCategory.getName())) { // Chỉ lấy món thuộc category hiện tại
                        ListFood.add(foodItem);
                    }
                }
                foodAdapter.setListFood(ListFood);
                foodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu: " + databaseError.getMessage());
            }
        });
    }
}