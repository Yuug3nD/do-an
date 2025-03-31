package com.example.do_an.fragments;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;
import static androidx.recyclerview.widget.LinearLayoutManager.VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.do_an.Category;
import com.example.do_an.CategoryActivity;
import com.example.do_an.CategoryNewActivity;
import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.example.do_an.SearchActivity;
import com.example.do_an.adapter.CategoryAdapter;
import com.example.do_an.adapter.FoodAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener {
    EditText  edtSearch;
    RecyclerView rvsShare, rcvCategory;
    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood;

    private CategoryAdapter adapter = new CategoryAdapter(this);

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvsShare = view.findViewById(R.id.rv_popular);
        edtSearch=view.findViewById(R.id.edtSearch);
        rcvCategory = view.findViewById(R.id.rcv_category_home);
        setUpRecyclerView();
        LoadData();
        loadCategoriesFromFirestore();

        rvsShare.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodAdapter(ListFood);
        rvsShare.setAdapter(foodAdapter);
        edtSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        return  view;
    }

    private void setUpRecyclerView() {
        rcvCategory.setLayoutManager(new LinearLayoutManager(getContext(), HORIZONTAL, false));
        rcvCategory.setAdapter(adapter);
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

    private void loadCategoriesFromFirestore() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("Category");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Category> categories = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }
                adapter.submitList(categories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi lấy danh mục: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}