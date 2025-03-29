package com.example.do_an.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.do_an.adapter.FoodAdapter;
import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class shareFragment extends Fragment {
    TextView tvMessage;
    RecyclerView rvsShare;
    FoodAdapter foodAdapter;
    ArrayList<FoodItem> ListFood;
    DatabaseReference postsRef;
    FirebaseAuth firebaseAuth;

    public shareFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        rvsShare = view.findViewById(R.id.rcv_share);
        tvMessage = view.findViewById(R.id.tv_message);

//        LoadData();

        ListFood = new ArrayList<>();
        rvsShare.setLayoutManager(new LinearLayoutManager(getContext()));
        foodAdapter = new FoodAdapter(ListFood);
        rvsShare.setAdapter(foodAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        loadUserPosts();

        return  view;
    }
    //    void LoadData()
//    {
//        ListFood = new ArrayList<>();
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-5.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//        ListFood.add(new FoodItem("Món 1", "ngom quá dễ lamfdef", "cf-1.jpg"));
//
//    }
    private void loadUserPosts() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            tvMessage.setText("Bạn chưa đăng nhập!");
            tvMessage.setVisibility(View.VISIBLE);
            return;
        }

        String userId = user.getUid();
        postsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListFood.clear();

                if (!snapshot.exists()) {
                    tvMessage.setText("Bạn chưa có bài viết nào!");
                    tvMessage.setVisibility(View.VISIBLE);
                    return;
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FoodItem food = postSnapshot.getValue(FoodItem.class);
                    if (food != null) {
                        ListFood.add(food);
                    }
                }

                foodAdapter.notifyDataSetChanged();
                tvMessage.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải bài viết: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu khi Fragment được hiển thị lại
        loadUserPosts();
    }
}