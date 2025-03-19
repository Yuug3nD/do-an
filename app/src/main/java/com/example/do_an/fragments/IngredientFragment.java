package com.example.do_an.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.do_an.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IngredientFragment extends Fragment {
    private String postId;

    public IngredientFragment() {
        // Constructor mặc định bắt buộc khi dùng setArguments()
    }

    public static IngredientFragment newInstance(String postId) {
        IngredientFragment fragment = new IngredientFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        TextView tvIngredients = view.findViewById(R.id.tv_ingredients);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String postId = bundle.getString("postId");
            Log.d("IngredientFragment", "Nhận được postId: " + postId);

            // Lấy dữ liệu từ Firebase
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
            postRef.child("ingredients").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String ingredientsString = snapshot.getValue(String.class);
                    Log.d("FirebaseData", "Fragment nhận dữ liệu: " + ingredientsString);

                    if (ingredientsString != null) {
                        getActivity().runOnUiThread(() -> { // Đảm bảo cập nhật trên UI Thread
                            tvIngredients.setText(ingredientsString);
                            tvIngredients.setVisibility(View.VISIBLE);
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            tvIngredients.setText("Không có nguyên liệu.");
                            tvIngredients.setVisibility(View.VISIBLE);
                        });
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseData", "Lỗi tải dữ liệu: " + error.getMessage());
                }
            });
        } else {
            Log.e("IngredientFragment", "Không nhận được postId!");
        }

        return view;
    }
}

