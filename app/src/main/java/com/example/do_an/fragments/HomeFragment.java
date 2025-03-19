package com.example.do_an.fragments;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.do_an.CategoryActivity;
import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.example.do_an.SearchActivity;
import com.example.do_an.adapter.FoodAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {
    EditText  edtSearch;
    RecyclerView rvsShare;
    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood;
    private ImageView imgMain, imgSalad, imgDrink, imgDesert;
    private TextView tvMain, tvSalad, tvDrink, tvDesert;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvsShare = view.findViewById(R.id.rv_popular);
        edtSearch=view.findViewById(R.id.edtSearch);

        LoadData();
        loadCategoriesFromFirestore(view);


        rvsShare.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodAdapter(ListFood);
        rvsShare.setAdapter(foodAdapter);
        edtSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        // Lấy tham chiếu View từ layout
        imgMain = view.findViewById(R.id.img_main);
        tvMain = view.findViewById(R.id.main);
        imgSalad = view.findViewById(R.id.img_salad);
        tvSalad = view.findViewById(R.id.salad);
        imgDrink = view.findViewById(R.id.img_drink);
        tvDrink = view.findViewById(R.id.drink);
        imgDesert = view.findViewById(R.id.img_desert);
        tvDesert = view.findViewById(R.id.desert);
        return  view;
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


    private void loadCategoriesFromFirestore(View view) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReference("Category"); // ✅ Trỏ đúng đến bảng "categories"

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DataSnapshot> categories = new ArrayList<>();

                // Log toàn bộ dữ liệu trả về từ Firebase
                Log.d("Firebase", "Data received: " + dataSnapshot.getValue());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    categories.add(snapshot);
                }

                if (categories.size() >= 4) {
                    // Lấy 4 danh mục đầu tiên
                    DataSnapshot catMain = categories.get(0);
                    DataSnapshot catSalad = categories.get(1);
                    DataSnapshot catDrink = categories.get(2);
                    DataSnapshot catDesert = categories.get(3);

                    // Kiểm tra log từng danh mục
                    Log.d("Firebase", "Main Category: " + catMain.getValue());
                    Log.d("Firebase", "Salad Category: " + catSalad.getValue());
                    Log.d("Firebase", "Drink Category: " + catDrink.getValue());
                    Log.d("Firebase", "Desert Category: " + catDesert.getValue());
                } else {
                    Log.e("Firebase", "Không đủ danh mục (phải có ít nhất 4 danh mục)");
                }
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
        onClick();
    }

    public void onClick() {
        Map<ImageView, TextView> map = new HashMap<>();
        map.put(imgMain, tvMain);
        map.put(imgSalad, tvSalad);
        map.put(imgDrink, tvDrink);
        map.put(imgDesert, tvDesert);

        View.OnClickListener listener = v -> {
            TextView categoryText = map.get(v);
            if (categoryText != null) {
                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                intent.putExtra("category", categoryText.getText().toString());
                startActivity(intent);
            }
        };

        for (ImageView img : map.keySet()) {
            img.setOnClickListener(listener);
        }
    }
}