package com.example.do_an.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.do_an.FoodItem;
import com.example.do_an.R;
import com.example.do_an.adapter.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class saveFragment extends Fragment {
    private RecyclerView recyclerView;
    private FoodAdapter foodAdapter;
    private ArrayList<FoodItem> savedRecipes;

    public saveFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rcv_save);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Danh sách công thức đã lưu
        savedRecipes = new ArrayList<>();
        foodAdapter = new FoodAdapter(savedRecipes);
        recyclerView.setAdapter(foodAdapter);

        // Tải danh sách công thức đã lưu
        loadSavedPosts();

        return view;
    }

    private void loadSavedPosts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference("SavedPosts").child(user.getUid());

        savedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> savedPostIds = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    savedPostIds.add(data.getKey());
                }

                loadRecipes(savedPostIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải bài viết đã lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRecipes(List<String> savedPostIds) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedRecipes.clear(); // Xóa danh sách cũ trước khi tải mới

                for (DataSnapshot data : snapshot.getChildren()) {
                    FoodItem recipe = data.getValue(FoodItem.class);
                    if (recipe != null && savedPostIds.contains(recipe.getPostId())) {
                        savedRecipes.add(recipe);
                    }
                }

                foodAdapter.notifyDataSetChanged(); // Cập nhật giao diện sau khi có dữ liệu mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải công thức!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
